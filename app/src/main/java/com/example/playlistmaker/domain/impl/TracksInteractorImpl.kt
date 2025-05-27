package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(
        expression: String,
        onSuccess: (List<Track>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        executor.execute {
            try {
                onSuccess.invoke(repository.searchTracks(expression))
            } catch (exception: Exception) {
                onFailure.invoke(exception)
            }
        }
    }
}