package com.example.marekmeyer.lyricquiz_kotlin.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.marekmeyer.lyricquiz_kotlin.R

class MainNavigation : AppCompatActivity() {

    private val TAG: String = "Main Navigation"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val token = intent.extras.getString("token")
        Log.e(TAG, "Token in Main activity: $token")
        setContentView(R.layout.activity_main_navigation)
    }

    fun navigateStartQuiz(view: View){
        Log.i(TAG, "navigate start quiz")
        val intent = Intent(this, QuizStart::class.java)
        startActivity(intent)
    }

    fun navigateTopArtists(view: View){
        Log.i(TAG, "navigate top artists")

    }

    fun navigateTopTracks(view: View){
        Log.i(TAG, "navigate top tracks")

    }

    fun navigateSettings(view: View){
        Log.i(TAG, "navigate settings")

    }

    fun navigateLogout(view: View){
        Log.i(TAG, "navigate logout")

    }
}
