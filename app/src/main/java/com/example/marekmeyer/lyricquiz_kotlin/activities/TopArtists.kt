package com.example.marekmeyer.lyricquiz_kotlin.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.example.marekmeyer.lyricquiz_kotlin.R
import com.example.marekmeyer.lyricquiz_kotlin.models.DataManager

class TopArtists : AppCompatActivity() {

    private val TAG = "Top Artists"
    private val localBroadcastManager= LocalBroadcastManager.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_artists)

        val filter = IntentFilter(DataManager.actionTopTracks)
        localBroadcastManager.registerReceiver(receiver, filter)

        processArtists()
    }

    fun processArtists(){
        if (DataManager.artistsAvailable){
            Log.e(TAG, "Top Artists ${DataManager.topArtists}")
        }
    }


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            processArtists()
        }
    }
}
