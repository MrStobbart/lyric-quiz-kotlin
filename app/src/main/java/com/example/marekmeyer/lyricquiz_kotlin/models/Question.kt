package com.example.marekmeyer.lyricquiz_kotlin.models

data class Question (
        val choices: List<String>,
        val lyrics: String,
        val trackName: String
)