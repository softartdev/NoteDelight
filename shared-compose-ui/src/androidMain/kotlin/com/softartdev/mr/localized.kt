package com.softartdev.mr

import android.content.Context
import dev.icerock.moko.resources.StringResource

// TODO remove
var staticApplicationContext: Context? = null

actual fun StringResource.localized(): String = requireNotNull(staticApplicationContext).getString(resourceId)