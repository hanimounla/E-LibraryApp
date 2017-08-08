package com.mounla.hani.e_library;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Statistics extends AppCompatActivity {

    PieChart pieChart;
    BarChart barChart;

    int colors [] = {Color.rgb(192, 255, 140), Color.rgb(255, 247, 140), Color.rgb(255, 208, 140),
            Color.rgb(140, 234, 255), Color.rgb(255, 140, 157),
            Color.rgb(193, 37, 82), Color.rgb(255, 102, 0), Color.rgb(245, 199, 0),
            Color.rgb(106, 150, 31), Color.rgb(179, 100, 53)};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        setTitle("Categories Statistics Charts");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        pieChart = (PieChart) findViewById(R.id.pieChart);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                PieEntry p = (PieEntry)e;
                startActivity(new Intent(getApplicationContext(),Books.class).putExtra("Category",p.getLabel()));
            }

            @Override
            public void onNothingSelected() {

            }
        });

        barChart = (BarChart)findViewById(R.id.barChart);
        FillCategories d =new FillCategories();
        d.execute("");

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


    public class FillCategories extends AsyncTask<String, String, String>
    {
        String z = "";

        List<Map<String,String>> Categories = new ArrayList<>();

        @Override
        protected void onPreExecute()
        {

        }

        @Override
        protected void onPostExecute(String r)
        {
            makePieChart();
            makeBarChart();
//            List<Integer> colors = new ArrayList<>();
//            colors.add(450);colors.add(300);
//            colors.add(200);colors.add(654);
//            colors.add(988);colors.add(123);
//            colors.add(433);

//

        }

        private void makePieChart() {
            final List<PieEntry> pieEntries = new ArrayList<>();
            for (Map<String,String> category: Categories) {
                if(Integer.parseInt(category.get("TotalBooks")) != 0 )
                    pieEntries.add(new PieEntry(Float.parseFloat(category.get("TotalBooks")), category.get("Name")));
            }
            PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
            pieDataSet.setColors(colors);
            final PieData pieData = new PieData(pieDataSet);

            pieChart.setData(pieData);
            pieChart.setCenterText("Pie Chart");
            pieChart.setEntryLabelTextSize(0);



            Legend l = pieChart.getLegend();
            l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
            l.setForm(Legend.LegendForm.CIRCLE);

            Description d = pieChart.getDescription();
            d.setEnabled(false);

            pieChart.setTouchEnabled(true);
            pieChart.invalidate();

            pieChart.animateX(2000);
            pieChart.animateY(2000);
        }

        private void makeBarChart() {
            List<BarEntry> barEntries = new ArrayList<>();
            Legend l = barChart.getLegend();

            int i =1;
            for (Map<String,String> category: Categories)
            {
                if(Integer.parseInt(category.get("TotalBooks")) != 0 )
                    barEntries.add(new BarEntry(i++,Float.parseFloat(category.get("TotalBooks"))));
            }


            BarDataSet barDataSet = new BarDataSet(barEntries, "Categories");
            barDataSet.setColors(colors);
            final BarData barData = new BarData(barDataSet);

            Description d = barChart.getDescription();
            d.setEnabled(false);

            barChart.setData(barData);
            barChart.invalidate();
            barChart.animateY(2000);
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
                    String query = "Select Name , TotalBooks from Categories";

                    PreparedStatement ps = ConnectionClass.conn.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next())
                    {
                        Map<String,String> category = new HashMap<>();
                        category.put("Name",rs.getString(1));
                        category.put("TotalBooks",rs.getString(2));
                        Categories.add(category);
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
