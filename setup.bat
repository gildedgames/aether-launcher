@echo off
:: Variables
set DIRECTORY=%~dp0gnuwin32
set PATH=%PATH%;%DIRECTORY%
title Aether II Launcher Workspace Setup

goto :ask_eclipse

:: Ask user if the eclipse workspace should be reinstalled.
:ask_eclipse
echo.
echo Would you like to install your eclipse workspace?

set choice=
set /P choice="[Y/N] "

if not '%choice%'=='' set choice=%choice:~0,1%

echo.
if /I '%choice%'=='y' goto :install_eclipse
if /I '%choice%'=='n' goto :finish

echo Invalid selection.
goto :ask_eclipse

:: Delete old eclipse workspace and extract eclipse.zip
:install_eclipse
echo.
IF EXIST eclipse (
echo Deleting eclipse...
RD /S /Q "eclipse"
)
echo Extracting eclipse workspace...
unzip -q eclipse.zip
echo Finished eclipse setup.
goto :finish

:: Finish!
:finish
echo.
pause
exit
