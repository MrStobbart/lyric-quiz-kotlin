package com.example.marekmeyer.lyricquiz_kotlin.models

import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.example.marekmeyer.lyricquiz_kotlin.App
import org.json.JSONObject

object DataManager{

    const val actionTopTracks = "ACTION_TOP_TRACKS"
    const val actionTopArtists = "ACTION_TOP_ARTISTS"

    private val TAG = "Data Manager"
    private val localBroadcastManager = LocalBroadcastManager.getInstance(App.instance.applicationContext)
    var spotifyAuthToken = ""

    lateinit var topTracks: List<Track>
    var tracksAvailable = false

    lateinit var topArtists: List<Artist>
    var artistsAvailable = false

    lateinit var quiz: Quiz



    fun getTopTracks(){

        if(spotifyAuthToken == ""){
            Log.e(TAG, "Spotify auth token missing!")
            return
        }

        AndroidNetworking.get("https://api.spotify.com/v1/me/top/tracks")
                .addHeaders("Authorization", "Bearer $spotifyAuthToken")
                .addQueryParameter("limit", "20")
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject?) {
                        if(response != null){
                           extractTopTracksFromJsonResponse(response)
                        }
                    }

                    override fun onError(anError: ANError?) {
                        if(anError != null){
                            Log.e(TAG, "Error: $anError")
                        }

                    }

                })
    }

    fun getTopArtists(){

        if(spotifyAuthToken == ""){
            Log.e(TAG, "Spotify auth token missing!")
            return
        }

        AndroidNetworking.get("https://api.spotify.com/v1/me/top/artists")
                .addHeaders("Authorization", "Bearer $spotifyAuthToken")
                .addQueryParameter("limit", "20")
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject?) {
                        if(response != null){
                            extractTopArtistsFromJsonResponse(response)
                        }
                    }

                    override fun onError(anError: ANError?) {
                        if(anError != null){
                            Log.e(TAG, "Error: $anError")
                        }

                    }

                })

    }

    private fun extractTopTracksFromJsonResponse(response: JSONObject){
        Log.e(TAG, "Response: $response")
        val parser = Parser()
        val json: JsonObject = parser.parse(StringBuilder(response.toString())) as JsonObject

        val nullableTopArtists: List<Track>? = json.array<JsonObject>("items")?.map {
            val name = it.string("name")!!
            val artists = it.array<JsonObject>("artists")!!
            Track(name, getArtistNames(artists))

        }

        if(nullableTopArtists != null) {
            topTracks = nullableTopArtists
            tracksAvailable = true
            val intent = Intent(actionTopTracks)
            localBroadcastManager.sendBroadcast(intent)
        }

    }

    private fun extractTopArtistsFromJsonResponse(response: JSONObject){
        Log.e(TAG, "Response: $response")
        val parser = Parser()
        val json: JsonObject = parser.parse(StringBuilder(response.toString())) as JsonObject

        val nullableTopArtists: List<Artist>? = json.array<JsonObject>("items")?.map {
            Artist(it.string("name")!!)
        }

        if(nullableTopArtists != null) {
            topArtists = nullableTopArtists
            artistsAvailable = true
            val intent = Intent(actionTopArtists)
            localBroadcastManager.sendBroadcast(intent)
        }

    }

    private fun getArtistNames(artists: JsonArray<JsonObject>): String{
        var artistsString = ""

        for (i in artists.indices){
            val artistName = artists[i].string("name")

            if(artistName != null){
                if(i > 1 && artists.size >= 4) {
                    return "$artistsString and others"
                }
                if(i == 0){
                    artistsString = artistName
                } else if(i == 1 && artists.size >= 3){
                    artistsString = "$artistsString, $artistName"
                } else if(artists.size >= 2){
                    artistsString = "$artistsString & $artistName"
                }
            }
        }
        return artistsString

    }
}