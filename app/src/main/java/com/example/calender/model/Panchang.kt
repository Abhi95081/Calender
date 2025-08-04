package com.example.calender.model

data class Panchang(
    val tithi: String,
    val nakshatra: String,
    val sunrise: String,
    val sunset: String,
    val rahukalam: String,
    val festivals: List<String>
)