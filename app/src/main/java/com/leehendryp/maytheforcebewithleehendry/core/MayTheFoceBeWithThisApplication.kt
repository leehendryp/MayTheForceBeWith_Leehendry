package com.leehendryp.maytheforcebewithleehendry.core

import android.app.Application
import com.leehendryp.maytheforcebewithleehendry.core.di.AppComponent
import com.leehendryp.maytheforcebewithleehendry.core.di.DaggerAppComponent

class MayTheFoceBeWithThisApplication : Application() {
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }
}