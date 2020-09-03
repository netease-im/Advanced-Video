
# Stub project file, because it's not possible to assign PROVISIONING_PROFILE_SPECIFIER for both host app and extensions without pollution

import os
import sys
import plistlib

PROJECT_FILE = "NERtcSample-ScreenShare-iOS-Objective-C.xcodeproj/project.pbxproj"
APP_BUNDLE_ID = "com.netease.nmc.NERtcSample-ScreenShare-iOS-Objective-C"
EXT_BUNDLE_ID = "com.netease.nmc.NERtcSample-ScreenShare-iOS-Objective-C.Broadcast"

APP_PROVISION = sys.argv[1]
EXT_PROVISION = sys.argv[2]

BUNDLE_PROVISION_DICT = {
	APP_BUNDLE_ID: APP_PROVISION,
	EXT_BUNDLE_ID: EXT_PROVISION
}

os.system("plutil -convert xml1 {}".format(PROJECT_FILE))
pl = plistlib.readPlist(PROJECT_FILE)

for k,obj in pl["objects"].items():
	if (obj["isa"] == "XCBuildConfiguration" and obj.has_key("buildSettings") and obj["buildSettings"].has_key("CODE_SIGN_IDENTITY")):
		settings = obj["buildSettings"]
		settings["CODE_SIGN_IDENTITY"] = "iPhone Distribution"
		settings["PROVISIONING_PROFILE_SPECIFIER"] = BUNDLE_PROVISION_DICT[settings["PRODUCT_BUNDLE_IDENTIFIER"]]
		settings["CODE_SIGN_STYLE"] = "Manual"

plistlib.writePlist(pl, PROJECT_FILE)


# Stub ExportOptions.plist file
EXPORT_OPTIONS_FILE = "Workflow/ExportOptions.plist"
pl = plistlib.readPlist(EXPORT_OPTIONS_FILE)
pl["provisioningProfiles"] = BUNDLE_PROVISION_DICT.copy()
plistlib.writePlist(pl, EXPORT_OPTIONS_FILE)
