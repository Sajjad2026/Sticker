package com.nova.pack.stickers.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nova.pack.stickers.model.StickerPackEntity

@Dao
interface Dao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStickers(newsEntity: StickerPackEntity)

    @Query("DELETE FROM StickerPackEntity")
    fun deleteAll()

    @Query("SELECT * FROM StickerPackEntity")
    fun getStickers(): List<StickerPackEntity>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateStickerPack(stickerPack: StickerPackEntity)

    @Query("SELECT * FROM StickerPackEntity WHERE identifier = :stickerPackIdentifier")
    suspend fun getStickerPackByIdentifier(stickerPackIdentifier: String): StickerPackEntity?

    @Query("DELETE FROM StickerPackEntity WHERE identifier = :stickerPackIdentifier")
    suspend fun deleteStickerPackByIdentifier(stickerPackIdentifier: String)

    @Query("SELECT COUNT(id) FROM StickerPackEntity")
    fun getCount(): Int




}