@echo off

SET out_path=build\source\

@rem set envs
echo Copy
xcopy /Y x86\* %out_path%Debug
xcopy /Y data\* %out_path%data\/s/e


pause