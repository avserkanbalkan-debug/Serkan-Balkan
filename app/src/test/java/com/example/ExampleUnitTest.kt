package com.example

import com.example.data.local.SampleQuestionsBank
import com.example.data.remote.GeminiService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleUnitTest {
    @Test
    fun testSampleQuestionsBank() {
        val mathQuestions = SampleQuestionsBank.getQuestionsForSubject("Matematik", 3)
        assertEquals(3, mathQuestions.size)
        mathQuestions.forEach { q ->
            assertTrue(q.q.isNotBlank())
            assertEquals(4, q.options.size)
            assertTrue(q.answer in 0..3)
            assertTrue(q.sol.isNotBlank())
        }
    }

    @Test
    fun testParseGeminiQuestionsJson() {
        val json = """
            [
              {
                "q": "2x + 6 = 14 ise x kaçtır?",
                "options": ["A) 2", "B) 4", "C) 6", "D) 8"],
                "answer": 1,
                "sol": "2x = 8 -> x = 4"
              }
            ]
        """.trimIndent()

        val service = GeminiService()
        val questions = service.parseQuestions(json)
        assertEquals(1, questions.size)
        val first = questions[0]
        assertEquals("2x + 6 = 14 ise x kaçtır?", first.q)
        assertEquals(4, first.options.size)
        assertEquals(1, first.answer)
        assertEquals("2x = 8 -> x = 4", first.sol)
    }

    @Test
    fun testNetCalculation() {
        val correct = 8
        val wrong = 3
        val penalty = 3.0 // MEB: 3 wrong cancels 1 correct
        val net = correct - (wrong / penalty)
        assertEquals(7.0, net, 0.001)
    }
}
