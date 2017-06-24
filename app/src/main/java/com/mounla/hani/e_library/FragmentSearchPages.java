package com.mounla.hani.e_library;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hani-_000 on 2017-05-17.
 */

public class FragmentSearchPages extends Fragment {

    ListView pagesList;
    int bookId;
    String searchFor;
    TextView totalPagesTV;
    ProgressBar progressBar;

    public FragmentSearchPages(int bookId , String searchFor)
    {
        this.bookId = bookId;
        this.searchFor = searchFor;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_pages, container, false);
        pagesList = (ListView)rootView.findViewById(R.id.pagesList);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        totalPagesTV = (TextView) rootView.findViewById(R.id.totalPages);

        pagesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selected = pagesList.getItemAtPosition(position).toString();
                String [] values = selected.split(" ");
                int pageId = Integer.parseInt(values[1]);
                FragmentManager fragMgr = getActivity().getSupportFragmentManager();
                FragmentTransaction fragTrans = fragMgr.beginTransaction();
                fragTrans.replace(R.id.mainFrame, new FragmentSearchPagesText(bookId,pageId, searchFor) , "searchPage");
                fragTrans.addToBackStack(null);
                fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragTrans.commit();
            }
        });
        FillPages f = new FillPages();
        f.execute("");
        return rootView;
    }

    private class FillPages extends AsyncTask<String, String, String>
    {
        String z = "";
        List<String> pagesArrayList = new ArrayList<String>();

        @Override
        protected void onPreExecute()
        {
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected void onPostExecute(String r)
        {
            progressBar.setVisibility(View.GONE);
            String [] Pages = pagesArrayList.toArray((new String[pagesArrayList.size()]));
            ArrayAdapter<String> pagesArrayAdapter = new ArrayAdapter<String>(
                    getActivity(), android.R.layout.select_dialog_item, Pages);
            pagesList.setAdapter(pagesArrayAdapter);
            totalPagesTV.setText("found " + pagesList.getCount() + " matches \n for result of: " + searchFor);
//            Toast.makeText(getActivity(),z, Toast.LENGTH_SHORT).show();
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
                    String query = "select d.pageid " +
                                    "from books b inner join Data d " +
                                    "on b.ID = d.BookID " +
                                    "where b.id = "+ bookId + " and d.Text like N'%" + searchFor + "%' " +
                                    "union " +
                                    "select pd.pageid " +
                                    "from books b inner join PicturesData pd " +
                                    "on b.ID = pd.BookID " +
                                    "where b.id = "+ bookId + " and pd.text like N'%" + searchFor + "%' ";

                    PreparedStatement ps = ConnectionClass.conn.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next())
                    {
                        pagesArrayList.add("Page: "+rs.getString(1));
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
