package my.kotlin.mykotlin.web

import my.kotlin.mykotlin.model.Role
import my.kotlin.mykotlin.model.User
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User as SecurityUser

fun safeAuthUser(): AuthUser? {
    val auth = SecurityContextHolder.getContext().authentication ?: return null
    val principal = auth.principal
    return if (principal is AuthUser) principal else null
}

fun authUser(): AuthUser = checkNotNull(safeAuthUser()) { "No authorized user found" }

class AuthUser(var user: User) : SecurityUser(user.email, user.password, user.roles) {
    fun id() = user.id()
    fun hasRole(role: Role) = user.hasRole(role)

    override fun toString() = "AuthUser:${user.id}[${user.email}]"
}