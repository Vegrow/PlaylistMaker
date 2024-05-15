package com.example.playlistmaker.models.data

data class TrackDTO(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
)