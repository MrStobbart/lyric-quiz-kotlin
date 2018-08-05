package com.example.marekmeyer.lyricquiz_kotlin.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import com.example.marekmeyer.lyricquiz_kotlin.R
import com.example.marekmeyer.lyricquiz_kotlin.models.DataManager

class TopTracks : AppCompatActivity() {

    private val localBroadcastManager= LocalBroadcastManager.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_tracks)


        val filter = IntentFilter(DataManager.actionTopTracks)
        localBroadcastManager.registerReceiver(receiver, filter)

        processTracks()
    }

    fun processTracks(){
        if (DataManager.tracksAvailable){
            // TODO do somethings with the tracks here
        }
    }


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            processTracks()
        }
    }


}
