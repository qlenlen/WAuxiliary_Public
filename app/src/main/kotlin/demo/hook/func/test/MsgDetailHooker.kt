package demo.hook.func.test

import android.app.AlertDialog
import android.view.View
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ViewClass
import me.hd.wauxv.hook.anno.HookAnno
import me.hd.wauxv.hook.anno.ViewAnno
import me.hd.wauxv.hook.base.common.CommonSwitchHooker
import me.hd.wauxv.hook.data.HostInfo
import me.hd.wauxv.hook.data.WechatVersion

@HookAnno
@ViewAnno
object MsgDetailHooker : CommonSwitchHooker() {
    override val funcName = "[测][8.0.45]消息详情"
    override val funcDesc = "点击消息弹窗显示对应的详细信息代码"
    override val isAvailable = WechatVersion.V8_0_46.code > HostInfo.versionCode && HostInfo.versionCode >= WechatVersion.V8_0_45.code

    override fun initOnce() {
        "com.tencent.mm.ui.chatting.g7".toClass(HostInfo.appClassLoader).apply {
            method {
                name = "onClick"
                param(ViewClass)
            }.hook {
                beforeIfEnabled {
                    val view = args(0).cast<View>()!!
                    val tagVar = view.tag
                    val prClass = "com.tencent.mm.ui.chatting.viewitems.pr".toClass(HostInfo.appClassLoader)
                    val q4Class = "com.tencent.mm.pluginsdk.ui.chat.q4".toClass(HostInfo.appClassLoader)
                    val msgVar = try {
                        prClass.method { name = "b" }.ignored().get().call(
                            prClass.field { name = "d" }.ignored().get(tagVar).any(),
                            field { name = "d" }.get(instance).any()
                        )
                    } catch (_: Exception) {
                        q4Class.field { name = "a" }.ignored().get(tagVar).any()
                    }!!
                    val msgId = msgVar::class.java.field {
                        name = "field_msgId"
                        superClass()
                    }.get(msgVar).any()
                    val type = msgVar::class.java.field {
                        name = "field_type"
                        superClass()
                    }.get(msgVar).any()
                    val content = msgVar::class.java.field {
                        name = "field_content"
                        superClass()
                    }.get(msgVar).any()
                    AlertDialog.Builder(view.context)
                        .setTitle("msgId:$msgId, type:$type")
                        .setMessage("content:$content")
                        .setPositiveButton("确定") { _, _ -> callOriginal() }
                        .setNegativeButton("取消", null)
                        .show()
                    resultNull()
                }
            }
        }
    }
}
