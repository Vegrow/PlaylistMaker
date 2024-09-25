package com.example.playlistmaker.presentation.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.presentation.utils.dpToPx

class TrackAdapter(
    private val tracks: List<Track>,
    private val onClick: (Track) -> Unit,
) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(parent.inflate(R.layout.track_item), onClick)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

}

class TrackViewHolder(
    itemView: View,
    private val onClick: (Track) -> Unit,
) : RecyclerView.ViewHolder(itemView) {

    private val trackName: TextView = itemView.findViewById(R.id.text_view_track_name)
    private val artistName: TextView = itemView.findViewById(R.id.text_view_track_artist)
    private val trackTime: TextView = itemView.findViewById(R.id.text_view_track_time)
    private val artworkIcon: ImageView = itemView.findViewById(R.id.track_icon)

    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = model.trackTime
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.track_item_image_placeholder)
            .fitCenter()
            .transform(RoundedCorners(2.dpToPx(itemView.context)))
            .into(artworkIcon)
        itemView.setOnClickListener { onClick.invoke(model) }
    }

}

private fun ViewGroup.inflate(@LayoutRes resource: Int): View {
    return LayoutInflater.from(context).inflate(resource, this, false)
}