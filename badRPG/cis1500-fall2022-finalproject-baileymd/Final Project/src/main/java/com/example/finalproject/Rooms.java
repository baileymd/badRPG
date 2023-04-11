package com.example.finalproject;

public class Rooms {
    private int gold;
    private boolean hasNPC;
    NPC newNPC = new NPC();

    public Rooms(int difficulty) {
        switch (difficulty){
            case(1)->{
                int hasRPC = (int)(Math.random()*2+1);      //easy difficulty, 1 in 2 chance room is guarded
                if (hasRPC==1){
                    hasNPC = true;
                }
                this.gold = (int) (Math.random() * 100 + 1);    //gold in room between 1-100
            }
            case(2)->{
                int hasRPC = (int)(Math.random()*3+1);      //medium difficulty, 2 in 3 chance room is guarded
                if (hasRPC!=1){
                    hasNPC = true;
                }
                    this.gold = (int) (Math.random() * 90 + 10);    //gold in room between 10-100
            }
            case(3)->{
                int hasRPC = (int)(Math.random()*4+1);      //hard difficulty, 3 in 4 chance room is guarded
                if (hasRPC!=1){
                    hasNPC = true;
                }
                this.gold = (int)(Math.random() * 80 + 20);     //gold in room between 20-100
            }
        }
    }

    public int getGold() {
        return gold;
    }

    public boolean hasNPC() {
        return hasNPC;
    }
}
