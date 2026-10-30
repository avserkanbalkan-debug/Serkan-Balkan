package com.example.data.remote

import android.util.Log
import com.example.data.model.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val models = listOf(
        "gemini-2.5-flash",
        "gemini-2.0-flash",
        "gemini-1.5-flash"
    )

    suspend fun verifyApiKey(apiKey: String): Result<String> = withContext(Dispatchers.IO) {
        val cleanKey = cleanApiKey(apiKey)
        if (cleanKey.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Lütfen bir API anahtarı girin."))
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models?key=$cleanKey"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (response.isSuccessful) {
                    val json = JSONObject(body)
                    val count = json.optJSONArray("models")?.length() ?: 0
                    Result.success("Anahtar geçerli! Kullanılabilir model sayısı: $count. Soru üretimine hazırsınız.")
                } else {
                    val errMsg = try {
                        val json = JSONObject(body)
                        json.optJSONObject("error")?.optString("message") ?: "HTTP ${response.code}"
                    } catch (e: Exception) {
                        "HTTP ${response.code}: $body"
                    }
                    Result.failure(Exception("Anahtar reddedildi: $errMsg"))
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception("Bağlantı kurulamadı: ${e.localizedMessage ?: "Ağ hatası"}"))
        }
    }

    suspend fun generateQuestions(
        apiKey: String,
        subject: String,
        count: Int
    ): Result<List<Question>> = withContext(Dispatchers.IO) {
        val cleanKey = cleanApiKey(apiKey)
        if (cleanKey.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("API anahtarı bulunamadı."))
        }

        val prompt = buildPrompt(subject, count)
        var lastError: Exception? = null

        for (model in models) {
            try {
                val questions = callModel(cleanKey, model, prompt)
                if (questions.isNotEmpty()) {
                    return@withContext Result.success(questions)
                }
            } catch (e: Exception) {
                Log.w("GeminiService", "Model $model failed: ${e.message}")
                lastError = e
            }
        }

        Result.failure(lastError ?: Exception("Yapay zeka soru üretemedi."))
    }

    private fun buildPrompt(subject: String, count: Int): String {
        return """
Sen MEB müfredatını ve LGS sınav formatını çok iyi bilen uzman bir 8. sınıf branş öğretmenisin.
"$subject" dersi için, 2027 LGS müfredatına ve yeni nesil mantık-muhakeme tarzına birebir uygun, tamamen özgün $count adet çoktan seçmeli soru hazırla.

Kurallar:
- Her sorunun 4 seçeneği olmalı: A, B, C, D (örnek: ["A) ...", "B) ...", "C) ...", "D) ..."]).
- "answer" alanı, doğru şıkkın SIFIR tabanlı indeksi olmalıdır (A=0, B=1, C=2, D=3).
- "sol" alanında sorunun adım adım MEB çözümünü ve konu özetini yaz.
- Matematik ve fen formüllerini düzgün okunaklı metin olarak yaz (örn: x^2, √3, 3/4).
- Yanıtı YALNIZCA geçerli bir JSON dizisi olarak ver. Markdown kod bloğu, selamlama veya başka metin ekleme.

JSON Formatı:
[
  {
    "q": "Soru metni...",
    "options": ["A) Şık 1", "B) Şık 2", "C) Şık 3", "D) Şık 4"],
    "answer": 0,
    "sol": "Adım adım detaylı çözüm..."
  }
]
        """.trimIndent()
    }

    private fun callModel(apiKey: String, model: String, prompt: String): List<Question> {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

        val jsonBody = JSONObject().apply {
            val partsArray = JSONArray().put(JSONObject().put("text", prompt))
            val contentsArray = JSONArray().put(JSONObject().put("role", "user").put("parts", partsArray))
            put("contents", contentsArray)

            val genConfig = JSONObject().apply {
                put("temperature", 0.7)
                put("maxOutputTokens", 8192)
                put("responseMimeType", "application/json")
            }
            put("generationConfig", genConfig)
        }

        val request = Request.Builder()
            .url(url)
            .post(jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                val errorMsg = try {
                    JSONObject(body).optJSONObject("error")?.optString("message") ?: "HTTP ${response.code}"
                } catch (e: Exception) {
                    "HTTP ${response.code}"
                }
                throw Exception("$model: $errorMsg")
            }

            val respJson = JSONObject(body)
            val candidates = respJson.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                throw Exception("Model yanıt üretmedi.")
            }

            val candidate = candidates.getJSONObject(0)
            val parts = candidate.optJSONObject("content")?.optJSONArray("parts")
            val textBuilder = StringBuilder()
            if (parts != null) {
                for (i in 0 until parts.length()) {
                    textBuilder.append(parts.getJSONObject(i).optString("text", ""))
                }
            }

            val rawText = textBuilder.toString()
            return parseQuestions(rawText)
        }
    }

    fun parseQuestions(rawText: String): List<Question> {
        var clean = rawText
            .replace("```json", "", ignoreCase = true)
            .replace("```", "")
            .trim()

        val jsonArray: JSONArray = try {
            if (clean.startsWith("[")) {
                JSONArray(clean)
            } else if (clean.startsWith("{")) {
                val obj = JSONObject(clean)
                obj.optJSONArray("questions")
                    ?: obj.optJSONArray("data")
                    ?: obj.optJSONArray("items")
                    ?: JSONArray()
            } else {
                // Try finding array within brackets
                val start = clean.indexOf('[')
                val end = clean.lastIndexOf(']')
                if (start != -1 && end != -1 && end > start) {
                    JSONArray(clean.substring(start, end + 1))
                } else {
                    throw Exception("JSON dizi bulunamadı")
                }
            }
        } catch (e: Exception) {
            throw Exception("Yapay zeka yanıtı ayrıştırılamadı: ${e.message}")
        }

        val list = mutableListOf<Question>()
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.optJSONObject(i) ?: continue
            val q = item.optString("q").ifBlank { item.optString("question") }
            if (q.isBlank()) continue

            val rawOptions = item.optJSONArray("options") ?: item.optJSONArray("choices")
            val optionsList = mutableListOf<String>()
            val prefixes = listOf("A) ", "B) ", "C) ", "D) ")

            if (rawOptions != null) {
                for (j in 0 until minOf(rawOptions.length(), 4)) {
                    var optText = rawOptions.optString(j, "").trim()
                    if (!optText.matches(Regex("^[A-D][).].*"))) {
                        optText = "${prefixes[j]}$optText"
                    }
                    optionsList.add(optText)
                }
            }

            while (optionsList.size < 4) {
                optionsList.add("${prefixes[optionsList.size]}Seçenek")
            }

            val rawAns = item.opt("answer") ?: item.opt("correct")
            var answerIndex = when (rawAns) {
                is Number -> rawAns.toInt()
                is String -> {
                    val s = rawAns.trim().uppercase()
                    when {
                        s.startsWith("A") -> 0
                        s.startsWith("B") -> 1
                        s.startsWith("C") -> 2
                        s.startsWith("D") -> 3
                        else -> s.toIntOrNull() ?: 0
                    }
                }
                else -> 0
            }
            if (answerIndex !in 0..3) answerIndex = 0

            val sol = item.optString("sol").ifBlank {
                item.optString("solution").ifBlank { "Adım adım MEB çözümü hazırlanmıştır." }
            }

            list.add(
                Question(
                    q = q,
                    options = optionsList,
                    answer = answerIndex,
                    sol = sol
                )
            )
        }

        if (list.isEmpty()) {
            throw Exception("Geçerli soru bulunamadı.")
        }

        return list
    }

    private fun cleanApiKey(key: String): String {
        val s = key.trim()
        val regex = Regex("AIza[0-9A-Za-z_\\-]{30,}")
        val match = regex.find(s)
        return match?.value ?: s
    }
}
