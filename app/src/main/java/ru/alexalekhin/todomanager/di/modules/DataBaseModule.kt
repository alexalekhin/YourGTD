package ru.alexalekhin.todomanager.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.alexalekhin.todomanager.data.TODODatabase
import javax.inject.Singleton

@Module(includes = [ContextModule::class])
class DataBaseModule {
    @Provides
    @Singleton
    fun provideDataBase(context: Context): TODODatabase {
        return Room.databaseBuilder(context, TODODatabase::class.java, "todo_db")
            .allowMainThreadQueries()
            .build()
    }

    @Provides
    @Singleton
    fun provideDataBaseName() = "todo_db"
}