package com.example.marekmeyer.lyricquiz_kotlin.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.content.LocalBroadcastManager
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
    private lateinit var buttonTrack1: Button
    private lateinit var buttonTrack2: Button
    private lateinit var buttonTrack3: Button
    private lateinit var buttonTrack4: Button
    private lateinit var question: Question

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_question)

        textViewLyrics = findViewById(R.id.quizQuestionLyrics)
        buttonTrack1 = findViewById(R.id.quizQuestionTrack1)
        buttonTrack2 = findViewById(R.id.quizQuestionTrack2)
        buttonTrack3 = findViewById(R.id.quizQuestionTrack3)
        buttonTrack4 = findViewById(R.id.quizQuestionTrack4)

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
        val clickedButtonId = view.id
        var selectedTrackName: String = ""

        when(clickedButtonId){
            R.id.quizQuestionTrack1 -> selectedTrackName = question.choices[0]
            R.id.quizQuestionTrack2 -> selectedTrackName = question.choices[1]
            R.id.quizQuestionTrack3 -> selectedTrackName = question.choices[2]
            R.id.quizQuestionTrack4 -> selectedTrackName = question.choices[3]
        }

        if(selectedTrackName == question.trackName){
            Log.e(TAG, "right answer")
            DataManager.quiz.questions[DataManager.quiz.questionCounter].correct
        }else{
            !DataManager.quiz.questions[DataManager.quiz.questionCounter].correct
            Log.e(TAG, "wrong answer")
        }

    }

    fun nextQuestion(view: View){

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
