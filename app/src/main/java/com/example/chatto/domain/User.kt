package com.example.chatto.domain

data class User(
    val userID: String,
    val userName: String,
    val userDeviceName: String,
    val user: User
)
