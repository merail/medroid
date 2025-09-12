package merail.tools.info

internal enum class AndroidVersionType(
    val sdkValue: Int,
    val humanValue: String,
    val releaseValue: String,
) {
    OREO_26(26, "Oreo", "8.0"),
    OREO_27(27, "Oreo", "8.1"),
    PIE(28, "Pie", "9"),
    Q(29, "Q", "10"),
    RED_VELVET_CAKE(30, "Red Velvet Cake", "11"),
    SNOW_CONE_31(31, "Snow Cone", "12"),
    SNOW_CONE_32(32, "Snow Cone", "12L"),
    TIRAMISU(33, "Tiramisu", "13"),
    UPSIDE_DOWN_CAKE(34, "Upside Down Cake", "14"),
    VANILLA_ICE_CREAM(35, "Vanilla Ice Cream", "15"),
    BAKLAVA(36, "Baklava", "16"),
    UNKNOWN(-1, "Unknown", ""),
    ;
}