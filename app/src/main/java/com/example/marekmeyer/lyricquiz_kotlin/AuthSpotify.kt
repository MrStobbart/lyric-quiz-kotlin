package com.example.marekmeyer.lyricquiz_kotlin

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.app.LoaderManager.LoaderCallbacks
import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView

import android.Manifest.permission.READ_CONTACTS
import android.content.Intent
import android.view.KeyEvent
import android.webkit.WebChromeClient

import kotlinx.android.synthetic.main.activity_auth_spotify.*
import android.webkit.WebView
import android.webkit.WebViewClient
import java.net.URLEncoder
import java.util.*


/**
 * A login screen that offers login via email/password.
 */
class AuthSpotify : AppCompatActivity(){
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
   override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_auth_spotify)
        val webView = findViewById<WebView>(R.id.spotifyWebView)

        createSpotifyRequestUrl()

        webView.settings.javaScriptEnabled
        webView.webViewClient = CustomWebViewClient()
        webView.loadUrl("https://google.com/");
    }

    fun createSpotifyRequestUrl(): String{
        val propertiesFile = application.assets.open("config.properties")
                .bufferedReader()
        val properties = Properties()
        properties.load(propertiesFile)

        val scope = "user-read-private user-read-email, user-top-read"
        val redirectUrl = "lyricquiz://callback"

        // TODO test if this is working as intended with the url
        val requestIdentifier = UUID.randomUUID().toString()

        var url = "https://accounts.spotify.com/authorize"
        url += "?response_type=token"
        url += "&client_id=${properties.getProperty("spotify.clientId")}"
        url += "&scope=$scope"
        url += "&redirect_uri=$redirectUrl"
        url += "&state=$requestIdentifier"

        return  URLEncoder.encode(url, "UTF-8")
    }


}


