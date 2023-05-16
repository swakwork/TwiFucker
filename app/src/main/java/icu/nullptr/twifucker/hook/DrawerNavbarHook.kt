package com.swak.twifucker.hook

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import dalvik.bytecode.Opcodes
import com.swak.twifucker.afterMeasure
import com.swak.twifucker.data.TwitterItem
import com.swak.twifucker.exceptions.CachedHookNotFound
import com.swak.twifucker.hook.HookEntry.Companion.dexKit
import com.swak.twifucker.hook.HookEntry.Companion.loadDexKit
import com.swak.twifucker.hostAppLastUpdate
import com.swak.twifucker.moduleLastModify
import com.swak.twifucker.modulePrefs
import com.swak.twifucker.ui.SettingsDialog
import com.swak.twifucker.ui.SettingsDialog.Companion.PREF_HIDDEN_BOTTOM_NAVBAR_ITEMS

object DrawerNavbarHook : BaseHook() {
    override val name: String
        get() = "DrawerNavbarHook"

    private const val HOOK_DRAWER_ITEMS_CLASS = "hook_drawer_items_class"
    private const val HOOK_DRAWER_ITEMS_GET_METHOD = "hook_drawer_items_get_method"
    private const val HOOK_BOTTOM_NAVBAR_ITEMS_CLASS = "hook_bottom_navbar_items_class"
    private const val HOOK_BOTTOM_NAVBAR_ITEMS_GET_METHOD = "hook_bottom_navbar_items_get_method"

    var drawerItems = mutableListOf<TwitterItem>()
    var bottomNavbarItems = mutableListOf<TwitterItem>()

    private lateinit var drawerItemsClassName: String
    private lateinit var drawerItemsGetMethod: String
    private lateinit var bottomNavbarItemsClassName: String
    private lateinit var bottomNavbarItemsGetMethod: String

    override fun init() {
        try {
            loadHookInfo()
        } catch (t: Throwable) {
            Log.e(t)
            return
        }

        MethodFinder.fromClass(loadClass(drawerItemsClassName)).filterByName(drawerItemsGetMethod)
            .first().createHook {
                afterMeasure(name) { param ->
                    val hiddenItems = modulePrefs.getStringSet(
                        SettingsDialog.PREF_HIDDEN_DRAWER_ITEMS, mutableSetOf()
                    )
                    drawerItems.clear()
                    val originalDrawerItems = param.result as List<*>
                    val newDrawerItems = originalDrawerItems.toMutableList()
                    originalDrawerItems.forEach { item ->
                        val itemName = item.toString()
                        drawerItems.add(
                            TwitterItem(
                                itemName, hiddenItems?.contains(itemName) == false
                            )
                        )
                        if (hiddenItems?.contains(itemName) == true && itemName != "SettingsAndSupportGroup") {
                            newDrawerItems.remove(item)
                        }
                    }
                    param.result = newDrawerItems.toList()
                }
            }

        MethodFinder.fromClass(loadClass(bottomNavbarItemsClassName)).filterByName(
            bottomNavbarItemsGetMethod
        ).first().createHook {
            afterMeasure(name) { param ->
                val hiddenItems =
                    modulePrefs.getStringSet(PREF_HIDDEN_BOTTOM_NAVBAR_ITEMS, mutableSetOf())
                bottomNavbarItems.clear()
                val originalBottomNavbarItems = param.result as List<*>
                val newBottomNavbarItems = originalBottomNavbarItems.toMutableList()
                originalBottomNavbarItems.forEach { item ->
                    val itemName = item.toString()
                    bottomNavbarItems.add(
                        TwitterItem(
                            itemName, hiddenItems?.contains(itemName) == false
                        )
                    )
                    if (hiddenItems?.contains(itemName) == true && itemName != "HOME") {
                        newBottomNavbarItems.remove(item)
                    }
                }
                param.result = newBottomNavbarItems.toList()
            }
        }
    }

    private fun loadCachedHookInfo() {
        modulePrefs.let {
            drawerItemsClassName =
                it.getString(HOOK_DRAWER_ITEMS_CLASS, null) ?: throw CachedHookNotFound()
            drawerItemsGetMethod =
                it.getString(HOOK_DRAWER_ITEMS_GET_METHOD, null) ?: throw CachedHookNotFound()
            bottomNavbarItemsClassName =
                it.getString(HOOK_BOTTOM_NAVBAR_ITEMS_CLASS, null) ?: throw CachedHookNotFound()
            bottomNavbarItemsGetMethod = it.getString(HOOK_BOTTOM_NAVBAR_ITEMS_GET_METHOD, null)
                ?: throw CachedHookNotFound()
        }
    }

    private fun saveHookInfo() {
        modulePrefs.let {
            it.putString(HOOK_DRAWER_ITEMS_CLASS, drawerItemsClassName)
            it.putString(HOOK_DRAWER_ITEMS_GET_METHOD, drawerItemsGetMethod)
            it.putString(HOOK_BOTTOM_NAVBAR_ITEMS_CLASS, bottomNavbarItemsClassName)
            it.putString(HOOK_BOTTOM_NAVBAR_ITEMS_GET_METHOD, bottomNavbarItemsGetMethod)
        }
    }

