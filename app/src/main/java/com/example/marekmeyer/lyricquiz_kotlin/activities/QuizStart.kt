package com.example.marekmeyer.lyricquiz_kotlin.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.marekmeyer.lyricquiz_kotlin.R

class QuizStart : AppCompatActivity() {

    private val TAG: String = "Quiz Start"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_start)
    }

    fun startPlaying(view: View){
        Log.i(TAG, "navigate start quiz")
        val intent = Intent(this, AuthSpotify::class.java)

        startActivity(intent)

    }

    fun createQuiz(){

    }
}
