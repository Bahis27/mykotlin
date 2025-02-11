package my.kotlin.mykotlin

import org.springframework.core.NestedExceptionUtils

fun Throwable.getRootCause(): Throwable = NestedExceptionUtils.getRootCause(this) ?: this