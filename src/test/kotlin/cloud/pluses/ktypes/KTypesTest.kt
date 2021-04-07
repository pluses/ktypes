package cloud.pluses.ktypes

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.startWith
import java.util.*
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.typeOf

interface Bar1 : Map<String, Int?>
interface Foo1 : Bar1

interface Bar2 : Map<String?, Int>
interface Foo2 : Bar2

interface Bar3<T> : Map<T, Int?>
interface Foo3 : Bar3<String>

interface Bar4<T> : Map<String, T>
interface Foo4 : Bar4<Int?>

interface Bar5<T> : Map<String, T?>
interface Foo5 : Bar5<Int>

interface Bar6<T> : Map<String, T?>
interface Foo6 : Bar6<Int?>

interface Bar7<T : CharSequence> : Map<T, Int?>
interface Foo7 : Bar7<String>

interface Bar8<T : Number?> : Map<String, T>
interface Foo8 : Bar8<Int?>

interface Bar9<T : Number> : Map<String, T?>
interface Foo9 : Bar9<Int>

interface Bar10<T : Number?> : Map<String, T?>
interface Foo10 : Bar10<Int?>

interface Bar11<T1, T2> : Map<T1, T2?>
interface Foo11 : Bar11<String, Int>

interface Bar12<T1, T2> : Map<T2, T1?>
interface Foo12 : Bar12<String?, Int>

interface Bar13<T1, T2> : Map<T1, T2?>
interface Foo13 : Bar13<String, Int?>, Map<String, Int?>

interface Bar14<T1, T2> : Map<T1, Map<T1?, List<T2>?>>
interface Foo14 : Bar14<String, Int?>

interface Bar15<T1, T2> : Map<T1, Map<out T1?, out List<out T2>?>>
interface Foo15 : Bar15<String, Int?>

interface Bar16<T1, T2> : Map<T1, T2?>
interface Foo16<T1, T2> : Bar16<T1, T2>

interface Bar17<T1, T2> : Map<T1, T2?>
interface Foo17<T1, T2> : Bar17<T1?, T2?>

