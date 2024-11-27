package com.mck.contacts.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ContactDao  {
    @Insert
    suspend fun insert(contact: Contact)

    @Delete
    suspend fun delete(contact: Contact)

    @Update
    suspend fun update(contact: Contact)

    @Query("SELECT * FROM contact_table WHERE id = :key")
    fun get(key: Long): LiveData<Contact>

    @Query("SELECT * FROM contact_table WHERE name LIKE :key ORDER BY name ASC")
    fun search(key: String): LiveData<List<Contact>>

    @Query("SELECT * FROM contact_table ORDER BY name ASC")
    fun getAll(): LiveData<List<Contact>>
}