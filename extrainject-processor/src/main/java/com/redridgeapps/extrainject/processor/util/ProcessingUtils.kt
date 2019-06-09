package com.redridgeapps.extrainject.processor.util

import javax.annotation.processing.Messager
import javax.lang.model.AnnotatedConstruct
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

/** Return true if this [AnnotatedConstruct] is annotated with `qualifiedName`. */
fun AnnotatedConstruct.hasAnnotation(qualifiedName: String) = getAnnotation(qualifiedName) != null

/** Return the first annotation matching [qualifiedName] or null. */
fun AnnotatedConstruct.getAnnotation(qualifiedName: String) = annotationMirrors
    .firstOrNull {
        it.annotationType.asElement().cast<TypeElement>().qualifiedName.contentEquals(qualifiedName)
    }

fun Messager.error(message: String, element: Element? = null) {
    printMessage(Diagnostic.Kind.ERROR, message, element)
}
