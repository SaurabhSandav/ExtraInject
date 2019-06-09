package com.redridgeapps.extrainject.processor.viewmodel

import com.google.auto.common.MoreTypes
import com.google.auto.service.AutoService
import com.redridgeapps.extrainject.annotations.Extra
import com.redridgeapps.extrainject.annotations.ViewModelInject
import com.redridgeapps.extrainject.processor.dependency.asDependencyRequest
import com.redridgeapps.extrainject.processor.util.castEach
import com.redridgeapps.extrainject.processor.util.toClassName
import com.redridgeapps.extrainject.processor.validation.ExtraValidation
import com.redridgeapps.extrainject.processor.validation.ViewModelInjectValidation
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

@AutoService(Processor::class)
class ViewModelInjectProcessor : AbstractProcessor() {

    private lateinit var messager: Messager
    private lateinit var filer: Filer
    private lateinit var elementUtils: Elements
    private lateinit var typeUtils: Types

    override fun init(env: ProcessingEnvironment) {
        super.init(env)

        messager = env.messager
        filer = env.filer
        elementUtils = env.elementUtils
        typeUtils = env.typeUtils
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun getSupportedAnnotationTypes() = setOf(
        Extra::class.java.canonicalName,
        ViewModelInject::class.java.canonicalName
    )

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {

        roundEnv.findCandidateTypeElements()
            .mapNotNull { it.toInjectElementsOrNull() }
            .forEach {
                it.buildGenerator()
                    .generateFactory(filer, it.targetClass)
            }

        ExtraValidation(roundEnv, messager).validate()

        return false
    }

    private fun RoundEnvironment.findCandidateTypeElements(): List<TypeElement> {
        return getElementsAnnotatedWith(ViewModelInject::class.java)
            .map { it.enclosingElement }
            .castEach<TypeElement>()
            .distinctBy { MoreTypes.equivalence().wrap(it.asType()) }
    }

    private fun TypeElement.toInjectElementsOrNull(): InjectElements? {

        val validator = ViewModelInjectValidation(this, elementUtils, typeUtils, messager)

        if (!validator.validate()) return null

        return InjectElements(this, validator.targetConstructor)
    }

    private fun InjectElements.buildGenerator(): ViewModelFactoryGenerator {
        val targetClassName = targetClass.toClassName()
        val dependencyRequests = targetConstructor.parameters.map { it.asDependencyRequest() }
        return ViewModelFactoryGenerator(targetClassName, dependencyRequests)
    }

    private data class InjectElements(val targetClass: TypeElement, val targetConstructor: ExecutableElement)
}
