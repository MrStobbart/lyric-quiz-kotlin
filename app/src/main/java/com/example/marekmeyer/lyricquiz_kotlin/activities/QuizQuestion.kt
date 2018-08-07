package com.example.marekmeyer.lyricquiz_kotlin.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.AppCompatButton
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.marekmeyer.lyricquiz_kotlin.R
import com.example.marekmeyer.lyricquiz_kotlin.models.DataManager
import com.example.marekmeyer.lyricquiz_kotlin.models.Question

class QuizQuestion : AppCompatActivity() {

    private val TAG: String = "Quiz Question"

    private lateinit var textViewLyrics: TextView
    private lateinit var buttonTrack1: AppCompatButton
    private lateinit var buttonTrack2: AppCompatButton
    private lateinit var buttonTrack3: AppCompatButton
    private lateinit var buttonTrack4: AppCompatButton
    private lateinit var buttonNextQuestion: AppCompatButton
    private lateinit var textViewWarning: TextView
    private lateinit var question: Question
    private var answerSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_question)

        textViewLyrics = findViewById(R.id.quizQuestionLyrics)
        buttonTrack1 = findViewById(R.id.quizQuestionTrack1)
        buttonTrack2 = findViewById(R.id.quizQuestionTrack2)
        buttonTrack3 = findViewById(R.id.quizQuestionTrack3)
        buttonTrack4 = findViewById(R.id.quizQuestionTrack4)
        buttonNextQuestion= findViewById(R.id.quizQuestionNextQuestion)
        textViewWarning = findViewById(R.id.quizQuestionWarning)

        if(!DataManager.quizAvailable) {
            Log.e(TAG, "Quiz not available")
        }
        loadQuestion()
    }

    private fun loadQuestion(){
        question = DataManager.quiz.questions[DataManager.quiz.questionCounter]
        textViewLyrics.text = question.lyrics
        buttonTrack1.text = question.choices[0]
        buttonTrack2.text = question.choices[1]
        buttonTrack3.text = question.choices[2]
        buttonTrack4.text = question.choices[3]
    }



    fun selectAnswer(view: View){
        textViewWarning.text = ""
        if(answerSelected){
            return
        }
        buttonNextQuestion.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#ebf7fd"))
        answerSelected = true

        val clickedButtonId = view.id
        var selectedTrackName = ""

        when(clickedButtonId){
            R.id.quizQuestionTrack1 -> selectedTrackName = question.choices[0]
            R.id.quizQuestionTrack2 -> selectedTrackName = question.choices[1]
            R.id.quizQuestionTrack3 -> selectedTrackName = question.choices[2]
            R.id.quizQuestionTrack4 -> selectedTrackName = question.choices[3]
        }

        val clickedButton: AppCompatButton = findViewById(clickedButtonId)

        if(selectedTrackName == question.trackName){
            Log.e(TAG, "right answer")
            clickedButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#f2fae3"))
            DataManager.quiz.questions[DataManager.quiz.questionCounter].correct = true
        }else{
            clickedButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#fff1f0"))
            DataManager.quiz.questions[DataManager.quiz.questionCounter].correct = false
            Log.e(TAG, "wrong answer")
        }

    }

    fun nextQuestion(view: View){
        if (!answerSelected){
            textViewWarning.text = resources.getString(R.string.text_activity_quiz_question_warning)
            return
        }

        val questionCounter = DataManager.quiz.questionCounter
        val numberOfQuestions = DataManager.quiz.questions.size

        if(questionCounter >= numberOfQuestions - 1){
            // Go to quiz results
            val intent = Intent(this, QuizResults::class.java)
            startActivity(intent)
        } else{
            // Restart activity
            DataManager.quiz.questionCounter = questionCounter + 1
            finish()
            startActivity(intent)
        }

    }


}
