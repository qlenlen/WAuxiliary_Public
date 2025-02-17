package demo.hook.func.find

import me.hd.wauxv.hook.anno.HookAnno
import me.hd.wauxv.hook.anno.ViewAnno
import me.hd.wauxv.hook.base.common.CommonSwitchHooker
import me.hd.wauxv.hook.base.impl.DescCache
import me.hd.wauxv.hook.base.impl.IDexFindBase
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.wrap.DexMethod

@HookAnno
@ViewAnno
object MockScanHooker : CommonSwitchHooker(), IDexFindBase {
    override val funcName = "模拟扫码"
    override val funcDesc = "将二维码识别方式模拟成微信扫一扫"

    enum class ScanScene(val source: Int, val a8KeyScene: Int) {
        WECHAT_SCAN(0, 4),// 微信扫一扫识别
        ALBUM_SCAN(1, 34),// 手机相册扫码识别
        LONG_PRESS_SCAN(4, 37)// 长按图片识别
    }

    override fun initOnce() {
        DexMethod(MockScanMethod.desc)
            .getMethodInstance(appClassLoader!!)
            .hook {
                beforeIfEnabled {
                    val source = args[2] as Int
                    val a8KeyScene = args[3] as Int
                    val matchedScene = ScanScene.entries.find { it.source == source && it.a8KeyScene == a8KeyScene }
                    if (matchedScene == ScanScene.ALBUM_SCAN || matchedScene == ScanScene.LONG_PRESS_SCAN) {
                        args[2] = ScanScene.WECHAT_SCAN.source
                        args[3] = ScanScene.WECHAT_SCAN.a8KeyScene
                    }
                }
            }
    }

    private object MockScanMethod : DescCache()

    override fun dexFind(initiate: DexKitBridge) {
        MockScanMethod.desc = initiate.findMethod {
            matcher {
                usingEqStrings("MicroMsg.QBarStringHandler", "key_offline_scan_show_tips")
            }
        }.single().toDexMethod().toString()
    }
}
