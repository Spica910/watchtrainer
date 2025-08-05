@echo off
echo === Galaxy Watch Exercise Trainer App Build ===
echo.
echo This script will help you build the app with minimal memory usage.
echo.

REM Set memory limits
set GRADLE_OPTS=-Xmx256m -XX:MaxMetaspaceSize=256m -XX:+HeapDumpOnOutOfMemoryError
set _JAVA_OPTIONS=-Xmx256m

echo Step 1: Cleaning previous builds...
call gradlew.bat clean

echo.
echo Step 2: Building APK with limited memory...
call gradlew.bat assembleDebug --no-daemon --max-workers=1 -Dorg.gradle.jvmargs="-Xmx256m -XX:MaxMetaspaceSize=256m" -Dkotlin.compiler.execution.strategy="in-process"

echo.
if exist app\build\outputs\apk\debug\app-debug.apk (
    echo BUILD SUCCESS!
    echo APK location: app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo To install on your Galaxy Watch:
    echo 1. Enable Developer Mode on your watch
    echo 2. Connect via ADB: adb connect [watch_ip]
    echo 3. Install: adb install app\build\outputs\apk\debug\app-debug.apk
) else (
    echo BUILD FAILED!
    echo Please check the error messages above.
    echo.
    echo Common solutions:
    echo 1. Close other applications to free memory
    echo 2. Restart your computer
    echo 3. Use Android Studio for building
)

pause