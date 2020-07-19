package ru.skillbranch.kotlinexampl

import androidx.annotation.VisibleForTesting

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

    fun loginUser(login: String, password: String): String? {
        return if (login.trim().startsWith('+')){
            map[login.replace("""[^+\d]""".toRegex(), "")]?.let {
                if (it.checkPassword(it.accessCode.toString())) it.userInfo
                else null
            }
        } else
            map[login.trim()]?.let {
                if (it.checkPassword(password)) it.userInfo
                else null
            }
    }

    fun requestAccessCode(login: String) {
        if (login.trim().startsWith('+')) {
            map[login.replace("""[^+\d]""".toRegex(), "")]?.let {
                it.changePassword(it.accessCode.toString(), it.generateAccessCode())

                it.sendAccessCodeToUser(it.login, it.accessCode.toString())
            }
        }
    }

    fun importUsers(list: List<String>): List{
        return list
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder() {
        map.clear()
    }
}