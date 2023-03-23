package fr.melanoxy.realestatemanager.domain.estateAgent

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "estateAgent")
data class EstateAgentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val picUrl: String,
)