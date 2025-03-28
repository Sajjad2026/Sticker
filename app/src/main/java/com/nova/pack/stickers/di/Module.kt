package com.nova.pack.stickers.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.nova.pack.stickers.repository.LocalDatabaseRepository
import com.nova.pack.stickers.room.Dao
import com.nova.pack.stickers.room.StickerDatabase
import com.nova.pack.stickers.viewmodel.EditImageViewModel
import com.nova.pack.stickers.viewmodel.GalleryViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class Module {
//    @Singleton
//    @Provides
//    fun provideApi():StickerApi = Retrofit.Builder()
//        .baseUrl(Config.BASE_URL)
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//        .create(StickerApi::class.java)

//    @Singleton
//    @Provides
//    fun provideRepository(api: StickerApi):RemoteDatabaseRepository {
//        return RemoteDatabaseRepository(api)
//    }

    @Provides
    fun provideLocalDatabase(@ApplicationContext context: Context): StickerDatabase {
        return Room.databaseBuilder(context.applicationContext,StickerDatabase::class.java,"stickers.db").build()
    }

    @Provides
    fun provideDatabaseDao(database: StickerDatabase): Dao {
        return database.getDatabaseDao()
    }

    @Provides
    fun provideLocalDatabaseRepository(db: StickerDatabase):LocalDatabaseRepository{
        return LocalDatabaseRepository(db)
    }

    @Singleton
    @Provides
    fun provideGalleryViewModel(context: Context):GalleryViewModel{
        return GalleryViewModel(context)
    }

    @Singleton
    @Provides
    fun provideEditImageViewModel(context: Context):EditImageViewModel{
        return EditImageViewModel(context)
    }

    @Singleton
    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }
}