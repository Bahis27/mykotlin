package my.kotlin.mykotlin.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import my.kotlin.mykotlin.HasIdAndEmail
import my.kotlin.mykotlin.validation.NoHtml
import java.util.*

@Entity
@Table(name = "users")
class User(
    id: Int?,
    name: String = "",

    @field:NotBlank
    @field:Size(min = 2, max = 128)
    @field:NoHtml
    @field:Column(name = "email", nullable = false, unique = true)
    override var email: String = "",

    @field:NotBlank
    @field:Size(max = 128)
    @field:Column(name = "password", nullable = false)
    @field:JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // https://stackoverflow.com/a/12505165/548473
    var password: String = "",

    @field:Column(name = "enabled", nullable = false, columnDefinition = "bool default true")
    var enabled: Boolean = true,

    @field:Column(name = "registered", nullable = false, columnDefinition = "timestamp default now()", updatable = false)
    @field:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val registered: Date = Date(),

    @field:Enumerated(EnumType.STRING)
    @field:CollectionTable(
        name = "user_role",
        joinColumns = [JoinColumn(name = "user_id")],
        uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "role"], name = "uk_user_role")]
    )
    @field:ElementCollection(fetch = FetchType.EAGER)
    @field:Column(name = "role")
    var roles: MutableSet<Role> = EnumSet.noneOf(Role::class.java)

) : NamedEntity(id, name), HasIdAndEmail {

    constructor(
        id: Int?,
        name: String,
        email: String,
        password: String,
        vararg roles: Role
    ) : this(
        id = id,
        name = name,
        email = email,
        password = password,
        enabled = true,
        registered = Date(),
        roles = EnumSet.copyOf(setOf(*roles))
    )

    fun copy(
        id: Int? = this.id,
        name: String = this.name,
        email: String = this.email,
        password: String = this.password,
        enabled: Boolean = this.enabled,
        registered: Date = this.registered,
        roles: MutableSet<Role> = this.roles
    ) = User(id, name, email, password, enabled, registered, roles)

    fun setRoles(roles: Collection<Role>) {
        this.roles = if (roles.isEmpty()) EnumSet.noneOf(Role::class.java) else EnumSet.copyOf(roles)
    }

    fun hasRole(role: Role) = roles.contains(role)

    override fun toString() = "User:$id[$email]"
}