package com.softartdev.notedelight

import androidx.multidex.MultiDexApplication
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.softartdev.notedelight.di.appModule
import com.softartdev.notedelight.di.mvvmModule
import com.softartdev.notedelight.util.PreferencesHelper
import com.softartdev.notedelight.util.ThemeHelper
import com.softartdev.notedelight.util.log.CrashlyticsTree
import com.softartdev.notedelight.util.log.TimberKoinLogger
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class NoteRoomApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        if (isInLeakCanaryAnalyzerProcess) return
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        Timber.plant(if (BuildConfig.DEBUG) Timber.DebugTree() else CrashlyticsTree())
        startKoin {
            logger(TimberKoinLogger(Level.DEBUG))
            androidContext(this@NoteRoomApp)
            modules(appModule + mvvmModule)
        }
        val preferencesHelper: PreferencesHelper = get()
        ThemeHelper.applyTheme(preferencesHelper.themeEntry, this)
    }

}
