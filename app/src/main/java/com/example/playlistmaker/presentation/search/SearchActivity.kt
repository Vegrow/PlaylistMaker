package com.example.playlistmaker.presentation.search

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.player.MediaPlayerActivity
import com.example.playlistmaker.presentation.search.adapter.TrackAdapter
import com.example.playlistmaker.presentation.utils.Debounce


class SearchActivity : AppCompatActivity() {

    private lateinit var tracksInteractor: TracksInteractor
    private val trackList = mutableListOf<Track>()
    private val searchHistoryTrackList = mutableListOf<Track>()
    private val trackAdapter = TrackAdapter(trackList, ::onClick)
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchTrack() }
    private lateinit var searchHistoryInteractor: SearchHistoryInteractor
    private var historyAdapter = TrackAdapter(searchHistoryTrackList, ::onClick)


    // Зачем нужна - не понятно, если можно напрямую достать текст из textEdit
    private var userSearchInput = ""
    private lateinit var inputEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var alertView: LinearLayout
    private lateinit var alertImage: ImageView
    private lateinit var alertTextView: TextView
    private lateinit var alertButton: Button
    private lateinit var historyView: View
    private lateinit var historyClearButton: Button
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        tracksInteractor = Creator.provideTracksInteractor()
        searchHistoryInteractor = Creator.provideSearchHistoryInteractor(context = this)
        searchHistoryInteractor.getHistory(::onHistoryRequestResult)
        setUpViews()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(searchRunnable)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_USER_INPUT_KEY, userSearchInput)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputEditText.setText(savedInstanceState.getString(SEARCH_USER_INPUT_KEY, ""))
    }

    private fun setUpViews() {
        setUpToolbar()
        setUpSearchBar()
        setUpRecycler()
        setUpAlertView()
        setUpSearchHistory()
        progressBar = findViewById(R.id.progress_bar)
    }

    private fun setUpSearchHistory() {
        historyView = findViewById(R.id.history_view)
        historyClearButton = findViewById(R.id.history_clear_button)
        historyClearButton.setOnClickListener {
            searchHistoryTrackList.clear()
            searchHistoryInteractor.clearHistoryList()
            historyView.visibility = GONE
        }
        historyRecyclerView = findViewById(R.id.history_recycle_view)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter
    }

    private fun setUpAlertView() {
        alertView = findViewById(R.id.alert_view)
        alertImage = findViewById(R.id.alert_image)
        alertTextView = findViewById(R.id.alert_text_view)
        alertButton = findViewById<Button?>(R.id.alert_button)
        alertButton.setOnClickListener {
            searchTrack()
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(findViewById(R.id.topAppBar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setUpSearchBar() {
        inputEditText = findViewById(R.id.edit_text_search)
        val clearButton = findViewById<ImageView>(R.id.image_view_clear)
        clearButton.visibility = clearButtonVisibility(inputEditText.text)
        clearButton.setOnClickListener {
            inputEditText.setText("")
            hideKeyboard()
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            hideAlertView()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                recyclerView.visibility = GONE
                updateHistoryViewVisibility()
            }

            override fun afterTextChanged(s: Editable?) {
                userSearchInput = s.toString()
                searchDebounce()
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handler.removeCallbacks(searchRunnable)
                searchTrack()
                return@setOnEditorActionListener true
            }
            false
        }
        inputEditText.setOnFocusChangeListener { _, _ -> updateHistoryViewVisibility() }
    }

    private fun searchTrack() {
        if (inputEditText.text.isNotEmpty()) {
            showLoader()

            tracksInteractor.searchTracks(
                expression = inputEditText.text.toString(),
                onSuccess = {
                    runOnUiThread {
                        onSuccess(it)
                        progressBar.visibility = GONE
                    }
                },
                onFailure = {
                    runOnUiThread {
                        showConnectionErrorAlert()
                        progressBar.visibility = GONE
                    }
                }
            )
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun onSuccess(foundTracks: List<Track>?) {
        if (foundTracks?.isNotEmpty() == true) {
            trackList.clear()
            trackList.addAll(foundTracks)
            hideAlertView()
            trackAdapter.notifyDataSetChanged()
            recyclerView.visibility = VISIBLE
            recyclerView.scrollToPosition(0)
        } else {
            showEmptyResultAlert()
        }
    }

    private fun clearButtonVisibility(s: CharSequence?) = if (s.isNullOrEmpty()) GONE else VISIBLE

    private fun hideKeyboard() {
        this.currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun setUpRecycler() {
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = trackAdapter
    }

    private fun hideAlertView() {
        alertView.visibility = View.GONE
    }

    private fun showConnectionErrorAlert() {
        showAlertView(
            imageRes = R.drawable.ill_120_connection_error,
            textRes = R.string.search_connection_error,
            showUpdateButton = true
        )
    }

    private fun showEmptyResultAlert() {
        showAlertView(
            imageRes = R.drawable.ill_120_no_tracks,
            textRes = R.string.search_result_empty,
            showUpdateButton = false
        )
    }

    private fun showAlertView(
        @DrawableRes imageRes: Int,
        @StringRes textRes: Int,
        showUpdateButton: Boolean
    ) {
        trackList.clear()
        trackAdapter.notifyDataSetChanged()
        alertImage.setImageResource(imageRes)
        alertTextView.text = getString(textRes)
        alertButton.visibility = if (showUpdateButton) VISIBLE else GONE
        alertView.visibility = VISIBLE
    }

    private fun updateHistoryViewVisibility() {
        historyView.visibility = if (
            inputEditText.hasFocus()
            && inputEditText.text.isEmpty()
            && searchHistoryTrackList.isNotEmpty()
        ) VISIBLE else GONE
    }

    private fun onClick(track: Track) {
        if (Debounce.isClickAllowed()) {
            searchHistoryTrackList.add(track)
            searchHistoryInteractor.addTrack(track)
            historyAdapter.notifyDataSetChanged()
            startActivity(MediaPlayerActivity.newIntent(this, track))
        }
    }

    private fun showLoader() {
        progressBar.visibility = VISIBLE
        alertView.visibility = GONE
        historyView.visibility = GONE
        recyclerView.visibility = GONE
    }

    private fun onHistoryRequestResult(tracks: List<Track>) {
        searchHistoryTrackList.clear()
        searchHistoryTrackList.addAll(tracks)
        historyAdapter.notifyDataSetChanged()
    }

    private companion object {
        const val SEARCH_USER_INPUT_KEY = "SEARCH_USER_INPUT"
        const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}