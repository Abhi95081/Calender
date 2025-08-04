package com.example.calender.data

import android.content.Context
import com.example.calender.model.Panchang
import org.json.JSONObject
import java.time.LocalDate

object PanchangProvider {
    private var panchangMap: Map<String, Panchang> = emptyMap()

    fun loadFromAssets(context: Context) {
        try {
            val inputStream = context.assets.open("panchang_2025.json")
            val json = inputStream.bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(json)
            val tempMap = mutableMapOf<String, Panchang>()

            for (key in jsonObject.keys()) {
                val entry = jsonObject.getJSONObject(key)
                val festivalsJsonArray = entry.getJSONArray("festivals")
                val festivals = List(festivalsJsonArray.length()) { i ->
                    festivalsJsonArray.getString(i)
                }

                tempMap[key] = Panchang(
                    tithi = entry.optString("tithi", ""),
                    nakshatra = entry.optString("nakshatra", ""),
                    sunrise = entry.optString("sunrise", ""),
                    sunset = entry.optString("sunset", ""),
                    rahukalam = entry.optString("rahukalam", ""),
                    festivals = festivals
                )
            }
            panchangMap = tempMap
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getPanchang(date: LocalDate): Panchang? {
        return panchangMap[date.toString()]
    }
}
