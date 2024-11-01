package io.github.malipo.kmppersiandate

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.periodUntil
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs
import kotlin.math.pow

/**
 * PersianDate provides functionality to work with Persian (Jalali) calendar dates.
 * This class handles conversion between Gregorian and Persian calendars, and provides
 * various date formatting and calculation utilities.
 *
 * @property formatPattern The pattern used for date formatting (default: "yyyy-MM-dd'T'HH:mm:ss'Z'")
 */
class PersianDate {
    private var shYear: Int = 0
    private var shMonth: Int = 0
    private var shDay: Int = 0
    private var timeInMilliSecond: Long = Clock.System.now().toEpochMilliseconds()
    private var formatPattern: String

    companion object {
        /** Number of milliseconds in one day */
        private val ONE_DAY_MS = (60.0.pow(2) * 1000) * 24

        /** Default date format pattern in ISO-8601 format */
        private const val DEFAULT_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"

        /** Array of Persian month names in order  */
        private val PERSIAN_MONTH_NAMES = arrayOf(
            "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
            "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
        )

        /**
         * Contains localized string constants for time-related text in Persian
         */
        private object TimeStrings {
            const val TODAY = "امروز"
            const val YESTERDAY = "دیروز"
            const val TOMORROW = "فردا"
            const val JUST_NOW = "همین الان"
            const val YEARS_AGO = "سال پیش"
            const val MONTHS_AGO = "ماه پیش"
            const val DAYS_AGO = "روز پیش"
            const val HOURS_AGO = "ساعت پیش"
            const val MINUTES_AGO = "دقیقه پیش"
            const val SECONDS_AGO = "ثانیه پیش"
            const val HOUR = "ساعت"
        }
    }

    /**
     * Creates a new PersianDate instance with the current system time
     */
    constructor() {
        this.formatPattern = DEFAULT_FORMAT
        initializeDate()
    }

    /**
     * Creates a new PersianDate instance with the current system time
     * @param formatPattern Date format pattern to use (defaults to ISO-8601 format)
     */
    constructor(formatPattern: String) {
        this.formatPattern = formatPattern
        initializeDate()
    }

    /**
     * Creates a new PersianDate instance with a specific timestamp
     * @param timeInMilliSecond Unix timestamp in milliseconds
     * @param formatPattern Date format pattern to use
     */
    constructor(timeInMilliSecond: Long, formatPattern: String = DEFAULT_FORMAT) {
        this.timeInMilliSecond = timeInMilliSecond
        this.formatPattern = formatPattern
        initializeDate()
    }

    /**
     * Creates a new PersianDate instance from a LocalDateTime object
     * @param localDateTime LocalDateTime instance to convert
     * @param formatPattern Date format pattern to use
     */
    constructor(localDateTime: LocalDateTime, formatPattern: String = DEFAULT_FORMAT) {
        this.timeInMilliSecond =
            localDateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        this.formatPattern = formatPattern
        initializeDate()
    }

    // Public Interface Methods

    /**
     * Updates the format pattern used for date formatting
     * @param format New format pattern to use
     * @return Current PersianDate instance for method chaining
     */
    fun setFormat(format: String): PersianDate {
        formatPattern = format
        return this
    }

    /**
     * Gets the current Persian calendar year
     * @return Year in Persian calendar
     */
    fun getShYear(): Int = shYear

    /**
     * Gets the current Persian calendar month (1-12)
     * @return Month in Persian calendar
     */
    fun getShMonth(): Int = shMonth

    /**
     * Gets the current Persian calendar day of month
     * @return Day of month in Persian calendar
     */
    fun getShDay(): Int = shDay


    /**
     * Returns a formatted Persian date string using month names
     * Example output: "15 فروردین 1402 ساعت 14:30"
     *
     * @param date Date string in the current format pattern
     * @return Formatted Persian date string with month name
     */
    fun getFullDatetimeWithMonthName(date: String): String {
        val dateTimeFormat = getDateTimeFormat()
        val dateObject = dateTimeFormat.parse(date)
        return formatPersianDate(dateObject, useMonthName = true)
    }

    /**
     * Returns a formatted Persian date string using month numbers
     * Example output: "1402/01/15 14:30"
     *
     * @param date Date string in the current format pattern
     * @return Formatted Persian date string with month number
     */
    fun getFullDatetimeWithMonthNumber(date: String): String {
        val dateTimeFormat = getDateTimeFormat()
        val dateObject = dateTimeFormat.parse(date)
        return formatPersianDate(dateObject, useMonthName = false)
    }

