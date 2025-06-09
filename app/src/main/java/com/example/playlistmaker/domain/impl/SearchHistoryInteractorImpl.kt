package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl(
    private val searchHistoryRepository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override fun getHistory(onResult: (List<Track>) -> Unit) =
        onResult.invoke(searchHistoryRepository.getHistory())

    override fun addTrack(track: Track) = searchHistoryRepository.addTrack(track)

    override fun clearHistoryList() = searchHistoryRepository.clearHistoryList()

    override fun saveHistory() = searchHistoryRepository.saveHistory()
}