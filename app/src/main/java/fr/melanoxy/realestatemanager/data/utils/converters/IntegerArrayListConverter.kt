package fr.melanoxy.realestatemanager.data.utils.converters

import androidx.room.TypeConverter

class IntegerArrayListConverter {
    @TypeConverter
    fun fromString(value: String?): ArrayList<Int>? {
        return value?.split(",")?.map { it.trim().toInt() }?.toCollection(ArrayList())
    }

    @TypeConverter
    fun toString(value: ArrayList<Int>?): String? {
        return value?.joinToString(",")
    }
}