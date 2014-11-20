SET logfile=geworkbench.log

IF EXIST jre (
	.\jre\bin\java -cp lib\ant.jar;lib\ant-launcher.jar org.apache.tools.ant.launch.Launcher run4G >> %UserProfile%\%logfile% 2>&1
) ELSE (
	java -cp lib\ant.jar;lib\ant-launcher.jar org.apache.tools.ant.launch.Launcher run4G >> %UserProfile%\%logfile% 2>&1
)

