package com.example.playlistmaker.domain.exception

class ConnectionException(val badResultCode: Int) : Exception("Result code = $badResultCode")