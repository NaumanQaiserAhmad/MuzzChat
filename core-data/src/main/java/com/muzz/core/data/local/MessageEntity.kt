package com.muzz.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fromUserId: String,
    val text: String,
    val sentAtEpochMs: Long,
    val deliveryStatus: Int
)
