package com.stockt;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemGraphActivity extends AppCompatActivity {

    private BarChart barChart;
    private ItemDatabaseHelper itemDatabaseHelper;
    private Button buttonItems, buttonHome, buttonSettings, buttonXAxis, buttonYAxis;
    private boolean isXItems = true;
    private boolean isCountMode = true;

    // Define a map for category colors
    private final Map<String, Integer> categoryColors = new HashMap<String, Integer>() {{
        put("Flowers", Color.parseColor("#FF4081")); // Pink
        put("Fruits", Color.parseColor("#FFC107")); // Amber
        put("Vegetables", Color.parseColor("#4CAF50")); // Green
        put("Dairy", Color.parseColor("#2196F3")); // Blue
        put("Meat", Color.parseColor("#FF5722")); // Deep Orange
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_graph_screen);

        barChart = findViewById(R.id.barChart);
        itemDatabaseHelper = new ItemDatabaseHelper(this);

        // Chart option buttons
        buttonXAxis = findViewById(R.id.chartOption1);
        buttonYAxis = findViewById(R.id.chartOption2);

        // Initialize bottom buttons
        buttonItems = findViewById(R.id.button5);
        buttonHome = findViewById(R.id.button4);
        buttonSettings = findViewById(R.id.button3);

        // Set up button listeners
        buttonItems.setOnClickListener(v -> openItemsActivity());
        buttonHome.setOnClickListener(v -> openHomeActivity());
        buttonSettings.setOnClickListener(v -> openSettingsActivity());
        buttonXAxis.setOnClickListener(v -> changeXAxis());
        buttonYAxis.setOnClickListener(v -> changeYAxis());


        // Load inventory data and create the graph
        loadInventoryData();
    }

    private void changeXAxis() {
        if (isXItems) {
            loadInventoryDataByCategory();
            buttonXAxis.setText("Group Items");
        } else {
            loadInventoryData();
            buttonXAxis.setText("Group Categories");
        }
        isXItems = !isXItems;
    }

    private void changeYAxis() {
        if (isCountMode) {
            buttonYAxis.setText("Display Count");
        } else {
            buttonYAxis.setText("Display Value");
        }
        isCountMode = !isCountMode; // Toggle between count and value
        // Reload data based on the current mode
        if (isXItems) {
            loadInventoryData();
        } else {
            loadInventoryDataByCategory();
        }

    }

    private void loadInventoryData() {
        List<Item> items = itemDatabaseHelper.getAllItems();
        List<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        double totalValue = 0; // Initialize total value

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (isCountMode) {
                entries.add(new BarEntry(i, item.getQuantity())); // Use count
            } else {
                entries.add(new BarEntry(i, (float) (item.getQuantity() * item.getPrice()))); // Use count or value
            }
            labels.add(item.getName());
            totalValue += item.getQuantity() * item.getPrice(); // Calculate total value
            Log.i("ItemGraphActivity", "loadInventoryData: item.category = " + item.getCategory());
        }

        createBarChart(entries, labels, items);
    }

    private void loadInventoryDataByCategory() {
        List<Item> items = itemDatabaseHelper.getAllItems();
        Map<String, Float> categoryMap = new HashMap<>();

        for (Item item : items) {
            String category = item.getCategory();
            Log.i("ItemGraphActivity", "loadInventoryDataByCategory: item.category = " + category);
            if (category != null) { // Check if category is not null
                if (isCountMode) {
                    categoryMap.put(category, categoryMap.getOrDefault(category, (float) 0) + item.getQuantity());
                } else {
                    categoryMap.put(category, categoryMap.getOrDefault(category, (float) 0) + (float) (item.getQuantity() * item.getPrice()));

                }
            }
        }

        List<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        int index = 0;

        for (Map.Entry<String, Float> entry : categoryMap.entrySet()) {
            entries.add(new BarEntry(index++, entry.getValue()));
            labels.add(entry.getKey());
        }

        createBarChart(entries, labels, null);
    }

    private void createBarChart(List<BarEntry> entries, ArrayList<String> labels, List<Item> items) {
        BarDataSet dataSet = new BarDataSet(entries, "Item Count");
        dataSet.setColors();
        dataSet.setValueTextSize(12f); // Set the text size for labels
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setDrawValues(true); // Enable drawing values on top of bars

        // Set colors based on category
        for (int i = 0; i < entries.size(); i++) {
            Integer color;
            if (items != null) {
                String category = items.get(i).getCategory();
                color = categoryColors.get(category);
            } else {
                // Use the label for category
                String label = labels.get(i);
                color = categoryColors.get(label);
            }
            dataSet.addColor(color != null ? color : Color.GRAY); // Default to gray if category not found
        }

        dataSet.setValueFormatter(new MyValueFormatter(labels)); // Set custom value formatter

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        // Y-axis label
        barChart.getAxisLeft().setDrawLabels(true); // Enable Y-axis labels
        barChart.getAxisRight().setEnabled(false); // Disable right Y-axis

        // Set chart description
        barChart.getDescription().setEnabled(true);
        barChart.getDescription().setText(isCountMode ? "Total Count" : "Total Value");
        barChart.getDescription().setTextSize(12f);

        barChart.invalidate(); // Refresh the chart
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