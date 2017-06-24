package com.mounla.hani.e_library;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BookDetails extends AppCompatActivity {
    EditText categoryTF, ISBNTF, YearTF, descriptionTF , PagesCount;
    ImageView coverPicIV;
    String Title;
    File pdfFile;
    Button downloadBTN , openBtn , backBTN;
    byte [] pdfFileBytes;
    String id;
    ConnectionClass connectionClass;

    private FragmentManager fmgr;
    FragmentProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        fmgr = getSupportFragmentManager();
        progressDialog = new FragmentProgressDialog();
        id = getIntent().getStringExtra("ID");
        getBookByID g = new getBookByID();
        g.execute("");

        categoryTF = (EditText) findViewById(R.id.categoryTF);
        ISBNTF = (EditText) findViewById(R.id.ISBNTF);
        YearTF = (EditText) findViewById(R.id.YearTF);
        descriptionTF = (EditText) findViewById(R.id.DescriptionTB);
        coverPicIV = (ImageView) findViewById(R.id.coverPic);
        PagesCount = (EditText)findViewById(R.id.pagesCount);
        downloadBTN = (Button)findViewById(R.id.downloadBTN);
        openBtn = (Button)findViewById(R.id.openPdfBTN);
        backBTN = (Button)findViewById(R.id.backBTN);

        downloadBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    progressDialog.show(fmgr, "DIALOG_F1");
                }
                catch(Exception ex)
                {
                    Toast.makeText(BookDetails.this,ex.getMessage() , Toast.LENGTH_SHORT).show();
                }
                SavePdfFile s = new SavePdfFile();
                s.execute("");
            }
        });

        openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.fromFile(pdfFile));
                try{
                    startActivity(i);
                }
                catch(ActivityNotFoundException anfe) {
                    Toast.makeText(getApplicationContext(), "You don't have a pdf reader app!", Toast.LENGTH_SHORT).show();
                    //user doesn't have an app that can open this type of file
                }
            }
        });

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        checkBookExisting();
    }

    private void checkBookExisting() {
        String storageDirectory = Environment.getExternalStorageDirectory().toString();
        pdfFile = new File(storageDirectory,"eLibraryPDFs"+"/"+ Title+".pdf");
        if (pdfFile.exists())
        {
            downloadBTN.setVisibility(View.GONE);
            openBtn.setVisibility(View.VISIBLE);
        }
        else
        {
            downloadBTN.setVisibility(View.VISIBLE);
            openBtn.setVisibility(View.GONE);
        }
    }

    public class SavePdfFile extends AsyncTask<String,String,String>
    {
        String z = "";
        @Override
        protected void onPostExecute(String r)
        {
            String result = "";
            if (z == "")
                result = getTitle() + " book saved in eLibraryPDFs folder";
            else
                result = z;
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            FragmentProgressDialog d1 = (FragmentProgressDialog) fmgr.findFragmentByTag("DIALOG_F1");
            if(d1 != null)
                fmgr.beginTransaction().remove(d1).commit();

        }

        @Override
        protected String doInBackground(String... strings)
        {
            try
            {
//                Connection con = connectionClass.CONN(ConnectionClass.ip);
                if (ConnectionClass.conn == null) {
                    z = "Error in connection with SQL server";
                } else
                {
                    String requets = "insert into BooksDownloadRequest values(" + id +" , getDate())";
                    PreparedStatement requestDownload = ConnectionClass.conn.prepareStatement(requets);
                    requestDownload.execute();
                    Thread.sleep(10000);

                    String getBookByteByID = "select pdFile from tempTable where bookid = " + id;
                    PreparedStatement ps = ConnectionClass.conn.prepareStatement(getBookByteByID);
                    ResultSet rs = ps.executeQuery();
                    while(rs.next())
                    {
                        pdfFileBytes = rs.getBytes(1);
                    }
                    CreateDirectoryAndSavePdfFile();
                }
            } catch (Exception ex) {
                z = "Error retrieving data from table";
            }
            return z;
        }
    }

    private void CreateDirectoryAndSavePdfFile()
    {
        String storageDirectory = Environment.getExternalStorageDirectory().toString();
        File main_Directory = new File(storageDirectory,"eLibraryPDFs");
        if (!main_Directory.exists())
            main_Directory.mkdirs();
        String pdfsFolder = main_Directory.getPath();
        File pdfFile = new File(pdfsFolder +"/"+ getTitle()+".pdf");
        OutputStream out = null;
        try
        {
            out = new BufferedOutputStream(new FileOutputStream(pdfFile));
            out.write(pdfFileBytes);
            out.close();
        }
        catch(Exception e){}
    }

    public class getBookByID extends AsyncTask<String, String, String>
    {
        String Category, ISBN, date, desc , pagesCount;
        byte[] coverPic;
        String z = "";

        @Override
        protected void onPostExecute(String r)
        {
            setTitle(Title);
            categoryTF.setText(Category);
            ISBNTF.setText(ISBN);
            YearTF.setText(date);
            descriptionTF.setText(desc);
            PagesCount.setText(pagesCount);


            Bitmap bmp = BitmapFactory.decodeByteArray(coverPic, 0, coverPic.length);
            coverPicIV.setImageBitmap(bmp);


//            categoryTF.setEnabled(false);
//            ISBNTF.setEnabled(false);
//            YearTF.setEnabled(false);
//            descriptionTF.setEnabled(false);
            Toast.makeText(getApplicationContext(), r, Toast.LENGTH_SHORT).show();
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
                    String getBookByID = "Select b.Title , c.Name Category,b.ISBN, b.PublishDate ," +
                            " b.Description, b.CoverPicture CoverPic, b.PagesCount from books b inner join categories c " +
                            "on b.categoryID = c.id where b.id = " + id;
                    PreparedStatement ps = ConnectionClass.conn.prepareStatement(getBookByID);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next())
                    {
                        Title = rs.getString("Title");
                        Category = rs.getString("Category");
                        ISBN = rs.getString("ISBN");
                        date = rs.getString("PublishDate");
                        desc = rs.getString("Description");
                        coverPic = rs.getBytes("CoverPic");
                        pagesCount = rs.getString("PagesCount");
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
