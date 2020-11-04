package ru.skillbranch.skillarticles.extensions

import android.os.Parcelable
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.view.children
import androidx.navigation.NavDestination
import com.google.android.material.bottomnavigation.BottomNavigationView

fun View.setMarginOptionally(left:Int = marginLeft, top : Int = marginTop, right : Int = marginRight, bottom : Int = marginBottom) {
    val param = this.layoutParams as ViewGroup.MarginLayoutParams
    param.setMargins(left, top, right, bottom)
    this.layoutParams = param
}

fun View.setPaddingOptionally(left: Int = paddingLeft, top: Int = paddingTop,
                              right: Int = paddingRight, bottom: Int = paddingBottom)
{
    setPadding(left , top , right, bottom)
}

fun ViewGroup.saveChildViewStates(): SparseArray<Parcelable> {
    val childViewStates = SparseArray<Parcelable>()
    children.forEach { child -> child.saveHierarchyState(childViewStates) }
    return childViewStates
}

fun ViewGroup.restoreChildViewStates(childViewStates: SparseArray<Parcelable>) {
    children.forEach { child -> child.restoreHierarchyState(childViewStates) }
}
