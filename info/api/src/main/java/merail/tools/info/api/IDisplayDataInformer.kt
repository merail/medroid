package merail.tools.info.api

import merail.tools.info.api.model.DensityQualifierType

interface IDisplayDataInformer {
    fun getDisplayWidth(): Int

    fun getDisplayHeight(): Int

    fun getDpi(): Int

    fun getDensity(): Float

    fun getDensityQualifier(): DensityQualifierType

    fun getRefreshRate(): Float
}