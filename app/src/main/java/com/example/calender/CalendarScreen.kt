package com.example.calender

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calender.data.PanchangProvider
import com.example.calender.model.Panchang
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen() {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val today = remember { LocalDate.now() }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        // Header with month and navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous Month")
            }

            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)} ${currentMonth.year}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next Month")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Weekdays Row (Sun to Sat)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            DayOfWeek.values().forEach {
                Text(
                    text = it.getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Grid for days
        val daysInMonth = currentMonth.lengthOfMonth()
        val firstDay = currentMonth.atDay(1).dayOfWeek.value % 7
        val total = daysInMonth + firstDay

        LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.fillMaxHeight()) {
            items(total) { index ->
                if (index < firstDay) {
                    Box(modifier = Modifier.size(48.dp))
                } else {
                    val day = index - firstDay + 1
                    val date = currentMonth.atDay(day)
                    DayCell(date = date, isToday = date == today) {
                        selectedDate = date
                    }
                }
            }
        }

        // Dialog for Panchang
        selectedDate?.let { date ->
            val panchang = PanchangProvider.getPanchang(date)
            if (panchang != null) {
                PanchangDialog(date, panchang) {
                    selectedDate = null
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayCell(date: LocalDate, isToday: Boolean, onClick: () -> Unit) {
    val bgColor = if (isToday) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    val textColor = if (isToday) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier
            .padding(4.dp)
            .size(48.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = date.dayOfMonth.toString(),
                color = textColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
