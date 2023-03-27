package com.softartdev.mr

import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.desc

actual fun StringResource.contextLocalized(): String = this.desc().localized()