package demo.hook.func.find

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
object PadModeHooker : CommonSwitchHooker(), IDexFindBase {
    private object PadModeHookerMethod : DescData()

    override val funcName = "平板模式"
    override val funcDesc = "可在当前设备登录另一台设备的微信号"

    override fun initOnce() {
        DexMethod(PadModeHookerMethod.desc)
            .getMethodInstance(HostInfo.appClassLoader)
            .hook {
                beforeIfEnabled {
                    resultTrue()
                }
            }
    }

    override fun dexFind(initiate: DexKitBridge) {
        PadModeHookerMethod.desc = initiate.findMethod {
            matcher {
                usingEqStrings("Lenovo TB-9707F")
            }
        }.single().toDexMethod().toString()
    }
}
