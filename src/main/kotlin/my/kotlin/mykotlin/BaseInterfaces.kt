package my.kotlin.mykotlin

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.util.Assert


interface HasId {
    @get:Schema(accessMode = Schema.AccessMode.READ_ONLY) // https://stackoverflow.com/a/28025008/548473
    var id: Int?

    @JsonIgnore
    fun isNew() = id == null

    // doesn't work for hibernate lazy proxy
    fun id(): Int {
        Assert.notNull(id, "Entity must has id")
        return id!!
    }
}


interface HasIdAndEmail : HasId {
    var email: String
}