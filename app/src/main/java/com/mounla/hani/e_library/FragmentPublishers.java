package com.mounla.hani.e_library;

import android.content.Intent;
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
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

        booksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                openBookDetails(position);
            }
        });

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
    private void openBookDetails(int position) {
        String selected = booksList.getItemAtPosition(position).toString();
        int bookID = 1;
        try
        {
            String [] values = selected.split(" ");
            bookID = Integer.parseInt(values[0].substring(3, values[0].length() - 1));
        }
        catch (Exception ex)
        {
            String [] values = selected.split(", ");
            bookID =  Integer.parseInt(values[1].substring(2,values[1].length()-1));
        }
//        Toast.makeText(getActivity(),selected,Toast.LENGTH_LONG).show();
        Intent i = new Intent(getActivity(),BookDetails.class);
        i.putExtra("ID",bookID + "");

        startActivity(i);

    }

    private class FillPublisherBooks extends AsyncTask<String, String, String>
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

            String[] from = {"A", "B"};
            int[] views = { R.id.nameLBL, R.id.idLBL};
            final SimpleAdapter ADA = new SimpleAdapter(getActivity(),
                    SearchList, R.layout.my_list_layout, from,views);
            booksList.setAdapter(ADA);
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
                    String query = "Select Title , b.id from Books b inner join Publishers c " +
                            "on b.PublisherId = c.id where c.name = N'" + Publisher+"'";

                    PreparedStatement ps = ConnectionClass.conn.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next())
                    {
                        Map<String, String> datanum = new HashMap<String, String>();
                        datanum.put("A", rs.getString(1));
                        datanum.put("B", rs.getString(2));
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
