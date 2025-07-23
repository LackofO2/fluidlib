package lack.fluidlib.enumInjector

import lack.fluidlib.enumInjector.Helper.getNullType
import org.jetbrains.annotations.ApiStatus

object CST {
    @ApiStatus.Internal
    data class Values internal constructor(
        val fieldName: String
    ) {
        @ApiStatus.Internal
        companion object {
            val NULL_TYPE = Values("NULL_TYPE")
        }
    }

    @JvmField
    val NULL_TYPE = getNullType()

}
