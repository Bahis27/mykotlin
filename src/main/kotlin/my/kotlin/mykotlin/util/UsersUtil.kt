package my.kotlin.mykotlin.util

import my.kotlin.mykotlin.model.Role
import my.kotlin.mykotlin.model.User
import my.kotlin.mykotlin.to.UserTo

object UsersUtil {
    fun createNewFromTo(userTo: UserTo) = User(null, userTo.name, userTo.email.lowercase(), userTo.password, Role.USER)

    fun updateFromTo(user: User, userTo: UserTo): User = with(user) {
        name = userTo.name
        email = userTo.email.lowercase()
        password = userTo.password
        this
    }
}