    /**
     * Formats a given LocalDateTime object into a Persian date string.
     * This function converts the given LocalDateTime into the Jalali calendar
     * format and constructs a string representation either using month names or
     * numeric values based on the `useMonthName` parameter.
     *
     * @param dateObject LocalDateTime object to be formatted into a Persian date.
     * @param useMonthName Boolean indicating whether to use month names (true)
     *                     or month numbers (false) in the formatted output.
     * @return Formatted Persian date string, either with month names or month
     *         numbers, depending on the value of `useMonthName`, including the
     *         time formatted as "HH:mm".
     */
    private fun formatPersianDate(dateObject: LocalDateTime, useMonthName: Boolean): String {
        val persianDateArray = gregorianToJalali(
            dateObject.year,
            dateObject.monthNumber,
            dateObject.dayOfMonth
        )

        val year = persianDateArray[0]
        val month = persianDateArray[1]
        var dayOfMonth = persianDateArray[2]

        if (month == 12) {
            val isLeapYear = isLeap(year)
            if (!isLeapYear && dayOfMonth == 30) {
                dayOfMonth = 29
            }
        }

        return if (useMonthName) {
            StringBuilder()
                .append(dayOfMonth)
                .append(" ")
                .append(PERSIAN_MONTH_NAMES[month - 1])
                .append(" ")
                .append(year)
                .append(" ")
                .append(TimeStrings.HOUR)
                .append(" ")
                .append(formatTime(dateObject))
                .toString()
        } else {
            StringBuilder()
                .append(year)
                .append("/")
                .append(month.toString().padStart(2, '0'))
                .append("/")
                .append(dayOfMonth.toString().padStart(2, '0'))
                .append(" ")
                .append(formatTime(dateObject))
                .toString()
        }
    }

    /**
     * Formats time component of a LocalDateTime
     * @param dateTime LocalDateTime instance to format
     * @return Formatted time string in HH:mm format with RTL mark
     */
    private fun formatTime(dateTime: LocalDateTime): String {
        val minutes = dateTime.minute.toString().padStart(2, '0')
        return "\u200E${dateTime.hour}:$minutes"
    }

    /**
     * Calculates the relative time difference from now to a given date
     * @param date Date string in the current format pattern
     * @return Human-readable string describing the time difference (e.g., "2 days ago")
     */
    fun daysAgo(date: String): String {
        val dateTimeFormat = getDateTimeFormat()
        val dateObject = dateTimeFormat.parse(date)
        return calculateAgoDate(
            dateObject.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        )
    }


    /**
     * Initializes the Persian date components based on the current timestamp
     * Converts Gregorian date to Persian date and sets year, month, and day fields
     */
    private fun initializeDate() {
        val dateTime = Instant.fromEpochMilliseconds(timeInMilliSecond)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        val (year, month, day) = gregorianToJalali(
            dateTime.year,
            dateTime.monthNumber,
            dateTime.dayOfMonth
        )

        shYear = year
        shMonth = month
        shDay = day
    }

    /**
     * Creates a DateTimeFormat instance based on the current format pattern
     * @return DateTimeFormat instance for parsing and formatting dates
     */
    @OptIn(FormatStringsInDatetimeFormats::class)
    private fun getDateTimeFormat(): DateTimeFormat<LocalDateTime> =
        LocalDateTime.Format { byUnicodePattern(formatPattern) }


    /**
     * Checks if a value falls within a given range
     * @param value Value to check
     * @param start Start of range (inclusive)
     * @param end End of range (exclusive)
     * @return Boolean indicating if value is in range
     */
    private fun isInRange(value: Long, start: Long, end: Long): Boolean =
        value in start until end

    /**
     * Checks if a given timestamp represents yesterday
     * @param date Timestamp to check
     * @return Boolean indicating if the date is yesterday
     */
    private fun isYesterday(date: Long): Boolean {
        val yesterdayTimestamp = Clock.System.now().toEpochMilliseconds() - ONE_DAY_MS.toLong()
        return isInRange(
            date,
            getBeginningOfDay(yesterdayTimestamp) + 1,
            getEndingOfDay(yesterdayTimestamp)
        )
    }

    /**
     * Checks if a given timestamp represents tomorrow
     * @param date Timestamp to check
     * @return Boolean indicating if the date is tomorrow
     */
    private fun isTomorrow(date: Long): Boolean {
        val tomorrowTimestamp = Clock.System.now().toEpochMilliseconds() + ONE_DAY_MS.toLong()
        return isInRange(
            date,
            getBeginningOfDay(tomorrowTimestamp) + 1,
            getEndingOfDay(tomorrowTimestamp)
        )
    }

