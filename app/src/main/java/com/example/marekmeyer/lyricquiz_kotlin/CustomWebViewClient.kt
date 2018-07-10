package com.example.marekmeyer.lyricquiz_kotlin

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient

class CustomWebViewClient : WebViewClient() {

//    private val TAG = "CustomWebViewClient"
//
//    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
//        super.onPageStarted(view, url, favicon)
//        Log.d(TAG, "The webView with the following url $url has started loading")
//    }
//
//    override fun onPageFinished(view: WebView, url: String) {
//        super.onPageFinished(view, url)
//        Log.d(TAG, "The webView with the following url $url has finished loading")
//    }
//
//    override fun onReceivedError(view: WebView, errorCode: Int,
//                                 description: String, failingUrl: String) {
//        super.onReceivedError(view, errorCode, description, failingUrl)
//        Log.d(TAG, "The webView with the following url " + failingUrl +
//                " failed with the following errorCode " +
//                "" + errorCode)
//
//    }
//
//    @TargetApi(Build.VERSION_CODES.M)
//    override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
//        super.onReceivedError(view, request, error)
//        Log.d(TAG, "The webView with the following url " + request.url.toString() +
//                " failed with the following errorCode " +
//                "" + error.description)
//    }
//
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    override fun onReceivedHttpError(
//            view: WebView, request: WebResourceRequest, errorResponse: WebResourceResponse) {
//        super.onReceivedHttpError(view, request, errorResponse)
//        Log.d(TAG, "The webView with the following url " + request.url.toString() +
//                " failed with the following errorCode " +
//                "" + errorResponse.statusCode)
//    }

    @SuppressWarnings("deprecation")
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        view.loadUrl(url)
        return true
    }
}