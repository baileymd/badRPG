package com.example.finalproject;

public class NPC{
    int hitPoints;
    int strength;
    int dexterity;
    int intelligence;

    public NPC() {
            setHitPoints(stats());
            setStrength();
            setDexterity();
            setIntelligence();
    }

    //generates new stats for each sleep attack NPC
    public void refreshNPC(){
        setHitPoints(stats());
        setStrength();
        setDexterity();
        setIntelligence();
    }

    //set NPC stats based on difficulty selected
    public void setDifficulty(int difficulty){
        switch(difficulty){
            case(1)->{}     //easy difficulty, no changes made to NPC stats
            case(2)-> setHitPoints((int)Math.ceil(this.hitPoints*1.25));    //medium difficulty, higher HP for NPC
            case(3)->{
                setHitPoints((int)(Math.ceil(this.hitPoints*1.25)));      //hard difficulty, higher HP and 25% boost
                this.strength=(int)(Math.ceil(this.hitPoints*1.25));        //all other stats get 25% boost over base value
                this.dexterity=(int)(Math.ceil(this.hitPoints*1.25));
                this.intelligence=(int)(Math.ceil(this.hitPoints*1.25));
            }
            default -> throw new IllegalStateException("Unexpected value: " + difficulty);
        }
    }

    public int stats(){
        return (int)(Math.random()*5+2);
    }   //random number 2-6 for HP of NPC

    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }
    public void setStrength() {
        this.strength = this.hitPoints*2;
    }
    public void setDexterity() {
        this.dexterity = this.hitPoints*2;
    }
    public void setIntelligence() {
        this.intelligence = this.hitPoints*2;
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
}
