package com.example.chatto.domain

data class Device(
    val deviceID: String,
    val deviceName: String,
    val isConnected: Boolean = false
)
