package com.mounla.hani.e_library;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class AllBooks extends Activity
{
    ConnectionClass connectionClass;
    EditText bookNameEB, BookDescEB;
    Button btnadd,btnupdate,btndelete;
    ProgressBar pbbar;
    ListView ListBooks;
    String BookID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allbooks);

        connectionClass = new ConnectionClass();
        bookNameEB = (EditText) findViewById(R.id.edtproname);
        BookDescEB = (EditText) findViewById(R.id.edtprodesc);
        btnadd = (Button) findViewById(R.id.btnadd);
        btnupdate = (Button) findViewById(R.id.btnupdate);
        btndelete = (Button) findViewById(R.id.btndelete);
        pbbar = (ProgressBar) findViewById(R.id.pbbar);
        pbbar.setVisibility(View.GONE);
        ListBooks = (ListView) findViewById(R.id.lstproducts);
        registerForContextMenu(ListBooks);
        BookID = "";

        FillList fillList = new FillList();
        fillList.execute("");

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddBook addPro = new AddBook();
                addPro.execute("");

            }
        });
        btnupdate.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                UpdateBook updateBook = new UpdateBook();
                updateBook.execute("");
            }
        });

        btndelete.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                DeletePro deletePro = new DeletePro();
                deletePro.execute("");
            }
        });

    }


    public class AddBook extends AsyncTask<String, String, String>
    {
        String z = "";
        Boolean isSuccess = false;
        String proname = bookNameEB.getText().toString();
        String prodesc = BookDescEB.getText().toString();


        @Override
        protected void onPreExecute()
        {
            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r)
        {
            pbbar.setVisibility(View.GONE);
            Toast.makeText(AllBooks.this, r, Toast.LENGTH_SHORT).show();
            if(isSuccess==true) {
                FillList fillList = new FillList();
                fillList.execute("");
            }

        }

        @Override
        protected String doInBackground(String... params)
        {
            if (proname.trim().equals("") || prodesc.trim().equals(""))
                z = "Please enter Name at least";
            else {
                try {
                    Connection con = connectionClass.CONN(ConnectionClass.ip);
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
            }
            return z;
        }
    }

    public class UpdateBook extends AsyncTask<String, String, String>
    {

        String z = "";
        Boolean isSuccess = false;
        String BookName = bookNameEB.getText().toString();
        String BookDescr = BookDescEB.getText().toString();

        @Override
        protected void onPreExecute()
        {
            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r)
        {
            pbbar.setVisibility(View.GONE);
            Toast.makeText(AllBooks.this, r, Toast.LENGTH_SHORT).show();
            if(isSuccess==true) {
                FillList fillList = new FillList();
                fillList.execute("");
            }

        }

        @Override
        protected String doInBackground(String... params)
        {
            if (BookName.trim().equals("") || BookDescr.trim().equals(""))
                z = "Please enter User Id and Password";
            else
            {
                try
                {
                    Connection con = connectionClass.CONN(connectionClass.ip);
                    if (con == null)
                    {
                        z = "Error in connection with SQL server";
                    }
                    else
                    {

                        String dates = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)
                                .format(Calendar.getInstance().getTime());

                        String query = "Update Books set Title='"+ BookName +"',Description= N'"+ BookDescr + "' where Id="+ BookID;
                        PreparedStatement preparedStatement = con.prepareStatement(query);
                        preparedStatement.executeUpdate();
                        z = "Updated Successfully";

                        isSuccess = true;
                    }
                }
                catch (Exception ex)
                {
                    isSuccess = false;
                    z = "Exceptions";
                }
            }
            return z;
        }
    }

    public class DeletePro extends AsyncTask<String, String, String>
    {

        String z = "";
        Boolean isSuccess = false;


        String proname = bookNameEB.getText().toString();
        String prodesc = BookDescEB.getText().toString();


        @Override
        protected void onPreExecute() {
            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);
            Toast.makeText(AllBooks.this, r, Toast.LENGTH_SHORT).show();
            if(isSuccess==true) {
                FillList fillList = new FillList();
                fillList.execute("");
            }

        }

        @Override
        protected String doInBackground(String... params) {
            if (proname.trim().equals("") || prodesc.trim().equals(""))
                z = "Please enter User Id and Password";
            else {
                try {
                    Connection con = connectionClass.CONN(connectionClass.ip);
                    if (con == null) {
                        z = "Error in connection with SQL server";
                    } else {

                        String dates = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)
                                .format(Calendar.getInstance().getTime());

                        String query = "delete from Producttbl where Id="+ BookID;
                        PreparedStatement preparedStatement = con.prepareStatement(query);
                        preparedStatement.executeUpdate();
                        z = "Deleted Successfully";
                        isSuccess = true;
                    }
                }
                catch (Exception ex)
                {
                    isSuccess = false;
                    z = "Exceptions";
                }
            }
            return z;
        }
    }

    public class FillList extends AsyncTask<String, String, String>
    {
        String z = "";

        List<Map<String, String>> prolist  = new ArrayList<Map<String, String>>();

        @Override
        protected void onPreExecute()
        {
            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r)
        {

            pbbar.setVisibility(View.GONE);
            Toast.makeText(AllBooks.this, r, Toast.LENGTH_SHORT).show();

            String[] from = { "A", "B"};
            int[] views = { R.id.nameLBL, R.id.idLBL};
            final SimpleAdapter ADA = new SimpleAdapter(AllBooks.this,
                    prolist, R.layout.my_list_layout, from,views);
            ListBooks.setAdapter(ADA);


            ListBooks.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
                {
                    HashMap<String, Object> obj = (HashMap<String, Object>) ADA
                            .getItem(position);
                    BookID = (String) obj.get("A");
                    String proname = (String) obj.get("B");
                    String prodesc = (String) obj.get("C");
                    BookDescEB.setText(prodesc);
                    bookNameEB.setText(proname);
                    //     qty.setText(qtys);
                }
            });
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                Connection con = connectionClass.CONN(connectionClass.ip);
                if (con == null)
                {
                    z = "Error in connection with SQL server";
                } else
                {
                    String query = "select * from Books";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next())
                    {
                        Map<String, String> datanum = new HashMap<String, String>();
                        datanum.put("A", rs.getString("Id"));
                        datanum.put("B", rs.getString("Title"));
                        datanum.put("C", rs.getString("Description"));
                        prolist.add(datanum);
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
