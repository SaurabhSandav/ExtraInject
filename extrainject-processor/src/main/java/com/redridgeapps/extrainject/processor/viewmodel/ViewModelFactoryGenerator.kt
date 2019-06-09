package com.redridgeapps.extrainject.processor.viewmodel

import com.redridgeapps.extrainject.annotations.ViewModelInject
import com.redridgeapps.extrainject.processor.dependency.DependencyRequest
import com.redridgeapps.extrainject.processor.util.ANDROIDX_NONNULL
import com.redridgeapps.extrainject.processor.util.JAVAX_INJECT
import com.redridgeapps.extrainject.processor.util.TSBuilder
import com.redridgeapps.extrainject.processor.util.VIEWMODEL
import com.redridgeapps.extrainject.processor.util.VIEWMODELPROVIDER_FACTORY
import com.redridgeapps.extrainject.processor.util.addDefaultFileComment
import com.redridgeapps.extrainject.processor.util.applyEach
import com.redridgeapps.extrainject.processor.util.asProviderType
import com.redridgeapps.extrainject.processor.util.assistedInjectFactoryName
import com.redridgeapps.extrainject.processor.util.joinToCode
import com.redridgeapps.extrainject.processor.util.parameterizedBy
import com.redridgeapps.extrainject.processor.util.provideArguments
import com.redridgeapps.extrainject.processor.util.rawClassName
import com.redridgeapps.extrainject.processor.util.toTypeVariableName
import com.redridgeapps.extrainject.processor.util.withBounds
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier.FINAL
import javax.lang.model.element.Modifier.PRIVATE
import javax.lang.model.element.Modifier.PUBLIC
import javax.lang.model.element.TypeElement

class ViewModelFactoryGenerator(
    private val targetClassName: ClassName,
    private val dependencyRequests: List<DependencyRequest>
) {

    companion object {
        private const val INITIALIZATION_TRACKER_FIELD = "isInitialized"
        private const val EXTRA_INITIALIZATION_METHOD = "with"
        private const val TARGET_CREATION_METHOD = "create"
        private const val MODEL_CLASS = "modelClass"
    }

    private val generatedClassName = targetClassName.rawClassName().assistedInjectFactoryName()
    private val extraKeys = dependencyRequests.filter { it.isExtra }.map { it.namedKey }
    private val providedKeys = dependencyRequests.filterNot { it.isExtra }

    fun generateFactory(filer: Filer, targetClass: TypeElement) {

        val generatedTypeSpec = generateClass()
            .toBuilder()
            .addOriginatingElement(targetClass)
            .build()

        JavaFile.builder(generatedClassName.packageName(), generatedTypeSpec)
            .addDefaultFileComment<ViewModelInject>()
            .build()
            .writeTo(filer)
    }

    private fun generateClass(): TypeSpec {
        return TypeSpec.classBuilder(generatedClassName)
            .addModifiers(PUBLIC, FINAL)
            .addSuperinterface(VIEWMODELPROVIDER_FACTORY)
            .addExtraFields()
            .addProvidedFields()
            .addIsInitializedField()
            .addConstructorWithProvidedParams()
            .addExtraInitializerMethod()
            .addCreateMethod()
            .build()
    }

    private fun TSBuilder.addExtraFields(): TSBuilder = applyEach(extraKeys) {
        addField(it.key.type, it.name, PRIVATE)
    }

    private fun TSBuilder.addProvidedFields(): TSBuilder = applyEach(providedKeys) {
        addField(it.asProviderType().withoutAnnotations(), it.name, PRIVATE, FINAL)
    }

    private fun TSBuilder.addIsInitializedField(): TSBuilder {

        val fieldSpec = FieldSpec
            .builder(TypeName.BOOLEAN, INITIALIZATION_TRACKER_FIELD, PRIVATE)
            .initializer("false")
            .build()

        return addField(fieldSpec)
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

    private fun TSBuilder.addExtraInitializerMethod(): TSBuilder {

        val extraInitializerMethod = MethodSpec
            .methodBuilder(EXTRA_INITIALIZATION_METHOD)
            .addModifiers(PUBLIC)
            .addAnnotation(ANDROIDX_NONNULL)
            .returns(generatedClassName)
            .applyEach(extraKeys) {
                addParameter(it.key.type, it.name)
                addStatement("this.$1N = $1N", it.name)
            }
            .addCode(
                """
                |
                |this.isInitialized = true;
                |
                |""".trimMargin()
            )
            .addStatement("return this")
            .build()

        return addMethod(extraInitializerMethod)
    }

    private fun TSBuilder.addCreateMethod(): TSBuilder {

        val modelClassParameter = ParameterSpec
            .builder(Class::class.parameterizedBy("T"), MODEL_CLASS) // Class<T> modelClass
            .addAnnotation(ANDROIDX_NONNULL)
            .build()

        val createBlock = """
            |
            |if (!isInitialized)
            |  throw new IllegalStateException("Extras Not Provided! Use $EXTRA_INITIALIZATION_METHOD() to provide Extras.");
            |
            |if (!$MODEL_CLASS.isAssignableFrom(${"$"}1T.class))
            |  throw new IllegalStateException("Invalid argument $MODEL_CLASS!");
            |
            |return (T) new ${"$"}1T(${"$"}2L);
            |""".trimMargin()

        val createMethod = MethodSpec.methodBuilder(TARGET_CREATION_METHOD)
            .addModifiers(PUBLIC)
            .addAnnotation(ANDROIDX_NONNULL)
            .addTypeVariable("T".withBounds(VIEWMODEL)) // <T : ViewModel>
            .returns("T".toTypeVariableName())
            .addParameter(modelClassParameter)
            .addCode(
                createBlock,
                targetClassName,
                dependencyRequests.map { it.provideArguments() }.joinToCode(",\n  ")
            )
            .build()

        return addMethod(createMethod)
    }
}
