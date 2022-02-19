package com.softartdev.notedelight.old.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.softartdev.notedelight.old.R
import com.softartdev.notedelight.old.ui.base.BaseActivity
import com.softartdev.notedelight.old.util.tintIcon

class SettingsActivity : BaseActivity(R.layout.activity_settings, false) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_oss, menu)
        menu.findItem(R.id.action_oss).tintIcon(this)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_oss -> {
            startActivity(Intent(this, OssLicensesMenuActivity::class.java))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}