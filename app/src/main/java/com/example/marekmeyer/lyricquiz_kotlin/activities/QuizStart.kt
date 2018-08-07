package com.example.marekmeyer.lyricquiz_kotlin.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.marekmeyer.lyricquiz_kotlin.R
import com.example.marekmeyer.lyricquiz_kotlin.models.DataManager


class QuizStart : AppCompatActivity(){

    private val TAG: String = "Quiz Start"
    private val localBroadcastManager = LocalBroadcastManager.getInstance(this)
    private var startPlayingClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_start)

        val filter = IntentFilter()
        filter.addAction(DataManager.actionQuiz)
        localBroadcastManager.registerReceiver(receiver, filter)
    }

    fun startPlaying(view: View){
        Log.i(TAG, "navigate start quiz")
        startPlayingClicked = true
        if(DataManager.quizAvailable){
            val warningText = findViewById<TextView>(R.id.quizStartLoadingMessage)
            warningText.text = ""
            navigateToFirstQuestion()
        } else{
            val warningText = findViewById<TextView>(R.id.quizStartLoadingMessage)
            warningText.text = resources.getString(R.string.text_activity_quiz_start_warning)
        }

    }

    override fun onDestroy() {
        localBroadcastManager.unregisterReceiver(receiver)
        super.onDestroy()
    }

    private fun navigateToFirstQuestion(){
        val intent = Intent(this, QuizQuestion::class.java)
        startActivity(intent)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if(startPlayingClicked){
                navigateToFirstQuestion()
            }
        }
    }
}
