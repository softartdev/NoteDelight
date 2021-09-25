package com.softartdev.notedelight

import androidx.multidex.MultiDexApplication
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.softartdev.notedelight.di.appModule
import com.softartdev.notedelight.di.mvvmModule
import com.softartdev.notedelight.util.PreferencesHelper
import com.softartdev.notedelight.util.ThemeHelper
import com.softartdev.notedelight.util.log.CrashlyticsAntilog
import com.softartdev.notedelight.util.log.NapierKoinLogger
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
            logger(NapierKoinLogger(Level.DEBUG))
            androidContext(this@NoteRoomApp)
            modules(appModule + mvvmModule)
        }
        val preferencesHelper: PreferencesHelper = get()
        ThemeHelper.applyTheme(preferencesHelper.themeEntry, this)
    }

}
