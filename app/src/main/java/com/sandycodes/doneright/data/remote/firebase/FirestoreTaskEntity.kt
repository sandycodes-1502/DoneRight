package com.sandycodes.doneright.data.remote.firebase

data class FirestoreTaskEntity(
    val id: String ="",
    val title: String = "",
    val description: String? = null,
    val status: String = "",
    val createdAt: Long = 0,
    val updatedAt: Long = 0
)