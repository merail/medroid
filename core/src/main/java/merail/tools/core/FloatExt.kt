package merail.tools.core

import kotlin.math.pow
import kotlin.math.roundToInt

fun Float.round(decimals: Int = 1): Float {
    val factor = 10.0f.pow(decimals)
    return (this * factor).roundToInt() / factor
}
