package my.kotlin.mykotlin.web.user

import my.kotlin.mykotlin.model.Role
import my.kotlin.mykotlin.model.User
import my.kotlin.mykotlin.repository.UserRepository
import my.kotlin.mykotlin.web.AbstractControllerTest
import my.kotlin.mykotlin.web.user.AdminUserController.Companion.REST_URL
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

private const val REST_URL_SLASH: String = "${REST_URL}/"

class AdminUserControllerTest @Autowired constructor(
    private val repository: UserRepository

) : AbstractControllerTest() {

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    fun get() {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + ADMIN_ID))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(USER_MATCHER.contentJson(admin))
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    fun getNotFound() {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + NOT_FOUND))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    fun getByEmail() {
        perform(MockMvcRequestBuilders.get("${REST_URL_SLASH}by-email?email=${admin.email}"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(USER_MATCHER.contentJson(admin))
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    fun delete() {
        perform(MockMvcRequestBuilders.delete(REST_URL_SLASH + USER_ID))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isNoContent)
        Assertions.assertFalse(repository.findById(USER_ID).isPresent)
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    fun deleteNotFound() {
        perform(MockMvcRequestBuilders.delete(REST_URL_SLASH + NOT_FOUND))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    fun enableNotFound() {
        perform(
            MockMvcRequestBuilders.patch(REST_URL_SLASH + NOT_FOUND)
                .param("enabled", "false")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun getUnauthorized() {
        perform(MockMvcRequestBuilders.get(REST_URL))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    fun getForbidden() {
        perform(MockMvcRequestBuilders.get(REST_URL))
            .andExpect(MockMvcResultMatchers.status().isForbidden)
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    fun update() {
        val updated = getUpdated()
        updated.id = null
        perform(
            MockMvcRequestBuilders.put(REST_URL_SLASH + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(updated, "newPass"))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isNoContent)
        USER_MATCHER.assertMatch(repository.getExisted(USER_ID), getUpdated())
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    fun createWithLocation() {
        val newUser = getNew()
        val action = perform(
            MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(newUser, "newPass"))
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
        val created = USER_MATCHER.readFromJson(action)
        val newId = created.id()
        newUser.id = newId
        USER_MATCHER.assertMatch(created, newUser)
        USER_MATCHER.assertMatch(repository.getExisted(newId), newUser)
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    fun getAll() {
        perform(MockMvcRequestBuilders.get(REST_URL))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(USER_MATCHER.contentJson(admin, guest, user))
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    fun enable() {
        perform(
            MockMvcRequestBuilders.patch(REST_URL_SLASH + USER_ID)
                .param("enabled", "false")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isNoContent)
        Assertions.assertFalse(repository.getExisted(USER_ID).enabled)
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    fun createInvalid() {
        val invalid = User(null, "", "", "newPass", Role.USER, Role.ADMIN)
        perform(
            MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(invalid, "newPass"))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity)
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    fun updateInvalid() {
        val invalid = user.copy(name = "")
        perform(
            MockMvcRequestBuilders.put(REST_URL_SLASH + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(invalid, "password"))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity)
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    fun updateHtmlUnsafe() {
        val updated = user.copy(name = "<script>alert(123)</script>")
        perform(
            MockMvcRequestBuilders.put(REST_URL_SLASH + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(updated, "password"))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity)
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    @WithUserDetails(value = ADMIN_MAIL)
    fun updateDuplicate() {
        val updated = user.copy(email = ADMIN_MAIL)
        perform(
            MockMvcRequestBuilders.put(REST_URL_SLASH + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(updated, "password"))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity)
            .andExpect(
                MockMvcResultMatchers.content()
                    .string(Matchers.containsString(UniqueMailValidator.EXCEPTION_DUPLICATE_EMAIL))
            )
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    @WithUserDetails(value = ADMIN_MAIL)
    fun createDuplicate() {
        val expected = User(null, "New", USER_MAIL, "newPass", Role.USER, Role.ADMIN)
        perform(
            MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(expected, "newPass"))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity)
            .andExpect(
                MockMvcResultMatchers.content()
                    .string(Matchers.containsString(UniqueMailValidator.EXCEPTION_DUPLICATE_EMAIL))
            )
    }
}