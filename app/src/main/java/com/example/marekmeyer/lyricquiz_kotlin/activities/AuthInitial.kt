package com.example.marekmeyer.lyricquiz_kotlin.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.marekmeyer.lyricquiz_kotlin.R

class AuthInitial : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth_initial)

    }

    fun startSpotifyAuth(view: View){
        val intent = Intent(this, AuthSpotify::class.java)
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        startActivity(intent)

    }

}
