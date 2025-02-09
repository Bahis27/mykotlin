package my.kotlin.mykotlin.model

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import my.kotlin.mykotlin.model.BaseEntity
import my.kotlin.mykotlin.validation.NoHtml

@MappedSuperclass
abstract class NamedEntity protected constructor(
    id: Int? = null,

    @field:NotBlank
    @field:Size(min = 2, max = 128)
    @field:NoHtml
    @field:Column(name = "name", nullable = false)
    var name: String = ""

) : BaseEntity(id) {
    override fun toString() = "${super.toString()}[$name]"
}