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
        lastName = "BINOT"
    ),
    EstateAgentEntity(
        firstName = "Jeanne",
        lastName = "BINOTE"
    ),
    EstateAgentEntity(
        firstName = "GUEST",
        lastName = "GUEST"
    ),
)

/*
Single-family homes: These are standalone houses intended for one family to live in, and they are
 commonly sold on the real estate market.

Condominiums: These individual units within a larger building can also be sold to new owners.

Townhouses: These are attached homes that share walls with neighboring units and are sold to new owners.

Multi-family homes: These are properties with two or more separate living units, such as duplexes or
 apartment buildings, and can be sold to new owners.

Commercial properties: These are properties used for business purposes, such as retail stores, office
 buildings, or warehouses, and can be sold to new owners.

Land: Vacant land can be sold for future development, farming, or other purposes.

Vacation homes: Secondary homes that are used for recreational purposes, such as beach houses or
mountain cabins, can also be listed for sale.
 */

val REAL_ESTATE_TYPES = listOf(
    "Single-family home",
    "Condominium",
    "Townhouse",
    "Multi-family home",
    "Commercial property",
    "Vacant land",
    "Vacation home"
)

val POINTS_OF_INTEREST = listOf(
    "Park",
    "School",
    "Grocery store",
    "Shopping mall",
    "Hospital"
)

