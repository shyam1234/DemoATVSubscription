package com.willow.android.tv.data.room.db

import androidx.room.TypeConverter
import java.util.Date

/**
 * Created by eldhosepaul on 14/02/23.
 */
class DateTypeConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}