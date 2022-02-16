package com.softartdev.notedelight.old

import androidx.multidex.MultiDexApplication
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.softartdev.notedelight.isInLeakCanaryAnalyzerProcess
import com.softartdev.notedelight.old.di.allAndroidModules
import com.softartdev.notedelight.old.util.PreferencesHelper
import com.softartdev.notedelight.old.util.ThemeHelper
import com.softartdev.notedelight.old.util.log.CrashlyticsAntilog
import com.softartdev.notedelight.old.util.log.NapierKoinLogger
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class NoteRoomApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        if (isInLeakCanaryAnalyzerProcess) return
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        Napier.base(antilog = if (BuildConfig.DEBUG) DebugAntilog() else CrashlyticsAntilog())
        startKoin {
            logger(NapierKoinLogger(Level.ERROR)) // TODO revert to Level.DEBUG after update Koin version above 3.1.5
            androidContext(this@NoteRoomApp)
            modules(allAndroidModules)
        }
        val preferencesHelper: PreferencesHelper = get()
        ThemeHelper.applyTheme(preferencesHelper.themeEntry, this)
    }

}
