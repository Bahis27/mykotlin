package my.kotlin.mykotlin.web.user

import my.kotlin.mykotlin.repository.UserRepository
import my.kotlin.mykotlin.to.UserTo
import my.kotlin.mykotlin.util.JsonUtil
import my.kotlin.mykotlin.util.UsersUtil
import my.kotlin.mykotlin.web.AbstractControllerTest
import my.kotlin.mykotlin.web.user.ProfileController.Companion.REST_URL
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class ProfileControllerTest @Autowired constructor(
    private val repository: UserRepository

) : AbstractControllerTest() {

    @Test
    @WithUserDetails(value = USER_MAIL)
    fun get() {
        perform(MockMvcRequestBuilders.get(REST_URL))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(USER_MATCHER.contentJson(user))
    }

    @Test
    fun getUnauthorized() {
        perform(MockMvcRequestBuilders.get(REST_URL))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    fun delete() {
        perform(MockMvcRequestBuilders.delete(REST_URL))
            .andExpect(MockMvcResultMatchers.status().isNoContent)
        USER_MATCHER.assertMatch(repository.findAll(), admin, guest)
    }

    @Test
    fun register() {
        val newTo = UserTo(null, "newName", "newemail@ya.ru", "newPassword")
        val newUser = UsersUtil.createNewFromTo(newTo)
        val action = perform(
            MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newTo))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isCreated)
        val created = USER_MATCHER.readFromJson(action)
        val newId = created.id()
        newUser.id = newId
        USER_MATCHER.assertMatch(created, newUser)
        USER_MATCHER.assertMatch(repository.getExisted(newId), newUser)
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    fun update() {
        val updatedTo = UserTo(null, "newName", USER_MAIL, "newPassword")
        perform(
            MockMvcRequestBuilders.put(REST_URL).contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isNoContent)
        USER_MATCHER.assertMatch(repository.getExisted(USER_ID), UsersUtil.updateFromTo(user.copy(), updatedTo))
    }

    @Test
    fun registerInvalid() {
        val newTo = UserTo(null, "", "", "")
        perform(
            MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newTo))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity)
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    fun updateInvalid() {
        val updatedTo = UserTo(null, "", "password", "")
        perform(
            MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity)
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    fun updateDuplicate() {
        val updatedTo = UserTo(null, "newName", ADMIN_MAIL, "newPassword")
        perform(
            MockMvcRequestBuilders.put(REST_URL).contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity)
            .andExpect(
                MockMvcResultMatchers.content()
                    .string(Matchers.containsString(UniqueMailValidator.EXCEPTION_DUPLICATE_EMAIL))
            )
    }
}