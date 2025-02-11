package my.kotlin.mykotlin.config

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.validation.BindingResult
import org.springframework.validation.ObjectError

fun BindingResult.getErrorMap(messageSource: MessageSource): Map<String, String?> {
    val invalidParams = linkedMapOf<String, String?>()
    for (error in globalErrors) {
        invalidParams[error.objectName] = error.getErrorMessage(messageSource)
    }
    for (error in fieldErrors) {
        invalidParams[error.field] = error.getErrorMessage(messageSource)
    }
    return invalidParams
}

fun ObjectError.getErrorMessage(messageSource: MessageSource) = when (code) {
    null -> defaultMessage
    else -> messageSource.getMessage(code!!, arguments, defaultMessage, LocaleContextHolder.getLocale())
}