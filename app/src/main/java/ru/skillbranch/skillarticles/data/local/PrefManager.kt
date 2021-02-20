package ru.skillbranch.skillarticles.data.local

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import ru.skillbranch.skillarticles.App
import ru.skillbranch.skillarticles.data.delegates.PrefDelegate
import ru.skillbranch.skillarticles.data.models.AppSettings

@SuppressLint("RestrictedApi")
object PrefManager {
    internal val preferences : SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext())
    }

    /*var storedBoolean by PrefDelegate(false)
    var storedString by PrefDelegate("test")
    var storedInt by PrefDelegate(Int.MAX_VALUE)
    var storedLong by PrefDelegate(Long.MAX_VALUE)
    var storedFloat by PrefDelegate(100f)*/

    private val isauth = MutableLiveData(false)
    private val appsettings = MutableLiveData(
        AppSettings(isDarkMode = false, isBigText = false)
    )

    init {
        isauth.postValue(preferences.getBoolean("isAuth", false))
        appsettings.postValue(
            AppSettings(
                isDarkMode = preferences.getBoolean("isDarkMode", false),
                isBigText = preferences.getBoolean("isBigText", false)
            )
        )
    }

    fun clearAll() {
        preferences.edit().clear().apply()
    }

    fun getAppSettings(): LiveData<AppSettings> = appsettings


    fun isAuth() : MutableLiveData<Boolean> = isauth

    fun setAuth(auth:Boolean): Unit {
        isauth.postValue(auth)
        preferences.edit().putBoolean("isAuth", auth).apply()
    }

    fun updateSettings(settings: AppSettings) {
        appsettings.postValue(settings)
        preferences.edit()
            .putBoolean("isDarkMode", settings.isDarkMode)
            .putBoolean("isBigText", settings.isBigText)
            .apply()
    }
}