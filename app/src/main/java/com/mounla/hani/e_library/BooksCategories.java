package com.mounla.hani.e_library;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BooksCategories extends AppCompatActivity {

    String AuthorID = "", PublisherID = "";
    ListView BooksCategoriesList;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_and_category);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        AuthorID = getIntent().getStringExtra("AuthorID");
        PublisherID = getIntent().getStringExtra("PublisherID");

        if (AuthorID != null)
            setTitle(getIntent().getStringExtra("AuthorName"));
        else
            setTitle(getIntent().getStringExtra("PublisherName"));

        BooksCategoriesList = (ListView) findViewById(R.id.booksCategoryList);

        BooksCategoriesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedID = ((TextView) (view.findViewById(R.id.bookID))).getText().toString();
                startActivity(new Intent(getApplicationContext(), BookDetails.class).putExtra("ID", selectedID));
            }
        });

        FillAuthorBooks f = new FillAuthorBooks();
        f.execute("");
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

    private class FillAuthorBooks extends AsyncTask<String, String, String>
    {
        String z = "";
        List<Map<String, String>> SearchList  = new ArrayList<Map<String, String>>();

        @Override
        protected void onPreExecute()
        {
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected void onPostExecute(String r)
        {
            progressBar.setVisibility(View.GONE);

            String[] from = {"A", "B","C"};
            int[] views = { R.id.bookTitle, R.id.bookID , R.id.BookCategory};
            final SimpleAdapter ADA = new SimpleAdapter(getApplicationContext(),
                    SearchList, R.layout.my_list_layout_2, from,views);
            BooksCategoriesList.setAdapter(ADA);
        }

        @Override
        protected String doInBackground(String... strings) {
            try
            {
                // Connection con = connectionClass.CONN(ConnectionClass.ip);
                if (ConnectionClass.conn == null)
                {
                    z = "Error in connection with SQL server";
                } else
                {
                    String query;
                    if (AuthorID != null)
                        query = "select b.Title ,b.ID , c.name " +
                                "from books b " +
                                "inner join BooksAuthors ba " +
                                "on b.ID = ba.BookID " +
                                "inner join Authors a " +
                                "on a.ID = ba.AuthorID " +
                                "inner join Categories c " +
                                "on b.categoryID = c.id " +
                                "where a.id = " + AuthorID + "";
                    else
                        query = "select b.Title ,b.ID , c.name " +
                                "from books b " +
                                "inner join Publishers p " +
                                "on b.PublisherID = p.id " +
                                "inner join Categories c " +
                                "on b.categoryID = c.id " +
                                "where p.id = " + PublisherID + "";

                    PreparedStatement ps = ConnectionClass.conn.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next())
                    {
                        Map<String, String> datanum = new HashMap<String, String>();
                        datanum.put("A", rs.getString(1));
                        datanum.put("B", rs.getString(2));
                        datanum.put("C", rs.getString(3));
                        SearchList.add(datanum);
                    }
                    z = "Success";
                }
            } catch (Exception ex)
            {
                z = "Error retrieving data from table";
            }
            return z;
        }
    }

    public class FillBooks extends AsyncTask<String, String, String> {
        String z = "";
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.setVisibility(View.GONE);
            SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), data,
                    R.layout.my_list_layout_2,
                    new String[] {"A", "B", "C"},
                    new int[] {R.id.bookTitle,R.id.bookID,
                            R.id.BookCategory});
            BooksCategoriesList.setAdapter(adapter);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                //Connection con = connectionClass.CONN(ConnectionClass.ip);
                if (ConnectionClass.conn == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query;
                    if (AuthorID != null)
                        query = "select b.Title ,b.ID , c.name " +
                                "from books b " +
                                "inner join BooksAuthors ba " +
                                "on b.ID = ba.BookID " +
                                "inner join Authors a " +
                                "on a.ID = ba.AuthorID " +
                                "inner join Categories c " +
                                "on b.categoryID = c.id " +
                                "where a.id = " + AuthorID + "";
                    else
                        query = "select b.Title ,b.ID , c.name " +
                                "from books b " +
                                "inner join Publishers p " +
                                "on b.PublisherID = p.id " +
                                "inner join Categories c " +
                                "on b.categoryID = c.id " +
                                "where p.id = " + PublisherID + "";

                    PreparedStatement ps = ConnectionClass.conn.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        Map<String, String> datanum = new HashMap<String, String>(2);
                        datanum.put("A", rs.getString(1));
                        datanum.put("B", rs.getString(2));
                        datanum.put("C", rs.getString(3));
                        data.add(datanum);
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
