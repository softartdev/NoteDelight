// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "_core_data_db_room",
  platforms: [
    .iOS("14.1")
  ],
  products: [
    .library(
      name: "_core_data_db_room",
      type: .none,
      targets: ["_core_data_db_room"]
    )
  ],
  dependencies: [
    .package(
      url: "https://github.com/sqlcipher/SQLCipher.swift.git",
      exact: "4.16.0"
    )
  ],
  targets: [
    .target(
      name: "_core_data_db_room",
      dependencies: [
        .product(
          name: "SQLCipher",
          package: "SQLCipher.swift"
        )
      ]
    )
  ]
)
