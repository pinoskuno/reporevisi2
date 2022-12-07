package com.example.storyappsubmission1.Data.Functon

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun String.withDateFormat(targetTimeZone: String): String {
    val instant = Instant.parse(this)
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        .withZone(ZoneId.of(targetTimeZone))
    return formatter.format(instant)
}
