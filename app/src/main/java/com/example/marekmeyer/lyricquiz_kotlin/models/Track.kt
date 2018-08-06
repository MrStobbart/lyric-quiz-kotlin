package com.example.marekmeyer.lyricquiz_kotlin.models

data class Track(
        val name: String,
        val artist: String
){
    lateinit var lyrics: String

    override fun toString(): String {
        return "$name ($artist)"
    }
}