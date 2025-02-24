package demo.hook.func.find

import android.view.LayoutInflater
import android.view.View
import com.highcapable.yukihookapi.hook.factory.method
import me.hd.wauxv.databinding.ModuleDialogLocationBinding
import me.hd.wauxv.factory.showDialog
import me.hd.wauxv.hook.anno.HookAnno
import me.hd.wauxv.hook.anno.ViewAnno
import me.hd.wauxv.hook.base.common.CommonSwitchHooker
import me.hd.wauxv.hook.base.data.DescData
import me.hd.wauxv.hook.base.data.TypeData
import me.hd.wauxv.hook.base.impl.IDexFindBase
import me.hd.wauxv.hook.data.HostInfo
import me.hd.wauxv.hook.data.WechatProcess
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.wrap.DexMethod

@HookAnno
@ViewAnno
object LocationHooker : CommonSwitchHooker(), IDexFindBase {
    private object LocationMethod : DescData()
    private object LocationMethodWgs84 : DescData()
    private object LocationMethodDefault : DescData()

    private object LatitudeData : TypeData("latitude", floatDefVal = LATITUDE_DEF_VAL)
    private object LongitudeData : TypeData("longitude", floatDefVal = LONGITUDE_DEF_VAL)

    private const val LATITUDE_DEF_VAL = 39.90307f
    private const val LONGITUDE_DEF_VAL = 116.39773f

    override val funcName = "虚拟定位"
    override val funcDesc = "将腾讯定位SDK结果虚拟为指定经纬度"
    override val targetProcess = arrayOf(
        WechatProcess.MAIN_PROCESS.processName,
        WechatProcess.APP_BRAND_0.processName
    )

    override var onClick: ((View) -> Unit)? = { layoutView ->
        val binding = ModuleDialogLocationBinding.inflate(LayoutInflater.from(layoutView.context))
        binding.moduleDialogEdtLatitude.setText("${LatitudeData.floatVal}")
        binding.moduleDialogEdtLongitude.setText("${LongitudeData.floatVal}")
        layoutView.context.showDialog {
            title = funcName
            view = binding.root
            positiveButton {
                LatitudeData.floatVal = binding.moduleDialogEdtLatitude.text.toString().toFloat()
                LongitudeData.floatVal = binding.moduleDialogEdtLongitude.text.toString().toFloat()
            }
            negativeButton()
            neutralButton("重置") {
                LatitudeData.floatVal = LATITUDE_DEF_VAL
                LongitudeData.floatVal = LONGITUDE_DEF_VAL
            }
        }
    }

    override fun initOnce() {
        DexMethod(LocationMethod.desc)
            .getMethodInstance(HostInfo.appClassLoader)
            .hook {
                beforeIfEnabled {
                    val location = args(0).any()!!
                    location::class.java.apply {
                        method { name = "getLatitude" }.hook {
                            beforeIfEnabled {
                                result = LatitudeData.floatVal.toDouble()
                            }
                        }
                        method { name = "getLongitude" }.hook {
                            beforeIfEnabled {
                                result = LongitudeData.floatVal.toDouble()
                            }
                        }
                    }
                    removeSelf()
                }
            }
        DexMethod(LocationMethodWgs84.desc)
            .getMethodInstance(HostInfo.appClassLoader)
            .hook {
                beforeIfEnabled {
                    val location = args(0).any()!!
                    location::class.java.apply {
                        method { name = "getLatitude" }.hook {
                            beforeIfEnabled {
                                result = LatitudeData.floatVal.toDouble()
                            }
                        }
                        method { name = "getLongitude" }.hook {
                            beforeIfEnabled {
                                result = LongitudeData.floatVal.toDouble()
                            }
                        }
                    }
                    removeSelf()
                }
            }
        DexMethod(LocationMethodDefault.desc)
            .getMethodInstance(HostInfo.appClassLoader)
            .hook {
                beforeIfEnabled {
                    val location = args(0).any()!!
                    location::class.java.apply {
                        method { name = "getLatitude" }.hook {
                            beforeIfEnabled {
                                result = LatitudeData.floatVal.toDouble()
                            }
                        }
                        method { name = "getLongitude" }.hook {
                            beforeIfEnabled {
                                result = LongitudeData.floatVal.toDouble()
                            }
                        }
                    }
                    removeSelf()
                }
            }
    }

    override fun dexFind(initiate: DexKitBridge) {
        LocationMethod.desc = initiate.findMethod {
            matcher {
                name = "onLocationChanged"
                usingEqStrings("MicroMsg.SLocationListener")
            }
        }.single().toDexMethod().toString()
        LocationMethodWgs84.desc = initiate.findMethod {
            matcher {
                name = "onLocationChanged"
                usingEqStrings("MicroMsg.SLocationListenerWgs84")
            }
        }.single().toDexMethod().toString()
        LocationMethodDefault.desc = initiate.findMethod {
            matcher {
                name = "onLocationChanged"
                usingEqStrings("MicroMsg.DefaultTencentLocationManager", "[mlocationListener]error:%d, reason:%s")
            }
        }.single().toDexMethod().toString()
    }
}
