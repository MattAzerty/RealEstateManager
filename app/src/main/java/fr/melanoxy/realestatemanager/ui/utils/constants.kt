package fr.melanoxy.realestatemanager.ui.utils

import android.Manifest
import fr.melanoxy.realestatemanager.domain.estateAgent.EstateAgentEntity

//PERMISSIONS
const val CAMERA_PERMISSION = Manifest.permission.CAMERA
const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION

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
        picUrl = "https://p7.hiclipart.com/preview/123/735/760/computer-icons-physician-login-medicine-user-avatar.jpg"
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

val FILTERING_CRITERIA_LIST = listOf(
    "[A]:Agent Name.",
    "[T]:Type of property.",
    "[C]:City.",
    "[POI]:Point of interest.",
    "[#P]:Minimum #pic.",
    "[SD>]:After sale date.",
    "[SD<]:Before sale date.",
    "[MED>]:After market date.",
    "[MED<]:Before market date.",
    "[$>]:Price superior to.",
    "[$<]:Price inferior to.",
    "[#R]:Numb. of room.",
    "[#B]:Numb. of bedroom.",
    "[S>]:Surface minimum.",
    "[S<]:Surface maximum.",
)

