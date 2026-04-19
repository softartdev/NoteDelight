package com.softartdev.notedelight.ui

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.testing.TestLifecycleOwner
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import co.touchlab.kermit.Logger
import co.touchlab.kermit.platformLogWriter
import com.softartdev.notedelight.App
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.di.sharedModules
import com.softartdev.notedelight.di.uiTestModules
import com.softartdev.notedelight.navigation.AppNavGraph
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.repository.SafeRepo
import com.softartdev.notedelight.util.kermitLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.java.KoinJavaComponent
import java.io.File

abstract class AbstractNavigationTest {
    internal val logger = Logger.withTag("ℹ️NavigationTest")

    abstract val composeTestRule: ComposeContentTestRule

    private lateinit var navController: NavHostController

    private lateinit var router: Router

    private lateinit var logJob: Job

    open val koinModules: List<Module> = sharedModules + uiTestModules

    @Before
    fun setUp() = runTest {
        Logger.setLogWriters(platformLogWriter())
        when (GlobalContext.getKoinApplicationOrNull()) {
            null -> startKoin {
                kermitLogger()
                modules(koinModules)
            }
            else -> loadKoinModules(koinModules)
        }
        val safeRepo: SafeRepo = KoinJavaComponent.get(SafeRepo::class.java)
        safeRepo.closeDatabase()
        sequenceOf("", "-wal", "-shm", "-journal")
            .map(safeRepo.dbPath::plus)
            .map(::File)
            .filter(File::exists)
            .forEach(File::delete)
        safeRepo.buildDbIfNeed()
        val noteDAO: NoteDAO = KoinJavaComponent.get(NoteDAO::class.java)
        noteDAO.deleteAll()
        val lifecycleOwner = TestLifecycleOwner()
        router = KoinJavaComponent.get(Router::class.java)
        composeTestRule.setContent {
            navController = rememberNavController()
            LaunchedEffect(navController, lifecycleOwner) {
                navController.setLifecycleOwner(lifecycleOwner)
            }
            CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner) {
                App(router, navController)
            }
        }
        logJob = launch(context = Dispatchers.Unconfined + SupervisorJob()) {
            navController.currentBackStack.map { entries: List<NavBackStackEntry> ->
                entries.map(NavBackStackEntry::destination)
            }.collect { destinations: List<NavDestination> ->
                logger.i("🧭 Back stack flow:\n${destinations.joinToString(separator = "\n")}")
            }
        }
        composeTestRule.awaitIdle()
    }

    @After
    fun tearDown() = runTest {
        logJob.cancel()
        unloadKoinModules(koinModules)
        Logger.setLogWriters()
    }

    open fun routerLaunchSingleTopTest() = runTest {
        assertEquals(2, navController.currentBackStack.value.size)
        withContext(Dispatchers.Main.immediate) { router.navigate(AppNavGraph.Settings) }
        composeTestRule.awaitIdle()
        assertEquals(3, navController.currentBackStack.value.size)
        withContext(Dispatchers.Main.immediate) { router.navigate(AppNavGraph.ChangePasswordDialog) }
        composeTestRule.awaitIdle()
        assertEquals(4, navController.currentBackStack.value.size)
        withContext(Dispatchers.Main.immediate) { router.navigate(AppNavGraph.ChangePasswordDialog) }
        composeTestRule.awaitIdle()
        assertEquals(4, navController.currentBackStack.value.size)
    }

    open fun navControllerLaunchDoubleTopTest() = runTest {
        assertEquals(2, navController.currentBackStack.value.size)
        withContext(Dispatchers.Main.immediate) { navController.navigate(AppNavGraph.Settings) }
        composeTestRule.awaitIdle()
        assertEquals(3, navController.currentBackStack.value.size)
        withContext(Dispatchers.Main.immediate) { navController.navigate(AppNavGraph.ChangePasswordDialog) }
        composeTestRule.awaitIdle()
        assertEquals(4, navController.currentBackStack.value.size)
        withContext(Dispatchers.Main.immediate) { navController.navigate(AppNavGraph.ChangePasswordDialog) }
        composeTestRule.awaitIdle()
        assertEquals(5, navController.currentBackStack.value.size)
    }
}
