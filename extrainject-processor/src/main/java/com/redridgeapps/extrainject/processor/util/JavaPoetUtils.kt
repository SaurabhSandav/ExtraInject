package com.redridgeapps.extrainject.processor.util

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeVariableName
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import kotlin.reflect.KClass

fun TypeElement.toClassName(): ClassName = ClassName.get(this)
fun TypeMirror.toTypeName(): TypeName = TypeName.get(this)
fun KClass<*>.toClassName(): ClassName = ClassName.get(java)

fun String.toTypeVariableName(): TypeVariableName = TypeVariableName.get(this)
fun String.withBounds(vararg bounds: TypeName): TypeVariableName = TypeVariableName.get(this, *bounds)

fun <T : Any> KClass<T>.parameterizedBy(name: String): ParameterizedTypeName =
    ParameterizedTypeName.get(this.toClassName(), TypeVariableName.get(name))

fun Iterable<CodeBlock>.joinToCode(separator: String = ", "): CodeBlock = CodeBlock.join(this, separator)

/**
 * Like [ClassName.peerClass] except instead of honoring the enclosing class names they are
 * concatenated with `$` similar to the reflection name. `foo.Bar.Baz` invoking this function with
 * `Fuzz` will produce `foo.Baz$Fuzz`.
 */
fun ClassName.peerClassWithReflectionNesting(name: String): ClassName {
    var prefix = ""
    var peek = this
    while (true) {
        peek = peek.enclosingClassName() ?: break
        prefix = peek.simpleName() + "$" + prefix
    }
    return ClassName.get(packageName(), prefix + name)
}

fun TypeName.rawClassName(): ClassName = when (this) {
    is ClassName -> this
    is ParameterizedTypeName -> rawType
    else -> throw IllegalStateException("Cannot extract raw class name from $this")
}
