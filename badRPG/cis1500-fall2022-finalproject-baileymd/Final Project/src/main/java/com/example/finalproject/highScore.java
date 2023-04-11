package com.example.finalproject;

public class highScore {
    private int placeOnList;
    private String initials;
    private int goldTotal;

    public highScore(int placeOnList, String initials, int goldTotal) {
        this.placeOnList = placeOnList;
        this.initials = initials;
        this.goldTotal = goldTotal;
    }

    public String getLineForFile(){
        return placeOnList + "|" + initials + "|" + goldTotal;
    }
    public int getPlaceOnList() {
        return placeOnList;
    }
    public String getInitials() {
        return initials;
    }
    public int getGoldTotal() {
        return goldTotal;
    }

    public void setPlaceOnList(int placeOnList) {
        this.placeOnList = placeOnList;
    }
    public void setInitials(String initials) {
        this.initials = initials;
    }
    public void setGoldTotal(int goldTotal) {
        this.goldTotal = goldTotal;
    }
}
