package com.aas.medi_bridge.Domain

data class CategoryModel(
    val Id: Int = 0,
    val Name: String = "",
    val Picture: String = "" // Changed from Int to String to handle Firebase URL data
)
