package fr.melanoxy.realestatemanager.ui.utils

import android.Manifest
import fr.melanoxy.realestatemanager.domain.estateAgent.EstateAgentEntity

//PERMISSIONS
const val CAMERA_PERMISSION = Manifest.permission.CAMERA

//ROOM
const val KEY_INPUT_DATA_WORK_MANAGER = "KEY_INPUT_DATA_WORK_MANAGER"
const val DATABASE_NAME = "RealEstateManager_DATABASE"
val ESTATE_AGENTS = listOf(
    EstateAgentEntity(
        firstName = "Jean",
        lastName = "BINOT",
        picUrl = "https://i.pravatar.cc/200?u=JeanBINOT5"
    ),
    EstateAgentEntity(
        firstName = "Jeanne",
        lastName = "BINOTE",
        picUrl = "https://i.pravatar.cc/200?u=JeanneBINOTE6"
    ),
    EstateAgentEntity(
        firstName = "GUEST",
        lastName = "GUEST",
        picUrl = "https://www.pngitem.com/pimgs/m/504-5040528_empty-profile-picture-png-transparent-png.png"
    ),
)

val REAL_ESTATE_TYPES = listOf(
    "Single-family home",
    "Flat",
    "Townhouse",
    "Multi-family home",
    "Duplex",
    "Penthouse",
    "Vacation home"
)