    /**
     * Calculates a human-readable representation of time elapsed
     * @param timestamp Timestamp to calculate elapsed time from
     * @return Human-readable string representing elapsed time
     */
    private fun calculateAgoDate(timestamp: Long): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val createdAt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        val period = now.date.periodUntil(createdAt.date)
        val days = abs(now.date.toEpochDays() - createdAt.date.toEpochDays())

        return when {
            abs(period.years) > 0 -> "${abs(period.years)} ${TimeStrings.YEARS_AGO}"
            abs(period.months) > 0 -> "${abs(period.months)} ${TimeStrings.MONTHS_AGO}"
            days > 0 -> "$days ${TimeStrings.DAYS_AGO}"
            abs(now.hour - createdAt.hour) > 0 -> "${abs(now.hour - createdAt.hour)} ${TimeStrings.HOURS_AGO}"
            abs(now.minute - createdAt.minute) > 0 -> "${abs(now.minute - createdAt.minute)} ${TimeStrings.MINUTES_AGO}"
            abs(now.second - createdAt.second) > 0 -> "${abs(now.second - createdAt.second)} ${TimeStrings.SECONDS_AGO}"
            else -> TimeStrings.JUST_NOW
        }
    }

    /**
     * Gets the timestamp for the start of a day
     * @param timestamp Reference timestamp
     * @return Timestamp representing 00:00:00 of the day
     */
    private fun getBeginningOfDay(timestamp: Long): Long {
        val dateTime = Instant.fromEpochMilliseconds(timestamp)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        return LocalDateTime(dateTime.year, dateTime.month, dateTime.dayOfMonth, 0, 0)
            .toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
    }

    /**
     * Gets the timestamp for the end of a day
     * @param timestamp Reference timestamp
     * @return Timestamp representing 23:59:59 of the day
     */
    private fun getEndingOfDay(timestamp: Long): Long {
        val dateTime = Instant.fromEpochMilliseconds(timestamp)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        return LocalDateTime(dateTime.year, dateTime.month, dateTime.dayOfMonth, 23, 59, 59)
            .toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
    }

    /**
     * Determines if a Persian year is a leap year
     * Uses a reference year (1375) and calculates based on 33-year cycles
     * @param year Persian year to check
     * @return Boolean indicating if the year is a leap year
     */
    private fun isLeap(year: Int): Boolean {
        val referenceYear = 1375
        var startYear = 1375
        val yearRes = year - referenceYear

        if (yearRes == 0 || yearRes % 33 == 0) {
            return true
        }

        if (yearRes > 0) {
            if (yearRes > 33) {
                val cycles = yearRes / 33
                startYear = referenceYear + (cycles * 33)
            }
        } else {
            if (yearRes > -33) {
                startYear = referenceYear - 33
            } else {
                val cycles = (-yearRes / 33) + 1
                startYear = referenceYear - (cycles * 33)
            }
        }

        val leapYears = intArrayOf(
            startYear,
            startYear + 4,
            startYear + 8,
            startYear + 12,
            startYear + 16,
            startYear + 20,
            startYear + 24,
            startYear + 28,
            startYear + 33
        )

        return binarySearch(leapYears, year) >= 0
    }

    private fun binarySearch(array: IntArray, target: Int): Int {
        var left = 0
        var right = array.size - 1

        while (left <= right) {
            val mid = (left + right) / 2
            when {
                array[mid] == target -> return mid
                array[mid] < target -> left = mid + 1
                else -> right = mid - 1
            }
        }
        return -1
    }

    /**
     * Converts Gregorian date to Jalali (Persian) date
     * @param gregorianYear The Gregorian year
     * @param gregorianMonth The Gregorian month (1-12)
     * @param gregorianDay The Gregorian day of month
     * @return Triple containing (year, month, day) in Jalali calendar
     */
    private fun gregorianToJalali(gy: Int, gm: Int, gd: Int): IntArray {
        val g_d_m: IntArray = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
        val gy2: Int = if (gm > 2) (gy + 1) else gy
        var days: Int =
            355666 + (365 * gy) + ((gy2 + 3) / 4) - ((gy2 + 99) / 100) + ((gy2 + 399) / 400) + gd + g_d_m[gm - 1]
        var jy: Int = -1595 + (33 * (days / 12053))
        days %= 12053
        jy += 4 * (days / 1461)
        days %= 1461
        if (days > 365) {
            jy += ((days - 1) / 365)
            days = (days - 1) % 365
        }
        val jm: Int;
        val jd: Int;
        if (days < 186) {
            jm = 1 + (days / 31)
            jd = 1 + (days % 31)
        } else {
            jm = 7 + ((days - 186) / 30)
            jd = 1 + ((days - 186) % 30)
        }
        return intArrayOf(jy, jm, jd)
    }

}



