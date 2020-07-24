package ru.alexalekhin.todomanager

import android.app.Application
import com.facebook.stetho.Stetho

import ru.alexalekhin.todomanager.di.DaggerTODOAppComponent
import ru.alexalekhin.todomanager.di.TODOAppComponent
import ru.alexalekhin.todomanager.di.modules.ContextModule

class TODOManagerApp : Application() {
    lateinit var component: TODOAppComponent
    override fun onCreate() {
        super.onCreate()
        component = DaggerTODOAppComponent.builder()
            .contextModule(ContextModule(this))
            .build()
        Stetho.initializeWithDefaults(this)
    }
}
