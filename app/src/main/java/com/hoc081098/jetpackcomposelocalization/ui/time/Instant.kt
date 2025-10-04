package com.hoc081098.jetpackcomposelocalization.ui.time

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Suppress("NOTHING_TO_INLINE")
inline fun DateTimeFormatter.formatInstant(
  instant: Instant,
  zoneId: ZoneId = ZoneId.systemDefault()
): String =
  instant.toZonedDateTime(zoneId).format(this)

@Suppress("NOTHING_TO_INLINE")
inline fun Instant.toZonedDateTime(zoneId: ZoneId = ZoneId.systemDefault()): ZonedDateTime =
  this.atZone(zoneId)
