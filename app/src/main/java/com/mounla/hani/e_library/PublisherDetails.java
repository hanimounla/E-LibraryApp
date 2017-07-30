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

public class PublisherDetails extends AppCompatActivity {

    EditText AddressET, ContactET , DetailsTE;
    ImageView PublisherIV;
    Button ViewBooksBTN;
    String id ;
    ConnectionClass connectionClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publisher_details);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        id = getIntent().getStringExtra("ID");
        getPublisherByID g = new getPublisherByID();
        g.execute("");

        AddressET = (EditText) findViewById(R.id.addressET);
        ContactET = (EditText) findViewById(R.id.contactET);
        PublisherIV = (ImageView) findViewById(R.id.publisherImage);
        DetailsTE = (EditText) findViewById(R.id.detailsET);
        ViewBooksBTN = (Button)findViewById(R.id.viewBooksBTN);

        ViewBooksBTN.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),BooksCategories.class).putExtra("PublisherID",id));
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


    public class getPublisherByID extends AsyncTask<String, String, String>
    {
        String name , address, contact , details;
        byte[] publisherImage;
        String z = "";

        @Override
        protected void onPostExecute(String r)
        {
            setTitle(name);
            AddressET.setText(address);
            ContactET.setText(contact);
            DetailsTE.setText(details);

            if(publisherImage != null) {
                Bitmap bmp = BitmapFactory.decodeByteArray(publisherImage, 0, publisherImage.length);
                PublisherIV.setImageBitmap(bmp);
            }
            else
                PublisherIV.setImageResource(R.drawable.publisher);

            AddressET.setEnabled(false);
            ContactET.setEnabled(false);
            DetailsTE.setEnabled(false);
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
                    String getAuthorByID = "Select Name , Address ,contact , details , PublisherPicture from Publishers where ID = " + id;
                    PreparedStatement ps = ConnectionClass.conn.prepareStatement(getAuthorByID);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next())
                    {
                        name = rs.getString("Name");
                        address = rs.getString("Address");
                        contact = rs.getString("contact");
                        details = rs.getString("details");
                        publisherImage = rs.getBytes("PublisherPicture");
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
