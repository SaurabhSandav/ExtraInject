package com.redridgeapps.extrainject.processor.validation

import com.redridgeapps.extrainject.processor.util.castEach
import com.redridgeapps.extrainject.processor.util.error
import com.squareup.javapoet.ClassName
import java.lang.reflect.Modifier
import javax.annotation.processing.Messager
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

abstract class BaseValidation(
    private val annotation: Class<out Annotation>,
    private val extendsType: ClassName,
    private val classElement: TypeElement,
    private val elementUtils: Elements,
    private val typeUtils: Types,
    private val messager: Messager
) {

    lateinit var targetConstructor: ExecutableElement
    private val annotationSimpleName = annotation.simpleName

    private val constructorElements = run {
        classElement
            .enclosedElements
            .filter { it.kind == ElementKind.CONSTRUCTOR }
            .filter { it.getAnnotation(annotation) != null }
            .castEach<ExecutableElement>()
    }

    open fun validate(): Boolean {
        return extendsGivenType() and
                isNotPrivate() and
                isStaticIfNested() and
                singleAnnotatedConstructorExists() &&
                constructorIsNotPrivate()
    }

    private fun extendsGivenType(): Boolean {

        val extendsTypeMirror = elementUtils.getTypeElement(extendsType.toString()).asType()

        if (typeUtils.isAssignable(classElement.asType(), extendsTypeMirror)) return true

        messager.error("@$annotationSimpleName-using types must inherit from $extendsType", classElement)
        return false
    }

    private fun isNotPrivate(): Boolean {

        if (Modifier.PRIVATE !in classElement.modifiers) return true

        messager.error("@$annotationSimpleName-using types must not be private", classElement)
        return false
    }

    private fun isStaticIfNested(): Boolean {

        if (classElement.enclosingElement.kind != ElementKind.CLASS || Modifier.STATIC in classElement.modifiers)
            return true

        messager.error("Nested @$annotationSimpleName-using types must be static", classElement)
        return false
    }

    private fun singleAnnotatedConstructorExists(): Boolean = when {
        constructorElements.isEmpty() -> {
            messager.error("Extra injection requires an @$annotationSimpleName-annotated constructor", classElement)
            false
        }
        constructorElements.size > 1 -> {
            messager.error("Multiple @$annotationSimpleName-annotated constructorElements found.", classElement)
            false
        }
        else -> true
    }

    private fun constructorIsNotPrivate(): Boolean {

        targetConstructor = constructorElements.single()

        if (Modifier.PRIVATE !in targetConstructor.modifiers) return true

        messager.error("@$annotationSimpleName constructor must not be private.", targetConstructor)
        return false
    }
}
