package com.mounla.hani.e_library;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //    private TextView txtSpeechInput;
//    private Button btnSpeak;
    String userName;
    TextView userNameDrawerLBL;
    View backGround;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        userName = getIntent().getStringExtra("userName");

        View navHeaderView = navigationView.getHeaderView(0);//navigationView.inflateHeaderView(R.layout.nav_header_main);
        userNameDrawerLBL = (TextView) navHeaderView.findViewById(R.id.userNameDrawerLBL);
        userNameDrawerLBL.setText("Welcome " + userName + " :)");

        backGround = this.getWindow().getDecorView();
        backGround.setBackgroundColor(Color.DKGRAY);


        setTitle("Search");
        loadFragment(new FragmentSearch(), "search");
    }

    private boolean exit = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (exit)
                finish();
            else {
//                FragmentSearch fragmentSearch = (FragmentSearch) getSupportFragmentManager().findFragmentByTag("search");
//                FragmentCategories FragmentCategories = (FragmentCategories) getSupportFragmentManager().findFragmentByTag("categories");
//                FragmentAuthors FragmentAuthors = (FragmentAuthors) getSupportFragmentManager().findFragmentByTag("authors");
//                FragmentPublishers FragmentPublishers = (FragmentPublishers) getSupportFragmentManager().findFragmentByTag("publishers");
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.mainFrame);
                if (currentFragment.getTag().equals("search") || currentFragment.getTag().equals("categories")||
                    currentFragment.getTag().equals("authors") || currentFragment.getTag().equals("publishers"))
                 {
                    Toast.makeText(this, "Press back once more to exit.",
                            Toast.LENGTH_SHORT).show();
                    exit = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            exit = false;
                        }
                    }, 2 * 1000);
                } else
                    getSupportFragmentManager().popBackStack();
            }
        }
    }
//    int i = 0;
//    @Override
//    public void onBackPressed()
//    {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            i++;
//            if (i == 1) {
//                Toast.makeText(this, "Press back once more to exit.",
//                        Toast.LENGTH_SHORT).show();
//            } else if(i>1)
//                finish();
//        }
//
//    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//        i = 0;
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_about) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        String tag = "";

        switch (id) {
            case R.id.nav_search:
                setTitle("Search");
                tag = "search";
                fragment = new FragmentSearch();
                break;
            case R.id.nav_books:
                openBooksDirectory();
                return true;
            case R.id.nav_categories:

                setTitle("Categories");
                tag = "categories";
                fragment = new FragmentCategories();
                break;
            case R.id.nav_authors:
                setTitle("Authors");
                tag = "authors";
                fragment = new FragmentAuthors();
                break;
            case R.id.nav_publishers:
                setTitle("Publishers");
                tag = "publishers";
                fragment = new FragmentPublishers();
                break;
            case R.id.nav_statistics:
                startActivity(new Intent(this, Statistics.class));
                return true;
//            case R.id.nav_share : Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivity(`i); break;

        }
        loadFragment(fragment, tag);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openBooksDirectory() {
        Uri booksDirectory = Uri.parse(Environment.getExternalStorageDirectory().toString() + "/eLibraryPDFs");
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(booksDirectory, "resource/folder");
        try {
            startActivity(i);
        } catch (ActivityNotFoundException anfe) {
            Toast.makeText(getApplicationContext(), "You don't have a pdf reader app!", Toast.LENGTH_SHORT).show();
            //user doesn't have an app that can open this type of file
        }

    }

    private void loadFragment(Fragment fragment, String tag) {
        FragmentManager fragMgr = getSupportFragmentManager();
        FragmentTransaction fragTrans = fragMgr.beginTransaction();
        fragTrans.replace(R.id.mainFrame, fragment, tag);
        fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragTrans.commit();

//        if (fragment != null) {
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            fragmentManager.beginTransaction().replace(R.id.mainFrame, fragment).commit();
//        }
//        else
//        {
//            Toast.makeText(this, "Error in creating fragment", Toast.LENGTH_SHORT).show();
//        }
    }
//    private void loadCategoriesFragment() {
//        FragmentCategories a = new FragmentCategories();
//        fragTrans.replace(R.id.mainFrame, a);
//        fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        try{
//            fragTrans.commit();
//
//        } catch(Exception ex) {
//            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void loadBooksFragment() {
//
//    }
//
//    private void loadSearchFragment()
//    {
//        FragmentSearch a = new FragmentSearch();
//        fragTrans.replace(R.id.mainFrame, a);
//        fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        try{fragTrans.commit();} catch(Exception ex) {
//            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
}


