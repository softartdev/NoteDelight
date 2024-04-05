package com.softartdev.notedelight.util

import platform.Foundation.NSUUID

actual class UUID(private val nsUUID: NSUUID) {

    override fun toString(): String = nsUUID.UUIDString

    actual companion object {
        actual fun randomUUID(): UUID = UUID(nsUUID = NSUUID.UUID())
    }
}