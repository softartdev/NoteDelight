// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "KotlinMultiplatformLinkedPackageDylib",
  platforms: [
    .iOS("15.0")
  ],
  products: [
    .library(
      name: "KotlinMultiplatformLinkedPackageDylib",
      type: .dynamic,
      targets: ["KotlinMultiplatformLinkedPackageDylib"]
    )
  ],
  dependencies: [
    .package(path: "../_core_data_db_sqldelight")
  ],
  targets: [
    .target(
      name: "KotlinMultiplatformLinkedPackageDylib",
      dependencies: [
        .product(name: "_core_data_db_sqldelight", package: "_core_data_db_sqldelight")
      ]
    )
  ]
)
