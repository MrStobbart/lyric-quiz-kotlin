package com.example.marekmeyer.lyricquiz_kotlin.activities

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Build
import android.os.Bundle

import android.graphics.Bitmap
import android.net.http.SslError
import android.util.Log
import android.webkit.*
import com.example.marekmeyer.lyricquiz_kotlin.R
import com.example.marekmeyer.lyricquiz_kotlin.models.DataManager
import java.net.URLDecoder

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


        val spotifyRequestUrl: String = createSpotifyRequestUrl()


        webView.settings.javaScriptEnabled = true

        val customWebViewClient = CustomWebViewClient()
        customWebViewClient.updateContext(this)
        webView.webViewClient = customWebViewClient
        webView.webChromeClient = CustomWebChromeClient()

        webView.loadUrl(spotifyRequestUrl)
    }

    private fun createSpotifyRequestUrl(): String{
        val propertiesFile = application.assets.open("config.properties")
                .bufferedReader()
        val properties = Properties()
        properties.load(propertiesFile)

        val scope = URLEncoder.encode("user-top-read", "UTF-8")
        val redirectUrl = URLEncoder.encode("https://lyricquiz.io/callback/", "UTF-8")
        val clientId = URLEncoder.encode(properties.getProperty("spotify.clientId"), "UTF-8")

        // TODO test if this is working as intended with the url
        val requestIdentifier = URLEncoder.encode(UUID.randomUUID().toString(), "UTF-8")

        var url = "https://accounts.spotify.com/authorize"
        url += "?response_type=token"
        url += "&client_id=$clientId"
        url += "&scope=$scope"
        url += "&redirect_uri=$redirectUrl"
        url += "&state=$requestIdentifier"
        url += "&show_dialog=true"

        return  url
    }

}

class CustomWebChromeClient : WebChromeClient(){
    private val TAG = "CustomWebViewClient"

    override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean{
        Log.e(TAG, consoleMessage.message())
        return true
    }


}

class CustomWebViewClient : WebViewClient() {

    private var context: Context? = null
    private val TAG = "CustomWebViewClient"

    fun updateContext(context: Context){
        this.context = context

    }

    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest ): Boolean {

        // false means webView should handle handle url and true means the new url should not be loaded
        Log.e(TAG, "url loading detected ${request.url}")
        if(request.url.toString().contains("access_token")) {
            val decodedUrl = URLDecoder.decode(request.url.toString(), "UTF-8")
            val token = decodedUrl.split("#access_token=")[1].split("&token_type=")[0]
            Log.e(TAG, "token split $token")
            DataManager.spotifyAuthToken = token
            Log.e(TAG, decodedUrl)

            val context = this.context
            if(context != null){
                val intent = Intent(context, MainNavigation::class.java)
                context.startActivity(intent)
            }


            return true
        }
        return false
    }


    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        Log.e(TAG, "url loading detected ${url}")
        return false
    }

    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
        Log.e("Spotify auth", "Hurra we got an ssl error")
        // Ignore SSL certificate errors
        handler.proceed()
    }

    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        Log.e(TAG, "The webView with the following url has started loading: $url")
    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        Log.e(TAG, "The webView with the following url has finished loading: $url")
    }

    override fun onReceivedError(view: WebView, errorCode: Int,
                                 description: String, failingUrl: String) {
        super.onReceivedError(view, errorCode, description, failingUrl)
        Log.e(TAG, "The webView with the following url " + failingUrl +
                " failed with the following errorCode " +
                "" + errorCode)

    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
        super.onReceivedError(view, request, error)
        Log.e(TAG, "The webView with the following url " + request.url.toString() +
                " failed with the following errorCode " +
                "" + error.description)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onReceivedHttpError(
            view: WebView, request: WebResourceRequest, errorResponse: WebResourceResponse) {
        super.onReceivedHttpError(view, request, errorResponse)
        Log.e(TAG, "The webView with the following url " + request.url.toString() +
                " failed with the following errorCode " +
                "" + errorResponse.statusCode)
    }


}


