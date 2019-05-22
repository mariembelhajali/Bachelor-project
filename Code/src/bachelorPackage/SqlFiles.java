package bachelorPackage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;
import javax.swing.JFrame;

import org.jfree.ui.RefineryUtilities;


/**
 * This class reads the SQL file to execute the create procedures .The user have to be run this class at least once to execute the 
 * queries.
 *
 */
public class SqlFiles {
	public static void resetDatabase(Connection c) throws SQLException
    {
        String s = new String();
        StringBuffer sb = new StringBuffer();
        try
        {
            FileReader fr = new FileReader(new File("/home/mouad/Desktop/eclipse-workspace/queries.sql"));

            BufferedReader br = new BufferedReader(fr); 
            while((s = br.readLine()) != null)
            {
                sb.append(s);
            }
            br.close();
            // At the end of each query we have to add  ; in order to read correctly each query . 
            String[] queries = sb.toString().split(";");
            Statement st = c.createStatement();
            for(int i = 0; i<queries.length; i++)
            {  // execution of each query
                if(!queries[i].trim().equals(""))
                {
                    st.executeUpdate(queries[i]);
                }
            }
   
        }
        catch(Exception e)
        {
            e.printStackTrace();
          
        }
 
    }


	public static void main(String[] args) throws SQLException, IOException {
		//setting up the connexion
		Connection con = null;
		String conUrl = "jdbc:sqlserver://"+Login.Host+"; databaseName="+Login.databaseName+"; user="+Login.user+"; password="+Login.password+";";;
		con = DriverManager.getConnection(conUrl);
		Statement stmt = con.createStatement( );
		resetDatabase(con);
	}
	


}