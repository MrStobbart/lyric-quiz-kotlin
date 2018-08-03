package com.example.marekmeyer.lyricquiz_kotlin.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.marekmeyer.lyricquiz_kotlin.R
import org.json.JSONArray
import org.json.JSONObject

class MainNavigation : AppCompatActivity() {

    private val TAG: String = "Main Navigation"
    private var spotifyAuthToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        spotifyAuthToken = intent.extras.getString("token")

        Log.e(TAG, "Token in Main activity: $spotifyAuthToken")
        setContentView(R.layout.activity_main_navigation)

        AndroidNetworking.get("https://api.spotify.com/v1/me/top/artists")
                .addHeaders("Authorization", "Bearer $spotifyAuthToken")
                .addQueryParameter("limit", "10")
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener{
                    override fun onResponse(response: JSONObject?) {
                        if(response != null){
                            Log.e(TAG, "Response: $response")
                        }
                    }

                    override fun onError(anError: ANError?) {
                        if(anError != null){
                            Log.e(TAG, "Error: $anError")
                        }

                    }

                })
    }

    fun navigateStartQuiz(view: View){
        Log.i(TAG, "navigate start quiz")
        val intent = Intent(this, QuizStart::class.java)
        startActivity(intent)
    }

    fun navigateTopArtists(view: View){
        Log.i(TAG, "navigate top artists")

    }

    fun navigateTopTracks(view: View){
        Log.i(TAG, "navigate top tracks")

    }

    fun navigateSettings(view: View){
        Log.i(TAG, "navigate settings")

    }

    fun navigateLogout(view: View){
        Log.i(TAG, "navigate logout")

    }
}
