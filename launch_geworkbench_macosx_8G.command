logfile=geworkbench.log

cd "`dirname "$0"`"
if [ -d jre ] 
then
	./jre/bin/java -cp lib/ant.jar:lib/ant-launcher.jar org.apache.tools.ant.launch.Launcher run8G >> ${HOME}/$logfile 2>&1
else
	java -cp lib/ant.jar:lib/ant-launcher.jar org.apache.tools.ant.launch.Launcher run8G >> ${HOME}/$logfile 2>&1
fi