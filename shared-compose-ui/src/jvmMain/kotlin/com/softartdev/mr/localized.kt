package com.softartdev.mr

import dev.icerock.moko.resources.StringResource

actual fun StringResource.contextLocalized(): String = this.localized()