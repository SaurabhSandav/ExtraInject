package com.redridgeapps.extrainject.processor.worker

import com.redridgeapps.extrainject.annotations.WorkerInject
import com.redridgeapps.extrainject.processor.util.DAGGER_BINDS
import com.redridgeapps.extrainject.processor.util.DAGGER_INTOMAP
import com.redridgeapps.extrainject.processor.util.DAGGER_MODULE
import com.redridgeapps.extrainject.processor.util.INJECTABLEWORKER
import com.redridgeapps.extrainject.processor.util.TSBuilder
import com.redridgeapps.extrainject.processor.util.WORKERKEY
import com.redridgeapps.extrainject.processor.util.addDefaultFileComment
import com.redridgeapps.extrainject.processor.util.applyEach
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

data class TargetClassToFactory(val targetClass: TypeElement, val generatedFactoryName: ClassName)

class WorkerModuleGenerator(
    private val targetClassToFactoryList: List<TargetClassToFactory>
) {

    companion object {
        private const val CLASS_NAME = "WorkerFactoryModule"
        private const val BINDING_METHOD_PREFIX = "bind_"
        private const val BINDING_PARAMETER_NAME = "factory"
    }

    fun generateModule(filer: Filer) {

        val generatedTypeSpec = generateClass()

        JavaFile.builder(targetClassToFactoryList.first().generatedFactoryName.packageName(), generatedTypeSpec)
            .addDefaultFileComment<WorkerInject>()
            .build()
            .writeTo(filer)
    }

    private fun generateClass(): TypeSpec {
        return TypeSpec.classBuilder(CLASS_NAME)
            .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
            .addAnnotation(DAGGER_MODULE)
            .applyEach(targetClassToFactoryList) {
                addBindingMethod(it)
                addOriginatingElement(it.targetClass)
            }
            .build()
    }

    private fun TSBuilder.addBindingMethod(it: TargetClassToFactory): TSBuilder {
        val methodSpec =
            MethodSpec.methodBuilder("$BINDING_METHOD_PREFIX${it.generatedFactoryName.simpleName()}")
                .addModifiers(Modifier.ABSTRACT)
                .addAnnotation(DAGGER_BINDS)
                .addAnnotation(DAGGER_INTOMAP)
                .addAnnotation(addWorkerKeyAnnotation(it))
                .addParameter(it.generatedFactoryName, BINDING_PARAMETER_NAME)
                .returns(INJECTABLEWORKER)
                .build()

        return addMethod(methodSpec)
    }

    private fun addWorkerKeyAnnotation(it: TargetClassToFactory): AnnotationSpec? {
        return AnnotationSpec.builder(WORKERKEY)
            .addMember(
                "value",
                CodeBlock.of("${it.targetClass.simpleName}.class")
            )
            .build()
    }
}
