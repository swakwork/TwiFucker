package icu.nullptr.twifucker.hook

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.finders.FieldFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import icu.nullptr.twifucker.afterMeasure
import icu.nullptr.twifucker.isEntryNeedsRemove

object JsonNotificationsTabHook : BaseHook() {
    override val name: String
        get() = "JsonNotificationsTabHook"

    override fun init() {
        val jsonNotificationEntryClass =
            loadClass("com.twitter.model.json.notificationstab")
        val jsonNotiicationEntryMapperClass =
            loadClass("com.twitter.model.json.notificationstab\$\$JsonObjectMapper")

        val entryIdField =
            FieldFinder.fromClass(jsonNotificationEntryClass).filterByType(String::class.java).first()
        val contentField =
            FieldFinder.fromClass(jsonNotificationEntryClass).filter { type.isInterface }.first()

        MethodFinder.fromClass(jsonNotiicationEntryMapperClass).filterByName("_parse")
            .filterByReturnType(jsonNotificationEntryClass).first().createHook {
                afterMeasure(name) { param ->
                    param.result ?: return@afterMeasure
                    // val entryId = entryIdField.get(param.result) as String
                    Log.i("Swak: "+entryIdField)
                    Log.i("Swak: "+contentField)
                    // Log.i("Swak: "+entryId)
                    // if (isEntryNeedsRemove(entryId)) {
                    //     contentField.set(param.result, null)
                    //     Log.d("Remove timeline entry item: $entryId")
                    // }
                }
            }
    }
}
