package com.example.finalproject;

public class Boss extends NPC{
    public Boss(){
        hitPoints = rollDice();
        strength = rollDice();
        dexterity = rollDice();
        intelligence = rollDice();
    }

    //override parent NPC class setDifficulty with boss stat boosts
    @Override
    public void setDifficulty(int difficulty) {
        switch(difficulty){
            case(1)->{}     //easy difficulty, no additional boost to boss stats
            case(2)-> setHitPoints((int)(Math.ceil(this.hitPoints*1.25)));     //medium difficulty, boss HP boosted 25%
            case(3)->{
                setHitPoints((int)(Math.ceil(this.hitPoints*1.25)));       //hard difficulty, boss HP boosted 25%
                this.strength=(int)(Math.ceil(this.strength*1.25));    //all other stats get 25% boost over base value
                this.dexterity=(int)(Math.ceil(this.dexterity*1.25));
                this.intelligence=(int)(Math.ceil(this.intelligence*1.25));
            }
            default -> throw new IllegalStateException("Unexpected value: " + difficulty);
        }
    }

    public int rollDice(){
        //boss values can be 4-6, eliminate possible super weak bosses
        return (int)(Math.random()*3+3) + (int)(Math.random()*3+3) + (int)(Math.random()*3+3);
    }
}
