package ru.skillbranch.skillarticles

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.facebook.stetho.Stetho
import ru.skillbranch.skillarticles.data.local.PrefManager

class App : Application() {

    companion object {
        private var instance : App? = null
        fun applicationContext() : Context = instance!!.applicationContext
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()

        //set default Night Mode
       /* if (PrefManager.getAppSettings().value?.isDarkMode == true)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)*/

        Stetho.initializeWithDefaults(this)
    }
}
