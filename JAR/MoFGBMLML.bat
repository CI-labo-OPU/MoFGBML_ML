@echo off

set PROPERTIES=%1
set DATANAME=%2
set MOPNO=%3

rem 3 times 10-fold cross-validation
for /l %%r in (0,1,2) do (
	for /l %%c in (0,1,9) do (
		call Java -jar MoFGBMLML.jar %PROPERTIES% dataset/%DATANAME%/a%%r_%%c_%DATANAME%-10tra.dat dataset/%DATANAME%/a%%r_%%c_%DATANAME%-10tst.dat trial%%r%%c %MOPNO%
	)
)
