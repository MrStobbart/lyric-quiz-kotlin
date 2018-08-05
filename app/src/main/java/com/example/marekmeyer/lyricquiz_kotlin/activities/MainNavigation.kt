package com.example.marekmeyer.lyricquiz_kotlin.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.marekmeyer.lyricquiz_kotlin.R
import com.example.marekmeyer.lyricquiz_kotlin.models.DataManager

class MainNavigation : AppCompatActivity() {

    private val TAG: String = "Main Navigation"
    private var spotifyAuthToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DataManager.getTopArtists()
        DataManager.getTopTracks()


        Log.e(TAG, "Token in Main activity: $spotifyAuthToken")
        setContentView(R.layout.activity_main_navigation)

    }

    fun navigateStartQuiz(view: View){
        Log.i(TAG, "navigate start quiz")
        val intent = Intent(this, QuizStart::class.java)
        startActivity(intent)
    }

    fun navigateTopArtists(view: View){
        Log.i(TAG, "navigate top artists")
        val intent = Intent(this, TopArtists::class.java)
        startActivity(intent)
    }

    fun navigateTopTracks(view: View){
        Log.i(TAG, "navigate top tracks")
        val intent = Intent(this, TopTracks::class.java)
        startActivity(intent)
    }

    fun navigateSettings(view: View){
        Log.i(TAG, "navigate settings")

    }

    fun navigateLogout(view: View){
        Log.i(TAG, "navigate logout")

    }
}
