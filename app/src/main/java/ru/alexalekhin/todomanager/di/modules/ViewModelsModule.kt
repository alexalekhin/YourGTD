package ru.alexalekhin.todomanager.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.alexalekhin.todomanager.domain.viewModels.InboxViewModel
import ru.alexalekhin.todomanager.domain.viewModels.MainViewModel
import ru.alexalekhin.todomanager.domain.viewModels.ProjectViewModel
import ru.alexalekhin.todomanager.di.ViewModelFactory
import ru.alexalekhin.todomanager.di.misc.ViewModelKey

@Module
abstract class ViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(InboxViewModel::class)
    internal abstract fun provideInboxViewModel(viewModel: InboxViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProjectViewModel::class)
    internal abstract fun provideProjectViewModel(viewModel: ProjectViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun provideMainScreenViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}