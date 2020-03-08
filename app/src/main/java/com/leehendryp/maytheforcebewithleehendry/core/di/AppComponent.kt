package com.leehendryp.maytheforcebewithleehendry.core.di

import android.content.Context
import com.leehendryp.maytheforcebewithleehendry.feed.data.di.DataModule
import com.leehendryp.maytheforcebewithleehendry.feed.domain.di.DomainModule
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.view.MainActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        DataModule::class,
        DomainModule::class,
        ViewModelModule::class
    ]
)
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(activity: MainActivity)
}