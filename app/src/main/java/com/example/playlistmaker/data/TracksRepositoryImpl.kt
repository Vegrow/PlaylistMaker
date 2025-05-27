package com.example.playlistmaker.data

import com.example.playlistmaker.data.convertors.TrackConverter
import com.example.playlistmaker.data.models.TrackResponse
import com.example.playlistmaker.data.models.TracksSearchRequest
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.exception.ConnectionException
import com.example.playlistmaker.domain.models.Track

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val trackConverter: TrackConverter,
) : TracksRepository {

    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return if (response.resultCode == 200) {
            (response as TrackResponse).results.map { trackConverter.convert(it) }
        } else {
            throw ConnectionException(response.resultCode)
        }
    }
}