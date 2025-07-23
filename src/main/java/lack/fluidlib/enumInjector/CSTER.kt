package lack.fluidlib.enumInjector

import com.chocohead.mm.api.ClassTinkerers
import com.chocohead.mm.api.EnumAdder
import net.fabricmc.loader.api.FabricLoader

@Suppress("unused")
object CSTER : Runnable {
    override fun run() {
        val mr = FabricLoader.getInstance().mappingResolver
        val cameraType = mr.mapClassName("intermediary", "net.minecraft.class_5636") // enum class

        val adder =
            ClassTinkerers.enumBuilder(
                cameraType
            )
        addToEnum(adder, CST.Values.NULL_TYPE)

        adder.build()
    }

    private fun addToEnum(adder: EnumAdder, values: CST.Values) {
        adder.addEnum(values.fieldName) {
            arrayOf(
                -1
            )
        }
    }
}
