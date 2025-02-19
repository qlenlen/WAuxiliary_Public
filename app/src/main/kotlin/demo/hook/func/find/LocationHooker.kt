package demo.hook.func.find

import android.view.LayoutInflater
import android.view.View
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.LooperClass
import me.hd.wauxv.databinding.ModuleDialogLocationBinding
import me.hd.wauxv.factory.showDialog
import me.hd.wauxv.hook.anno.HookAnno
import me.hd.wauxv.hook.anno.ViewAnno
import me.hd.wauxv.hook.base.common.CommonSwitchHooker
import me.hd.wauxv.hook.base.data.TypeData
import me.hd.wauxv.hook.data.HostInfo

@HookAnno
@ViewAnno
object LocationHooker : CommonSwitchHooker() {
    private object LatitudeData : TypeData("latitude", floatDefVal = LATITUDE_DEF_VAL)
    private object LongitudeData : TypeData("longitude", floatDefVal = LONGITUDE_DEF_VAL)

    private const val LATITUDE_DEF_VAL = 39.90307f
    private const val LONGITUDE_DEF_VAL = 116.39773f
    private val ManagerClass by lazyClass("com.tencent.map.geolocation.sapp.TencentLocationManager") { HostInfo.appClassLoader }

    override val funcName = "虚拟定位"
    override val funcDesc = "将腾讯定位SDK结果虚拟为自定义经纬度"

    override var onClick: ((View) -> Unit)? = {
        val binding = ModuleDialogLocationBinding.inflate(LayoutInflater.from(it.context))
        binding.moduleDialogEdtLatitude.setText("${LatitudeData.floatVal}")
        binding.moduleDialogEdtLongitude.setText("${LongitudeData.floatVal}")
        it.context.showDialog {
            title = funcName
            view = binding.root
            positiveButton {
                LatitudeData.floatVal = binding.moduleDialogEdtLatitude.text.toString().toFloat()
                LongitudeData.floatVal = binding.moduleDialogEdtLongitude.text.toString().toFloat()
            }
            negativeButton()
            neutralButton("默认") {
                LatitudeData.floatVal = LATITUDE_DEF_VAL
                LongitudeData.floatVal = LONGITUDE_DEF_VAL
            }
        }
    }

    override fun initOnce() {
        ManagerClass.method {
            name = "requestLocationUpdates"
            paramCount = 3
            param { it[2] == LooperClass }
        }.hook {
            beforeIfEnabled {
                val listener = args[1] as Any
                listener::class.java.method {
                    paramCount = 10
                }.hook {
                    beforeIfEnabled {
                        args[1] = LatitudeData.floatVal
                        args[2] = LongitudeData.floatVal
                    }
                }
            }
        }
    }
}
