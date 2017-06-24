package com.mounla.hani.e_library;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by hani-_000 on 2017-05-17.
 */

public class FragmentSearchPagesText  extends Fragment {
    int bookId, PageId;
    String searchFor;
    TextView pageDescLBL;
    EditText pageTextTB;
    ProgressBar progressBar;

    ConnectionClass connectionClass;

    FragmentSearchPagesText(int bookId, int pageId, String searchFor) {
        this.bookId = bookId;
        this.PageId = pageId;
        this.searchFor = searchFor;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_pages_text, container, false);
        pageDescLBL = (TextView)rootView.findViewById(R.id.pageDescriptionLBL);
        pageTextTB = (EditText) rootView.findViewById(R.id.pageText);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);


        pageDescLBL.setText("Page: " + PageId + " Result For: " + searchFor);
        progressBar.setVisibility(View.GONE);

        FillPagesText f = new FillPagesText();
        f.execute("");
        return rootView;
    }

    private class FillPagesText extends AsyncTask<String, String, String>
    {
        String z = "";
        String pageText;

        @Override
        protected void onPreExecute()
        {
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected void onPostExecute(String r)
        {
            progressBar.setVisibility(View.GONE);
            pageText = pageText.replaceAll(searchFor,"<font color = 'red'>"+searchFor+"</font>");
            pageTextTB.setText(Html.fromHtml(pageText));
        }

        @Override
        protected String doInBackground(String... strings) {
            try
            {
                connectionClass = new ConnectionClass();
                Connection con = connectionClass.CONN(ConnectionClass.ip);
                if (con == null)
                {
                    z = "Error in connection with SQL server";
                } else
                {
                    String query = "select d.Text " +
                            "from books b inner join Data d " +
                            "on b.ID = d.BookID " +
                            "where b.id = "+ bookId +" and d.pageId = " + PageId + " and d.Text like N'%" + searchFor + "%' " +
                            "union " +
                            "select pd.Text " +
                            "from books b inner join PicturesData pd " +
                            "on b.ID = pd.BookID " +
                            "where b.id = "+ bookId +" and pd.pageId = " + PageId + " and pd.text like N'%" + searchFor + "%' ";

                    PreparedStatement ps = ConnectionClass.conn.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next())
                    {
                        pageText = rs.getString(1);
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
