package com.nova.pack.stickers.repository

import com.nova.pack.stickers.model.StickerPackEntity
import com.nova.pack.stickers.room.StickerDatabase
import javax.inject.Inject

class LocalDatabaseRepository @Inject constructor(private val db:StickerDatabase) {
    fun getStickers(): List<StickerPackEntity> = db.getDatabaseDao().getStickers()
    suspend fun addStickersPack(stickerPackEntity: StickerPackEntity) = db.getDatabaseDao().insertStickers(stickerPackEntity)
    suspend fun deleteAllStickers() = db.getDatabaseDao().deleteAll()
    suspend fun getStickerByIdentifier(identifier: String):StickerPackEntity? = db.getDatabaseDao().getStickerPackByIdentifier(identifier)
    suspend fun updateStickerPack(stickerPackEntity: StickerPackEntity) = db.getDatabaseDao().updateStickerPack(stickerPackEntity)
    suspend fun deleteStickerPack(identifier:String) = db.getDatabaseDao().deleteStickerPackByIdentifier(identifier)
    suspend fun getItemCount() = db.getDatabaseDao().getCount()
}