interface Bar18<T1, T2> : Map<T1, T2?>
interface Foo18<T1 : CharSequence, T2 : Number?> : Bar18<T1, T2>

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class KTypesTest : FunSpec({

    test("arrayComponentType") {
        table(
            headers("type", "expected arrayComponentType"),
            row(typeOf<IntArray>(), typeOf<Int>()),
            row(typeOf<UIntArray?>(), typeOf<UInt>()),
            row(typeOf<Array<String?>>(), typeOf<String?>()),
            row(typeOf<Array<String>?>(), typeOf<String>()),
            row(typeOf<Array<Array<String?>>>(), typeOf<Array<String?>>()),
            row(typeOf<Array<List<String>>>(), typeOf<List<String>>()),
            row(typeOf<Array<Array<*>>>(), typeOf<Array<*>>()),
            row(typeOf<Array<List<*>?>>(), typeOf<List<*>?>())
        ).forAll { type, expectedArrayComponentType ->
            type.arrayComponentType shouldBe expectedArrayComponentType
        }

        shouldThrow<IllegalArgumentException> { typeOf<List<String>>().arrayComponentType }.let {
            it.message should startWith("Not an array type")
        }
        shouldThrow<IllegalStateException> { typeOf<Array<*>>().arrayComponentType }.let {
            it.message should startWith("Could not get array component type")
        }
    }

    test("collectionElementType") {
        table(
            headers("type", "expected collectionElementType"),
            row(typeOf<LinkedHashSet<String?>>(), typeOf<String?>()),
            row(typeOf<LinkedHashSet<String>?>(), typeOf<String>()),
            row(typeOf<LinkedHashSet<Array<String?>>>(), typeOf<Array<String?>>()),
            row(typeOf<LinkedHashSet<IntArray>>(), typeOf<IntArray>()),
            row(typeOf<LinkedHashSet<List<String>>>(), typeOf<List<String>>()),
            row(typeOf<LinkedHashSet<Array<*>>>(), typeOf<Array<*>>()),
            row(typeOf<LinkedHashSet<List<*>?>>(), typeOf<List<*>?>())
        ).forAll { type, expectedCollectionElementType ->
            type.collectionElementType shouldBe expectedCollectionElementType
        }

        shouldThrow<IllegalArgumentException> { typeOf<Array<String>>().collectionElementType }.let {
            it.message should startWith("Not an collection type")
        }
        shouldThrow<IllegalStateException> { typeOf<LinkedHashSet<*>>().collectionElementType }.let {
            it.message should startWith("Could not get collection element type")
        }
    }

    test("mapKeyType") {
        table(
            headers("type", "expected mapKeyType"),
            row(typeOf<LinkedHashMap<String, Int?>>(), typeOf<String>()),
            row(typeOf<LinkedHashMap<String?, Int>?>(), typeOf<String?>()),
            row(typeOf<LinkedHashMap<Array<String?>, Array<Int>>>(), typeOf<Array<String?>>()),
            row(typeOf<LinkedHashMap<IntArray, UIntArray>>(), typeOf<IntArray>()),
            row(typeOf<LinkedHashMap<List<String>, List<Int?>>>(), typeOf<List<String>>()),
            row(typeOf<LinkedHashMap<Array<*>, List<*>>>(), typeOf<Array<*>>()),
            row(typeOf<LinkedHashMap<List<*>?, Array<*>?>>(), typeOf<List<*>?>())
        ).forAll { type, expectedMapKeyType ->
            type.mapKeyType shouldBe expectedMapKeyType
        }

        shouldThrow<IllegalArgumentException> { typeOf<Set<String>>().mapKeyType }.let {
            it.message should startWith("Not an map type")
        }
        shouldThrow<IllegalStateException> { typeOf<HashMap<*, *>>().mapKeyType }.let {
            it.message should startWith("Could not get map key type")
        }
    }

    test("mapValueType") {
        table(
            headers("type", "expected mapValueType"),
            row(typeOf<LinkedHashMap<String, Int?>>(), typeOf<Int?>()),
            row(typeOf<LinkedHashMap<String?, Int>?>(), typeOf<Int>()),
            row(typeOf<LinkedHashMap<Array<String?>, Array<Int>>>(), typeOf<Array<Int>>()),
            row(typeOf<LinkedHashMap<IntArray, UIntArray>>(), typeOf<UIntArray>()),
            row(typeOf<LinkedHashMap<List<String>, List<Int?>>>(), typeOf<List<Int?>>()),
            row(typeOf<LinkedHashMap<Array<*>, List<*>>>(), typeOf<List<*>>()),
            row(typeOf<LinkedHashMap<List<*>?, Array<*>?>>(), typeOf<Array<*>?>())
        ).forAll { type, expectedMapValueType ->
            type.mapValueType shouldBe expectedMapValueType
        }

        shouldThrow<IllegalArgumentException> { typeOf<Set<String>>().mapValueType }.let {
            it.message should startWith("Not an map type")
        }
        shouldThrow<IllegalStateException> { typeOf<HashMap<*, *>>().mapValueType }.let {
            it.message should startWith("Could not get map value type")
        }
    }

    test("findSuperType") {
        table(
            headers("type", "expected superType"),
            row(Foo1::class.starProjectedType, typeOf<Map<String, Int?>>()),
            row(Foo2::class.starProjectedType, typeOf<Map<String?, Int>>()),
            row(Foo3::class.starProjectedType, typeOf<Map<String, Int?>>()),
            row(Foo4::class.starProjectedType, typeOf<Map<String, Int?>>()),
            row(Foo5::class.starProjectedType, typeOf<Map<String, Int?>>()),
            row(Foo6::class.starProjectedType, typeOf<Map<String, Int?>>()),
            row(Foo7::class.starProjectedType, typeOf<Map<String, Int?>>()),
            row(Foo8::class.starProjectedType, typeOf<Map<String, Int?>>()),
            row(Foo9::class.starProjectedType, typeOf<Map<String, Int?>>()),
            row(Foo10::class.starProjectedType, typeOf<Map<String, Int?>>()),
            row(Foo11::class.starProjectedType, typeOf<Map<String, Int?>>()),
            row(Foo12::class.starProjectedType, typeOf<Map<Int, String?>>()),
            row(Foo13::class.starProjectedType, typeOf<Map<String, Int?>>()),
            row(Foo14::class.starProjectedType, typeOf<Map<String, Map<String?, List<Int?>?>>>()),
            row(Foo15::class.starProjectedType, typeOf<Map<String, Map<out String?, out List<out Int?>?>>>()),
            row(Foo16::class.starProjectedType, typeOf<Map<*, *>>()),
            row(Foo17::class.starProjectedType, typeOf<Map<*, *>>()),
            row(Foo18::class.starProjectedType, typeOf<Map<*, *>>())
        ).forAll { type, expectedSuperType ->
            type.findSuperType(Map::class) shouldBe expectedSuperType
        }
    }
})
