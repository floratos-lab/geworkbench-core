SET logfile=geworkbench.log

IF EXIST jre (
	.\jre\bin\java -cp lib\ant.jar;lib\ant-launcher.jar org.apache.tools.ant.launch.Launcher run1G >> %UserProfile%\%logfile%.stdout.log 2>> %UserProfile%\%logfile%.stderr.log
) ELSE (
	java -cp lib\ant.jar;lib\ant-launcher.jar org.apache.tools.ant.launch.Launcher run1G >> %UserProfile%\%logfile%.stdout.log 2>> %UserProfile%\%logfile%.stderr.log
)

