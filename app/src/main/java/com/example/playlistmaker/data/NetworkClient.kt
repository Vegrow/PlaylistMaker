package com.example.playlistmaker.data

import com.example.playlistmaker.data.models.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response
}