package com.example.marekmeyer.lyricquiz_kotlin

import android.app.Application
import android.util.Log
import com.androidnetworking.AndroidNetworking

class App : Application(){

    override fun onCreate(){
        super.onCreate()
        AndroidNetworking.initialize(applicationContext)
    }

}