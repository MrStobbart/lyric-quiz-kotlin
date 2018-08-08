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
import kotlinx.coroutines.experimental.*
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Whitelist
import java.net.URLEncoder
import java.util.*

object DataManager{

    const val actionTopTracks = "ACTION_TOP_TRACKS"
    const val actionTopArtists = "ACTION_TOP_ARTISTS"
    const val actionQuiz = "ACTION_QUIZ"

    private val TAG = "Data Manager"
    private val localBroadcastManager = LocalBroadcastManager.getInstance(App.instance.applicationContext)
    var spotifyAuthToken = ""

    lateinit var topTracks: List<Track>
    var tracksAvailable = false

    lateinit var topArtists: List<Artist>
    var artistsAvailable = false

    lateinit var quiz: Quiz
    var quizAvailable = false



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
                .addQueryParameter("limit", "50")
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
                .addQueryParameter("limit", "50")
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

            // Also trigger question creation when tracks are available
            createQuestions()
            val intent = Intent(actionTopTracks)
            localBroadcastManager.sendBroadcast(intent)
        }

    }

    private fun extractTopArtistsFromJsonResponse(response: JSONObject){
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

        val numberOfTracksToSelect = 5
        val numberOfLyricLinesInQuestion = 2


        val shuffledTracks = topTracks.shuffled()
        val selectedTracks = shuffledTracks.slice(IntRange(0, numberOfTracksToSelect - 1))


        val deferredTrackListWithLyrics = selectedTracks.mapIndexed { index, track ->
            getLyricsRecursion(shuffledTracks, index)
        }

        launch {

            val tracksWithLyrics = deferredTrackListWithLyrics.map { deferredTrack ->
                deferredTrack.await()
            }

            val tracksWithLyricsNotNull = tracksWithLyrics.filterNotNull()

            val questions = tracksWithLyricsNotNull.map { track ->

                val choices = topTracks.shuffled().slice((0..3))
                val choiceNames= choices.map { it.name }.toMutableList()

                if(!choiceNames.any { choice -> choice.contains(track.name) }){
                    choiceNames[0] = track.name
                }

                val shuffledChoices = choiceNames.shuffled()

                return@map Question(shuffledChoices, track.lyrics, track.name)
            }

            quiz = Quiz(questions)
            quizAvailable = true
            val intent = Intent(actionQuiz)
            localBroadcastManager.sendBroadcast(intent)

        }



    }

    private fun getLyricsRecursion(tracks: List<Track>, index: Int): Deferred<Track?>{

        return async {
            val track = getLyrics(tracks, index).await()

            if(track != null){
                return@async track
            }

            val nextIndexToTry = index + 5
            if(nextIndexToTry >= tracks.size){
                return@async null
            }

            return@async getLyricsRecursion(tracks, nextIndexToTry).await()


        }
    }

    private fun getLyrics(tracks: List<Track>, index: Int): Deferred<Track?>{

        val artist = tracks[index].artists[0]
        val track = tracks[index]

        return async {

            val lyricsUrl = searchTrackOnGenius(track.name, artist.name).await()

            if(lyricsUrl != null){

                val lyrics = scrapeLyrics(lyricsUrl).await()
                val extractedLyricsForQuestion = selectLyrics(lyrics)
                track.lyrics = extractedLyricsForQuestion
                return@async track

            }

            return@async null

        }
    }

    private fun searchTrackOnGenius(track: String, artist: String): Deferred<String?>{

        val trackAndArtist = URLEncoder.encode("$track, $artist", "UTF-8")
        val baseUrl = properties.getProperty("genius.baseUrl")
        val url = "$baseUrl/search?q=$trackAndArtist"

        val accessToken = URLEncoder.encode(properties.getProperty("genius.accessToken"), "UTF-8")

        return async (CommonPool){
            val noLyricsFound = null


            val headers = mapOf("Authorization" to "Bearer $accessToken")
            val (request, response, result) = url.httpGet().header(headers).awaitStringResponse()

            val(data, error) = result
            if(error != null){
                return@async noLyricsFound
            }

            if(data == null){
                return@async noLyricsFound
            }

            val parser = Parser()
            val json: JsonObject = parser.parse(StringBuilder(data)) as JsonObject

            val foundTracks = json.obj("response")
                    ?.array<JsonObject>("hits")
                    ?: return@async noLyricsFound

            val noTrackFound = foundTracks.size == 0

            if(noTrackFound){
                return@async noLyricsFound
            }


            val fullFoundTitle = foundTracks[0]
                    .obj("result")
                    ?.string("full_title")
                    ?.toLowerCase()
                    ?: noLyricsFound

            if(fullFoundTitle == noLyricsFound){
                return@async noLyricsFound
            }

            val foundTitleMatches = fullFoundTitle.contains(track.toLowerCase())
            val foundArtistMatches = fullFoundTitle.contains(artist.toLowerCase())

            if(foundTitleMatches && foundArtistMatches){
                val lyricsUrl = foundTracks[0]
                        .obj("result")
                        ?.string("url")
                        ?: noLyricsFound

                if(lyricsUrl == noLyricsFound){
                    return@async noLyricsFound
                }

                return@async lyricsUrl
            }

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

        return selectedLines

    }
}