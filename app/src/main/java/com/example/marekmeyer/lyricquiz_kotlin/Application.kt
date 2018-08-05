package com.example.marekmeyer.lyricquiz_kotlin

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import com.androidnetworking.AndroidNetworking
import com.example.marekmeyer.lyricquiz_kotlin.activities.AuthInitial

class App : Application(){


    override fun onCreate(){
        super.onCreate()
        instance = this
        AndroidNetworking.initialize(applicationContext)
    }


    companion object {
        lateinit var instance: App
            private set
    }

}