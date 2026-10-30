package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.local.AppDatabase
import com.example.data.local.QuizHistoryEntity
import com.example.data.local.SampleQuestionsBank
import com.example.data.model.OverallStats
import com.example.data.model.Question
import com.example.data.model.Screen
import com.example.data.model.UserQuestionAnswer
import com.example.data.preferences.AppPreferences
import com.example.data.remote.GeminiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max

data class LgsUiState(
    val currentScreen: Screen = Screen.HOME,
    val activeSubject: String = "Matematik",
    val questionCount: Int = 10,
    val useAiGeneration: Boolean = true,
    val apiKey: String = "",
    val isApiKeyValid: Boolean? = null,
    val apiStatusMessage: String = "",
    val isVerifyingKey: Boolean = false,
    val isLoading: Boolean = false,
    val loadingTip: String = "",
    val errorMessage: String? = null,
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOption: Int? = null,
    val isAnswerConfirmed: Boolean = false,
    val userAnswers: List<UserQuestionAnswer> = emptyList(),
    val scoreCorrect: Int = 0,
    val scoreWrong: Int = 0,
    val scoreNet: Double = 0.0,
    val historyList: List<QuizHistoryEntity> = emptyList(),
    val stats: OverallStats = OverallStats(),
    val netPenaltyRatio: Float = 3.0f // 3 for MEB standard (3 yanlış 1 doğru)
)

class LgsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = AppPreferences(application)
    private val geminiService = GeminiService()
    private val database = AppDatabase.getInstance(application)
    private val historyDao = database.quizHistoryDao()

    private val _uiState = MutableStateFlow(
        LgsUiState(
            apiKey = getEffectiveApiKey(),
            questionCount = prefs.defaultCount,
            netPenaltyRatio = prefs.netPenalty
        )
    )
    val uiState: StateFlow<LgsUiState> = _uiState.asStateFlow()

    init {
        observeHistory()
    }

    private fun getEffectiveApiKey(): String {
        val saved = prefs.apiKey
        if (saved.isNotBlank()) return saved
        val buildKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
        return if (buildKey.isNotBlank() && buildKey != "MY_GEMINI_API_KEY") buildKey else ""
    }

    private fun observeHistory() {
        viewModelScope.launch {
            historyDao.getAllHistory().collectLatest { list ->
                var totalQ = 0
                var totalCorrect = 0
                var totalWrong = 0
                var totalNet = 0.0

                for (item in list) {
                    totalQ += item.total
                    totalCorrect += item.correct
                    totalWrong += item.wrong
                    totalNet += item.net
                }

                val accuracy = if (totalQ > 0) ((totalCorrect.toDouble() / totalQ) * 100).toInt() else 0

                _uiState.value = _uiState.value.copy(
                    historyList = list,
                    stats = OverallStats(
                        totalQuestions = totalQ,
                        totalCorrect = totalCorrect,
                        totalWrong = totalWrong,
                        totalNet = max(0.0, totalNet),
                        accuracyPercent = accuracy
                    )
                )
            }
        }
    }

    fun setApiKeyInput(key: String) {
        val clean = key.trim()
        prefs.apiKey = clean
        _uiState.value = _uiState.value.copy(
            apiKey = clean,
            isApiKeyValid = null,
            apiStatusMessage = ""
        )
    }

    fun verifyApiKey() {
        val key = _uiState.value.apiKey.ifBlank { getEffectiveApiKey() }
        if (key.isBlank()) {
            _uiState.value = _uiState.value.copy(
                isApiKeyValid = false,
                apiStatusMessage = "Önce bir API anahtarı girin."
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            isVerifyingKey = true,
            apiStatusMessage = "Anahtar Google Generative AI üzerinden doğrulanıyor..."
        )

        viewModelScope.launch {
            val result = geminiService.verifyApiKey(key)
            result.onSuccess { msg ->
                _uiState.value = _uiState.value.copy(
                    isVerifyingKey = false,
                    isApiKeyValid = true,
                    apiStatusMessage = msg
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isVerifyingKey = false,
                    isApiKeyValid = false,
                    apiStatusMessage = err.message ?: "Doğrulama başarısız oldu."
                )
            }
        }
    }

    fun setQuestionCount(count: Int) {
        prefs.defaultCount = count
        _uiState.value = _uiState.value.copy(questionCount = count)
    }

    fun setNetPenaltyRatio(ratio: Float) {
        prefs.netPenalty = ratio
        _uiState.value = _uiState.value.copy(netPenaltyRatio = ratio)
    }

    fun toggleUseAiGeneration(useAi: Boolean) {
        _uiState.value = _uiState.value.copy(useAiGeneration = useAi)
    }

    fun navigateTo(screen: Screen) {
        _uiState.value = _uiState.value.copy(currentScreen = screen, errorMessage = null)
    }

    fun startQuiz(subject: String) {
        val count = _uiState.value.questionCount
        val useAi = _uiState.value.useAiGeneration
        val key = _uiState.value.apiKey.ifBlank { getEffectiveApiKey() }

        _uiState.value = _uiState.value.copy(
            activeSubject = subject,
            currentScreen = Screen.LOADING,
            loadingTip = "Yapay zeka 2027 MEB LGS formatında $count özgün soru üretiyor...",
            errorMessage = null
        )

        viewModelScope.launch {
            if (useAi && key.isNotBlank()) {
                val result = geminiService.generateQuestions(key, subject, count)
                result.onSuccess { generatedQuestions ->
                    initQuizState(generatedQuestions)
                }.onFailure { error ->
                    // Fallback to high quality offline MEB sample questions
                    val fallbackQuestions = SampleQuestionsBank.getQuestionsForSubject(subject, count)
                    initQuizState(
                        fallbackQuestions,
                        noticeMessage = "Yapay zeka bağlantısında sorun oluştu (${error.message}). MEB 2027 örnek soru havuzu yüklendi."
                    )
                }
            } else {
                // Offline MEB sample pool
                val questions = SampleQuestionsBank.getQuestionsForSubject(subject, count)
                initQuizState(questions)
            }
        }
    }

    private fun initQuizState(questions: List<Question>, noticeMessage: String? = null) {
        _uiState.value = _uiState.value.copy(
            currentScreen = Screen.QUIZ,
            questions = questions,
            currentIndex = 0,
            selectedOption = null,
            isAnswerConfirmed = false,
            userAnswers = emptyList(),
            scoreCorrect = 0,
            scoreWrong = 0,
            scoreNet = 0.0,
            errorMessage = noticeMessage
        )
    }

    fun selectOption(optionIndex: Int) {
        val state = _uiState.value
        if (state.isAnswerConfirmed) return // already locked

        val currentQ = state.questions.getOrNull(state.currentIndex) ?: return
        val isCorrect = optionIndex == currentQ.answer

        val newCorrect = if (isCorrect) state.scoreCorrect + 1 else state.scoreCorrect
        val newWrong = if (!isCorrect) state.scoreWrong + 1 else state.scoreWrong

        val updatedAnswers = state.userAnswers.toMutableList().apply {
            add(UserQuestionAnswer(question = currentQ, selectedIndex = optionIndex))
        }

        _uiState.value = state.copy(
            selectedOption = optionIndex,
            isAnswerConfirmed = true,
            scoreCorrect = newCorrect,
            scoreWrong = newWrong,
            userAnswers = updatedAnswers
        )
    }

    fun nextQuestion() {
        val state = _uiState.value
        val nextIdx = state.currentIndex + 1

        if (nextIdx >= state.questions.size) {
            finishQuiz()
        } else {
            _uiState.value = state.copy(
                currentIndex = nextIdx,
                selectedOption = null,
                isAnswerConfirmed = false
            )
        }
    }

    fun finishQuiz() {
        val state = _uiState.value
        val penalty = state.netPenaltyRatio
        val net = max(0.0, state.scoreCorrect - (state.scoreWrong / penalty.toDouble()))
        val roundedNet = (Math.round(net * 100.0) / 100.0)

        val dateFormat = SimpleDateFormat("d MMM, HH:mm", Locale("tr", "TR"))
        val dateString = dateFormat.format(Date())

        val record = QuizHistoryEntity(
            subject = state.activeSubject,
            total = state.questions.size,
            correct = state.scoreCorrect,
            wrong = state.scoreWrong,
            net = roundedNet,
            dateString = dateString
        )

        viewModelScope.launch {
            historyDao.insert(record)
        }

        _uiState.value = state.copy(
            scoreNet = roundedNet,
            currentScreen = Screen.RESULT
        )
    }

    fun clearHistory() {
        viewModelScope.launch {
            historyDao.clearAll()
        }
    }
}
