@echo off

set PROPERTIES=%1
set DATANAME=%2

rem 3 times 10-fold cross-validation
for /l %%r in (0,1,2) do (
	for /l %% c in (0,1,9) do (
		call Java -jar BinaryRelevance.jar %PROBERTIES% dataset/%DATANAME%/a%%r_%%c_%DATANAME%-10tra.dat dataset/%DATANAME%/a%%r_%%c_%DATANAME%-10tst.dat BR%%r%%c 0
	)
)

