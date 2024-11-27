package com.mck.contacts.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact_table")
data class Contact (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    var name: String = "",
    var number: String = "",
    var email : String = "",

    @ColumnInfo(name = "picture_uri")
    var picture: String? = ""   // Store URI as a string
)