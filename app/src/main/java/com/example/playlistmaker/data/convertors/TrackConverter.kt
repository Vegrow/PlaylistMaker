package com.example.playlistmaker.data.convertors

import com.example.playlistmaker.data.models.TrackDTO
import com.example.playlistmaker.domain.models.Track

interface TrackConverter {
    fun convert(track: TrackDTO): Track
}