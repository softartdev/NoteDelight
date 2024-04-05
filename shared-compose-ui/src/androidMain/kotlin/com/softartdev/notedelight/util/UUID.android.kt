package com.softartdev.notedelight.util

import java.util.UUID as JUUID

actual class UUID(private val jUUID: JUUID) {

    override fun toString(): String = jUUID.toString()

    actual companion object {
        actual fun randomUUID(): UUID = UUID(jUUID = JUUID.randomUUID())
    }
}
