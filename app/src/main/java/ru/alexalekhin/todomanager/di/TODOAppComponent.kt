package ru.alexalekhin.todomanager.di

import dagger.Component
import ru.alexalekhin.todomanager.presentation.fragments.InboxFragment
import ru.alexalekhin.todomanager.presentation.fragments.MainFragment
import ru.alexalekhin.todomanager.presentation.fragments.EditProjectFragment
import ru.alexalekhin.todomanager.presentation.fragments.ProjectFragment
import ru.alexalekhin.todomanager.di.modules.DataBaseModule
import ru.alexalekhin.todomanager.di.modules.ViewModelsModule
import javax.inject.Singleton

@Component(modules = [ViewModelsModule::class, DataBaseModule::class])
@Singleton
interface TODOAppComponent {
    fun inject(fragment: InboxFragment)
    fun inject(fragment: ProjectFragment)
    fun inject(fragment: EditProjectFragment)
    fun inject(fragment: MainFragment)
}
