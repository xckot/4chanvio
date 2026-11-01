package com.nuvio.app.features.fourchan

import kotlinx.serialization.Serializable

@Serializable
data class FChanCatalog(
    val page: Int = 0,
    val threads: List<FChanThread> = emptyList()
)

@Serializable
data class FChanThread(
    val no: Int,
    val sub: String? = null,
    val com: String? = null,
    val tim: Long? = null,
    val ext: String? = null,
    val replies: Int = 0
)
