package merail.tools.info.api.model

enum class NetworkConnectionType(
    val humanValue: String,
) {
    MOBILE("Mobile"),
    WI_FI("Wi-Fi"),
    OTHER("Other"),
    NONE("None"),
    ;
}