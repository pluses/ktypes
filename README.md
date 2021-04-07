# KTypes

KTypes is a zero-dependency Kotlin library for accurately introspecting type information

## Examples

```kotlin
import kotlin.reflect.full.createType

interface Bar<T> : List<Array<T?>>

interface Foo : Bar<String>

fun main() {
    val fooType = Foo::class.createType()
    val collectionElementType = fooType.collectionElementType

    println(collectionElementType)// Array<String?>
}
```

```kotlin
import kotlin.reflect.full.createType

interface Bar<T1, T2> : Map<T1, List<T2>?>

interface Foo : Bar<String, Int?>

fun main() {
    val fooType = Foo::class.createType()
    val mapType = fooType.findSuperType(Map::class)
    val mapKeyType = fooType.mapKeyType
    val mapValueType = fooType.mapValueType

    println(mapType)// Map<String, List<Int?>?>
    println(mapKeyType)// String
    println(mapValueType)// List<Int?>?
}
```

```kotlin
import kotlin.reflect.full.createType

interface Bar<T> : List<Array<T?>>

interface Foo : Bar<String>

fun main() {
    val fooType = Foo::class.createType()

    println(fooType.isFinal)// false
    println(fooType.isOpen)// false
    println(fooType.isAbstract)// true
    println(fooType.isSealed)// false
    println(fooType.isData)// false
    println(fooType.isInner)// false
    println(fooType.isCompanion)// false
    println(fooType.isFun)// false
    println(fooType.isInterface)// true
    println(fooType.isConcrete)// false
    println(fooType.isPrimitive)// false
    println(fooType.isAnnotation)// false
    println(fooType.isEnum)// false
    println(fooType.isArray)// false
    println(fooType.isList)// true
    println(fooType.isSet)// false
    println(fooType.isCollection)// true
    println(fooType.isCollectionLike)// true
    println(fooType.isMap)// false
}
```

## Download

Gradle:
```gradle
dependencies {
  implementation 'cloud.pluses:ktypes:1.0.0'
}
```

Maven:
```xml
<dependency>
  <groupId>cloud.pluses</groupId>
  <artifactId>ktypes</artifactId>
  <version>1.0.0</version>
</dependency>
```
