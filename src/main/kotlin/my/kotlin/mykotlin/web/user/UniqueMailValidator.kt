package my.kotlin.mykotlin.web.user

import jakarta.servlet.http.HttpServletRequest
import my.kotlin.mykotlin.HasIdAndEmail
import my.kotlin.mykotlin.model.User
import my.kotlin.mykotlin.repository.UserRepository
import my.kotlin.mykotlin.web.authUser
import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.Validator

@Component
class UniqueMailValidator(
    private val repository: UserRepository,
    private val request: HttpServletRequest

) : Validator {
    override fun supports(clazz: Class<*>) = HasIdAndEmail::class.java.isAssignableFrom(clazz)

    override fun validate(target: Any, errors: Errors) {
        val user: HasIdAndEmail = target as HasIdAndEmail
        if (user.email.isBlank()) return
        repository.findByEmailIgnoreCase(user.email)?.also { dbUser: User ->
            if (request.method == "PUT") {  // UPDATE
                val dbId: Int = dbUser.id()

                // it is ok, if update ourselves
                if (user.id != null && dbId == user.id) return@also

                // Workaround for update with user.id=null in request body
                // ValidationUtil.assureIdConsistent called after this validation
                val requestURI = request.requestURI
                if (requestURI.endsWith("/$dbId") || dbId == authUser().id() && requestURI.contains("/profile")) return@also
            }
            errors.rejectValue("email", "", EXCEPTION_DUPLICATE_EMAIL)
        }
    }

    companion object {
        const val EXCEPTION_DUPLICATE_EMAIL = "User with this email already exists"
    }
}
