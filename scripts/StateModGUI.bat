@echo off
rem The HOMED and JREHOMED variables are set during the installation process.
rem The names are hopefully unique enough that they do not conflict with other
rem software settings while the batch file runs.
rem 
rem Guidelines for setting HOMED and JREHOMED:
rem
rem 1)If software is installed on a server and the location is consistently
rem   mapped to the same drive letter for all users and computers, specify an
rem   absolute path to the location.  This allows the software to be run from
rem   other drives and still find the install location for configuration and
rem   log files.  For example, use:
rem
rem      HOMED=P:\CDSS
rem      JREHOMED=P:\CDSS\jre_142
rem
rem   It is also possible to use a UNC (Universal Naming Convention) to indicate
rem   the installation point.  This can be used when a drive letter is not
rem   always mapped but a machine provides access to the software files.  For
rem   example:
rem
rem      HOMED=\\CDSSServer\CDSS
rem      JREHOMED=\\CDSSServer\CDSS\jre_142
rem
rem 2)If software is installed on a local drive (e.g., C:) or a CD drive
rem   (e.g., D:), and the software will ALWAYS be run from the drive where the
rem   software are installed, the drive letter can be omitted from the
rem   variables.  This allows the software to be installed/copied generically
rem   without further configuration.  If the software is run from a different
rem   drive, the software may not be able to find supporting files or open a
rem   log file.  For example, use:
rem
rem	HOMED=\CDSS
rem	JREHOMED=\CDSS\jre_142
rem
rem 3)If the software is installed on a local drive (e.g., C:) but may be run
rem   from a different drive, use approach (1).
rem
rem The following allows State of Colorado computers to access CDSS directly or
rem under the Apps folder.  Since non-state computers will likely not have this
rem directory structure, the default should apply for most of those computers.

SET HOMED=\CDSS
SET JREHOMED=\CDSS\JRE_142
Set JAR_HOMED="%HOMED%\bin"

rem Run the Java Runtime Environment (JRE), which runs the StateMod GUI.
rem If there is an error, try using "java" instead of "javaw" below.  Using
rem "java" will not hide the command shell window and therefore allows more
rem troubleshooting.

@echo on
"%JREHOMED%\bin\javaw" -mx512m -Djava.compiler=NONE -cp "%JAR_HOMED%\StateModGUI_142.jar;%JAR_HOMED%\StateMod_142.jar;%JAR_HOMED%\StateCU_142.jar;%JAR_HOMED%\HydroBaseDMI_142.jar;%JAR_HOMED%\SatmonSysDMI_142.jar;%JAR_HOMED%\RTi_Common_142.jar;%JAR_HOMED%\Xerces.jar;%JAR_HOMED%\jcommon.jar;%JAR_HOMED%\jfreechart.jar;%HOMED%\bin\TS_Services.jar" DWR.StateModGUI.StateModGUI -home "%HOMED%" %1 %2 %3 %4 %5 %6 %7
@echo off

Set HOMED=
Set JREHOMED=
Set JAR_HOMED=
