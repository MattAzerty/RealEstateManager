package fr.melanoxy.realestatemanager.data.utils.converters

import androidx.room.TypeConverter
import java.util.*
//https://developer.android.com/training/data-storage/room/referencing-data?hl=fr
class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}