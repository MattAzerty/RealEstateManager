package fr.melanoxy.realestatemanager.domain.realEstate

import androidx.room.*
import fr.melanoxy.realestatemanager.domain.Address
import fr.melanoxy.realestatemanager.data.utils.converters.ByteArrayPngConverter
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

@TypeConverters(DateConverter::class, IntegerArrayListConverter::class, StringArrayListConverter::class, ByteArrayPngConverter::class )
data class RealEstateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(index = true)
    val estateAgentId: Long,
    val propertyType: String,
    val price: Double,
    val surfaceArea: Double,
    val numberOfRooms: Int,
    val numberOfBedrooms: Int,
    val description: String,
    val thumbnail: ByteArray,
    val photosList: ArrayList<String>,
    @Embedded
    val address: Address,
    val pointsOfInterest:ArrayList<Int>,
    val marketEntryDate: Date,
    val saleDate: Date,
)