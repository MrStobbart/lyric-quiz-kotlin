package com.example.marekmeyer.lyricquiz_kotlin.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.View
import com.example.marekmeyer.lyricquiz_kotlin.R
import com.example.marekmeyer.lyricquiz_kotlin.models.DataManager


class QuizStart : AppCompatActivity(){

    private val TAG: String = "Quiz Start"
    private val localBroadcastManager = LocalBroadcastManager.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_start)

        triggerQuizCreation()
        val filter = IntentFilter()
        filter.addAction(DataManager.actionTopTracks)
        localBroadcastManager.registerReceiver(receiver, filter)
    }

    fun startPlaying(view: View){
        Log.i(TAG, "navigate start quiz")
        val intent = Intent(this, AuthSpotify::class.java)

        startActivity(intent)

    }

    override fun onDestroy() {
        localBroadcastManager.unregisterReceiver(receiver)
        super.onDestroy()
    }

    fun triggerQuizCreation(){
        if(DataManager.tracksAvailable){
            DataManager.createQuestions()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            triggerQuizCreation()
        }
    }
}
