package com.softartdev.notedelight.shared

expect fun <T> runOnUiThread(block: () -> T): T