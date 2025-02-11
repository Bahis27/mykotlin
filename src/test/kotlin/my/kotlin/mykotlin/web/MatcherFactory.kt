package my.kotlin.mykotlin.web

import org.assertj.core.api.Assertions
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.ResultMatcher
import my.kotlin.mykotlin.util.JsonUtil
import java.util.function.BiConsumer
import kotlin.reflect.KClass

/**
 * Factory for creating test matchers.
 *
 * Comparing actual and expected objects via AssertJ
 * Support converting json MvcResult to objects for comparation.
 */

object MatcherFactory {
    fun <T : Any> usingAssertions(
        clazz: KClass<T>,
        assertion: BiConsumer<T, T>,
        iterableAssertion: BiConsumer<Iterable<T>, Iterable<T>>
    ): Matcher<T> = Matcher(clazz, assertion, iterableAssertion)

    fun <T : Any> usingEqualsComparator(clazz: KClass<T>) = usingAssertions(
        clazz,
        { a: T, e: T -> Assertions.assertThat(a).isEqualTo(e) },
        { a: Iterable<T>?, e: Iterable<T>? -> Assertions.assertThat(a).isEqualTo(e) })

    fun <T : Any> usingIgnoringFieldsComparator(clazz: KClass<T>, vararg fieldsToIgnore: String) = usingAssertions(
        clazz,
        { a: T, e: T -> Assertions.assertThat(a).usingRecursiveComparison().ignoringFields(*fieldsToIgnore).isEqualTo(e) },
        { a: Iterable<T>?, e: Iterable<T>? ->
            Assertions.assertThat(a).usingRecursiveFieldByFieldElementComparatorIgnoringFields(*fieldsToIgnore).isEqualTo(e)
        })

    class Matcher<T : Any> internal constructor(
        private val kClass: KClass<T>,
        private val assertion: BiConsumer<T, T>,
        private val iterableAssertion: BiConsumer<Iterable<T>, Iterable<T>>

    ) {
        fun assertMatch(actual: T, expected: T): Unit = assertion.accept(actual, expected)

        fun assertMatch(actual: Iterable<T>, vararg expected: T): Unit = assertMatch(actual, listOf(*expected))

        fun assertMatch(actual: Iterable<T>, expected: Iterable<T>): Unit = iterableAssertion.accept(actual, expected)

        fun contentJson(expected: T): ResultMatcher =
            ResultMatcher { result -> assertMatch(JsonUtil.readValue(getContent(result), kClass), expected) }

        fun contentJson(vararg expected: T): ResultMatcher = contentJson(listOf(*expected))

        fun contentJson(expected: Iterable<T>): ResultMatcher =
            ResultMatcher { result -> assertMatch(JsonUtil.readValues(getContent(result), kClass), expected) }

        fun readFromJson(action: ResultActions): T = JsonUtil.readValue(getContent(action.andReturn()), kClass)

        private fun getContent(result: MvcResult): String = result.response.contentAsString
    }
}
