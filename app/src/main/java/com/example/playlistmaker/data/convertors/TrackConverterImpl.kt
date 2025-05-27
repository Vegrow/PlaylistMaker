package com.example.playlistmaker.data.convertors

import com.example.playlistmaker.data.models.TrackDTO
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackConverterImpl : TrackConverter {

    private val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())

    override fun convert(track: TrackDTO) = Track(
        trackId = track.trackId,
        trackName = track.trackName,
        artistName = track.artistName,
        trackTime = formatter.format(track.trackTimeMillis),
        artworkUrl100 = track.artworkUrl100,
        collectionName = track.collectionName,
        releaseDate = track.releaseDate,
        primaryGenreName = track.primaryGenreName,
        country = track.country,
        previewUrl = track.previewUrl,
    )
}