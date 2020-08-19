package ru.skillbranch.skillarticles.viewmodels.base

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewModelDelegate<T: ViewModel>(
    private val clazz: Class<T>,
    private val arg: Any?
) : ReadOnlyProperty<FragmentActivity, T> {
    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: FragmentActivity, property: KProperty<*>): T {
        val vmFactory = arg?.let { ViewModelFactory(it) }
        return ViewModelProviders.of(thisRef, vmFactory).get(clazz)
    }
}