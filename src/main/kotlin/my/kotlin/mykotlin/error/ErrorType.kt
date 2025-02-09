package my.kotlin.mykotlin.error

import org.springframework.http.HttpStatus

enum class ErrorType(val title: String, val status: HttpStatus) {
    APP_ERROR("Application error", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_DATA("Wrong data", HttpStatus.UNPROCESSABLE_ENTITY),
    BAD_REQUEST("Bad request", HttpStatus.UNPROCESSABLE_ENTITY),
    DATA_CONFLICT("DataBase conflict", HttpStatus.CONFLICT),
    NOT_FOUND("Resource not found", HttpStatus.NOT_FOUND),
    AUTH_ERROR("Authorization error", HttpStatus.FORBIDDEN),
    UNAUTHORIZED("Request unauthorized", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("Request forbidden", HttpStatus.FORBIDDEN)
}
