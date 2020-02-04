@echo off

set SEED=2019
set FILENAME=emotion.dat

rem Only Dataset Name; Please NOT file Name
set DATANAME=emotion

set REPEAT=3
set CV=10

Java -jar CrossValidation_for_CILABformat.jar %SEED% %FILENAME% %DATANAME% %REPEAT% %CV%
