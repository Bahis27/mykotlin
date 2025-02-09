package my.kotlin.mykotlin.to

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import my.kotlin.mykotlin.HasId
import my.kotlin.mykotlin.validation.NoHtml

abstract class BaseTo protected constructor(
    override var id: Int? = null

) : HasId {
    override fun toString() = "${javaClass.simpleName}:$id"
}

open class NamedTo(
    id: Int? = null,

    @field:NoHtml
    @field:Size(min = 2, max = 128)
    @field:NotBlank
    open var name: String = ""

) : BaseTo(id) {
    override fun toString() = "${super.toString()}[$name]"
}