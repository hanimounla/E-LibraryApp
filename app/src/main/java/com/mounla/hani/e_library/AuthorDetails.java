package com.mounla.hani.e_library;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthorDetails extends AppCompatActivity {

    EditText NationalityET, BioET;
    ImageView authorIV;
    Button ViewBooksBTN;
    String id ;
    ConnectionClass connectionClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_details);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        id = getIntent().getStringExtra("ID");
        getAuthorByID g = new getAuthorByID();
        g.execute("");

        NationalityET = (EditText) findViewById(R.id.nationalityET);
        BioET = (EditText) findViewById(R.id.BioET);
        authorIV = (ImageView) findViewById(R.id.authorImage);
        ViewBooksBTN = (Button)findViewById(R.id.viewBooksBTN);

        ViewBooksBTN.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),BooksCategories.class).putExtra("AuthorID",id));
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public class getAuthorByID extends AsyncTask<String, String, String>
    {
        String name , nationality , bio;
        byte[] authorImage;
        String z = "";

        @Override
        protected void onPostExecute(String r)
        {
            setTitle(name);
            NationalityET.setText(nationality);
            BioET.setText(bio);

            if(authorImage != null) {
                Bitmap bmp = BitmapFactory.decodeByteArray(authorImage, 0, authorImage.length);
                authorIV.setImageBitmap(bmp);
            }
            else
                authorIV.setImageResource(R.drawable.publisher);

            NationalityET.setEnabled(false);
            BioET.setEnabled(false);
//            Toast.makeText(getApplicationContext(), r, Toast.LENGTH_SHORT).show();
        }
        @Override
        protected String doInBackground(String... strings)
        {
            try
            {
                //Connection con = connectionClass.CONN(ConnectionClass.ip);
                if (ConnectionClass.conn == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String getAuthorByID = "Select Name , Nationality , AuthorPicture , Bio from Authors where ID = " + id;
                    PreparedStatement ps = ConnectionClass.conn.prepareStatement(getAuthorByID);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next())
                    {
                        name = rs.getString("Name");
                        nationality = rs.getString("Nationality");
                        bio = rs.getString("Bio");
                        authorImage = rs.getBytes("AuthorPicture");
                    }
                    z = "Success";
                }
            } catch (Exception ex) {
                z = "Error retrieving data from table";
            }
            return z;
        }
    }
}
