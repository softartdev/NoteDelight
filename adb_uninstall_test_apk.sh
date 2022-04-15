echo Before:
adb shell pm list packages|grep com.softartdev

adb uninstall com.softartdev.noteroom
adb uninstall com.softartdev.noteroom.test
adb uninstall com.softartdev.notedelight.old
adb uninstall com.softartdev.notedelight.old.test

echo After:
adb shell pm list packages|grep com.softartdev
