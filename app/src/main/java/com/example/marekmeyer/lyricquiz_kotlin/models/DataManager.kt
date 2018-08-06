package com.example.marekmeyer.lyricquiz_kotlin.models

import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import awaitStringResponse
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.example.marekmeyer.lyricquiz_kotlin.App
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.httpGet
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Whitelist
import java.net.URLEncoder
import java.util.*

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


    private val propertiesFile = App.instance.assets.open("config.properties")
            .bufferedReader()
    private val properties = Properties()

    init {
        properties.load(propertiesFile)
    }


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
            val artistsList = artists.map { Artist(it.string("name")!!) }
            Track(name, getArtistNames(artists), artistsList)

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


    fun createQuestions(){

        val shuffledTracks = topTracks.shuffled()
        val selectedTracks = shuffledTracks.slice(IntRange(0, 4))
        Log.e(TAG, "Number of selected tracks: ${selectedTracks.size}")



        val selectedLyrics = selectLyrics("\\n\\nNow we gonna do this like we used to \\n Taking it back to the roots, you know \\n \\n Repeat it in your mind \\n You better understand \\n We gonna start a fire \\n That wasn't supposed to end \\n We taking it back [x3] \\n Yeah, we taking it back \\n \\n We gonna keep it real \\n The rest down the drain \\n We run melodies \\n But you know we ain't playing \\n We taking it back [x3] \\n Yeah, we taking it back \\n \\n Yeah \\n We taking it back \\n Come on \\n \\n Repeat it in your mind \\n You better understand \\n We gonna start a fire \\n That wasn't supposed to end \\n We taking it back [x3] \\n Yeah, we taking it back \\n \\n We gonna keep it real \\n The rest down the drain \\n We run melodies \\n But you know we ain't playing \\n We taking it back [x3] \\n Come on \\n \\n We taking it back [x3] \\n Ha, yeah \\n We taking it back [x3] \\n Let's go \\n We taking it back [x3] \\n Ha, yeah \\n We ain't playing no games no more\n")

        Log.e(TAG, "Selected lyrics: $selectedLyrics")

        async(CommonPool) {

            Log.e(TAG, "Start coroutine")
            val lyricsUrl = searchTrackOnGenius("Takin' it back", "Headhunterz").await()
            Log.e(TAG, "Lyrics url: $lyricsUrl")
            if(lyricsUrl != null){
                val lyrics = scrapeLyrics(lyricsUrl).await()
                Log.e(TAG, "Lyrics: $lyrics")
                val extractedLyricsForQuestion = selectLyrics(lyrics)
                Log.e(TAG,"Extracted lyrics:\n$extractedLyricsForQuestion")
            }
            Log.e(TAG, "We need some error handling")

        }


    }

    private fun getLyricsRecursion(tracks: List<Track>, index: Int){

    }

    private fun getLyrics(tracks: List<Track>, index: Int){
        val artist = tracks[index].artists[0].name
        val track = tracks[index].name

        async {

            val lyricsUrl = searchTrackOnGenius(track, artist).await()
            if(lyricsUrl != null){
                val lyrics = scrapeLyrics(lyricsUrl).await()
                val extractedLyricsForQuestion = selectLyrics(lyrics)
                Log.e(TAG,"Extracted lyrics:\n$extractedLyricsForQuestion")
            }
            Log.e(TAG, "We need some error handling")

        }
    }

    private fun searchTrackOnGenius(track: String, artist: String): Deferred<String?>{

        Log.e(TAG, "in search on genius")
        val trackAndArtist = URLEncoder.encode("$track, $artist", "UTF-8")
        val baseUrl = properties.getProperty("genius.baseUrl")
        val url = "$baseUrl/search?q=$trackAndArtist"

        val accessToken = URLEncoder.encode(properties.getProperty("genius.accessToken"), "UTF-8")

        return async (CommonPool){
            val noLyricsFound = null


            val headers = mapOf("Authorization" to "Bearer $accessToken")
            val (request, response, result) = url.httpGet().header(headers).awaitStringResponse()

            Log.e(TAG, "request: $request")
            Log.e(TAG, "response: $response")
            val(data, error) = result
            if(error != null){
                Log.e(TAG, "Error: $error")
                return@async noLyricsFound
            }

            if(data == null){
                Log.e(TAG, "No data")
                return@async noLyricsFound
            }
            Log.e(TAG, "data: $data")

            val parser = Parser()
            val json: JsonObject = parser.parse(StringBuilder(data)) as JsonObject

            val foundTracks = json.obj("response")
                    ?.array<JsonObject>("hits")
                    ?: return@async noLyricsFound

            Log.e(TAG, "found tracks: $foundTracks")
            val noTrackFound = foundTracks.size == 0

            if(noTrackFound){
                Log.e(TAG, "No track found $track $artist")
                return@async noLyricsFound
            }


            val fullFoundTitle = foundTracks[0]
                    .obj("result")
                    ?.string("full_title")
                    ?.toLowerCase()
                    ?: noLyricsFound

            if(fullFoundTitle == noLyricsFound){
                Log.e(TAG, "No title found")
                return@async noLyricsFound
            }

            val foundTitleMatches = fullFoundTitle.contains(track.toLowerCase())
            val foundArtistMatches = fullFoundTitle.contains(artist.toLowerCase())

            Log.e(TAG, "if statement $foundTitleMatches\n $foundArtistMatches\n $fullFoundTitle")
            if(foundTitleMatches && foundArtistMatches){
                Log.e(TAG, "Daatatata, $data")
                val lyricsUrl = foundTracks[0]
                        .obj("result")
                        ?.string("url")
                        ?: noLyricsFound

                if(lyricsUrl == noLyricsFound){
                    Log.e(TAG, "No lyrics url found")
                    return@async noLyricsFound
                }

                return@async lyricsUrl
            }

            Log.e(TAG, "Track did not match")
            return@async noLyricsFound

        }

    }

    private fun scrapeLyrics(geniusLyricsUrl: String): Deferred<String>{

        return async {
            val document = Jsoup.connect(geniusLyricsUrl).get()
            document.outputSettings(Document.OutputSettings().prettyPrint(false))
            val lyricsElement = document.select(".lyrics").first()
            document.select("br").append("\\n")
            document.select("p").prepend("\\n\\n")
            val lyrics = lyricsElement.text().replace("\\\\n", "\n")
            return@async Jsoup.clean(lyrics, "", Whitelist.none(), Document.OutputSettings().prettyPrint(false))
        }
    }

    private fun selectLyrics(lyrics: String): String {

        val numberOfLines = 2

        val lines = lyrics.split("\\n").map { it.trim() }

        val lyricLinesToIgnore = listOf(
                "Refrain]",
                "[Bridge]",
                "Chorus]",
                "[Verse",
                "[Drop]"
        )

        val trimmedLines = lines.filter { line ->
            // Remove empty lines
            if (line == "") {
                return@filter false
            }

            // Remove lyric lines to ignore
            if (lyricLinesToIgnore.any { lineToIgnore -> line.contains(lineToIgnore) }) {
                return@filter false
            }
            return@filter true
        }


        val selectedLineIndex = (0..(trimmedLines.size - numberOfLines)).shuffled().last()


        val selectedLines = trimmedLines.foldIndexed(trimmedLines[selectedLineIndex]) { currentIndex, selectedLines, currentLine ->
            if(currentIndex > selectedLineIndex && currentIndex < selectedLineIndex + numberOfLines){
                return@foldIndexed "$selectedLines\n$currentLine"
            }
            return@foldIndexed selectedLines
        }

        Log.e(TAG, "Selected lines in function $selectedLines")
        return selectedLines

    }
}