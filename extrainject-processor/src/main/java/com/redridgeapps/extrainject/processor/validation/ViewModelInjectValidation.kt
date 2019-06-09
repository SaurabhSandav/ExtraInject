package com.redridgeapps.extrainject.processor.validation

import com.redridgeapps.extrainject.annotations.ViewModelInject
import com.redridgeapps.extrainject.processor.util.VIEWMODEL
import javax.annotation.processing.Messager
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

class ViewModelInjectValidation(
    classElement: TypeElement,
    elementUtils: Elements,
    typeUtils: Types,
    messager: Messager
) : BaseValidation(ViewModelInject::class.java, VIEWMODEL, classElement, elementUtils, typeUtils, messager)
