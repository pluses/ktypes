package cloud.pluses.ktypes

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.typeOf

annotation class Ann(val type: KClass<*>)

private object StringMap : TypeCapture<Map<String, String?>>

@Ann(type = StringMap::class)
class Foo

@ExperimentalStdlibApi
class TypeCaptureTest : FunSpec({

    @Suppress("UNCHECKED_CAST")
    test("capture") {
        val type = Foo::class.findAnnotation<Ann>()?.type

        if (type != null && type.isSubclassOf(TypeCapture::class)) {
            (type as KClass<TypeCapture<*>>).capture() shouldBe typeOf<Map<String, String?>>()
        }
    }
})
