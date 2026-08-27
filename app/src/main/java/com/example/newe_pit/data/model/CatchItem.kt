package com.example.newe_pit.data.model

import java.util.UUID

/**
 * Model Item Tangkapan dalam Keranjang (Cart Metaphor)
 */
data class CatchItem(
    val id: String = UUID.randomUUID().toString(),
    val speciesName: String,
    val weightKg: Int,
    val quantityCount: Int
)