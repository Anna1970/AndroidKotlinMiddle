package ru.skillbranch.skillarticles.data.remote.interceptors

import android.util.Log
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.remote.NetworkManager
import ru.skillbranch.skillarticles.data.remote.req.RefreshReq

class TokenAuthenticator : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {

        Log.e("-=TokenAuthenticator=-", "refreshToken = ${PrefManager.refreshToken}")
        Log.e("-=TokenAuthenticator=-", "response.code = ${response.code}")
        Log.e("-=TokenAuthenticator=-", "response.body = ${response.body}")

        if (response.code != 401) return null

        val refreshRes = NetworkManager.api.refreshToken(RefreshReq(PrefManager.refreshToken)).execute()

        if (!refreshRes.isSuccessful) return null

        PrefManager.accessToken = "Bearer ${refreshRes.body()!!.accessToken}"
        PrefManager.refreshToken = refreshRes.body()!!.refreshToken

        return response.request
                .newBuilder()
                .header(
                    "Authorization",
                    "Bearer ${refreshRes.body()!!.accessToken} "
                ).build()
    }
}