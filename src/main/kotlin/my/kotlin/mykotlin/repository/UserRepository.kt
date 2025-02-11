package my.kotlin.mykotlin.repository

import my.kotlin.mykotlin.config.PASSWORD_ENCODER
import my.kotlin.mykotlin.error.AppException
import my.kotlin.mykotlin.model.User
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
interface UserRepository : BaseRepository<User> {
    @Query("SELECT u FROM User u WHERE u.email = LOWER(:email)")
    fun findByEmailIgnoreCase(email: String): User?

    @Transactional
    fun prepareAndSave(user: User): User = with(user) {
        password = PASSWORD_ENCODER.encode(password)
        email = email.lowercase()
        save(this)
    }

    fun getExistedByEmail(email: String): User =
        findByEmailIgnoreCase(email) ?: throw AppException.NotFoundException("User with email=$email not found")
}