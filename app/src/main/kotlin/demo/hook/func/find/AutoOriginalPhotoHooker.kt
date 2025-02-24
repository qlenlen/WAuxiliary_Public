package demo.hook.func.find

import android.app.Activity
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import me.hd.wauxv.data.factory.PackageName
import me.hd.wauxv.hook.anno.HookAnno
import me.hd.wauxv.hook.anno.ViewAnno
import me.hd.wauxv.hook.base.common.CommonSwitchHooker
import me.hd.wauxv.hook.data.HostInfo

@HookAnno
@ViewAnno
object AutoOriginalPhotoHooker : CommonSwitchHooker() {
    private val AlbumPreviewUIClass by lazyClass("${PackageName.WECHAT}.plugin.gallery.ui.AlbumPreviewUI") { HostInfo.appClassLoader }
    private val ImagePreviewUIClass by lazyClass("${PackageName.WECHAT}.plugin.gallery.ui.ImagePreviewUI") { HostInfo.appClassLoader }

    override val funcName = "自动原图"
    override val funcDesc = "在发送图片和视频时自动勾选原图选项"

    override fun initOnce() {
        AlbumPreviewUIClass.apply {
            method {
                name = "onCreate"
                param(BundleClass)
            }.hook {
                beforeIfEnabled {
                    val activity = instance<Activity>()
                    activity.intent.putExtra("send_raw_img", true)
                }
            }
        }
        ImagePreviewUIClass.apply {
            method {
                name = "onCreate"
                param(BundleClass)
            }.hook {
                beforeIfEnabled {
                    val activity = instance<Activity>()
                    activity.intent.putExtra("send_raw_img", true)
                }
            }
        }
    }
}
