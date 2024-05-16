package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.adapter.TrackAdapter
import com.example.playlistmaker.data.ITunesApi
import com.example.playlistmaker.data.TrackConverter
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.models.data.TrackResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {


    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val imdbService = retrofit.create(ITunesApi::class.java)
    private val trackConverter = TrackConverter()
    private val trackList = mutableListOf<Track>()
    private val trackAdapter = TrackAdapter(trackList)

    // Зачем нужна - не понятно, если можно напрямую достать текст из textEdit
    private var userSearchInput = ""
    private lateinit var inputEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var alertView: LinearLayout
    private lateinit var alertImage: ImageView
    private lateinit var alertTextView: TextView
    private lateinit var alertButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setUpViews()
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
            hideAlertView()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                userSearchInput = s.toString()
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrack()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun searchTrack() {
        if (inputEditText.text.isNotEmpty()) {
            imdbService.search(inputEditText.text.toString())
                .enqueue(object : Callback<TrackResponse> {
                    override fun onResponse(
                        call: Call<TrackResponse>,
                        response: Response<TrackResponse>
                    ) {
                        if (response.code() == 200) {
                            onSuccess(response.body())
                        } else {
                            showConnectionErrorAlert()
                        }
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        showConnectionErrorAlert()
                    }
                })
        }
    }

    private fun onSuccess(response: TrackResponse?) {
        if (response?.results?.isNotEmpty() == true) {
            response.results.mapTo(trackList) {
                trackConverter.convert(it)
            }
            hideAlertView()
            trackAdapter.notifyDataSetChanged()
            recyclerView.scrollToPosition(0)
        } else {
            showEmptyResultAlert()
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

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
        alertButton.visibility = if (showUpdateButton) View.VISIBLE else View.GONE
        alertView.visibility = View.VISIBLE
    }

    private companion object {
        const val SEARCH_USER_INPUT_KEY = "SEARCH_USER_INPUT"
    }
}