// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "_core_data_db_sqldelight",
  platforms: [
    .iOS("15.0")
  ],
  products: [
    .library(
      name: "_core_data_db_sqldelight",
      type: .none,
      targets: ["_core_data_db_sqldelight"]
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
      name: "_core_data_db_sqldelight",
      dependencies: [
        .product(
          name: "SQLCipher",
          package: "SQLCipher.swift"
        )
      ]
    )
  ]
)
