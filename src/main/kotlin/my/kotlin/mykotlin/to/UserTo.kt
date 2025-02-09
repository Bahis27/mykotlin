package my.kotlin.mykotlin.to

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import my.kotlin.mykotlin.HasIdAndEmail
import my.kotlin.mykotlin.to.NamedTo
import my.kotlin.mykotlin.validation.NoHtml

data class UserTo(
    override var id: Int? = null,
    override var name: String = "",

    @field:NotBlank
    @field:Size(max = 128)
    @field:Email
    @field:NoHtml // https://stackoverflow.com/questions/17480809
    override var email: String = "",

    @field:NotBlank
    @field:Size(min = 5, max = 32)
    var password: String = ""

) : NamedTo(id, name), HasIdAndEmail {
    override fun toString() = "UserTo:$id[$email]";
}
