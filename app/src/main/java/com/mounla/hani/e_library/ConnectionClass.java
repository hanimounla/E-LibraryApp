package com.mounla.hani.e_library;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by H-PC on 16-Oct-15.
 */
public class ConnectionClass
{
    public static String ip ;
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    String db = "E-LibraryDB";
    String un = "Hani Mounla";
    String password = "hanihani";
    public static Connection conn = null;

//    @SuppressLint("NewApi")
    public Connection CONN(String ip)
    {
        this.ip = ip;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String ConnURL = null;
        try
        {
            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + ip + ";"
                    + "databaseName=" + db + ";user=" + un + ";password="
                    + password + ";";
            conn = DriverManager.getConnection(ConnURL);
        }
        catch (SQLException se)
        {
            Log.e("ERRO", se.getMessage());
        }
        catch (ClassNotFoundException e)
        {
            Log.e("ERRO", e.getMessage());
        }
        catch (Exception e)
        {
            Log.e("ERRO", e.getMessage());
        }
        return conn;
    }
}
