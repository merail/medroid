package merail.tools.info

internal enum class DensityQualifierType(
    val humanValue: String,
) {
    LDPI("Low"),
    MDPI("Medium"),
    HDPI("High"),
    XHDPI("Extra High"),
    XXHDPI("Extra Extra High"),
    XXXHDPI("Extra Extra Extra High"),
    UNKNOWN("Unknown"),
    ;
}