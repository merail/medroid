package merail.tools.info.api

import merail.tools.info.api.model.AndroidVersionType

interface IOsDataInformer {
    fun getAndroidVersion(): AndroidVersionType

    fun getPatchLevel(): String

    fun getBuildId(): String
}