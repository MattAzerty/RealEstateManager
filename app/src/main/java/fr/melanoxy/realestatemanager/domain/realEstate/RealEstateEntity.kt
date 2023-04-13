package fr.melanoxy.realestatemanager.domain.realEstate

import android.graphics.Bitmap
import androidx.room.*
import fr.melanoxy.realestatemanager.domain.Address
import fr.melanoxy.realestatemanager.data.utils.converters.ByteArrayBitmapConverter
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

@TypeConverters(DateConverter::class, IntegerArrayListConverter::class, StringArrayListConverter::class, ByteArrayBitmapConverter::class )
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
    val thumbnail: Bitmap,
    val numberOfPictures:Int,
    @Embedded
    val address: Address,
    val coordinates:String,
    val pointsOfInterest:ArrayList<String>,
    val marketEntryDate: Date,
    val saleDate: Date?,
)