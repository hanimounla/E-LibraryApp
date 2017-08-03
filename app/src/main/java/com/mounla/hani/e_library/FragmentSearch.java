package com.mounla.hani.e_library;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by hani-_000 on 2017-04-12.
 */
public class FragmentSearch extends Fragment
{
    ConnectionClass connectionClass;
    String table = "" , column = "";
    Spinner tableSpinner , columnSpinner;
    Button searchBTN;
    EditText searchET;
    String searchFor;
    ProgressBar pbbar;
    ListView searchResultList;

    private Button btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        connectionClass = new ConnectionClass();
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        tableSpinner = (Spinner)rootView.findViewById(R.id.tableSpinner);
        columnSpinner = (Spinner)rootView.findViewById(R.id.colmnSpinner);
        searchBTN = (Button)rootView.findViewById(R.id.searchBTN);
        searchET = (EditText)rootView.findViewById(R.id.searchET);
        searchResultList = (ListView)rootView.findViewById(R.id.searchResultListView);
        registerForContextMenu(searchResultList);
        pbbar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        pbbar.setVisibility(View.GONE);

        ArrayAdapter<String> SpinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, android.R.id.text1);
        SpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tableSpinner.setAdapter(SpinnerAdapter);
        SpinnerAdapter.add("Book");
        SpinnerAdapter.add("Author");
        SpinnerAdapter.add("Publisher");
        SpinnerAdapter.add("Phrase or word");
        btnSpeak = (Button) rootView.findViewById(R.id.btnSpeak);

        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(table != "Data") {
                    DoSearch d = new DoSearch();
                    d.execute("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int tableindex = tableSpinner.getSelectedItemPosition();
                switch (tableindex)
                {
                    case 0: fillBook(); table = "Books"; searchBTN.setVisibility(View.GONE);
                        break;
                    case 1: fillAuthor(); table = "Authors"; searchBTN.setVisibility(View.GONE);
                        break;
                    case 2: fillPubllisher(); table = "Publishers"; searchBTN.setVisibility(View.GONE);
                        break;
                    case 3 : fillSearch(); table = "Data"; searchBTN.setVisibility(View.VISIBLE);
                    default:
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
                // your code here
            }
        });

        columnSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                column = columnSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        searchResultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                switch (table)
                {
                    case "Books": openBookDetails(view);break;
                    case "Authors": openAuthorDetails(view);break;
                    case "Publishers" : openPublisherDetails(view);break;
                    case "Data": openBooksPages(view);break;
                }
            }
        });

        searchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoSearch d = new DoSearch();
                d.execute("");
            }
        });

        return rootView;
    }

    private void openBooksPages(View view) {
        String selectedTitle = ((TextView)(view.findViewById(R.id.nameLBL))).getText().toString();
        String selectedID = ((TextView)(view.findViewById(R.id.idLBL))).getText().toString();

        getActivity().setTitle(selectedTitle);
        FragmentManager fragMgr = getActivity().getSupportFragmentManager();
        FragmentTransaction fragTrans = fragMgr.beginTransaction();
        fragTrans.replace(R.id.mainFrame, new FragmentSearchPages(Integer.parseInt(selectedID), searchFor) , "searchPage");
        fragTrans.addToBackStack(null);
        fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragTrans.commit();
    }


    private void openPublisherDetails(View view) {
        String selectedID = ((TextView)(view.findViewById(R.id.idLBL))).getText().toString();
        startActivity(new Intent(getActivity(),PublisherDetails.class).putExtra("ID",selectedID));
    }

    private void openAuthorDetails(View view) {
        String selectedID = ((TextView)(view.findViewById(R.id.idLBL))).getText().toString();
        startActivity(new Intent(getActivity(),AuthorDetails.class).putExtra("ID",selectedID));
    }

    private void openBookDetails(View view) {
        String selectedID = ((TextView)(view.findViewById(R.id.idLBL))).getText().toString();
        startActivity(new Intent(getActivity(),BookDetails.class).putExtra("ID",selectedID));

    }

    private void fillBook()
    {
        columnSpinner.setVisibility(View.VISIBLE);
        ArrayAdapter<String> bookAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, android.R.id.text1);
        bookAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        columnSpinner.setAdapter(bookAdapter);
        bookAdapter.add("Title");
        bookAdapter.add("PublishDate");
        bookAdapter.add("ISBN");
    }
    private void fillAuthor()
    {
        columnSpinner.setVisibility(View.VISIBLE);
        ArrayAdapter<String> authorAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, android.R.id.text1);
        authorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        columnSpinner.setAdapter(authorAdapter);
        authorAdapter.add("Name");
        authorAdapter.add("Nationality");
    }
    private void fillPubllisher()
    {
        columnSpinner.setVisibility(View.VISIBLE);
        ArrayAdapter<String> PubllisherAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, android.R.id.text1);
        PubllisherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        columnSpinner.setAdapter(PubllisherAdapter);
        PubllisherAdapter.add("Name");
        PubllisherAdapter.add("Address");
        PubllisherAdapter.add("Details");

    }
    private void fillSearch()
    {
        ArrayAdapter<String> PubllisherAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, android.R.id.text1);
        PubllisherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        columnSpinner.setVisibility(View.GONE);
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.action_settings));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getActivity(),
                    getString(R.string.action_settings),
                    Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == getActivity().RESULT_OK && null != data)
                {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchET.setText(result.get(0));
                }
                break;
            }
        }
    }


    public class DoSearch extends AsyncTask<String, String, String>
    {
        String z = "";

        List<Map<String, String>> SearchList  = new ArrayList<Map<String, String>>();

        @Override
        protected void onPreExecute()
        {
            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r)
        {

            pbbar.setVisibility(View.GONE);
//            Toast.makeText(getActivity(), r, Toast.LENGTH_SHORT).show();

            String[] from = {"A", "B"};
            int[] views = { R.id.nameLBL, R.id.idLBL};
            final SimpleAdapter ADA = new SimpleAdapter(getActivity(),
                    SearchList, R.layout.my_list_layout, from,views);
            searchResultList.setAdapter(ADA);

        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                connectionClass = new ConnectionClass();
                 Connection con = connectionClass.CONN(ConnectionClass.ip);
                if (con == null)
                {
                    z = "Error in connection with SQL server";
                } else
                {
                    String query = "";
                    searchFor = searchET.getText().toString();
                    if (table !="Data")
                        query = "select * from [" + table + "] where " + column + " like N'%" + searchFor + "%'" ;
                    else {
                        query = "select b.ID ,b.Title " +
                                "from books b inner join Data d " +
                                "on b.ID = d.BookID " +
                                "where d.Text like N'%" + searchFor + "%' " +
                                "union " +
                                "select b.ID ,b.Title " +
                                "from books b inner join PicturesData pd " +
                                "on b.ID = pd.BookID " +
                                "where pd.text like N'%" + searchFor + "%' " +
                                "order by b.Title";

//                        query = "select b.Title ,d.pageid [Page Number] , d.text [Text] from books b inner join Data d " +
//                                "on b.ID = d.BookID " +
//                                "where d.Text like '%" + searchFor + "%' " +
//                                "union " +
//                                "select b.Title ,pd.pageid [Page Number] , 'Image : ' + pd.text [Text] " +
//                                "from books b inner join PicturesData pd " +
//                                "on b.ID = pd.BookID " +
//                                "where pd.text like '%" + searchFor + "%' " +
//                                "order by b.Title";
                    }

                    PreparedStatement ps = ConnectionClass.conn.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next())
                    {
                        Map<String, String> datanum = new HashMap<String, String>();
                        datanum.put("A", rs.getString(2));
                        datanum.put("B", rs.getString(1));
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
}
