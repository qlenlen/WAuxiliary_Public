package demo.hook.func.find

import android.view.View
import android.widget.Toast
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
    private object RoundAvatarMethod : DescData()

    override val funcName = "圆形头像"
    override val funcDesc = "可自定义微信全局头像渲染的圆形弧度"

    override var onClick: ((View) -> Unit)? = { layoutView ->
        Toast.makeText(layoutView.context, "暂不支持自定义弧度哦~", Toast.LENGTH_SHORT).show()
    }

    override fun initOnce() {
        DexMethod(RoundAvatarMethod.desc)
            .getMethodInstance(HostInfo.appClassLoader)
            .hook {
                beforeIfEnabled {
                    args(2).set(0.38f)
                }
            }
    }

    override fun dexFind(initiate: DexKitBridge) {
        RoundAvatarMethod.desc = initiate.findMethod {
            matcher {
                paramTypes(ImageViewClass, StringClass, FloatType, BooleanType)
                usingEqStrings("MicroMsg.AvatarDrawable")
            }
        }.single().toDexMethod().toString()
    }
}
