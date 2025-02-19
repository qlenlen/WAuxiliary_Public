package demo.hook.func.find

import com.highcapable.yukihookapi.hook.type.android.ImageViewClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.FloatType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import me.hd.wauxv.hook.anno.HookAnno
import me.hd.wauxv.hook.anno.ViewAnno
import me.hd.wauxv.hook.base.common.CommonSwitchHooker
import me.hd.wauxv.hook.base.data.DescData
import me.hd.wauxv.hook.base.impl.IDexFindBase
import me.hd.wauxv.hook.data.HostInfo
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.wrap.DexMethod

@HookAnno
@ViewAnno
object RoundAvatarHooker : CommonSwitchHooker(), IDexFindBase {
    private object RoundAvatarHookerMethod : DescData()

    override val funcName = "圆形头像"
    override val funcDesc = "将微信中出现的头像显示为圆形"

    override fun initOnce() {
        DexMethod(RoundAvatarHookerMethod.desc)
            .getMethodInstance(HostInfo.appClassLoader)
            .hook {
                beforeIfEnabled {
                    args[2] = 0.3f
                }
            }
    }

    override fun dexFind(initiate: DexKitBridge) {
        RoundAvatarHookerMethod.desc = initiate.findMethod {
            matcher {
                paramTypes(ImageViewClass, StringClass, FloatType, BooleanType)
                usingEqStrings("MicroMsg.AvatarDrawable")
            }
        }.single().toDexMethod().toString()
    }
}
