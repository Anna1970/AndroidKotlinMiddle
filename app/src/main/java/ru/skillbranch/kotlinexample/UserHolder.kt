package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting
import ru.skillbranch.kotlinexample.extensions.loginNormalized

object UserHolder {

    private val map = mutableMapOf<String, User>()

    fun registerUser(
        fullName: String,
        email: String,
        password: String
    ): User = if (!map.containsKey(email.toLowerCase()))
        User.makeUser(fullName, email = email, password = password)
            .also { user -> map[user.login] = user }
    else throw IllegalArgumentException("A user with this email already exists")

    fun registerUserByPhone(fullName: String, rawPhone: String): User {
        val phone = """(\(|\)|-| )""".toRegex().replace(rawPhone, "")
        return when {
            """[a-zA-Z]""".toRegex()
                .containsMatchIn(phone) -> throw IllegalArgumentException("Enter a valid phone number starting with a + and containing 11 digits")
            map.containsKey(phone) -> throw IllegalArgumentException("A user with this phone already exists")
            else -> {
                User.makeUser(fullName, phone = rawPhone).also { user -> map[user.login] = user }
            }
        }
    }

    fun loginUser(login: String, password: String): String? =
        map[login.loginNormalized()]?.let {
                if (it.checkPassword(password)) it.userInfo
                else null
            }

    fun requestAccessCode(login: String) {
        if (login.trim().startsWith('+')) {
            map[login.replace("""[^+\d]""".toRegex(), "")]?.let {
                it.changePassword(it.accessCode.toString(), it.generateAccessCode())
                it.sendAccessCodeToUser(it.login, it.accessCode.toString())
            }
        }
    }

    fun importUsers(list: List<String>): List<User>{
        val result: MutableList<User> = mutableListOf()
        for ( str in list) {
            val userInf = str.split(";")
            val saltHash =userInf[2].trim().split(":")

            var email:String? = null
            if(userInf[1].isNotEmpty()) email=userInf[1].trim()
            var phone:String? = null
            if (userInf[3].isNotEmpty()) phone = userInf[3]

            var  login = (email?:phone!!).loginNormalized()

            if (!map.containsKey(login) ) {
                println("map not contain login $login")
                result.add(User.makeUser(userInf[0], email = email, salt = saltHash[0], password = saltHash[1], phone = phone ).also { user -> map[login] = user })
            }
        }
        return result
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder() {
        map.clear()
    }
}