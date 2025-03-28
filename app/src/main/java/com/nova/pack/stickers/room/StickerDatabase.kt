package com.nova.pack.stickers.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nova.pack.stickers.model.StickerPackEntity

@Database(
    entities = [StickerPackEntity::class],
    version = 1
)
@TypeConverters(value = [ListStringConverter::class])
abstract class StickerDatabase: RoomDatabase() {
    abstract fun getDatabaseDao():Dao
    companion object{
        @Volatile
        private var instance: StickerDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance?:synchronized(LOCK){
            instance?: createDatabase(context).also {
                instance = it
            }
        }
        private fun createDatabase(context: Context) = Room.databaseBuilder(context.applicationContext,StickerDatabase::class.java,"stickers.db").build()
    }
}