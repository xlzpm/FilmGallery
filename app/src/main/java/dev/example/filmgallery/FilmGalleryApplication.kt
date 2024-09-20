package dev.example.filmgallery

import android.app.Application
import dev.example.filmgallery.ui.AppViewModel
import dev.example.network.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

class FilmGalleryApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        val app = module {
            single { AppViewModel(get()) }
        }

        startKoin {
            androidContext(this@FilmGalleryApplication)
            modules(app, networkModule)
        }
    }
}