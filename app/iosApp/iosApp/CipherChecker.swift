//
//  CipherChecker.swift
//  iosApp
//
//  Created by Artur Babichev on 15.07.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation

class CipherChecker {

    func checkCipherVersion() -> String? {
        var result: String? = nil

        var rc: Int32
        var db: OpaquePointer? = nil
        var stmt: OpaquePointer? = nil
        let password: String = "correct horse battery staple"
        rc = sqlite3_open(":memory:", &db)
        if (rc != SQLITE_OK) {
            let errmsg = String(cString: sqlite3_errmsg(db))
            result = "Error opening database: \(errmsg)"
            NSLog(result ?? "❌ error")
        }
        rc = sqlite3_key(db, password, Int32(password.utf8CString.count))
        if (rc != SQLITE_OK) {
            let errmsg = String(cString: sqlite3_errmsg(db))
            result = "Error setting key: \(errmsg)"
            NSLog(result ?? "❌ error")
        }
        rc = sqlite3_prepare(db, "PRAGMA cipher_version;", -1, &stmt, nil)
        if (rc != SQLITE_OK) {
            let errmsg = String(cString: sqlite3_errmsg(db))
            result = "Error preparing SQL: \(errmsg)"
            NSLog(result ?? "❌ error")
        }
        rc = sqlite3_step(stmt)
        if (rc == SQLITE_ROW) {
            result = String(cString: sqlite3_column_text(stmt, 0))
            NSLog(result ?? "❌ error")
        } else {
            let errmsg = String(cString: sqlite3_errmsg(db))
            result = "Error retrieiving cipher_version: \(errmsg)"
            NSLog(result ?? "❌ error")
        }
        sqlite3_finalize(stmt)
        sqlite3_close(db)

        return result
    }
}
