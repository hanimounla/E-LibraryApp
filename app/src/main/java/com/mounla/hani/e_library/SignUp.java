package com.mounla.hani.e_library;

import android.app.Activity;
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
    static final String PREFS_NAME = "MyPrefsFile";
    String PREF_USERNAME = "prefsUsername";
    String PREF_PASSWORD ="prefsPassword";
    String PREF_SAVED = "prefsSaved";
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

    private class DoSignUp extends AsyncTask<String,String,String>
    {
        String z  = "";
        @Override
        protected void onPostExecute(String z){
            if(z == "Success")
            {


            }
        }
        @Override
        protected String doInBackground(String... strings) {

            try
            {
                connectionClass = new ConnectionClass();
                Connection con = connectionClass.CONN(ConnectionClass.ip);
                if (ConnectionClass.conn == null)
                    z = "Error in connection with SQL server";
                else
                {
                    String query = "Insert into users values ('" + enterdName + "','" + confirmPass + "',FALSE)";
                    Statement stmt = con.createStatement();
                    stmt.executeQuery(query);
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
