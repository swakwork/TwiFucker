package com.swak.twifucker.hook

abstract class BaseHook {
    var isInit: Boolean = false
    abstract val name: String
    abstract fun init()
}