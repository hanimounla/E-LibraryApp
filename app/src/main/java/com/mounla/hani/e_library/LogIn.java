package com.mounla.hani.e_library;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.rey.material.widget.CheckBox;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class LogIn extends Activity
{
    public static Activity LogInActivity;
    ConnectionClass connectionClass;
    EditText userNameTB, userPasswordTB;
    Button btnlogin, btnSignUp;
    ProgressBar pbbar;
    CurrentUser currentUser;
    CheckBox rememberMe;
    EditText IPTB;
    public static final String PREFS_NAME = "MyPrefsFile";
    public String PREFS_USERNAME= "prefsUsername";
    public String PREFS_PASSWORD="prefsPassword";
    public String PREFS_SAVED = "prefsSaved";
    public String PREFS_IP = "ipAddress";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        LogInActivity = this;



        connectionClass = new ConnectionClass();
        userNameTB = (EditText) findViewById(R.id.userNameTB);
        userPasswordTB = (EditText) findViewById(R.id.userPasswordTB);
        btnlogin = (Button) findViewById(R.id.btn_login);
        pbbar = (ProgressBar) findViewById(R.id.pbbar);
        rememberMe = (CheckBox)findViewById(R.id.rememberMeCB);
        IPTB = (EditText)findViewById(R.id.IPTB);
        btnSignUp = (Button)findViewById(R.id.btnSignUp);



        pbbar.setVisibility(View.GONE);
        IPTB.setVisibility(View.GONE);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogIn.this,SignUp.class).putExtra("IP",IPTB.getText().toString()));
            }
        });

        checkRememberMeSettings();

        btnlogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setRememberMeSettings();
                DoLogin  doLogin = new DoLogin();
                doLogin.execute("");
            }
        });
    }

    private void checkRememberMeSettings() {
        SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        String username = pref.getString(PREFS_USERNAME, "");
        String upass = pref.getString(PREFS_PASSWORD, "");
        Boolean saved = pref.getBoolean(PREFS_SAVED,false);
        String IPPref = pref.getString(PREFS_IP,"");
        if (saved == true) {
            userNameTB.setText(username);
            userPasswordTB.setText(upass);
            rememberMe.setChecked(true);
            btnSignUp.setVisibility(View.GONE);
            if(!IPPref.isEmpty())
                IPTB.setText(IPPref);
            else
                IPTB.setText("202.202.202.202");
            DoLogin  doLogin = new DoLogin();
            doLogin.execute("");
        }
    }

    private void setRememberMeSettings() {
//        SharedPreferences pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        String passwordInString = userNameTB.getText().toString();
        String userNameInString = userPasswordTB.getText().toString();
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putString(PREFS_USERNAME, passwordInString)
                .putString(PREFS_PASSWORD, userNameInString)
                .putBoolean(PREFS_SAVED, rememberMe.isChecked())
                .putString(PREFS_IP,IPTB.getText().toString())
                .commit();
    }

    public void setIP(View view) {
        if (IPTB.getVisibility() == View.GONE)
            IPTB.setVisibility(View.VISIBLE);
        else
            IPTB.setVisibility(View.GONE);
    }

    public class DoLogin extends AsyncTask<String,String,String>
    {
        String Result = "";
        Boolean isSuccess = false;

        String enterdUserName = userNameTB.getText().toString();
        String enterdPassword = userPasswordTB.getText().toString();
        String ipAddress = IPTB.getText().toString();
        Connection con;

        @Override
        protected void onPreExecute()
        {
            pbbar.setVisibility(View.VISIBLE);
            btnSignUp.setEnabled(false);
            btnlogin.setEnabled(false);
        }

        @Override
        protected void onPostExecute(String r)
        {
            pbbar.setVisibility(View.GONE);
            btnSignUp.setEnabled(true);
            btnlogin.setEnabled(true);
            Toast.makeText(LogIn.this,r,Toast.LENGTH_SHORT).show();

            if(isSuccess)
            {
                Intent i = new Intent(LogIn.this, Main.class);
                i.putExtra("userName",enterdUserName);
                //i.putExtra("CurrentUser", (Parcelable) currentUser);
                startActivity(i);
               finish();
            }
        }

        @Override
        protected String doInBackground(String... params)
        {
            if(enterdUserName.trim().equals("")|| enterdUserName.trim().equals(""))
                Result = "Please enter User Id and Password";
            else
            {
                try
                {
                    con = connectionClass.CONN(ipAddress);
                    if (con == null)
                        Result = "Error in connection with SQL server";
                    else
                    {
                        String query = "select UserName , PassWord , SecurityLevel from Users where UserName = '" + enterdUserName + "' and password = hashbytes('MD5','" + enterdPassword + "')";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        if(rs.next())
                        {
                            currentUser = new CurrentUser(rs.getString(1),rs.getString(2),rs.getString(3));
                            Result = "Welcom back " + enterdUserName;
                            isSuccess = true;
                        }
                        else
                        {
                            Result = "Invalid Credentials";
                            isSuccess = false;
                        }
                    }
                }
                catch (Exception ex)
                {
                    isSuccess = false;
                    Result = "Exceptions";
                }
            }
            return Result;
        }
    }
}