    private fun searchHook() {
        val drawerClassDesc = dexKit.findMethodUsingOpPrefixSeq {
            opSeq = intArrayOf(
                Opcodes.OP_CONST_16,
                Opcodes.OP_NEW_ARRAY,
                Opcodes.OP_SGET_OBJECT,
                Opcodes.OP_CONST_4,
                Opcodes.OP_APUT_OBJECT,
                Opcodes.OP_SGET_OBJECT,
                Opcodes.OP_CONST_4,
                Opcodes.OP_APUT_OBJECT,
                Opcodes.OP_SGET_OBJECT,
                Opcodes.OP_CONST_4,
                Opcodes.OP_APUT_OBJECT,
                Opcodes.OP_SGET_OBJECT,
                Opcodes.OP_CONST_4,
                Opcodes.OP_APUT_OBJECT,
                Opcodes.OP_SGET_OBJECT,
                Opcodes.OP_CONST_4,
                Opcodes.OP_APUT_OBJECT,
                Opcodes.OP_SGET_OBJECT,
                Opcodes.OP_CONST_4,
                Opcodes.OP_APUT_OBJECT,
                Opcodes.OP_SGET_OBJECT,
                Opcodes.OP_CONST_4,
                Opcodes.OP_APUT_OBJECT,
                Opcodes.OP_SGET_OBJECT,
                Opcodes.OP_CONST_4,
                Opcodes.OP_APUT_OBJECT,
                Opcodes.OP_SGET_OBJECT,
                Opcodes.OP_CONST_16,
                Opcodes.OP_APUT_OBJECT,
                Opcodes.OP_SGET_OBJECT,
                Opcodes.OP_CONST_16,
                Opcodes.OP_APUT_OBJECT,
                Opcodes.OP_SGET_OBJECT,
                Opcodes.OP_CONST_16,
                Opcodes.OP_APUT_OBJECT,
                Opcodes.OP_SGET_OBJECT,
                Opcodes.OP_CONST_16,
                Opcodes.OP_APUT_OBJECT,
                Opcodes.OP_SGET_OBJECT,
                Opcodes.OP_CONST_16,
                Opcodes.OP_APUT_OBJECT,
                Opcodes.OP_SGET_OBJECT,
                Opcodes.OP_CONST_16,
                Opcodes.OP_APUT_OBJECT,
                Opcodes.OP_SGET_OBJECT,
                Opcodes.OP_CONST_16,
                Opcodes.OP_APUT_OBJECT,
                Opcodes.OP_SGET_OBJECT,
                Opcodes.OP_CONST_16,
                Opcodes.OP_APUT_OBJECT,
                Opcodes.OP_SGET_OBJECT,
            )
            methodName = "invoke"
            methodReturnType = Object::class.java.name
            methodParamTypes = emptyArray()
        }.first()

        val bottomNavbarDesc = dexKit.findMethodUsingOpCodeSeq {
            opSeq = intArrayOf(
                Opcodes.OP_MOVE_RESULT,
                Opcodes.OP_IGET_OBJECT,
                Opcodes.OP_INVOKE_INTERFACE,
                Opcodes.OP_MOVE_RESULT,
                Opcodes.OP_SGET_OBJECT,
                Opcodes.OP_CONST_4,
                Opcodes.OP_NEW_ARRAY,
                Opcodes.OP_IF_EQZ,
                Opcodes.OP_SGET_OBJECT,
            )
            methodReturnType = List::class.java.name
            methodParamTypes = emptyArray()
        }.first()

        drawerItemsClassName = drawerClassDesc.declaringClassName
        drawerItemsGetMethod = drawerClassDesc.name
        bottomNavbarItemsClassName = bottomNavbarDesc.declaringClassName
        bottomNavbarItemsGetMethod = bottomNavbarDesc.name
    }

    private fun loadHookInfo() {
        val hookDrawerLastUpdate = modulePrefs.getLong("hook_drawer_last_update", 0)

        Log.d("hookDrawerLastUpdate: $hookDrawerLastUpdate, hostAppLastUpdate: $hostAppLastUpdate, moduleLastModify: $moduleLastModify")

        val timeStart = System.currentTimeMillis()

        if (hookDrawerLastUpdate > hostAppLastUpdate && hookDrawerLastUpdate > moduleLastModify) {
            loadCachedHookInfo()
            Log.d("Drawer Hook load time: ${System.currentTimeMillis() - timeStart} ms")
        } else {
            loadDexKit()
            searchHook()
            Log.d("Drawer Hook search time: ${System.currentTimeMillis() - timeStart} ms")
            saveHookInfo()
            modulePrefs.putLong("hook_drawer_last_update", System.currentTimeMillis())
        }
    }
}
