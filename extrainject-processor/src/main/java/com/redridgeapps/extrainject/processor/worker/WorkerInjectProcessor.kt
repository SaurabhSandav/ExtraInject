package com.redridgeapps.extrainject.processor.worker

import com.google.auto.common.MoreTypes
import com.google.auto.service.AutoService
import com.redridgeapps.extrainject.annotations.WorkerInject
import com.redridgeapps.extrainject.processor.dependency.asDependencyRequest
import com.redridgeapps.extrainject.processor.util.castEach
import com.redridgeapps.extrainject.processor.util.toClassName
import com.redridgeapps.extrainject.processor.validation.WorkerInjectValidation
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
class WorkerInjectProcessor : AbstractProcessor() {

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

    override fun getSupportedAnnotationTypes() = setOf(WorkerInject::class.java.canonicalName)

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {

        val targetClassToFactoryList = roundEnv.findCandidateTypeElements()
            .mapNotNull { it.toInjectElementsOrNull() }
            .map {
                val generatedFactoryName = it.buildGenerator()
                    .generateFactory(filer, it.targetClass)

                TargetClassToFactory(it.targetClass, generatedFactoryName)
            }

        if (targetClassToFactoryList.isNotEmpty())
            WorkerModuleGenerator(targetClassToFactoryList).generateModule(filer)

        return false
    }

    private fun RoundEnvironment.findCandidateTypeElements(): List<TypeElement> {
        return getElementsAnnotatedWith(WorkerInject::class.java)
            .map { it.enclosingElement }
            .castEach<TypeElement>()
            .distinctBy { MoreTypes.equivalence().wrap(it.asType()) }
    }

    private fun TypeElement.toInjectElementsOrNull(): InjectElements? {

        val validator = WorkerInjectValidation(this, elementUtils, typeUtils, messager)

        if (!validator.validate()) return null

        return InjectElements(
            this,
            validator.targetConstructor
        )
    }

    private fun InjectElements.buildGenerator(): WorkerFactoryGenerator {
        val targetClassName = targetClass.toClassName()
        val dependencyRequests = targetConstructor.parameters.mapIndexed { index, variableElement ->
            variableElement.asDependencyRequest(isExtra = index <= 1)
        }
        return WorkerFactoryGenerator(targetClassName, dependencyRequests)
    }

    private data class InjectElements(val targetClass: TypeElement, val targetConstructor: ExecutableElement)
}
