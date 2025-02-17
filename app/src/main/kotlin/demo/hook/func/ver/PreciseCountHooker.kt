package demo.hook.func.ver

import android.widget.TextView
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.injectModuleAppResources
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.IntType
import me.hd.wauxv.R
import me.hd.wauxv.factory.dpFloat
import me.hd.wauxv.hook.anno.HookAnno
import me.hd.wauxv.hook.anno.ViewAnno
import me.hd.wauxv.hook.base.common.CommonSwitchHooker
import me.hd.wauxv.hook.data.WechatVersion
import me.hd.wauxv.hook.data.requireVersion

@HookAnno
@ViewAnno
object PreciseCountHooker : CommonSwitchHooker() {
    override val funcName = "[8.0.45]精确数量"
    override val funcDesc = "显示底部导航栏的具体未读数量"
    override val isAvailable = requireVersion(WechatVersion.WECHAT_8_0_45)

    override fun initOnce() {
        /**
         * 相关定位信息
         * 类名 com.tencent.mm.ui.MainTabUI
         * 关键 doOnCreate
         */
        "com.tencent.mm.ui.LauncherUIBottomTabView".toClass().apply {
            val methodFieldMap = mapOf(
                "j" to "f", // MainTabUnread
                "h" to "h", // ContactTabUnread
                "i" to "g", // FriendTabUnread
                "k" to "i"  // SettingsTabUnread
            )
            methodFieldMap.forEach { (methodName, fieldName) ->
                method {
                    name = methodName
                    param(IntType)
                }.hook {
                    afterIfEnabled {
                        val unreadNum = args[0] as Int
                        if (unreadNum > 99) {
                            val view = field { name = fieldName }.get(instance).any()!!
                            val textView = view::class.java.field { name = "g" }.get(view).cast<TextView>()!!
                            textView.apply {
                                context.injectModuleAppResources()
                                text = "$unreadNum"
                                setTextSize(0, 9.dpFloat(context))
                                setBackgroundResource(R.drawable.bg_precise_count)
                            }
                        }
                    }
                }
            }
        }
    }
}
