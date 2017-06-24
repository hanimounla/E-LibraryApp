package com.mounla.hani.e_library;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Statistics extends AppCompatActivity {

    PieChart pieChart;
    BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        setTitle("Statistics Charts");

        pieChart = (PieChart) findViewById(R.id.pieChart);

        barChart = (BarChart)findViewById(R.id.barChart);
        FillCategories d =new FillCategories();
        d.execute("");

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
            List<Integer> colors = new ArrayList<>();
            colors.add(450);colors.add(300);
            colors.add(200);colors.add(654);
            colors.add(988);colors.add(123);
            colors.add(433);


            List<PieEntry> pieEntries = new ArrayList<>();
            List<BarEntry> barEntries = new ArrayList<>();
            int i =1;
            for (Map<String,String> category: Categories)
            {
                pieEntries.add(new PieEntry(Float.parseFloat(category.get("TotalBooks")),category.get("Name")));
                barEntries.add(new BarEntry(Float.parseFloat(category.get("TotalBooks")),i++));
            }
            PieDataSet pieDataSet = new PieDataSet(pieEntries, "Categories");
            pieDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
            PieData pieData = new PieData(pieDataSet);
            pieChart.setData(pieData);
            pieChart.invalidate();

            BarDataSet barDataSet = new BarDataSet(barEntries, "BarDataSet");
            barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            BarData barData = new BarData(barDataSet);
            barData.setBarWidth(0.5f); // set custom bar width
            barChart.setData(barData);
            barChart.setFitBars(false); // make the x-axis fit exactly all bars
            barChart.invalidate(); // refresh
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
