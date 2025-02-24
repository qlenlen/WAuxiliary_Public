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
object AntiRevokeHooker : CommonSwitchHooker(), IDexFindBase {
    private object AntiRevokeMethod : DescData()

    override val funcName = "阻止撤回"
    override val funcDesc = "防止对方撤回消息以确保完整聊天体验"

    override fun initOnce() {
        DexMethod(AntiRevokeMethod.desc)
            .getMethodInstance(HostInfo.appClassLoader)
            .hook {
                beforeIfEnabled {
                    resultNull()
                }
            }
    }

    override fun dexFind(initiate: DexKitBridge) {
        AntiRevokeMethod.desc = initiate.findMethod {
            matcher {
                usingEqStrings("doRevokeMsg xmlSrvMsgId=%d talker=%s isGet=%s")
            }
        }.single().toDexMethod().toString()
    }
}
