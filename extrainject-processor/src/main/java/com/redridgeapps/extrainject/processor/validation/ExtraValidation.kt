package com.redridgeapps.extrainject.processor.validation

import com.redridgeapps.extrainject.annotations.Extra
import com.redridgeapps.extrainject.annotations.ViewModelInject
import com.redridgeapps.extrainject.annotations.WorkerInject
import com.redridgeapps.extrainject.processor.util.castEach
import com.redridgeapps.extrainject.processor.util.error
import com.redridgeapps.extrainject.processor.util.hasAnnotation
import javax.annotation.processing.Messager
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ExecutableElement

class ExtraValidation(
    private val roundEnv: RoundEnvironment,
    private val messager: Messager
) {

    private val assistedMethods = run {
        roundEnv.getElementsAnnotatedWith(Extra::class.java)
            .filterNotNull()
            .map { it.enclosingElement }
            .castEach<ExecutableElement>()
    }

    fun validate() {
        isOnlyUsedOnConstructors()
        isConstructorAnnotated()
        isNotUsedAlongWithJavaXInject()
    }

    private fun isOnlyUsedOnConstructors() {
        assistedMethods
            .filterNot { it.simpleName.contentEquals("<init>") }
            .forEach {
                messager.error("@Extra is only supported on constructor parameters", it)
            }
    }

    private fun isConstructorAnnotated() {
        assistedMethods
            .filter { it.simpleName.contentEquals("<init>") }
            .filter { it.annotationMirrors.isEmpty() }
            .forEach {
                messager.error(
                    "@Extra parameter use requires a constructor annotation such as " +
                            "@${ViewModelInject::class.java.simpleName} or @${WorkerInject::class.java.simpleName}",
                    it
                )
            }
    }

    private fun isNotUsedAlongWithJavaXInject() {
        assistedMethods
            .filter { it.simpleName.contentEquals("<init>") }
            .filter { it.hasAnnotation("javax.inject.Inject") }
            .forEach {
                messager.error("@Extra parameter does not work with @Inject!", it)
            }
    }
}
