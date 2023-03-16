package fr.melanoxy.realestatemanager.data.utils.converters

import androidx.room.TypeConverter

class StringArrayListConverter {
    @TypeConverter
    fun fromString(value: String?): ArrayList<String>? {
        return value?.split(",")?.map { it.trim() }?.toCollection(ArrayList())
    }

    @TypeConverter
    fun toString(value: ArrayList<String>?): String? {
        return value?.joinToString(",")
    }
}