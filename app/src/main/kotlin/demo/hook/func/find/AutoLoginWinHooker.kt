package demo.hook.func.find

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.android.ButtonClass
import me.hd.wauxv.data.factory.PackageName
import me.hd.wauxv.databinding.ModuleDialogAutoLoginWinBinding
import me.hd.wauxv.factory.showDialog
import me.hd.wauxv.hook.anno.HookAnno
import me.hd.wauxv.hook.anno.ViewAnno
import me.hd.wauxv.hook.base.common.CommonSwitchHooker
import me.hd.wauxv.hook.base.data.TypeData
import me.hd.wauxv.hook.data.HostInfo

@HookAnno
@ViewAnno
object AutoLoginWinHooker : CommonSwitchHooker() {
    private object AutoSyncMsgData : TypeData("autoSyncMsg", booleanDefVal = true)
    private object ShowLoginDeviceData : TypeData("showLoginDevice", booleanDefVal = true)
    private object AutoLoginDeviceData : TypeData("autoLoginDevice", booleanDefVal = false)

    private const val AUTO_SYNC_MSG = 0b001   // 同步最近消息
    private const val SHOW_LOGIN_DEVICE = 0b010 // 显示登录设备
    private const val AUTO_LOGIN_DEVICE = 0b100 // 自动登录设备
    private val ExtDeviceWXLoginUIClass by lazyClass("${PackageName.WECHAT}.plugin.webwx.ui.ExtDeviceWXLoginUI") { HostInfo.appClassLoader }

    override val funcName = "自动登录"
    override val funcDesc = "微信请求登录时自动勾选项及点击按钮"

    override var onClick: ((View) -> Unit)? = { layoutView ->
        val binding = ModuleDialogAutoLoginWinBinding.inflate(LayoutInflater.from(layoutView.context))
        binding.moduleDialogCbAutoSyncMsg.isChecked = AutoSyncMsgData.booleanVal
        binding.moduleDialogCbShowLoginDevice.isChecked = ShowLoginDeviceData.booleanVal
        binding.moduleDialogCbAutoLoginDevice.isChecked = AutoLoginDeviceData.booleanVal
        layoutView.context.showDialog {
            title = funcName
            view = binding.root
            positiveButton("保存") {
                AutoSyncMsgData.booleanVal = binding.moduleDialogCbAutoSyncMsg.isChecked
                ShowLoginDeviceData.booleanVal = binding.moduleDialogCbShowLoginDevice.isChecked
                AutoLoginDeviceData.booleanVal = binding.moduleDialogCbAutoLoginDevice.isChecked
            }
            negativeButton()
        }
    }

    override fun initOnce() {
        ExtDeviceWXLoginUIClass.apply {
            method {
                name = "onCreate"
                param(BundleClass)
            }.hook {
                beforeIfEnabled {
                    val activity = instance<Activity>()
                    var functionControl = 0
                    if (AutoSyncMsgData.booleanVal) functionControl = functionControl or AUTO_SYNC_MSG
                    if (ShowLoginDeviceData.booleanVal) functionControl = functionControl or SHOW_LOGIN_DEVICE
                    if (AutoLoginDeviceData.booleanVal) functionControl = functionControl or AUTO_LOGIN_DEVICE
                    activity.intent.putExtra("intent.key.function.control", functionControl)
                }
            }
            method {
                name = "initView"
                emptyParam()
            }.hook {
                afterIfEnabled {
                    val button = field {
                        type = ButtonClass
                    }.get(instance).cast<Button>()!!
                    button.callOnClick()
                }
            }
        }
    }
}
