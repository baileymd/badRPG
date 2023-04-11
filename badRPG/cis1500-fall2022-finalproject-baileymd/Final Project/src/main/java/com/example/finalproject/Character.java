package com.example.finalproject;

public class Character {
    private int hitPoints;
    private int strength;
    private int dexterity;
    private int intelligence;
    private int gold;

    public Character() {
        this.hitPoints = 20;
        this.strength = rollDice();
        this.dexterity = rollDice();
        this.intelligence = rollDice();
        this.gold = 0;
    }

    public int rollDice(){
        return (int)(Math.random()*6+1) + (int)(Math.random()*6+1) + (int)(Math.random()*6+1);
    }

    public int getHitPoints() {
        return hitPoints;
    }
    public int getStrength() {
        return strength;
    }
    public int getDexterity() {
        return dexterity;
    }
    public int getIntelligence() {
        return intelligence;
    }
    public int getGold() {
        return gold;
    }

    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }
    public void setStrength(int strength) {
        this.strength = strength;
    }
    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }
    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }
    public void setGold(int gold) {
        this.gold += gold;
    }
}