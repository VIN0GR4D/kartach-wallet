package com.kartach.app.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    
    @Query("SELECT * FROM cards ORDER BY date_added DESC")
    fun getAllCards(): Flow<List<CardEntity>>
    
    @Query("SELECT * FROM cards ORDER BY date_added DESC")
    suspend fun getAllCardsList(): List<CardEntity>
    
    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun getCardById(id: Long): CardEntity?
    
    @Query("SELECT * FROM cards WHERE store_name LIKE '%' || :query || '%' OR card_number LIKE '%' || :query || '%' OR card_type LIKE '%' || :query || '%'")
    fun searchCards(query: String): Flow<List<CardEntity>>
    
    @Insert
    suspend fun insertCard(card: CardEntity): Long
    
    @Update
    suspend fun updateCard(card: CardEntity)
    
    @Delete
    suspend fun deleteCard(card: CardEntity)
    
    @Query("DELETE FROM cards WHERE id = :id")
    suspend fun deleteCardById(id: Long): Int
}