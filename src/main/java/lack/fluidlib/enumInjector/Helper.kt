package lack.fluidlib.enumInjector

import com.chocohead.mm.api.ClassTinkerers
import net.minecraft.block.enums.CameraSubmersionType

@Suppress("unused")
object Helper {
    @JvmStatic
    fun printHelloWorld() {
        println("Hello Fabric World!")
    }

    @JvmStatic
    fun getNullType(): CameraSubmersionType =
        ClassTinkerers.getEnum(CameraSubmersionType::class.java, CST.Values.NULL_TYPE.fieldName)

}
