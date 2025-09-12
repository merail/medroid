package merail.tools.info.api

interface IDeviceDataInformer {
    fun getManufacturer(): String

    fun getModel(): String

    fun getBrand(): String

    fun getAbis(): String

    fun getCoresCount(): Int

    fun getAvailableRam(): Float

    fun getTotalRam(): Float

    fun getAvailableStorage(): Float

    fun getTotalStorage(): Float
}