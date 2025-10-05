//
//  Note_Delight_Unit_Tests.swift
//  Note Delight Unit-Tests
//
//  Created by Artur Babichev on 08.01.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import XCTest
@testable import iosApp

final class Note_Delight_Unit_Tests: XCTestCase {

    override func setUpWithError() throws {
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDownWithError() throws {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

    func testExample() throws {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
        // Any test you write for XCTest can be annotated as throws and async.
        // Mark your test throws to produce an unexpected failure when your test encounters an uncaught error.
        // Mark your test async to allow awaiting for asynchronous code to complete. Check the results with assertions afterwards.
    }
    
    func testCipherChecker() throws {
        let cipherChecker = CipherChecker()
        let cipherVersion = cipherChecker.checkCipherVersion()
        XCTAssertNotNil(cipherVersion)
        XCTAssertEqual(cipherVersion, "4.5.4 community")
    }

    func testPerformanceExample() throws {
        // This is an example of a performance test case.
        measure {
            // Put the code you want to measure the time of here.
        }
    }

}
