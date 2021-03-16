package ru.skillbranch.skillarticles.data.remote.interceptors

import android.util.Log
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.remote.NetworkManager
import ru.skillbranch.skillarticles.data.remote.req.RefreshTokenReq

class TokenAuthenticator : Authenticator {
    private val preferences = PrefManager
    private val api by lazy { NetworkManager.api }

    override fun authenticate(route: Route?, response: Response): Request? {

        Log.e("-=TokenAuthenticator=-", "response.code = ${response.code}")

        if (response.code != 401) return null

        return getRefreshToken()?.let {
            response.request.newBuilder()
                .header("Authorization", it)
                .build()
        }
    }

    private fun getRefreshToken(): String? {
        val refreshToken = preferences.refreshToken
        val response = api.refreshToken(RefreshTokenReq(refreshToken)).execute()
        return if (response.isSuccessful) {
            response.body()?.let {
                val newAccessToken = "Bearer ${it.accessToken}"
                preferences.accessToken = newAccessToken
                preferences.refreshToken = it.refreshToken
                newAccessToken
            }
        } else null
    }
}