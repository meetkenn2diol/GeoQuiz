package com.bignerdbranch.android.geoquiz

import android.util.Log
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"

class QuizViewModel : ViewModel() {
    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )
    var currentIndex = 0
    var isCheater=false
    var numOfCorrectlyAnswered = 0
    var numOfQuestionAnswered = 0
    var numOfQuestionsCheated=0
    var answeredQuestionsSet = hashSetOf<Int>(questionBank.size + 1)
    var cheatedQuestionsSet= hashSetOf<Int>(questionBank.size+1)


    val currentQuestionAnswer: Boolean get() = questionBank[currentIndex].answer
    val currentQuestionText: Int get() = questionBank[currentIndex].textResId

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun moveToPrevious() {
        currentIndex =
            (if (currentIndex == 0) questionBank.size - 1 else --currentIndex) % questionBank.size
    }

    fun updateNumOfCorrectlyAnswered(answerIsCorrect: Boolean) {
        if (answerIsCorrect) numOfCorrectlyAnswered += 1
    }

    fun increaseNumOfQuestionAnswered() {
        ++numOfQuestionAnswered
    }

    fun updateAnsweredQuestionSet() {
        answeredQuestionsSet.add(currentIndex)
    }

    fun getQuestionBankSize(): Int = questionBank.size
}