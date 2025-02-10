package my.kotlin.mykotlin.web

import my.kotlin.mykotlin.HasId
import my.kotlin.mykotlin.error.AppException

object RestValidation {
    fun checkNew(bean: HasId) {
        if (!bean.isNew()) throw AppException.IllegalRequestDataException("${bean.javaClass.simpleName} must be new (id=null)")
    }

    //  Conservative when you reply, but accept liberally (http://stackoverflow.com/a/32728226/548473)
    fun assureIdConsistent(bean: HasId, id: Int) = when {
        bean.isNew() -> bean.id = id
        bean.id() != id -> throw AppException.IllegalRequestDataException("${bean.javaClass.simpleName} must has id=$id")
        else -> {}
    }
}