package demo.hook.func.find

import me.hd.wauxv.hook.anno.HookAnno
import me.hd.wauxv.hook.anno.ViewAnno
import me.hd.wauxv.hook.base.common.CommonSwitchHooker
import me.hd.wauxv.hook.base.impl.DescCache
import me.hd.wauxv.hook.base.impl.IDexFindBase
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.wrap.DexMethod

@HookAnno
@ViewAnno
object PadModeHooker : CommonSwitchHooker(), IDexFindBase {
    override val funcName = "平板模式"
    override val funcDesc = "可在当前设备登录另一台设备的微信号"

    override fun initOnce() {
        DexMethod(PadModeHookerMethod.desc)
            .getMethodInstance(appClassLoader!!)
            .hook {
                beforeIfEnabled {
                    resultTrue()
                }
            }
    }

    private object PadModeHookerMethod : DescCache()

    override fun dexFind(initiate: DexKitBridge) {
        PadModeHookerMethod.desc = initiate.findMethod {
            matcher {
                usingEqStrings("Lenovo TB-9707F")
            }
        }.single().toDexMethod().toString()
    }
}
