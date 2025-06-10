package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.models.Response
import com.example.playlistmaker.data.models.TracksSearchRequest

class RetrofitNetworkClient(private val iTunesService: ITunesApi) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        return if (dto is TracksSearchRequest) {
            val resp = iTunesService.search(dto.expression).execute()
            val body = resp.body() ?: Response()
            body.apply { resultCode = resp.code() }
        } else {
            Response().apply { resultCode = 400 }
        }
    }
}