/*
 * Created on 4 May 2003
 *
 * This file is part of Bayesian Network for Java (BNJ).
 *
 * BNJ is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * BNJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BNJ in LICENSE.txt file; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package edu.ksu.cis.kdd.data;

import edu.ksu.cis.kdd.util.Parameter;
import edu.ksu.cis.kdd.util.ParameterTable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * Import / export database
 *
 * @author Roby Joehanes
 */
public class DatabaseLoader {

    //    public static String inputFromKeyboard(String msg) {
    //        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
    //        System.out.print(msg);
    //        try {
    //            return stdin.readLine();
    //        } catch (Exception e) {
    //            return null;
    //        }
    //    }

    public static void main(String[] args) {
        ParameterTable params = Parameter.process(args);
        String inputFile = params.getString("-i");
        String outputFile = params.getString("-o");
        String driver = params.getString("-d");
        String login = params.getString("-l");
        String passwd = params.getString("-p");
        String url = params.getString("-u");
        String tableListString = params.getString("-t");

        if (driver == null || url == null || login == null || passwd == null || (inputFile == null && outputFile == null) || (inputFile != null && outputFile != null)) {
            System.out.println("This program's is to export local file to database server OR");
            System.out.println("to import remote database contents to local file");
            System.out.println();
            System.out.println("Usage: edu.ksu.cis.kdd.data.DatabaseLoader (-i:inputfile | -o:outputfile [-t:tablenamelist,...]) -d:drivername -l:login -p:passwd -u:serverurl");
            System.out.println();
            System.out.println("Example: To load a local file to server:");
            System.out.println("java edu.ksu.cis.kdd.data.DatabaseLoader -i:myfile.arff -d:org.postgresql.Driver -l:mylogon -p:mypasswd -u:jdbc:postgresql://localhost/mydb");
            System.out.println();
            System.out.println("Example: To import remote database contents (only table1, table2, and table3) to a local file:");
            System.out.println("java edu.ksu.cis.kdd.data.DatabaseLoader -o:myoutputfile.arff -t:table1,table2,table3 -d:org.postgresql.Driver -l:mylogon -p:mypasswd -u:jdbc:postgresql://localhost/mydb");
            System.out.println("Note: To import ALL tables' contents, omit option -t. However, take note that some databases (like Oracle) may contain some system tables that are not loadable by everyone.");
            System.out.println();
            System.out.println("NOTE: In some databases, table names are CASE SENSITIVE. " + "Some even behave strangely, like Oracle, who treats table names as case insensitive, but when " + "it comes to handling metadata, table names are case sensitive.");
            return;
        }

        LinkedList tableList = null;

        if (tableListString != null) {
            tableList = new LinkedList();
            StringTokenizer tok = new StringTokenizer(tableListString, ","); // $NON-NLS-1$
            while (tok.hasMoreTokens()) {
                tableList.add(tok.nextToken());
            }
        }

        try {
            Class.forName(driver);
            Connection c = DriverManager.getConnection(url, login, passwd);

            if (inputFile != null) { // Exporting local file to server
                Database db = Database.load(inputFile);
                db.exportToServer(c);
            } else {  // Importing database contents to local file
                assert (outputFile != null);
                Database db = Database.importRemoteSchema(c, tableList);
                db.importDatabaseToLocal();
                db.save(outputFile);

                // debugging message
                DatabaseTally tally = new JDBCTally(db);
                tally.dumpTallyStatus();
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
