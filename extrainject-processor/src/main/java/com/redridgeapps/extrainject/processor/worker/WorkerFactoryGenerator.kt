package com.redridgeapps.extrainject.processor.worker

import com.redridgeapps.extrainject.annotations.WorkerInject
import com.redridgeapps.extrainject.processor.dependency.DependencyRequest
import com.redridgeapps.extrainject.processor.util.ANDROIDX_NONNULL
import com.redridgeapps.extrainject.processor.util.INJECTABLEWORKER
import com.redridgeapps.extrainject.processor.util.JAVAX_INJECT
import com.redridgeapps.extrainject.processor.util.LISTENABLEWORKER
import com.redridgeapps.extrainject.processor.util.MSBuilder
import com.redridgeapps.extrainject.processor.util.TSBuilder
import com.redridgeapps.extrainject.processor.util.addDefaultFileComment
import com.redridgeapps.extrainject.processor.util.applyEach
import com.redridgeapps.extrainject.processor.util.asProviderType
import com.redridgeapps.extrainject.processor.util.assistedInjectFactoryName
import com.redridgeapps.extrainject.processor.util.joinToCode
import com.redridgeapps.extrainject.processor.util.provideArguments
import com.redridgeapps.extrainject.processor.util.rawClassName
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeSpec
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier.FINAL
import javax.lang.model.element.Modifier.PRIVATE
import javax.lang.model.element.Modifier.PUBLIC
import javax.lang.model.element.TypeElement

class WorkerFactoryGenerator(
    private val targetClassName: ClassName,
    private val dependencyRequests: List<DependencyRequest>
) {

    companion object {
        private const val TARGET_CREATION_METHOD = "create"
    }

    private val generatedClassName = targetClassName.rawClassName().assistedInjectFactoryName()
    private val extraKeys = dependencyRequests.filter { it.isExtra }.map { it.namedKey }
    private val providedKeys = dependencyRequests.filterNot { it.isExtra }

    fun generateFactory(filer: Filer, targetClass: TypeElement): ClassName {

        val generatedTypeSpec = generateClass()
            .toBuilder()
            .addOriginatingElement(targetClass)
            .build()

        JavaFile.builder(generatedClassName.packageName(), generatedTypeSpec)
            .addDefaultFileComment<WorkerInject>()
            .build()
            .writeTo(filer)

        return generatedClassName
    }

    private fun generateClass(): TypeSpec {
        return TypeSpec.classBuilder(generatedClassName)
            .addModifiers(PUBLIC, FINAL)
            .addSuperinterface(INJECTABLEWORKER)
            .addProvidedFields()
            .addConstructorWithProvidedParams()
            .addCreateMethod()
            .build()
    }

    private fun TSBuilder.addProvidedFields(): TSBuilder = applyEach(providedKeys) {
        addField(it.asProviderType().withoutAnnotations(), it.name, PRIVATE, FINAL)
    }

    private fun TSBuilder.addConstructorWithProvidedParams(): TSBuilder {

        val constructor = MethodSpec
            .constructorBuilder()
            .addModifiers(PUBLIC)
            .addAnnotation(JAVAX_INJECT)
            .applyEach(providedKeys) {
                addParameter(it.asProviderType(), it.name)
                addStatement("this.$1N = $1N", it.name)
            }
            .build()

        return addMethod(constructor)
    }

    private fun TSBuilder.addCreateMethod(): TSBuilder {

        val createBlock = """
            |
            |return new ${"$"}1T(${"$"}2L);
            |""".trimMargin()

        val createMethod = MethodSpec.methodBuilder(TARGET_CREATION_METHOD)
            .addModifiers(PUBLIC)
            .addAnnotation(ANDROIDX_NONNULL)
            .addAnnotation(Override::class.java)
            .returns(LISTENABLEWORKER)
            .addExtraParameters()
            .addCode(
                createBlock,
                targetClassName,
                dependencyRequests.map { it.provideArguments() }.joinToCode(",\n  ")
            )
            .build()

        return addMethod(createMethod)
    }

    private fun MSBuilder.addExtraParameters(): MSBuilder = applyEach(extraKeys) {

        val modelClassParameter = ParameterSpec
            .builder(it.key.type, it.name)
            .addAnnotation(ANDROIDX_NONNULL)
            .build()

        addParameter(modelClassParameter)
    }
}
