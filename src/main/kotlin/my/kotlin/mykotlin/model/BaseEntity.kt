package my.kotlin.mykotlin.model

import jakarta.persistence.*
import my.kotlin.mykotlin.HasId
import org.springframework.data.util.ProxyUtils

@MappedSuperclass //  https://stackoverflow.com/a/6084701/548473
@Access(AccessType.FIELD)
abstract class BaseEntity(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Int? = null

) : HasId {
    //    https://stackoverflow.com/questions/1638723
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != ProxyUtils.getUserClass(other)) return false
        val that = other as BaseEntity
        return id != null && id == that.id
    }

    override fun hashCode() = id ?: 0
    override fun toString() = "${javaClass.simpleName}:$id"
}