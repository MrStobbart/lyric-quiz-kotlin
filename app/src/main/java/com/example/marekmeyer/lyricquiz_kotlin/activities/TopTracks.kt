package com.example.marekmeyer.lyricquiz_kotlin.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.*
import android.util.Log
import com.example.marekmeyer.lyricquiz_kotlin.R
import com.example.marekmeyer.lyricquiz_kotlin.models.DataManager

class TopTracks : AppCompatActivity() {

    private val TAG = "Top Tracks"
    private val localBroadcastManager= LocalBroadcastManager.getInstance(this)
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TopTracksRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_tracks)


        val filter = IntentFilter(DataManager.actionTopTracks)
        localBroadcastManager.registerReceiver(receiver, filter)


        title = resources.getString(R.string.title_activity_top_tracks)
        recyclerView = findViewById(R.id.topTracksRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutCompat.VERTICAL))


        processTracks()
    }

    fun processTracks(){
        if (DataManager.tracksAvailable){
            adapter = TopTracksRecyclerViewAdapter(DataManager.topTracks)
            recyclerView.adapter = adapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        recyclerView.adapter = null
    }


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            processTracks()
        }
    }


}
