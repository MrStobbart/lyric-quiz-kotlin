package com.example.marekmeyer.lyricquiz_kotlin.models

data class Track(
        val name: String,
        val artistString: String,
        val artists: List<Artist>
){
    lateinit var lyrics: String

    override fun toString(): String {
        return "$name ($artistString)"
    }
}