package my.kotlin.mykotlin.repository

import my.kotlin.mykotlin.error.AppException
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.transaction.annotation.Transactional

// https://stackoverflow.com/questions/42781264/multiple-base-repositories-in-spring-data-jpa
@NoRepositoryBean
@JvmDefaultWithCompatibility
interface BaseRepository<T> : JpaRepository<T, Int?> {
    //    https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query.spel-expressions
    @Transactional
    @Modifying
    @Query("DELETE FROM #{#entityName} e WHERE e.id=:id")
    fun delete(id: Int): Int

    //  https://stackoverflow.com/a/60695301/548473 (existed delete code 204, not existed: 404)
    fun deleteExisted(id: Int) {
        if (delete(id) == 0) throw AppException.NotFoundException("Entity with id=$id not found")
    }

    fun getExisted(id: Int): T =
        findById(id).orElseThrow { AppException.NotFoundException("Entity with id=$id not found") }
}