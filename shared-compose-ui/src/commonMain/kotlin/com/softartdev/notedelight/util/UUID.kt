package com.softartdev.notedelight.util

expect class UUID {

    companion object {
        fun randomUUID(): UUID
    }
}