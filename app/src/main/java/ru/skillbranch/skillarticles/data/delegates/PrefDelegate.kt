package ru.skillbranch.skillarticles.data.delegates


import android.content.SharedPreferences
import android.content.res.Resources
import androidx.preference.PreferenceManager
import ru.skillbranch.skillarticles.data.local.PrefManager
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PrefDelegate <T> (private val defaultValue: T) : ReadWriteProperty<PrefManager, T>{

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: PrefManager, property: KProperty<*>): T {
        return thisRef.get(property.name, defaultValue)
    }

    override fun setValue(thisRef: PrefManager, property: KProperty<*>, value: T) {
        //(thisRef as SharedPreferences).put(property.name, value)
        thisRef.put(property.name, value)
    }
}

fun <T> PrefManager.put(key: String, value: T) {
    with((this as SharedPreferences).edit()) {
        when (value) {
            is Boolean -> putBoolean(key, value)
            is Int -> putInt(key, value)
            is Float -> putFloat(key, value)
            is Long -> putLong(key, value)
            is String -> putString(key, value)
            else -> throw IllegalStateException("Type of property $key is not supported")
        }
        apply()
    }
}

fun <T> PrefManager.get(key: String, defaultValue: T): T {
    with(this as SharedPreferences) {
        return when (defaultValue) {
            is Boolean -> (getBoolean(key, defaultValue) as? T) ?: defaultValue
            is Int -> (getInt(key, defaultValue) as T) ?: defaultValue
            is Float -> (getFloat(key, defaultValue) as T) ?: defaultValue
            is Long -> (getLong(key, defaultValue) as T) ?: defaultValue
            is String -> (getString(key, defaultValue) as T) ?: defaultValue
            else -> throw IllegalStateException("Type of property $key is not supported")
        }
    }
}
