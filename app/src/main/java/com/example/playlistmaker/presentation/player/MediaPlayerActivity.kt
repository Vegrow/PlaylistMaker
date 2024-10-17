package com.example.playlistmaker.presentation.player

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.presentation.utils.dpToPx

class MediaPlayerActivity : AppCompatActivity() {

    private lateinit var track: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_player)
        track = intent.getParcelableExtra(TRACK_KEY) ?: error("Без трека сюда не ходи")
        setUpViews()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setUpToolbar() {
        setSupportActionBar(findViewById(R.id.topAppBar))
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setUpViews() {
        setUpToolbar()
        setUpTrackAttributes()
    }

    private fun setUpTrackAttributes() {
        setUpTrackImage()
        findViewById<TextView>(R.id.text_view_track_name).text = track.trackName
        findViewById<TextView>(R.id.text_view_track_artist).text = track.artistName
        findViewById<TextView>(R.id.text_view_track_duration_value).text =
            track.trackTime.trimStart('0')
        setUpCollectionName()
        setUpReleaseDate()
        findViewById<TextView>(R.id.text_view_track_genre_value).text = track.primaryGenreName
        findViewById<TextView>(R.id.text_view_track_country_value).text = track.country
    }

    private fun setUpTrackImage() {
        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.track_item_image_placeholder)
            .fitCenter()
            .transform(RoundedCorners(8.dpToPx(baseContext)))
            .into(findViewById(R.id.image_view_artwork))
    }

    private fun setUpCollectionName() {
        if (track.collectionName.isNullOrBlank()) {
            findViewById<TextView>(R.id.text_view_track_album_value).visibility = View.GONE
            findViewById<TextView>(R.id.text_view_track_album).visibility = View.GONE
        } else {
            findViewById<TextView>(R.id.text_view_track_album_value).text = track.collectionName
        }
    }

    private fun setUpReleaseDate() {
        if (track.releaseDate.isNullOrBlank()) {
            findViewById<TextView>(R.id.text_view_track_year_value).visibility = View.GONE
            findViewById<TextView>(R.id.text_view_track_year).visibility = View.GONE
        } else {
            findViewById<TextView>(R.id.text_view_track_year_value).text =
                track.releaseDate?.substring(0, 4)
        }
    }

    companion object {

        private const val TRACK_KEY = "track_key"

        fun newIntent(context: Context, track: Track) =
            Intent(context, MediaPlayerActivity::class.java)
                .putExtra(TRACK_KEY, track)
    }
}