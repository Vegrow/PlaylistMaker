package com.example.playlistmaker.data

import com.example.playlistmaker.models.Track
import com.example.playlistmaker.models.data.TrackDTO
import java.text.SimpleDateFormat
import java.util.Locale

class TrackConverter {

    private val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())

    fun convert(track: TrackDTO) = Track(
        trackId = track.trackId,
        trackName = track.artistName,
        artistName = track.artistName,
        trackTime = formatter.format(track.trackTimeMillis),
        artworkUrl100 = track.artworkUrl100,
        collectionName = track.collectionName,
        releaseDate = track.releaseDate,
        primaryGenreName = track.primaryGenreName,
        country = track.country,
    )
}