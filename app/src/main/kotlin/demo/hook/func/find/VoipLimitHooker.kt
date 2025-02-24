package demo.hook.func.find

import com.highcapable.yukihookapi.hook.type.android.ContextClass
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
object VoipLimitHooker : CommonSwitchHooker(), IDexFindBase {
    private object VoipLimitMethodIsVoiceUsing : DescData()
    private object VoipLimitMethodIsMultiTalking : DescData()
    private object VoipLimitMethodCheckAppBrand : DescData()

    override val funcName = "通话限制"
    override val funcDesc = "将通话中无法播放视频的弹窗限制移除"

    override fun initOnce() {
        DexMethod(VoipLimitMethodIsVoiceUsing.desc)
            .getMethodInstance(HostInfo.appClassLoader)
            .hook {
                beforeIfEnabled {
                    resultFalse()
                }
            }
        DexMethod(VoipLimitMethodIsMultiTalking.desc)
            .getMethodInstance(HostInfo.appClassLoader)
            .hook {
                beforeIfEnabled {
                    resultFalse()
                }
            }
        DexMethod(VoipLimitMethodCheckAppBrand.desc)
            .getMethodInstance(HostInfo.appClassLoader)
            .hook {
                beforeIfEnabled {
                    resultFalse()
                }
            }
    }

    override fun dexFind(initiate: DexKitBridge) {
        VoipLimitMethodIsVoiceUsing.desc = initiate.findMethod {
            matcher {
                paramTypes(ContextClass)
                usingEqStrings("MicroMsg.DeviceOccupy", "isVoiceUsing")
            }
        }.single().toDexMethod().toString()
        VoipLimitMethodIsMultiTalking.desc = initiate.findMethod {
            matcher {
                paramTypes(ContextClass)
                usingEqStrings("MicroMsg.DeviceOccupy", "isMultiTalking")
            }
        }.single().toDexMethod().toString()
        VoipLimitMethodCheckAppBrand.desc = initiate.findMethod {
            matcher {
                paramTypes(ContextClass)
                usingEqStrings("MicroMsg.DeviceOccupy", "checkAppBrandVoiceUsingAndShowToast isVoiceUsing:%b, isCameraUsing:%b")
            }
        }.single().toDexMethod().toString()
    }
}
