package com.stockt;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Adjustment {
    private int id;
    private int itemId;
    private String itemName;
    private int quantityChange;
    private float valueChange;
    LocalDateTime adjustmentTime;
    private String adjustmentReason;

    public Adjustment(int id, int itemId, String itemName, int quantityChange, LocalDateTime adjustmentTime, String adjustmentReason) {
        this.id = id;
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantityChange = quantityChange;
        this.adjustmentTime = adjustmentTime;
        this.adjustmentReason = adjustmentReason;
    }

    public Adjustment(int id, int itemId, String itemName, int quantityChange, String adjustmentTime, String adjustmentReason) {
        this.id = id;
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantityChange = quantityChange;
        this.adjustmentTime = LocalDateTime.parse(adjustmentTime);
        this.adjustmentReason = adjustmentReason;
    }



    //Getters and setters
    public int getId() { return id; }
    public int getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public int getQuantityChange() { return quantityChange; }
    public LocalDateTime getAdjustmentTime() { return adjustmentTime; }
    public String getAdjustmentReason() { return adjustmentReason; }
}