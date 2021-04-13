package cloud.pluses.ktypes

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType

@OptIn(ExperimentalUnsignedTypes::class)
public interface TypeCapture<T> {

    public object BooleanSet : TypeCapture<Set<Boolean>>

    public object ByteSet : TypeCapture<Set<Byte>>

    public object ShortSet : TypeCapture<Set<Short>>

    public object IntSet : TypeCapture<Set<Int>>

    public object LongSet : TypeCapture<Set<Long>>

    public object FloatSet : TypeCapture<Set<Float>>

    public object DoubleSet : TypeCapture<Set<Double>>

    public object CharSet : TypeCapture<Set<Char>>

    public object StringSet : TypeCapture<Set<String>>

    public object UByteSet : TypeCapture<Set<UByte>>

    public object UShortSet : TypeCapture<Set<UShort>>

    public object UIntSet : TypeCapture<Set<UInt>>

    public object ULongSet : TypeCapture<Set<ULong>>

    public object BooleanList : TypeCapture<List<Boolean>>

    public object ByteList : TypeCapture<List<Byte>>

    public object ShortList : TypeCapture<List<Short>>

    public object IntList : TypeCapture<List<Int>>

    public object LongList : TypeCapture<List<Long>>

    public object FloatList : TypeCapture<List<Float>>

    public object DoubleList : TypeCapture<List<Double>>

    public object CharList : TypeCapture<List<Char>>

    public object StringList : TypeCapture<List<String>>

    public object UByteList : TypeCapture<List<UByte>>

    public object UShortList : TypeCapture<List<UShort>>

    public object UIntList : TypeCapture<List<UInt>>

    public object ULongList : TypeCapture<List<ULong>>

    public object BooleanCollection : TypeCapture<Collection<Boolean>>

    public object ByteCollection : TypeCapture<Collection<Byte>>

    public object ShortCollection : TypeCapture<Collection<Short>>

    public object IntCollection : TypeCapture<Collection<Int>>

    public object LongCollection : TypeCapture<Collection<Long>>

    public object FloatCollection : TypeCapture<Collection<Float>>

    public object DoubleCollection : TypeCapture<Collection<Double>>

    public object CharCollection : TypeCapture<Collection<Char>>

    public object StringCollection : TypeCapture<Collection<String>>

    public object UByteCollection : TypeCapture<Collection<UByte>>

    public object UShortCollection : TypeCapture<Collection<UShort>>

    public object UIntCollection : TypeCapture<Collection<UInt>>

    public object ULongCollection : TypeCapture<Collection<ULong>>
}

private val typeCaptureCache = ConcurrentHashMap<KClass<out TypeCapture<*>>, KType>()

public fun KClass<out TypeCapture<*>>.capture(): KType? =
    typeCaptureCache.getOrPut(this) {
        starProjectedType
            .findSuperType(TypeCapture::class)
            ?.arguments
            ?.firstOrNull()
            ?.type
            ?: return null
    }
