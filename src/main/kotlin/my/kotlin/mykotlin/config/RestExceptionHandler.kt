package my.kotlin.mykotlin.config

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.persistence.EntityNotFoundException
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ValidationException
import my.kotlin.mykotlin.error.AppException
import my.kotlin.mykotlin.error.ErrorType
import my.kotlin.mykotlin.error.ErrorType.*
import my.kotlin.mykotlin.getRootCause
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.ProblemDetail
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.firewall.RequestRejectedException
import org.springframework.validation.BindException
import org.springframework.web.ErrorResponse
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.ServletRequestBindingException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException
import java.io.FileNotFoundException
import java.net.URI
import java.nio.file.AccessDeniedException
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

const val ERR_PFX = "ERR#"

// more specific first
private val HTTP_STATUS_MAP: Map<KClass<out Exception>, ErrorType> = linkedMapOf(
    AppException.NotFoundException::class to NOT_FOUND,
    AuthenticationException::class to UNAUTHORIZED,
    FileNotFoundException::class to NOT_FOUND,
    NoHandlerFoundException::class to NOT_FOUND,
    AppException.DataConflictException::class to DATA_CONFLICT,
    AppException.IllegalRequestDataException::class to BAD_REQUEST,
    AppException::class to APP_ERROR,
    UnsupportedOperationException::class to APP_ERROR,
    EntityNotFoundException::class to DATA_CONFLICT,
    DataIntegrityViolationException::class to DATA_CONFLICT,
    IllegalArgumentException::class to BAD_DATA,
    ValidationException::class to BAD_REQUEST,
    HttpRequestMethodNotSupportedException::class to BAD_REQUEST,
    ServletRequestBindingException::class to BAD_REQUEST,
    RequestRejectedException::class to BAD_REQUEST,
    AccessDeniedException::class to FORBIDDEN,
)

private fun Map<KClass<out Exception>, ErrorType>.findErrorType(kClass: KClass<out Exception>) =
    entries.firstOrNull { kClass.isSubclassOf(it.key) }?.value


@RestControllerAdvice
class RestExceptionHandler(
    private val messageSource: MessageSource

) {
    private val log = KotlinLogging.logger {}

    @ExceptionHandler(BindException::class)
    fun bindException(ex: BindException, request: HttpServletRequest): ProblemDetail {
        val invalidParams = ex.bindingResult.getErrorMap(messageSource)
        val path = request.requestURI
        log.warn { "$ERR_PFX BindException with invalidParams $invalidParams at request $path" }
        return createProblemDetail(
            ex = ex,
            path = path,
            type = BAD_REQUEST,
            defaultDetail = "BindException",
            additionalParams = java.util.Map.of<String, Any>("invalid_params", invalidParams)
        )
    }

    @ExceptionHandler(Exception::class)
    fun exception(ex: Exception, request: HttpServletRequest): ProblemDetail = processException(ex, request, mapOf())

    fun processException(
        ex: Exception, request: HttpServletRequest, additionalParams: Map<String, Any>
    ): ProblemDetail {
        val path = request.requestURI
        val errorType = HTTP_STATUS_MAP.findErrorType(ex::class)
        return if (errorType != null) {
            log.error { "$ERR_PFX Exception $ex at request $path" }
            createProblemDetail(ex, path, errorType, ex.message!!, additionalParams)
        } else {
            val root = ex.getRootCause()
            log.error(root) { "$ERR_PFX Exception $root at request $path" }
            createProblemDetail(ex, path, APP_ERROR, "Exception ${root.javaClass.simpleName}", additionalParams)
        }
    }

    //    https://datatracker.ietf.org/doc/html/rfc7807
    private fun createProblemDetail(
        ex: Exception,
        path: String,
        type: ErrorType,
        defaultDetail: String,
        additionalParams: Map<String, Any>
    ): ProblemDetail = ErrorResponse.builder(ex, type.status, defaultDetail)
        .title(type.title)
        .instance(URI.create(path))
        .build()
        .updateAndGetBody(messageSource, LocaleContextHolder.getLocale())
        .apply {
            additionalParams.forEach { (name: String, value: Any) -> this.setProperty(name, value) }
        }
}
