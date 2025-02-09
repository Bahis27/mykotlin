package my.kotlin.mykotlin.validation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import org.jsoup.Jsoup
import org.jsoup.safety.Safelist
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [NoHtmlValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class NoHtml(
    val message: String = "{error.noHtml}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class NoHtmlValidator : ConstraintValidator<NoHtml, String?> {
    override fun isValid(value: String?, ctx: ConstraintValidatorContext) =
        value == null || Jsoup.isValid(value, Safelist.none())
}
