@echo on
SET PLATFORM=android-8
SET ANDROID_SDK_ROOT=C:\Android\android-sdk
SET PRJ_DIR=D:\Workspaces\sqLite\raSQLite
SET CLASS_PKG_PREFIX=ra.sqlite
cd %PRJ_DIR%\bin\classes
javah -classpath %ANDROID_SDK_ROOT%\platforms\%PLATFORM%\android.jar;.  %CLASS_PKG_PREFIX%.%~n1
pause