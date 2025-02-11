package my.kotlin.mykotlin.web.user

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import my.kotlin.mykotlin.model.User
import my.kotlin.mykotlin.to.UserTo
import my.kotlin.mykotlin.util.UsersUtil
import my.kotlin.mykotlin.web.AuthUser
import my.kotlin.mykotlin.web.RestValidation.assureIdConsistent
import my.kotlin.mykotlin.web.RestValidation.checkNew

@RestController
@RequestMapping(value = [ProfileController.REST_URL], produces = [MediaType.APPLICATION_JSON_VALUE])
class ProfileController : AbstractUserController() {
    companion object {
        const val REST_URL = "/api/profile"
    }

    @GetMapping
    fun get(@AuthenticationPrincipal authUser: AuthUser): User {
        log.info { "get $authUser" }
        return authUser.user
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@AuthenticationPrincipal authUser: AuthUser) = super.delete(authUser.id())

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody @Valid userTo: UserTo): ResponseEntity<User> {
        log.info { "register $userTo" }
        checkNew(userTo)
        val created = repository.prepareAndSave(UsersUtil.createNewFromTo(userTo))
        val uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath().path(REST_URL).build().toUri()
        return ResponseEntity.created(uriOfNewResource).body(created)
    }

    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    fun update(@RequestBody @Valid userTo: UserTo, @AuthenticationPrincipal authUser: AuthUser) {
        log.info { "update $userTo with id=${authUser.id()}" }
        assureIdConsistent(userTo, authUser.id())
        repository.prepareAndSave(UsersUtil.updateFromTo(authUser.user, userTo))
    }
}