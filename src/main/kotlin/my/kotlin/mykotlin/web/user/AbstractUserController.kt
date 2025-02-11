package my.kotlin.mykotlin.web.user

import io.github.oshai.kotlinlogging.KotlinLogging
import my.kotlin.mykotlin.model.User
import my.kotlin.mykotlin.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.InitBinder

abstract class AbstractUserController {
    val log = KotlinLogging.logger {}

    @Autowired
    protected lateinit var repository: UserRepository

    @Autowired
    private lateinit var emailValidator: UniqueMailValidator

    @InitBinder
    open fun initBinder(binder: WebDataBinder): Unit = binder.addValidators(emailValidator)

    open operator fun get(id: Int): User {
        log.info { "get $id" }
        return repository.getExisted(id)
    }

    open fun delete(id: Int) {
        log.info { "delete $id" }
        repository.deleteExisted(id)
    }
}