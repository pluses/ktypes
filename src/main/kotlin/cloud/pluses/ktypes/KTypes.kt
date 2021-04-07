package cloud.pluses.ktypes

import java.util.*
import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.reflect.jvm.jvmErasure

@OptIn(ExperimentalUnsignedTypes::class)
private val ARRAY_CLASSES = setOf(
    Array::class,
    BooleanArray::class,
    ByteArray::class,
    ShortArray::class,
    IntArray::class,
    LongArray::class,
    FloatArray::class,
    DoubleArray::class,
    CharArray::class,
    UByteArray::class,
    UShortArray::class,
    UIntArray::class,
    ULongArray::class
)

@OptIn(ExperimentalUnsignedTypes::class)
private val ARRAY_COMPONENT_TYPE_MAP = mapOf(
    BooleanArray::class to Boolean::class.createType(),
    ByteArray::class to Byte::class.createType(),
    ShortArray::class to Short::class.createType(),
    IntArray::class to Int::class.createType(),
    LongArray::class to Long::class.createType(),
    FloatArray::class to Float::class.createType(),
    DoubleArray::class to Double::class.createType(),
    CharArray::class to Char::class.createType(),
    UByteArray::class to UByte::class.createType(),
    UShortArray::class to UShort::class.createType(),
    UIntArray::class to UInt::class.createType(),
    ULongArray::class to ULong::class.createType()
)

private val COLLECTION_TYPE = Collection::class.starProjectedType

private val MAP_TYPE = Map::class.starProjectedType

public val KType.isFinal: Boolean get() = jvmErasure.isFinal

public val KType.isOpen: Boolean get() = jvmErasure.isOpen

public val KType.isAbstract: Boolean get() = jvmErasure.isAbstract

public val KType.isSealed: Boolean get() = jvmErasure.isSealed

public val KType.isData: Boolean get() = jvmErasure.isData

public val KType.isInner: Boolean get() = jvmErasure.isInner

public val KType.isCompanion: Boolean get() = jvmErasure.isCompanion

public val KType.isFun: Boolean get() = jvmErasure.isFun

public val KType.isInterface: Boolean get() = jvmErasure.java.isInterface

public val KType.isConcrete: Boolean get() = !isAbstract && !isSealed && !isInterface

public val KType.isPrimitive: Boolean get() = jvmErasure.java.isPrimitive

public val KType.isAnnotation: Boolean get() = jvmErasure.java.isAnnotation

public val KType.isEnum: Boolean get() = jvmErasure.java.isEnum

public val KType.isArray: Boolean get() = jvmErasure in ARRAY_CLASSES || jvmErasure.java.isArray

public val KType.isList: Boolean get() = jvmErasure.isSubclassOf(List::class)

public val KType.isSet: Boolean get() = jvmErasure.isSubclassOf(Set::class)

public val KType.isCollection: Boolean get() = jvmErasure.isSubclassOf(Collection::class)

public val KType.isCollectionLike: Boolean get() = isArray || isCollection

public val KType.isMap: Boolean get() = jvmErasure.isSubclassOf(Map::class)

public val KType.arrayComponentType: KType
    get() {
        require(isArray) { "Not an array type: $this" }

        return arguments
            .firstOrNull()
            ?.type
            ?: ARRAY_COMPONENT_TYPE_MAP[jvmErasure]
            ?: error("Could not get array component type for $this.")
    }

public val KType.collectionElementType: KType
    get() {
        require(isCollection) { "Not an collection type: $this" }

        return findSuperType(COLLECTION_TYPE)
            ?.arguments
            ?.first()
            ?.type
            ?: error("Could not get collection element type for $this.")
    }

public val KType.componentType: KType
    get() = when {
        isArray -> arrayComponentType
        isCollection -> collectionElementType
        else -> throw IllegalArgumentException("Not an array or collection type: $this")
    }

public val KType.mapKeyType: KType
    get() {
        require(isMap) { "Not an map type: $this" }

        return findSuperType(MAP_TYPE)
            ?.arguments
            ?.first()
            ?.type
            ?: error("Could not get map key type for $this.")
    }

public val KType.mapValueType: KType
    get() {
        require(isMap) { "Not an map type: $this" }

        return findSuperType(MAP_TYPE)
            ?.arguments
            ?.get(1)
            ?.type
            ?: error("Could not get map value type for $this.")
    }

@Suppress("ComplexMethod", "ReturnCount")
public fun KType.findSuperType(superType: KType): KType? {
    if (this.jvmErasure == superType.jvmErasure) return this

    val argumentMap by lazy {
        this
            .jvmErasure
            .typeParameters
            .mapIndexed { index, typeParameter ->
                typeParameter.name to this.arguments[index]
            }.toMap()
    }

    fun reifyType(type: KType): KType {
        if (type.arguments.isEmpty()) return type

        fun reifyArguments(arguments: List<KTypeProjection>): List<KTypeProjection> =
            arguments.map { argument ->
                val argumentType = argument.type ?: return@map argument

                when (val argumentTypeClassifier = argumentType.classifier) {
                    is KTypeParameter -> {
                        val actualArgument = argumentMap[argumentTypeClassifier.name]!!
                        val actualArgumentType = actualArgument.type?.let {
                            if (argumentType.isMarkedNullable) it.withNullability(true) else it
                        } ?: return@map KTypeProjection.STAR

                        KTypeProjection(actualArgument.variance.takeIf { it != KVariance.INVARIANT } ?: argument.variance, actualArgumentType)
                    }
                    is KClass<*> -> argument.copy(type = reifyType(argumentType))
                    else -> argument
                }
            }

        return type.jvmErasure.createType(reifyArguments(type.arguments), type.isMarkedNullable, type.annotations)
    }

    this
        .jvmErasure
        .supertypes
        .forEach {
            if (it.isSubtypeOf(superType)) {
                return reifyType(it).findSuperType(superType)
            }
        }
    return null
}

public fun KType.findSuperType(superClass: KClass<*>): KType? = findSuperType(superClass.starProjectedType)
