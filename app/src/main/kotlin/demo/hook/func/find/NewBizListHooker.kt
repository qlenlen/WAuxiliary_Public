package demo.hook.func.find

import android.content.ComponentName
import android.content.Intent
import me.hd.wauxv.data.factory.PackageName
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
object NewBizListHooker : CommonSwitchHooker(), IDexFindBase {
    private object NewBizListMethodShowAsActivity : DescData()
    private object NewBizListMethodMarkStartOpen : DescData()

    private val NewBizConversationUIClass by lazyClass("${PackageName.WECHAT}.ui.conversation.NewBizConversationUI") { HostInfo.appClassLoader }
    private const val FLUTTER_ACTIVITY_CLASS = "${PackageName.WECHAT}.plugin.brandservice.ui.flutter.BizFlutterTLFlutterViewActivity"
    private const val TIMELINE_UI_CLASS = "${PackageName.WECHAT}.plugin.brandservice.ui.timeline.BizTimeLineUI"

    override val funcName = "订阅列表"
    override val funcDesc = "订阅号消息从瀑布流模式改为列表模式"

    override fun initOnce() {
        DexMethod(NewBizListMethodShowAsActivity.desc)
            .getMethodInstance(HostInfo.appClassLoader)
            .hook {
                beforeIfEnabled {
                    val clz = args(2).cast<Class<*>>()
                    if (clz?.name == FLUTTER_ACTIVITY_CLASS) {
                        args(2).set(NewBizConversationUIClass)
                    }
                }
            }
        DexMethod(NewBizListMethodMarkStartOpen.desc)
            .getMethodInstance(HostInfo.appClassLoader)
            .hook {
                beforeIfEnabled {
                    val intent = args(0).cast<Intent>()
                    if (intent?.component?.className == TIMELINE_UI_CLASS) {
                        intent.component = ComponentName(PackageName.WECHAT, NewBizConversationUIClass.name)
                    }
                }
            }
    }

    override fun dexFind(initiate: DexKitBridge) {
        NewBizListMethodShowAsActivity.desc = initiate.findMethod {
            matcher {
                usingEqStrings("com/tencent/mm/flutter/base/MMFlutterInstance", "showAsActivity")
            }
        }.single().toDexMethod().toString()
        NewBizListMethodMarkStartOpen.desc = initiate.findMethod {
            matcher {
                usingEqStrings("MicroMsg.BizTimeReport", "markStartOpen")
            }
        }.single().toDexMethod().toString()
    }
}
