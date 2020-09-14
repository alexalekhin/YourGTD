package ru.alexalekhin.todomanager.di

import dagger.Component
import ru.alexalekhin.todomanager.presentation.inbox.InboxFragment
import ru.alexalekhin.todomanager.presentation.head.HeadFragment
import ru.alexalekhin.todomanager.presentation.project.projecteditor.EditProjectFragment
import ru.alexalekhin.todomanager.presentation.project.ProjectFragment
import ru.alexalekhin.todomanager.di.modules.DataBaseModule
import ru.alexalekhin.todomanager.di.modules.ViewModelsModule
import ru.alexalekhin.todomanager.presentation.project.projecteditor.deadline.DatePickerFragment
import ru.alexalekhin.todomanager.presentation.project.projecteditor.deadline.TimePickerFragment
import javax.inject.Singleton

@Component(modules = [ViewModelsModule::class, DataBaseModule::class])
@Singleton
interface TODOAppComponent {

    fun inject(fragment: InboxFragment)
    fun inject(fragment: ProjectFragment)
    fun inject(fragment: EditProjectFragment)
    fun inject(fragment: DatePickerFragment)
    fun inject(fragment: TimePickerFragment)
    fun inject(fragment: HeadFragment)
}
