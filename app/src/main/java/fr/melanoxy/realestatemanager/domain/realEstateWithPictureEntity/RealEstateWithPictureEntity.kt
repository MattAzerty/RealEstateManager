package fr.melanoxy.realestatemanager.domain.realEstateWithPictureEntity

import androidx.room.Embedded
import androidx.room.Relation
import fr.melanoxy.realestatemanager.domain.estatePicture.EstatePictureEntity
import fr.melanoxy.realestatemanager.domain.realEstate.RealEstateEntity

data class RealEstateWithPictureEntity(
    @Embedded
    val realEstateEntity: RealEstateEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "realEstateId"
    )
    val estatePictureEntities: List<EstatePictureEntity>,
)