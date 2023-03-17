package fr.melanoxy.realestatemanager.domain

data class Address(
    val street: String,
    val city: String,
    val state: String,
    val zipCode: String
)