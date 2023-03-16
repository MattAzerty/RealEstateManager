package fr.melanoxy.realestatemanager.domain.realEstate

import androidx.room.*
import fr.melanoxy.realestatemanager.data.utils.converters.DateConverter
import fr.melanoxy.realestatemanager.data.utils.converters.IntegerArrayListConverter
import fr.melanoxy.realestatemanager.data.utils.converters.StringArrayListConverter
import fr.melanoxy.realestatemanager.domain.estateAgent.EstateAgentEntity
import java.util.*
import kotlin.collections.ArrayList

@Entity(
    tableName = "realEstate",
    foreignKeys = [
        ForeignKey(
            entity = EstateAgentEntity::class,
            parentColumns = ["id"],
            childColumns = ["estateAgentId"],
        )
    ],
)

@TypeConverters(DateConverter::class, IntegerArrayListConverter::class, StringArrayListConverter::class )
data class RealEstateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(index = true)//TODO for foreign key?
    val estateAgentId: Long,
    val propertyType: String,
    val price: Double,
    val surfaceArea: Double,
    val numberOfRooms: Int,
    val numberOfBathrooms: Int,
    val numberOfBedrooms: Int,
    val description: String,
    val photosList: ArrayList<String>,
    val address: String,
    val pointsOfInterest:ArrayList<Int>,
    val status: Boolean,
    val marketEntryDate: Date,
    val saleDate: Date,
)