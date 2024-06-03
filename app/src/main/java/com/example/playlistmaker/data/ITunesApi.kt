package com.example.playlistmaker.data

import com.example.playlistmaker.models.data.TrackResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {

    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<TrackResponse>
}