package ru.skillbranch.skillarticles.viewmodels.auth

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.SavedStateHandle
import ru.skillbranch.skillarticles.data.repositories.RootRepository
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.NavigationCommand
import ru.skillbranch.skillarticles.viewmodels.base.Notify

class AuthViewModel(handle: SavedStateHandle) : BaseViewModel<AuthState>(handle, AuthState()), IAuthViewModel{
    private val repository = RootRepository
    init {
        subscribeOnDataSource(repository.isAuth()){isAuth, state ->
            state.copy(isAuth = isAuth)
        }
    }

    override fun handleLogin(login:String, pass:String, dest:Int?){
        launchSafety {
            repository.login(login, pass)
            navigate(NavigationCommand.FinishLogin(dest))
        }
    }


    override fun handleRegister(name: String, login: String, pass: String, dest: Int?) {
        if (name.isEmpty() || login.isEmpty() || pass.isEmpty()) {
            notify(Notify.ErrorMessage("Name, login, password it is required fields and not must be empty"))
            return
        }
        if (!isValidName(name)) {
            notify(Notify.ErrorMessage("The name must be at least 3 characters long and contain only letters and numbers and can also contain the characters \"-\" and \"_\""))
            return
        }
        if (!isValidLogin(login)) {
            notify(Notify.ErrorMessage("Incorrect Email entered"))
            return
        }
        if (!isValidPassword(pass)) {
            notify(Notify.ErrorMessage("Password must be at least 8 characters long and contain only letters and numbers"))
            return
        }

        launchSafety {
            repository.handleRegister(name, login, pass, dest)
            navigate(NavigationCommand.FinishLogin(dest))
        }
    }

    fun isValidName(name: String) = "^[a-zA-Z0-9_-]{2,}\$".toRegex().matches(name)
    fun isValidPassword(password: String) = "^[a-zA-Z0-9]{8,}\$".toRegex().matches(password)
    fun isValidLogin(login: String) = login.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(login).matches()
}

data class AuthState(val isAuth: Boolean = false): IViewModelState
