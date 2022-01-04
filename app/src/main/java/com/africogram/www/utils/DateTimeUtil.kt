package com.africogram.www.utils

import com.github.thunder413.datetimeutils.DateTimeUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * DateTimeUtil :: contain every recurring task dealing with Date and Time
 */
class DateTimeUtil {
    /**
     * Set default time zone UTC
     */
    private fun setDefaultTimeZone() {
        DateTimeUtils.setTimeZone(TimeZone.getDefault().displayName)
    }

    fun getTodayDate(): String {
        setDefaultTimeZone()
        //val sdf = SimpleDateFormat("EEEE, MMMM dd yyyy hh:mm a",Locale.getDefault())
        val sdf = SimpleDateFormat("MMMM dd yyyy hh:mm a",Locale.getDefault())
        return sdf.format(Date())
    }

    fun getDateFromTimestampInSeconds(timestamp: Long): String {
        setDefaultTimeZone()
        val sdf = SimpleDateFormat("MMMM dd hh:mm a",Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }

    fun getDateFromTimestampInMilliseconds(timestamp: Long): String {
        setDefaultTimeZone()
        val sdf = SimpleDateFormat("MMMM dd yyyy hh:mm a",Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}