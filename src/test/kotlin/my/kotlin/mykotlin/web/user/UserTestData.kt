package my.kotlin.mykotlin.web.user

import my.kotlin.mykotlin.model.Role
import my.kotlin.mykotlin.model.User
import my.kotlin.mykotlin.util.JsonUtil
import my.kotlin.mykotlin.web.MatcherFactory
import java.util.*

val USER_MATCHER: MatcherFactory.Matcher<User> = MatcherFactory.usingIgnoringFieldsComparator(User::class, "registered", "password")
const val USER_ID = 1
const val ADMIN_ID = 2
const val GUEST_ID = 3
const val NOT_FOUND = 100
const val USER_MAIL = "user@yandex.ru"
const val ADMIN_MAIL = "admin@gmail.com"
const val GUEST_MAIL = "guest@gmail.com"
val user: User = User(USER_ID, "User", USER_MAIL, "password", Role.USER)
val admin: User = User(ADMIN_ID, "Admin", ADMIN_MAIL, "admin", Role.ADMIN, Role.USER)
val guest: User = User(GUEST_ID, "Guest", GUEST_MAIL, "guest")

fun getNew() = User(null, "New", "new@gmail.com", "newPass", false, Date(), EnumSet.of(Role.USER))
fun getUpdated() = User(USER_ID, "UpdatedName", USER_MAIL, "newPass", false, Date(), EnumSet.of(Role.ADMIN))
fun jsonWithPassword(user: User, passw: String) = JsonUtil.writeAdditionProps(user, "password", passw)

