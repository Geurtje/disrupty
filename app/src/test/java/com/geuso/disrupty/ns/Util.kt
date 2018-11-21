package com.geuso.disrupty.ns

import java.text.SimpleDateFormat
import java.time.Instant

fun instantOf(dateTimeStr: String): Instant {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
    return dateFormat.parse(dateTimeStr).toInstant()
}