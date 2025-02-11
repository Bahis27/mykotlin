package my.kotlin.mykotlin.web.user

import jakarta.validation.Valid
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import my.kotlin.mykotlin.model.User
import my.kotlin.mykotlin.web.RestValidation.assureIdConsistent
import my.kotlin.mykotlin.web.RestValidation.checkNew

@RestController
@RequestMapping(value = [AdminUserController.REST_URL], produces = [MediaType.APPLICATION_JSON_VALUE])

class AdminUserController : AbstractUserController() {
    companion object {
        const val REST_URL = "/api/admin/users"
    }

    @GetMapping("/{id}")
    override fun get(@PathVariable id: Int) = super.get(id)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun delete(@PathVariable id: Int) = super.delete(id)

    @GetMapping
    fun getAll(): List<User> {
        log.info { "getAll" }
        return repository.findAll(Sort.by(Sort.Direction.ASC, "name", "email"))
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createWithLocation(@RequestBody @Valid user: User): ResponseEntity<User> {
        log.info { "create $user" }
        checkNew(user)
        val created: User = repository.prepareAndSave(user)
        val uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("$REST_URL/{id}")
            .buildAndExpand(created.id).toUri()
        return ResponseEntity.created(uriOfNewResource).body(created)
    }

    @PutMapping(value = ["/{id}"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@RequestBody @Valid user: User, @PathVariable id: Int) {
        log.info { "update $user with id=$id" }
        assureIdConsistent(user, id)
        repository.prepareAndSave(user)
    }

    @GetMapping("/by-email")
    fun getByEmail(@RequestParam email: String): User {
        log.info { "getByEmail $email" }
        return repository.getExistedByEmail(email)
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    fun enable(@PathVariable id: Int, @RequestParam enabled: Boolean) {
        log.info { if (enabled) "enable $id" else "disable $id" }
        val user = repository.getExisted(id)
        user.enabled = enabled
    }
}