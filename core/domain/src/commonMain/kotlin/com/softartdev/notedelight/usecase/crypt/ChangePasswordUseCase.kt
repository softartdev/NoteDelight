package com.softartdev.notedelight.usecase.crypt

import com.softartdev.notedelight.model.PlatformSQLiteState
import com.softartdev.notedelight.repository.SafeRepo


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