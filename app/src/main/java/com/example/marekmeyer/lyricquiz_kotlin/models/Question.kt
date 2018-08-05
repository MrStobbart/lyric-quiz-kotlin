package com.example.marekmeyer.lyricquiz_kotlin.models

data class Question (
        var choices: List<String>,
        var lyrics: String,
        var trackName: String
)