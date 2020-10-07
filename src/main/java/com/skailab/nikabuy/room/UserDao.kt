package com.skailab.nikabuy.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)
    @Query("SELECT * from user_nikabuy_table WHERE id = :key")
    fun get(key: Long): User?

    @Query("DELETE FROM user_nikabuy_table")
    fun clear()
    @Query("UPDATE user_nikabuy_table SET productProvider=:provider WHERE id = :id")
    fun update(provider: String, id: Int)
    @Query("UPDATE user_nikabuy_table SET audioSearchLanguage=:language WHERE id = :id")
    fun updateAudioLanguage(language: String, id: Int)
}

