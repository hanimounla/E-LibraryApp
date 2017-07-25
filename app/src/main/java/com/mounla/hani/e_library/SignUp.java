package com.mounla.hani.e_library;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rey.material.widget.CheckBox;

import java.sql.Connection;
import java.sql.Statement;

public class SignUp extends Activity {

    ConnectionClass connectionClass;
    EditText userNameTB, userPasswordTB , confirmPassTB;
    Button btnSignUp;
    CheckBox rememberMe;
    public static final String PREFS_NAME = "MyPrefsFile";
    public String PREFS_USERNAME= "prefsUsername";
    public String PREFS_PASSWORD="prefsPassword";
    public String PREFS_SAVED = "prefsSaved";
    public String PREFS_IP = "ipAddress";
    SharedPreferences pref ;

    String enterdName;
    String enterdPass;
    String confirmPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Sign Up to E-Library");
        pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        connectionClass = new ConnectionClass();

        userNameTB = (EditText) findViewById(R.id.UserNameTB);
        userPasswordTB = (EditText) findViewById(R.id.PasswordTB);
        confirmPassTB = (EditText) findViewById(R.id.confirmPass);
        rememberMe = (CheckBox)findViewById(R.id.rememberMeCB);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterdName = userNameTB.getText().toString();
                enterdPass = userPasswordTB.getText().toString();
                confirmPass = confirmPassTB.getText().toString();
                if (!enterdName.isEmpty())
                {
                    if(enterdName.length() > 4) {
                        if (confirmPass.equals(enterdPass)) {
                            DoSignUp s = new DoSignUp();
                            s.execute("");
                        } else
                            Toast.makeText(SignUp.this, "Passwords dont match !", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(SignUp.this, "UserName lengh must be more than 4 charecters!", Toast.LENGTH_SHORT).show();

                    }
                }
                else
                    Toast.makeText(SignUp.this, "Enter Name !", Toast.LENGTH_SHORT).show();
            }
        });
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
                .commit();
    }

    private class DoSignUp extends AsyncTask<String,String,String>
    {
        String z  = "";
        @Override
        protected void onPostExecute(String z){

            Toast.makeText(getApplicationContext(), z, Toast.LENGTH_SHORT).show();
            if(z.equals("Account Created"))
            {
                setRememberMeSettings();
                LogIn.LogInActivity.finish();
//                Intent intent = getIntent();
//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                finish();
                startActivity(new Intent(SignUp.this,Main.class).putExtra("userName",enterdName));
            }
        }
        @Override
        protected String doInBackground(String... strings) {

            try
            {
                SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                String IPPref = pref.getString("ipAddress","");
                if(IPPref.isEmpty());
                    IPPref = "202.202.202.202";

                connectionClass = new ConnectionClass();
                Connection con = connectionClass.CONN(IPPref);
                if (ConnectionClass.conn == null)
                    z = "Error in connection with SQL server";
                else
                {
                    String query = "Exec sp_CreateUser '"+ enterdName +"', '" + enterdPass +"'";
                    Statement stmt = con.createStatement();
                    stmt.execute(query);
                    z = "Account Created";
//                    stmt.executeQuery(query);
                }
            }
            catch (Exception ex)
            {
                z = "Exceptions";
            }
            return z;
        }
    }
}
