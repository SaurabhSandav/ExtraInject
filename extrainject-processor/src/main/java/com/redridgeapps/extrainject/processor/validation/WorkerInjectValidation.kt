package com.redridgeapps.extrainject.processor.validation

import com.redridgeapps.extrainject.annotations.WorkerInject
import com.redridgeapps.extrainject.processor.util.CONTEXT
import com.redridgeapps.extrainject.processor.util.LISTENABLEWORKER
import com.redridgeapps.extrainject.processor.util.WORKERPARAMETERS
import com.redridgeapps.extrainject.processor.util.error
import javax.annotation.processing.Messager
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

class WorkerInjectValidation(
    classElement: TypeElement,
    private val elementUtils: Elements,
    private val typeUtils: Types,
    private val messager: Messager
) : BaseValidation(WorkerInject::class.java, LISTENABLEWORKER, classElement, elementUtils, typeUtils, messager) {

    override fun validate(): Boolean {
        return super.validate() and constructorExtraArgumentsAreInOrder()
    }

    private fun constructorExtraArgumentsAreInOrder(): Boolean {

        val contextType = elementUtils.getTypeElement(CONTEXT.toString()).asType()
        val workerParametersType = elementUtils.getTypeElement(WORKERPARAMETERS.toString()).asType()

        val (firstArg, secondArg) = targetConstructor.parameters.take(2).map { it.asType() }

        if (typeUtils.isAssignable(firstArg, contextType) &&
            typeUtils.isAssignable(secondArg, workerParametersType)
        ) return true

        messager.error(
            "@${WorkerInject::class.java.simpleName} constructor should have Context and WorkerParameters as first and second parameters",
            targetConstructor
        )
        return false
    }
}
