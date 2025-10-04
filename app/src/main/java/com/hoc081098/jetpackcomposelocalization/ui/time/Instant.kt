package com.hoc081098.jetpackcomposelocalization.ui.time

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Formats an [Instant] using this [DateTimeFormatter] in the specified [zoneId].
 *
 * The [instant] is first converted to a [ZonedDateTime] with [zoneId] (defaulting to
 * [ZoneId.systemDefault]) and then formatted with this formatter.
 *
 * @param instant the point in time to format.
 * @param zoneId the time zone to apply when interpreting [instant]. Defaults to the system zone.
 * @return the formatted date-time text.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun DateTimeFormatter.formatInstant(
  instant: Instant,
  zoneId: ZoneId = ZoneId.systemDefault()
): String =
  instant.toZonedDateTime(zoneId).format(this)

/**
 * Converts this [Instant] to a [ZonedDateTime] using the given [zoneId].
 *
 * Defaults to [ZoneId.systemDefault] when no zone is provided.
 *
 * @param zoneId the desired time zone.
 * @return the [ZonedDateTime] representing this instant in [zoneId].
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Instant.toZonedDateTime(zoneId: ZoneId = ZoneId.systemDefault()): ZonedDateTime =
  this.atZone(zoneId)
