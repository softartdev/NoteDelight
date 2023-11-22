package com.softartdev.notedelight.shared.usecase.crypt

import com.softartdev.notedelight.shared.PlatformSQLiteState
import com.softartdev.notedelight.shared.db.SafeRepo

class ChangePasswordUseCase(
    private val safeRepo: SafeRepo
): (CharSequence?, CharSequence?) -> Unit {

    override fun invoke(oldPass: CharSequence?, newPass: CharSequence?) {
        if (safeRepo.databaseState == PlatformSQLiteState.ENCRYPTED) {
            requireNotNull(oldPass)
            if (newPass.isNullOrEmpty()) {
                safeRepo.decrypt(oldPass)
            } else {
                safeRepo.rekey(oldPass, newPass)
            }
        } else {
            requireNotNull(newPass)
            safeRepo.encrypt(newPass)
        }
        safeRepo.relaunchListFlowCallback?.invoke()
    }
}