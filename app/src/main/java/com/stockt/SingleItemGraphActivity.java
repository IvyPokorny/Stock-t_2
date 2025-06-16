package com.stockt;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.time.LocalDateTime;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SingleItemGraphActivity extends AppCompatActivity {
    private LineChart lineChart;
    private Button toggleButton;
    private Button buttonItems, buttonHome, buttonSettings;
    private boolean showUnitsSold = true; //true for units sold, false for value sold
    private ItemDatabaseHelper itemDatabaseHelper;
    private InventoryHistoryDatabaseHelper inventoryHistoryHelper;
    private int itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_item_graph_screen);

        lineChart = findViewById(R.id.lineChart);
        toggleButton = findViewById(R.id.chartOption1);

        buttonItems = findViewById(R.id.button5);
        buttonHome = findViewById(R.id.button4);
        buttonSettings = findViewById(R.id.button3);

        itemDatabaseHelper = new ItemDatabaseHelper(this);
        inventoryHistoryHelper = new InventoryHistoryDatabaseHelper(this);

        //Get itemId from intent extras
        itemId = getIntent().getIntExtra("ITEM_ID", -1);

        setupChart();
        loadData();

        buttonItems.setOnClickListener(v -> openItemsActivity());
        buttonHome.setOnClickListener(v -> openHomeActivity());
        buttonSettings.setOnClickListener(v -> openSettingsActivity());
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUnitsSold = !showUnitsSold; //Toggle between units and value
                loadData(); //Reload data with the new setting
            }
        });
    }

    private void setupChart() {
        lineChart.getDescription().setEnabled(false);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); //Set granularity for grouping
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
    }

    private void loadData() {
        List<Adjustment> adjustments = inventoryHistoryHelper.getAllAdjustments();
        List<Entry> entries = new ArrayList<>();

        Log.i("loadData", "loadData: Size of Adjustments=" + adjustments.size());
        Log.i("loadData", "loadData: ItemID by Activity Extra=" + String.valueOf(itemId));
        //Group sales by day, week, month logic
        for (Adjustment adjustment : adjustments) {
            if (adjustment.getItemId() == itemId && adjustment.getAdjustmentReason().equals("Sold")) {
                //Use LocalDateTime to parse the date and time
                //LocalDateTime dateTime = LocalDateTime.parse(adjustment.getAdjustmentTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                LocalDateTime dateTime = adjustment.getAdjustmentTime();

                //Convert LocalDateTime to epoch days for X-axis
                long epochDay = dateTime.toEpochSecond(ZoneOffset.UTC);
                float value = showUnitsSold ? adjustment.getQuantityChange() : (float) (adjustment.getQuantityChange() * itemDatabaseHelper.getItem(adjustment.getItemId()).getPrice());

                //Add to entries
                entries.add(new Entry(epochDay, value));
            }
        }
        Log.i("loadData", "loadData: Size of Entries=" + entries.size());


        LineDataSet dataSet = new LineDataSet(entries, showUnitsSold ? "Units Sold" : "Value Sold");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextColor(R.color.black); //Set value color

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate(); //Refresh chart
    }

    private void openItemsActivity() {
        Intent intent = new Intent(this, InventoryActivity.class);
        startActivity(intent);
    }

    private void openHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}