package merail.tools.core

private const val BINARY_BASE = 1024F

fun Long.toGb(): Float = this / (BINARY_BASE * BINARY_BASE * BINARY_BASE)
