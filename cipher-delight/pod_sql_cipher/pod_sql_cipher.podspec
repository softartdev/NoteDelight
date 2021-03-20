Pod::Spec.new do |spec|
    spec.name                     = 'SQLCipher'
    spec.version                  = '4.4.3'
    spec.homepage                 = 'https://www.zetetic.net/sqlcipher/'
#     spec.source                   = { :git => "https://github.com/sqlcipher/sqlcipher.git", :tag => "v4.4.3" }
    spec.authors                  = 'Zetetic LLC'
    spec.license                  = { :type => "BSD", :file => "LICENSE" }
    spec.summary                  = 'Full Database Encryption for SQLite.'
    spec.source_files             = 'src/*'
    spec.public_header_files      = 'src/*.h'

#     spec.platform     = :ios, "8.0"
    spec.platform     = :ios
    spec.ios.deployment_target = "13.5"
#     spec.osx.deployment_target = "10.9"
#     spec.watchos.deployment_target = "2.0"
#     spec.tvos.deployment_target = "9.0"
#
#     spec.frameworks = "Foundation", "Security"
#     spec.requires_arc = false

  # spec.xcconfig = { "HEADER_SEARCH_PATHS" => "$(SDKROOT)/usr/include/libxml2" }
  # spec.dependency "JSONKit", "~> 1.4"
end