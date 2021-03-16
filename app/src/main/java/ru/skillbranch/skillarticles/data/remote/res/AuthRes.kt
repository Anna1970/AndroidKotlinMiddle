package ru.skillbranch.skillarticles.data.remote.res

import ru.skillbranch.skillarticles.data.models.User

data class AuthRes(
    val user: User? = null, //for refresh Token
    val refreshToken:String,
    val accessToken:String
)