package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {
    fun getHistory(onResult: (List<Track>) -> Unit)
    fun addTrack(track: Track)
    fun clearHistoryList()
    fun saveHistory()
}