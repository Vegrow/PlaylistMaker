package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
) : SearchHistoryRepository {

    private val historyList = ArrayList<Track>(getHistoryFromPreferences())

    override fun getHistory(): List<Track> = historyList.toList()

    override fun addTrack(track: Track) {
        val index = historyList.indexOfFirst { it.trackId == track.trackId }
        if (index != -1) {
            moveTrackToTop(index)
        } else {
            if (historyList.size >= HISTORY_MAX_CAPACITY) {
                historyList.removeAt(historyList.lastIndex)
            }
            historyList.add(0, track)
        }
        saveHistory()
    }

    override fun clearHistoryList() {
        historyList.clear()
        saveHistory()
    }

    override fun saveHistory() {
        val json = gson.toJson(historyList)
        sharedPreferences.edit()
            .putString(TRACK_HISTORY_KEY, json)
            .apply()
    }

    private fun getHistoryFromPreferences(): List<Track> {
        val json = sharedPreferences.getString(TRACK_HISTORY_KEY, null) ?: return emptyList()
        return gson.fromJson(json, Array<Track>::class.java).toList()
    }

    private fun moveTrackToTop(index: Int) {
        val trackToMove = historyList[index]
        historyList.removeAt(index)
        historyList.add(0, trackToMove)
    }

    private companion object {
        const val TRACK_HISTORY_KEY = "track_history_key"
        const val HISTORY_MAX_CAPACITY = 10
    }
}