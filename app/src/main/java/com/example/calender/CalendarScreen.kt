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
            .padding(16.dp)
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFF5F7FA), Color(0xFFE4EBF5))
                )
            )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF6A11CB), Color(0xFF2575FC))
                    ),
                    shape = MaterialTheme.shapes.large
                )
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
            }

            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)} ${currentMonth.year}",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color.White
            )

            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DayOfWeek.values().forEach { day ->
                Text(
                    text = day.getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF6A11CB),
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
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(dates) { _, date ->
                DayCellPremium(
                    date = date,
                    isToday = date == today,
                    hasEvent = eventsThisMonth.any { it.first == date },
                    onClick = { if (date != null) selectedDate = date }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider(thickness = 1.dp, color = Color(0xFFCCCCCC))
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "🌟 Festivals & Events",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
            color = Color(0xFF6A11CB)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .weight(1f, false)
                .verticalScroll(rememberScrollState())
        ) {
            if (eventsThisMonth.isEmpty()) {
                Text(
                    text = "No events this month.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            } else {
                eventsThisMonth.forEach { (date, panchang) ->
                    if (panchang.festivals.isNotEmpty()) {
                        Card(
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "${date.dayOfMonth} ${date.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)} — ${panchang.tithi}",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2575FC)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = panchang.festivals.joinToString(", "),
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFD32F2F)
                                )
                            }
                        }
                    }
                }
            }
        }

        selectedDate?.let { date ->
            val panchang = PanchangProvider.getPanchang(date)
            if (panchang != null) {
                PanchangDialog(date, panchang) { selectedDate = null }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayCellPremium(date: LocalDate?, isToday: Boolean, hasEvent: Boolean, onClick: () -> Unit) {
    if (date == null) {
        Box(modifier = Modifier.size(48.dp))
    } else {
        val bgColor = when {
            isToday -> Brush.linearGradient(listOf(Color(0xFF6A11CB), Color(0xFF2575FC)))
            else -> Brush.verticalGradient(listOf(Color.White, Color(0xFFE3E6F0)))
        }

        Card(
            modifier = Modifier
                .size(48.dp)
                .clickable { onClick() },
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(bgColor)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        color = if (isToday) Color.White else Color.Black,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                    )
                    if (hasEvent) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .background(Color(0xFFD32F2F), shape = MaterialTheme.shapes.small)
                        )
                    }
                }
            }
        }
    }
}
