package com.flowledger.app.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

object DateUtils {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val monthFormatter = DateTimeFormatter.ofPattern("yyyy年M月")
    private val shortDateFormatter = DateTimeFormatter.ofPattern("M/d")

    fun toEpochDay(date: LocalDate): Long = date.toEpochDay()

    fun fromEpochDay(epochDay: Long): LocalDate =
        LocalDate.ofEpochDay(epochDay)

    fun formatDate(epochDay: Long): String =
        fromEpochDay(epochDay).format(dateFormatter)

    fun formatMonth(epochDay: Long): String =
        fromEpochDay(epochDay).format(monthFormatter)

    fun formatShortDate(epochDay: Long): String =
        fromEpochDay(epochDay).format(shortDateFormatter)

    fun formatMonthYear(year: Int, month: Int): String =
        "${year}年${month}月"

    fun getDayOfWeekDisplay(epochDay: Long): String =
        fromEpochDay(epochDay).dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.CHINESE)

    fun today(): LocalDate = LocalDate.now()

    fun todayEpochDay(): Long = today().toEpochDay()

    fun monthStart(year: Int, month: Int): Long =
        LocalDate.of(year, month, 1).toEpochDay()

    fun monthEnd(year: Int, month: Int): Long =
        LocalDate.of(year, month, 1).plusMonths(1).minusDays(1).toEpochDay()

    fun currentMonthStart(): Long =
        today().withDayOfMonth(1).toEpochDay()

    fun currentMonthEnd(): Long =
        today().withDayOfMonth(1).plusMonths(1).minusDays(1).toEpochDay()

    fun toEpochMillis(epochDay: Long): Long =
        fromEpochDay(epochDay).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    fun fromEpochMillis(epochMillis: Long): LocalDate =
        Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDate()
}
