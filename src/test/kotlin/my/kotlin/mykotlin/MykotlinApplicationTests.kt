package my.kotlin.mykotlin

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MykotlinApplicationTests {

    @Test
    fun contextLoads() {

    }

    @Test
    fun foo() {
        listOf(1, 2, 3, 4, 5).forEach lit@{
            if (it == 3) return@lit // локальный возврат внутри лямбды, то есть к циклу forEach
            print(it)
        }
        print(" выполнится с использованием явной метки(lit@)")
    }
}
