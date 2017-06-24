package com.mounla.hani.e_library;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddBook extends Activity
{
    EditText booktitleET,isbnET,yearET,dsescriptionET;
    Spinner categorySpinner , publisherSpinner ,authorSpinner;
    Connection conn;
    List<String> CategoriesList = new ArrayList<String>();
    List<String> PublishersList = new ArrayList<String>();
    List<String> AuthorsList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        publisherSpinner = (Spinner) findViewById(R.id.publisherSpinner);
        authorSpinner = (Spinner) findViewById(R.id.authorSpinner);
        //booktitleET = (EditText) findViewById(R.id.bookNameTF);
        isbnET = (EditText) findViewById(R.id.ISBNTF);
        yearET = (EditText) findViewById(R.id.YearTF);
        dsescriptionET = (EditText) findViewById(R.id.DescriptionTB);

        FillLists f = new FillLists();
        f.execute("");
    }

    private void fillSpinners()
    {
        //String[] array = CategoriesList.toArray(new String[0]);
        ArrayAdapter categoriesAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, CategoriesList);
        categorySpinner.setAdapter(categoriesAdapter);

        ArrayAdapter publishersAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, PublishersList);
        publisherSpinner.setAdapter(publishersAdapter);

        ArrayAdapter authorsAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, AuthorsList);
        authorSpinner.setAdapter(authorsAdapter);
    }

    public void close(View view)
    {
        finish();
    }

    public void AddBook(View view)
    {
        AddBookk ab = new AddBookk();
        ab.execute("");

    }

    public class AddBookk extends AsyncTask<String, String, String>
    {
        String z = "";
        String bookTitle = booktitleET.getText().toString();
        String ISBN = isbnET.getText().toString();


        @Override
        protected void onPreExecute()
        {

        }

        @Override
        protected void onPostExecute(String r)
        {

        }

        @Override
        protected String doInBackground(String... params)
        {
           /*if (proname.trim().equals("") || prodesc.trim().equals(""))
                z = "Please enter User Id and Password";
            else {
                try {
                    Connection con = conn.CONN(ConnectionClass.ip);
                    if (con == null) {
                        z = "Error in connection with SQL server";
                    } else {

                        String dates = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)
                                .format(Calendar.getInstance().getTime());
                        String query = "insert into Producttbl (ProName,ProDesc,OnDate) values ('" + proname + "','" + prodesc + "','" + dates + "')";
                        PreparedStatement preparedStatement = con.prepareStatement(query);
                        preparedStatement.executeUpdate();
                        z = "Added Successfully";
                        isSuccess = true;
                    }
                } catch (Exception ex) {
                    isSuccess = false;
                    z = "Exceptions";
                }
            }*/
            return z;
        }
    }

    public class FillLists extends AsyncTask<String, String, String>
    {
        String z = "";

        @Override
        protected void onPreExecute()
        {

        }

        @Override
        protected void onPostExecute(String r)
        {
            fillSpinners();
            Toast.makeText(AddBook.this, r, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                conn = ConnectionClass.conn;
                if (conn == null)
                {
                    z = "Error in connection with SQL server";
                }
                else
                {
                    fillCategories(conn);
                    fillPublishers(conn);
                    fillAuthors(conn);
                    z = "Success";
                }
            }
            catch (Exception ex)
            {
                z = "Error retrieving data from table";
            }
            return z;
        }
        private void fillAuthors(Connection conn) throws SQLException
        {
            String categoriesQuery = "select Name from Authors";
            PreparedStatement ps = conn.prepareStatement(categoriesQuery);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                AuthorsList.add(rs.getString("Name"));
            }
        }

        private void fillPublishers(Connection conn) throws SQLException
        {
            String categoriesQuery = "select * from Publishers";
            PreparedStatement ps = conn.prepareStatement(categoriesQuery);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                PublishersList.add(rs.getString("Name"));
            }
        }

        private void fillCategories(Connection conn) throws SQLException
        {
            String categoriesQuery = "select * from Categories";
            PreparedStatement ps = conn.prepareStatement(categoriesQuery);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                CategoriesList.add(rs.getString("Name"));
            }
        }
    }
}
