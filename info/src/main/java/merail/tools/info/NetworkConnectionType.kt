package merail.tools.info

internal enum class NetworkConnectionType(
    val humanValue: String,
) {
    MOBILE("Mobile"),
    WI_FI("Wi-Fi"),
    OTHER("Other"),
    NONE("None"),
    ;
}