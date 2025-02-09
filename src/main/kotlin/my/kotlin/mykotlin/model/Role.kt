package my.kotlin.mykotlin.model

import org.springframework.security.core.GrantedAuthority

enum class Role : GrantedAuthority {
    USER,
    ADMIN;

    //   https://stackoverflow.com/a/19542316/548473
    override fun getAuthority(): String = "ROLE_$name";
}