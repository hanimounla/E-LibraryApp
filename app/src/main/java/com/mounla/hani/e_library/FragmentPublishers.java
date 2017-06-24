package com.mounla.hani.e_library;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by hani-_000 on 2017-05-01.
 */

public class FragmentPublishers extends Fragment {

    Spinner PublishersSpinner;
    String Publisher;
    ListView booksList;
    ProgressBar progressBar;
    TextView totalBooks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_publishers, container, false);
        PublishersSpinner = (Spinner)rootView.findViewById(R.id.publishersSpinner);
        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);
        booksList = (ListView)rootView.findViewById(R.id.BooksList);
        totalBooks = (TextView)rootView.findViewById(R.id.totalBooks);
        progressBar.setVisibility(View.GONE);

        PublishersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Publisher = PublishersSpinner.getItemAtPosition(i).toString();
                FillPublisherBooks f = new FillPublisherBooks();
                f.execute("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        PublishersSpinner.setOnItemClickListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//            }
//        });

        FillPublishers f = new FillPublishers();
        f.execute();
//        Spinner spn_label = (Spinner)rootView.findViewById(R.id.spinner_label);
//        String[] items = new String[20];
//        for(int i = 0; i < items.length; i++)
//            items[i] = "Item " + String.valueOf(i + 1);
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.row_spn, items);
//        adapter.setDropDownViewResource(R.layout.row_spn_dropdown);
//        spn_label.setAdapter(adapter);
        return rootView;
    }

    private class FillPublisherBooks extends AsyncTask<String, String, String>
    {
        String z = "";
        List<String> PublisherBooks  = new ArrayList<String>();

        @Override
        protected void onPreExecute()
        {
            progressBar.setVisibility(View.VISIBLE);

        }
        @Override
        protected void onPostExecute(String r)
        {
            progressBar.setVisibility(View.GONE);

            String [] books = PublisherBooks.toArray((new String[PublisherBooks.size()]));
            ArrayAdapter<String> booksArrayAdapter = new ArrayAdapter<String>(
                    getActivity(), android.R.layout.select_dialog_item, books);
            booksList.setAdapter(booksArrayAdapter);
            totalBooks.setText("Total Books: " + booksList.getCount());
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
                    String query = "Select Title from Books b inner join Publishers c " +
                            "on b.PublisherId = c.id where c.name = '" + Publisher+"'";

                    PreparedStatement ps = ConnectionClass.conn.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next())
                    {
                        PublisherBooks.add(rs.getString(1));
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

    public class FillPublishers extends AsyncTask<String, String, String>
    {
        String z = "";

        List<String> Publishers = new ArrayList<>();

        @Override
        protected void onPreExecute()
        {
        }

        @Override
        protected void onPostExecute(String r)
        {

            String[] wee = Publishers.toArray(new String[Publishers.size()]);
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                    getActivity(), android.R.layout.simple_spinner_item, wee);
            spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
            PublishersSpinner.setAdapter(spinnerArrayAdapter);
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                // Connection con = connectionClass.CONN(ConnectionClass.ip);
                if (ConnectionClass.conn == null)
                {
                    z = "Error in connection with SQL server";
                } else
                {
                    String query = "Select Name from Publishers";

                    PreparedStatement ps = ConnectionClass.conn.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next())
                    {
                        Publishers.add(rs.getString(1));
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




}
