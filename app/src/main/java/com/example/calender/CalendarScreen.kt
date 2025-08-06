package com.example.calender

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
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

    val eventsThisMonth = remember(currentMonth) {
        (1..currentMonth.lengthOfMonth()).mapNotNull { day ->
            val date = currentMonth.atDay(day)
            PanchangProvider.getPanchang(date)?.let { panchang ->
                date to panchang
            }
        }
    }

    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value % 7
    val dates = buildList<LocalDate?> {
        repeat(firstDayOfWeek) { add(null) }
        repeat(daysInMonth) { add(currentMonth.atDay(it + 1)) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary
                        )
                    ),
                    shape = MaterialTheme.shapes.medium
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.height(20.dp))
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous Month")
            }

            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)} ${currentMonth.year}",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
            )

            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next Month")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val weekdayColors = listOf(
            Color.Red,
            Color.Gray,
            Color.Gray,
            Color.Gray,
            Color.Gray,
            Color.Blue,
            Color.Magenta
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DayOfWeek.values().forEachIndexed { index, day ->
                Text(
                    text = day.getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = weekdayColors[index],
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsIndexed(dates) { _, date ->
                DayCell(
                    date = date,
                    isToday = date == today,
                    onClick = { if (date != null) selectedDate = date }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "🌟 Events in ${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.weight(1f,
            false)
            .verticalScroll(rememberScrollState())
        ) {
            if (eventsThisMonth.isEmpty()) {
                Text(
                    text = "No events this month.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                eventsThisMonth.forEach { (date, panchang) ->
                    if (panchang.festivals.isNotEmpty()) {
                        Text(
                            text = buildAnnotatedString {
                                append("• ${date.dayOfMonth} ${date.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)} — ${panchang.tithi}")
                                append(" | ")
                                withStyle(SpanStyle(color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)) {
                                    append(panchang.festivals.joinToString("\n"))
                                }
                            },
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }

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
fun DayCell(date: LocalDate?, isToday: Boolean, onClick: () -> Unit) {
    if (date == null) {
        Box(modifier = Modifier.size(48.dp))
    } else {
        val bgColor = when {
            isToday -> MaterialTheme.colorScheme.primaryContainer
            date.dayOfWeek == DayOfWeek.SUNDAY -> Color(0xFFFFCDD2)
            date.dayOfWeek == DayOfWeek.SATURDAY -> Color(0xFFBBDEFB)
            else -> MaterialTheme.colorScheme.surfaceVariant
        }

        val textColor = when {
            isToday -> MaterialTheme.colorScheme.onPrimaryContainer
            date.dayOfWeek == DayOfWeek.SUNDAY -> Color(0xFFD32F2F)
            date.dayOfWeek == DayOfWeek.SATURDAY -> Color(0xFF1976D2)
            else -> MaterialTheme.colorScheme.onSurface
        }

        Card(
            modifier = Modifier
                .size(48.dp)
                .clickable { onClick() },
            shape = MaterialTheme.shapes.small,
            colors = CardDefaults.cardColors(containerColor = bgColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(
                    text = date.dayOfMonth.toString(),
                    color = textColor,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
