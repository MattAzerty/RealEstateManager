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

val FILTERING_CRITERIA_LIST = listOf(
    "[A]:Filter by Agent Name.",
    "[T]:Filter by type of property.",
    "[C]:Filter by city.",
    "[POI]:Include point of interest.",
    "[#P]:Minimum number of picture.",
    "[SD>]:After this sale date.",
    "[SD<]:Before this sale date.",
    "[MED>]:After this market entry date.",
    "[MED<]:Before this market entry date.",
    "[$>]:Price superior to.",
    "[$<]:Price inferior to.",
    "[#R]:Number of room(s).",
    "[#B]:Number of bedroom(s).",
    "[S>]:Surface minimum.",
    "[S<]:Surface maximum.",
)

