source .bash_profile
cd /root
/usr/bin/wget http://gforge.nci.nih.gov/snapshots.php?group_id=78 -O gwb.tar
cd gwb-tests
/bin/tar -zxf ../gwb.tar
cd *
cd geworkbench
export JAVA_HOME=/opt/java
export ANT_HOME=/opt/ant
/opt/ant/bin/ant junit-run > antresult.txt 2> anterr.txt
echo 'Build and test results attached.' | /usr/bin/mutt -a antresult.txt -s 'geWorkbench Build and Test Results' watkin@c2b2.columbia.edu floratos@c2b2.columbia.edu
rm -fr /var/www/html/junitreport
mv testing/junitreport /var/www/html
cd ..
cd ..
rm -fr *
