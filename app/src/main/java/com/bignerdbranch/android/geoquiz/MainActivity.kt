package com.bignerdbranch.android.geoquiz

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider

private const val TAG = "MainActivity.kt"
private const val KEY_INDEX = "index"
private const val REQUEST_CODE_CHEAT = 0


class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: Button
    private lateinit var previousButton: Button
    private lateinit var questionTextView: TextView
    private lateinit var cheatButton: Button


    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProvider(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex

        //extra data for disabling a button and/or grading questions
        val numOfCorrectlyAnswered = savedInstanceState?.getInt("numOfCorrectlyAnswered", 0) ?: 0
        quizViewModel.numOfCorrectlyAnswered = numOfCorrectlyAnswered
        val numOfQuestionAnswered = savedInstanceState?.getInt("numOfQuestionAnswered", 0) ?: 0
        quizViewModel.numOfQuestionAnswered = numOfQuestionAnswered
        val answeredQuestionsSet = savedInstanceState?.getIntArray("answeredQuestionsSet")
        if (answeredQuestionsSet != null) {
            quizViewModel.answeredQuestionsSet = answeredQuestionsSet.toHashSet()
        }


        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        previousButton = findViewById(R.id.previous_button)
        questionTextView = findViewById(R.id.question_text_view)
        cheatButton = findViewById(R.id.cheat_button)


        trueButton.setOnClickListener { view: View ->
            checkAnswer(true)
        }
        falseButton.setOnClickListener { view: View ->
            checkAnswer(false)
        }
        nextButton.setOnClickListener { view: View ->
            quizViewModel.moveToNext()
            updateQuestion()
        }
        previousButton.setOnClickListener { view: View ->
            quizViewModel.moveToPrevious()
            updateQuestion();
        }
        questionTextView.setOnClickListener { view: View ->
            quizViewModel.moveToNext()
            updateQuestion()
        }
        cheatButton.setOnClickListener { view: View ->
            //start Activity
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            val optionsCompat =
                ActivityOptionsCompat.makeClipRevealAnimation(view, 0, 0, view.width, view.height)
            resultLauncher.launch(intent, optionsCompat)

            /*  startActivityForResult(intent, REQUEST_CODE_CHEAT)*/
        }
        updateQuestion()
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
            }
            val data: Intent? = result.data
            quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false

            //adding values to know which number was cheated upon
            if (quizViewModel.isCheater) {
                quizViewModel.cheatedQuestionsSet.add(quizViewModel.currentIndex)
                quizViewModel.numOfQuestionsCheated += 1
            }

            //   }

        }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i(TAG, "onSavedInstanceState  has been called")
        outState.putInt(KEY_INDEX, quizViewModel.currentIndex)

        //extra data for disabling a button and/or grading questions
        outState.putInt("numOfCorrectlyAnswered", quizViewModel.numOfCorrectlyAnswered)
        outState.putInt("numOfQuestionAnswered", quizViewModel.numOfQuestionAnswered)
        outState.putIntArray(
            "answeredQuestionsSet",
            quizViewModel.answeredQuestionsSet.toIntArray()
        )
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
        Log.d(TAG, "onDestroy() and isKillable(): ${isFinishing()}")
    }

    private fun updateQuestion() {
        //before updating the question to the textview, confirm that the question is already anwered or not
        disableAnswerButtons(shouldDisableButton())
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
    }


    private fun checkAnswer(userAnswer: Boolean) {
        var correctAnswer = quizViewModel.currentQuestionAnswer

        var answerIsCorrect = (userAnswer == correctAnswer)
        val messageResId = when {
            quizViewModel.isCheater && quizViewModel.cheatedQuestionsSet.contains(quizViewModel.currentIndex) -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }
        quizViewModel.updateNumOfCorrectlyAnswered(answerIsCorrect)

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()

        //increase the number of answered question count and add the question answered into the set
        quizViewModel.increaseNumOfQuestionAnswered()
        quizViewModel.updateAnsweredQuestionSet()
        //disable the button on answering the question
        disableAnswerButtons(shouldDisableButton())

        //Display the final Score if Complete
        toastTheFinalScore()
    }

    private fun disableAnswerButtons(answer: Boolean) {
        trueButton.setEnabled(!answer)
        falseButton.setEnabled(!answer)
    }

    private fun shouldDisableButton(): Boolean =
        quizViewModel.answeredQuestionsSet.contains(quizViewModel.currentIndex)

    private fun toastTheFinalScore() {
        //Toast the total Scores after all the questions are answered
        if (quizViewModel.numOfQuestionAnswered == quizViewModel.getQuestionBankSize()) {
            Toast.makeText(
                this,
                "Your Test Scored is: ${quizViewModel.numOfCorrectlyAnswered}/${quizViewModel.getQuestionBankSize()} \n " +
                        "You cheated in: ${quizViewModel.numOfQuestionsCheated}/${quizViewModel.getQuestionBankSize()} questions",
                Toast.LENGTH_LONG
            ).show()
        }

    }
}
