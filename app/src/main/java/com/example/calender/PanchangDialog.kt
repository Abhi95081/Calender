package com.example.calender

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.calender.model.Panchang
import java.time.LocalDate

@Composable
fun PanchangDialog(date: LocalDate, panchang: Panchang, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
        title = { Text("Panchang: $date") },
        text = {
            Column {
                Text("🌙 Tithi: ${panchang.tithi}")
                Text("⭐ Nakshatra: ${panchang.nakshatra}")
                Text("🌅 Sunrise: ${panchang.sunrise}")
                Text("🌇 Sunset: ${panchang.sunset}")
                Text("⛔ Rahukalam: ${panchang.rahukalam}")
                if (panchang.festivals.isNotEmpty()) {
                    Text("🎉 Festivals: ${panchang.festivals.joinToString()}")
                }
            }
        }
    )
}
