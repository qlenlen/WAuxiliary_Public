package demo.hook.debug

import android.view.View
import com.highcapable.yukihookapi.hook.log.YLog
import me.hd.wauxv.hook.anno.HookAnno
import me.hd.wauxv.hook.anno.ViewAnno
import me.hd.wauxv.hook.base.common.CommonSwitchHooker

@HookAnno
@ViewAnno
object DevDebugHooker : CommonSwitchHooker(true) {

    override val funcName = "[测]"
    override var onClick: ((View) -> Unit)? = {
    }

    override fun initOnce() {
        YLog.error("DevDebugHooker initOnce")
    }
}
