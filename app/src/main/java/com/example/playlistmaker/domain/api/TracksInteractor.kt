package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TracksInteractor {
    fun searchTracks(
        expression: String,
        onSuccess: (List<Track>) -> Unit,
        onFailure: (Throwable) -> Unit
    )
}