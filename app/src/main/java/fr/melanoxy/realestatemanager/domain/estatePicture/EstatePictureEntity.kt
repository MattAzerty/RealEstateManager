package fr.melanoxy.realestatemanager.domain.estatePicture

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import fr.melanoxy.realestatemanager.domain.realEstate.RealEstateEntity

@Entity(tableName = "estatePicture",
    foreignKeys = [
        ForeignKey(
            entity = RealEstateEntity::class,
            parentColumns = ["id"],
            childColumns = ["realEstateId"],
        )
    ],
    )

data class EstatePictureEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(index = true)
    val realEstateId: Long,
    val name: String,
    val path: String
)