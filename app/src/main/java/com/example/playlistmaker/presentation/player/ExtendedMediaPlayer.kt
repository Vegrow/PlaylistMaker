package com.example.playlistmaker.presentation.player

import android.media.MediaPlayer

class ExtendedMediaPlayer : MediaPlayer() {

    private var playerState = STATE_DEFAULT
    private var onStartListener: OnStartListener? = null
    private var onPauseListener: OnPauseListener? = null

    fun preparePlayer(url: String) {
        setDataSource(url)
        prepareAsync()
    }

    fun onPlaybackClick() {
        when (playerState) {
            STATE_PLAYING -> {
                pause()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                start()
            }
        }
    }

    fun setOnStartListener(onStartListener: OnStartListener) {
        this.onStartListener = onStartListener
    }

    fun setOnPauseListener(onPauseListener: OnPauseListener) {
        this.onPauseListener = onPauseListener
    }

    override fun start() {
        super.start()
        playerState = STATE_PLAYING
        onStartListener?.onStart()
    }

    override fun pause() {
        super.pause()
        playerState = STATE_PAUSED
        onPauseListener?.onPause()
    }

    override fun setOnPreparedListener(listener: OnPreparedListener?) {
        super.setOnPreparedListener {
            listener?.onPrepared(it)
            playerState = STATE_PREPARED
        }
    }

    override fun setOnCompletionListener(listener: OnCompletionListener?) {
        super.setOnCompletionListener {
            listener?.onCompletion(it)
            playerState = STATE_PREPARED
        }
    }

    private companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
    }

    fun interface OnStartListener {
        fun onStart()
    }

    fun interface OnPauseListener {
        fun onPause()
    }
}