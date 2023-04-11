package com.example.finalproject;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.io.*;
import java.util.ArrayList;

public class HelloController {

    @FXML
    private AnchorPane mainMap;
    @FXML
    private AnchorPane difficultyMenu;
    @FXML
    private Button easyButton;
    @FXML
    private Button mediumButton;
    @FXML
    private Button hardButton;
    @FXML
    private Button saveScore;
    @FXML
    private Button playAgain;
    @FXML
    private Button northButton;
    @FXML
    private Button southButton;
    @FXML
    private Button eastButton;
    @FXML
    private Button westButton;
    @FXML
    private Button attackButton;
    @FXML
    private Button fleeButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button sleepButton;
    @FXML
    private TextArea gameplayText;
    @FXML
    private TextArea highScoresDisplay;
    @FXML
    private TextField userInitials;
    @FXML
    private Label playerStats;
    @FXML
    private Label npcStats;
    @FXML
    private Rectangle userCharacter;
    @FXML
    private Rectangle enemy;
    @FXML
    private Rectangle doorKey;
    @FXML
    private Rectangle roomSetOne;
    @FXML
    private Rectangle roomSetTwo;
    @FXML
    private Rectangle roomSetThree;
    @FXML
    private Rectangle roomSetFour;
    @FXML
    private Rectangle doorOne;
    @FXML
    private Rectangle doorTwo;
    @FXML
    private Rectangle doorThree;
    @FXML
    private Rectangle doorFour;
    @FXML
    private Rectangle doorFive;
    @FXML
    private Rectangle doorSix;
    @FXML
    private Rectangle doorSeven;
    @FXML
    private Rectangle doorEight;
    @FXML
    private Rectangle doorNine;
    @FXML
    private Rectangle doorTen;
    @FXML
    private Rectangle doorEleven;
    @FXML
    private Rectangle doorTwelve;
    @FXML
    private Rectangle finalDoor;
    @FXML
    private Rectangle treasureChest;

    //images for character
    Image walkNorth1 = new Image("/walknorth1.png");
    Image walkNorth2 = new Image("/walknorth2.png");
    Image walkEast1 = new Image("/walkeast1.png");
    Image walkEast2 = new Image("/walkeast2.png");
    Image walkSouth1 = new Image("/walksouth1.png");
    Image walkSouth2 = new Image("/walksouth2.png");
    Image walkWest1 = new Image("/walkwest1.png");
    Image walkWest2 = new Image("/walkwest2.png");
    Image guardLeft = new Image("guardleft.png");
    Image guardRight = new Image("guardright.png");
    Image sleeping = new Image("/fire.png");
    Image dead = new Image("/dead.png");

    //images for NPC
    Image attackRight = new Image("/attackright.png");
    Image attackLeft = new Image("/attackleft.png");
    Image npcAttackRight = new Image("/npcattackright.png");
    Image npcAttackLeft = new Image("/npcattackleft.png");
    Image bossAttack = new Image("/bossattack.png");
    Image bossDead = new Image("/npcdead.png");

    //misc images
    Image chest = new Image("/treasurechest.png");
    Image chestOpen = new Image("/treasurechestopened.png");
    Image gold = new Image("/gold.png");
    Image key = new Image("/key.png");

    int walking = 1;    //used to switch between walking images (create "animation")
    String duplicateText = "";    //used to check if displaying same direction of movement repeatedly
    String guardSide="";        //display proper left/right guarding image
    String attackSide="";       //display proper left/right attack image after blocking enemy attack

    Character newCharacter = new Character();   //user character
    Boss finalBoss = new Boss();    //final boss

    Rectangle[] doors = new Rectangle[12];      //array of rectangles to store FXML dungeon doors for interaction detection
    ArrayList<Rooms> listOfRooms = new ArrayList<>();   //array of rooms for NPCs and gold amount
    ArrayList<highScore> highScores = new ArrayList<>();        //array for previous high scores loaded from file
    ArrayList<highScore> newHighScores = new ArrayList<>();     //array for new high scores to save to file

    int difficultyLevel = 0;    //user selected difficulty 1-3 easy-medium-hard
    int roomsCleared = 0;     //count of cleared rooms
    int roomNumber;   //variable to cycle through rooms as player finds door
    int saveRoomNum;    //store dungeon room number if attacked while sleeping (extra room created for NPC use)
    boolean isWallIntersecting = false;     //is character running into wall
    boolean isFirstCheck = true;    //is character opening treasure chest (first time interaction opens chest)
    boolean hasKey = false;     //has user opened chest and obtained boss room key
    boolean checkHighScore = false;       //only check for high score on game win
    double prevXPos;    //store character's previous X pos
    double prevYPos;    //store character's previous Y pos
    double saveHeight;      //save original height for post sleep image
    double saveWidth;        //save original width for post sleep image
    String initials;    //save score with player's initials

    public void initialize() throws IOException {
        initializeScreen();     //set button enable/visible/etc for initial game load
        disableAllButtons();    //disable all other buttons until difficulty selected
        saveHeight = userCharacter.getHeight();      //save original height for post sleep image
        saveWidth = userCharacter.getWidth();        //save original width for post sleep image
    }

    @FXML
    public void onAttackButtonPress()  {
        //if all dungeon rooms have been cleared, and not a sleep attack
        if (roomsCleared == 12 && roomNumber != 12) {
            bossFight();    //fight final boss
        } else {
            monsterFight();     //otherwise fight regular NPC
        }

        checkSleep();   //display Sleep button if not at full health after fighting
    }

    @FXML
    public void onFleeButtonPress() {
        gameplayText.appendText("\nAs you turn to run the monster attacks... ");
        if (rollTwentySidedDie() > listOfRooms.get(roomNumber).newNPC.getIntelligence()) {  //determine outcome of monster's attack while fleeing
            newCharacter.setHitPoints(newCharacter.getHitPoints() - (listOfRooms.get(roomNumber).newNPC.getStrength() / 3));    //reduce HitPoints if successful attack
            gameplayText.appendText("successfully dealing " + (listOfRooms.get(roomNumber).newNPC.getStrength() / 3) + " damage!");   //display successful attack
            userCharacter.setFill(new ImagePattern(walkSouth1));
            checkGameOver();    //check if character HitPoints <= 0
            displayPlayerStats();   //update display of PlayerStats
        } else {
            gameplayText.appendText("but it missed. You got away!");  //monster's flee attack missed\
            enemy.setVisible(false);
            userCharacter.setFill(new ImagePattern(walkSouth1));
            npcStats.setText("");   //clear display of NPC stats
        }

        enableMoveButtons();    //re-enable movement buttons from Attack sequence
        checkSleep();   //display Sleep button if not at full health
    }

    @FXML
    public void onSearchButtonPress() {
        searchButton.setDisable(true);  //disable Search button after a search

        //check if roomNumber (set in onMovementButtonPress method when door collision detected) is guarded
        if (listOfRooms.get(roomNumber).hasNPC()) {     //boolean hasNPC is true for roomNumber (room is guarded)
            gameplayText.appendText("\nThe room is guarded, you must fight the monster or flee!");
            displayEnemy();     //show NPC image "guarding" room

            disableMoveButtons();   //only Attack and Flee buttons enabled
            displayNPCStats();  //update display with NPC stats
        } else {
            //boolean hasNPC is false for roomNumber (room unguarded, collect gold in room)
            gameplayText.appendText("\nThe room is empty except for " + listOfRooms.get(roomNumber).getGold() + " gold, " +
                    "you add it to your collection!");
            displayEnemy();     //make sure enemy rectangle set to correct door for gold display
            enemy.setFill(new ImagePattern(gold));  //display gold in enemy rectangle
            userCharacter.setFill(new ImagePattern(walkSouth1));    //change character from attack
            newCharacter.setGold(listOfRooms.get(roomNumber).getGold());    //update characters total gold
            displayPlayerStats();   //update display of character's stats (gold total)
            closeDoor(roomNumber);  //hide FXML door of cleared dungeon room, prevents collision detection of cleared room
        }
    }

    @FXML
    public void onSleepButtonPress() {
        enemy.setVisible(false);    //hides found gold
        int sleepAttack = (int) (Math.random() * 6 + 1);    //1 in 6 chance NPC attacks while sleeping

        if (sleepAttack == 1) {     //if attacked while sleeping
            saveRoomNum = roomNumber;   //store dungeon room number for after sleep fight (ensure no gold from sleep fight win)
            roomNumber = 12;    //index listOfRooms to the "extra room" for NPC to fight from sleep attack

            gameplayText.appendText("\nA monster found you while sleeping! You must fight it or run away!");
            listOfRooms.get(roomNumber).newNPC.refreshNPC();    //create new NPC for sleep attack (if attacked while sleeping  more than once)

            displayEnemy();         //display sleep attack enemy
            displayNPCStats();      //update display with NPC stats
            disableMoveButtons();   //only Attack and Flee buttons enabled
        } else {
            gameplayText.appendText("\nYou feel refreshed after sleeping, HP fully restored!");
            newCharacter.setHitPoints(20);      //reset character's hitPoints
            userCharacter.setHeight(25);
            userCharacter.setWidth(41);
            if(userCharacter.getLayoutX()>151 && userCharacter.getLayoutX()<224){
                userCharacter.setLayoutX(177);
            }
            userCharacter.setFill(new ImagePattern(sleeping));
            checkSleep();   //disable sleep button since full health
            displayPlayerStats();   //update display of character's stats (full health)
        }
    }

    @FXML
    public void onMovementButtonPress(ActionEvent actionEvent) {
        userCharacter.setHeight(saveHeight);    //resets character size if larger sleep image used
        userCharacter.setWidth(saveWidth);
        enemy.setVisible(false);        //hides found gold
        searchButton.setDisable(true);  //disable search button until collision detected
        moveFromButton(actionEvent);    //determine direction of movement

        for (int i = 0; i < 12; i++) {  //check for character intersecting a dungeon door
            if (userCharacter.getBoundsInParent().intersects(doors[i].getBoundsInParent())) {
                gameplayText.appendText("\nYou found a room!");

                userCharacter.setLayoutX(prevXPos);//prevent character from walking through doors
                userCharacter.setLayoutY(prevYPos);//character would intersect with wall, displaying "Cannot move here!" when actually at a door

                searchButton.setDisable(false);     //enable Search button to check for NPC and gold
                roomNumber = i;     //set roomNumber to index value for listOfRooms in onSearchButtonPress method to check for NPC
            }
        }

        if (userCharacter.getBoundsInParent().intersects(treasureChest.getBoundsInParent())) {
            //prevent character from walking through treasureChest image (set to previous position if collision detected)
            userCharacter.setLayoutX(prevXPos);
            userCharacter.setLayoutY(prevYPos);

            //open treasure chest if it's the first time character intersects treasureChest
            if (isFirstCheck) {
                openTreasureChest();    //switch treasureChest to opened image, boost character stats, give player key
                gameplayText.appendText("""
                        \nUpon opening the treasure chest you feel a power rush over you!
                        Your HP increases and your stats double!
                        At the bottom of the chest you find a key!""");
                displayPlayerStats();   //update display of character's stats (has key)
                checkSleep();
                isFirstCheck = false;   //prevent message from appearing if character intersects opened treasure chest
            }
        }

        //user has boss room key and is intersecting boss room door
        if (hasKey && userCharacter.getBoundsInParent().intersects(finalDoor.getBoundsInParent())) {
            hasKey = false;     //remove key from "inventory" (won't display in stats)
            displayPlayerStats();

            gameplayText.appendText("""
                    \nYou turn the key and open the giant door. This room seems different than the rest...
                    You enter and see an enormous monster, much bigger than any other you've encountered!
                    As you walk into the room the door slams shut behind you, only a few candles provide some light.
                    There is no fleeing from this fight...""");
            finalDoor.setFill(Color.GREENYELLOW);   //change boss room door color from red to green (unlocked)
            doorKey.setVisible(false);

            //display user fighting boss
            userCharacter.setLayoutX(164);
            userCharacter.setLayoutY(17);
            userCharacter.setFill(new ImagePattern(attackRight));
            enemy.setLayoutX(198);
            enemy.setLayoutY(14);
            enemy.setFill(new ImagePattern(bossAttack));
            enemy.setVisible(true);

            //only allow user to Attack, no flee option
            disableAllButtons();
            attackButton.setDisable(false);

            displayBossStats(); //update NPC display with boss stats
        } else if (userCharacter.getBoundsInParent().intersects(finalDoor.getBoundsInParent())) {   //user doesn't have key yet
            gameplayText.appendText("\nThis room is locked!");
            userCharacter.setLayoutX(prevXPos);     //prevent walking through boss door into wall
            userCharacter.setLayoutY(prevYPos);
        }
    }

    public void moveFromButton(ActionEvent actionEvent) {
        //store character's current position as previous position before next movement
        prevXPos = userCharacter.getLayoutX();
        prevYPos = userCharacter.getLayoutY();

        if (actionEvent.getSource() == northButton) {
            moveNorth();    //move up on North button press
        } else if (actionEvent.getSource() == eastButton) {
            moveEast();     //move right on East button press
        } else if (actionEvent.getSource() == southButton) {
            moveSouth();    //move down on South button press
        } else if (actionEvent.getSource() == westButton) {
            moveWest();     //move left on West button press
        }
    }

    //save user initials and total gold as high score
    @FXML
    public void hasInitialsEntered(ActionEvent actionEvent) {
        saveScore.setDisable(userInitials.getLength() != 3);
    }
    @FXML
    public void onSaveScorePress() throws IOException {
        gameplayText.appendText("\n");
        if(userInitials.getLength()!=3){
            gameplayText.appendText("\nYou must enter 3 initials for high scores.");
        }else {
            checkHighScore();

            //save file
            try {
                //create file
                PrintWriter fileOutput = new PrintWriter(new BufferedWriter(new FileWriter("Bad RPG High Scores.txt")));

                //write to file
                for (highScore nextHighScore : newHighScores) {
                    fileOutput.println(nextHighScore.getLineForFile());
                }

                //close file
                fileOutput.close();
            } catch (
                    IOException exception) {   //catch exceptions
                exception.printStackTrace();
            }

            //display new high scores
            highScores = newHighScores;
            readHighScores();
            userInitials.setDisable(true);
            saveScore.setDisable(true);
        }
    }
    public void readHighScores() throws IOException {
        //open file for reading
        BufferedReader inputFile = new BufferedReader(new FileReader("Bad RPG High Scores.txt"));

        //read file
        String line = inputFile.readLine();
        while (line != null) {
            if (!line.isEmpty()) {
                String[] parts = line.split("\\|");     //split file line by '|' character
                int posOnList = Integer.parseInt(parts[0]);     //get position on high score list
                String initials = parts[1];     //get initials of high score
                int goldTotal = Integer.parseInt(parts[2]);     //get high score
                highScore nextHighScore = new highScore(posOnList, initials, goldTotal);        //add data from file to new highScore instance
                highScores.add(nextHighScore);      //populate array with current high scores from file
                line = inputFile.readLine();
            }
        }
        //close file
        inputFile.close();

        //display high scores
        highScoresDisplay.clear();
        highScoresDisplay.appendText("High Scores:");
        for(int i=0; i<3; i++) {
            highScoresDisplay.appendText("\n");
            highScoresDisplay.appendText(highScores.get(i).getLineForFile());
        }
    }
    public void checkHighScore(){
        boolean highScoreSet = false;
        initials = userInitials.getText();


        //check current score against high scores
        for(int i=0; i<3; i++){
            if (!highScoreSet && newCharacter.getGold() >= highScores.get(i).getGoldTotal()){
                highScore nextHighScore = new highScore(i+1, initials, newCharacter.getGold());
                switch(i) {
                    case 0 -> {     //set new 1st place score
                        newHighScores.add(nextHighScore);
                        highScores.get(0).setPlaceOnList(2);
                        newHighScores.add(highScores.get(0));
                        highScores.get(1).setPlaceOnList(3);
                        newHighScores.add(highScores.get(1));
                        highScoreSet = true;
                        gameplayText.appendText("\nNEW HIGH SCORE!!");
                    }
                    case 1 -> {     //set new 2nd place score
                        newHighScores.add(highScores.get(0));
                        newHighScores.add(nextHighScore);
                        highScores.get(1).setPlaceOnList(3);
                        newHighScores.add(highScores.get(1));
                        highScoreSet = true;
                        gameplayText.appendText("\nNEW HIGH SCORE!!");
                    }
                    case 2 -> {     //set new 3rd place score
                        newHighScores.add(highScores.get(0));
                        newHighScores.add(highScores.get(1));
                        newHighScores.add(nextHighScore);
                        highScoreSet = true;
                        gameplayText.appendText("\nNEW HIGH SCORE!!");
                    }
                }
            }
        }
        if(!highScoreSet){
            gameplayText.appendText("\nNo new high score. Better luck next time.");
            newHighScores = highScores;     //copy old high scores since no new high score
        }
    }

    //start game over after win/death
    @FXML
    public void onPlayAgainPress(ActionEvent actionEvent) throws IOException {
        initializeScreen();
        resetDoorFXMLs();

        //reset variables
        roomsCleared = 0;
        isWallIntersecting = false;
        isFirstCheck = true;
        hasKey = false;
        checkHighScore = false;

        newCharacter = new Character();     //new character stats
        finalBoss = new Boss();     //reset boss stats
        displayPlayerStats();
        npcStats.setText("");
        gameplayText.clear();

        disableAllButtons();
        difficultyMenu.setVisible(true);
    }

    public void moveNorth(){
        userCharacter.setLayoutY(userCharacter.getLayoutY()-5);

        //stop character from walking through walls
        if (userCharacter.getBoundsInParent().intersects(roomSetOne.getBoundsInParent()) ||
                userCharacter.getBoundsInParent().intersects(roomSetTwo.getBoundsInParent())){
            if (!isWallIntersecting) {
                gameplayText.appendText("\nCannot move here!");
                isWallIntersecting=true;
            }
            userCharacter.setLayoutY(91);
        }
        //or from walking out of top of map
        else if (userCharacter.getLayoutY()<2){
            if(!isWallIntersecting) {
                gameplayText.appendText("\nCannot move here!");
                isWallIntersecting=true;
            }
            userCharacter.setLayoutY(2);
        }
        else {
            //valid move up
            if (!duplicateText.equals("North")) {        //only display direction once in gamePlay text
                duplicateText = "North";
                gameplayText.appendText("\nYou head North!");
            }

            //alternate between walking images to simulate animation
            if (walking == 1) {
                userCharacter.setFill(new ImagePattern(walkNorth1));
                walking = 2;
            } else if (walking == 2) {
                userCharacter.setFill(new ImagePattern(walkNorth2));
                walking = 1;
            }
            isWallIntersecting = false;      //character is not intersecting wall on valid move
        }
    }
    public void moveEast(){
        userCharacter.setLayoutX(userCharacter.getLayoutX() + 5);

        //stop character from walking through walls
        if (userCharacter.getBoundsInParent().intersects(roomSetTwo.getBoundsInParent()) ||
                userCharacter.getBoundsInParent().intersects(roomSetFour.getBoundsInParent())){
            if(!isWallIntersecting) {
                gameplayText.appendText("\nCannot move here!");
                isWallIntersecting = true;
            }
                userCharacter.setLayoutX(223);
        }
        //or from walking out of right side of map
        else if (userCharacter.getLayoutX() > 372) {
            if(!isWallIntersecting) {
                gameplayText.appendText("\nCannot move here!");
                isWallIntersecting = true;
            }
            userCharacter.setLayoutX(372);
        }
        else {
            //valid move to right
            if(!duplicateText.equals("East")) {     //only display direction once in gamePlay text
                duplicateText = "East";
                gameplayText.appendText("\nYou head East!");
            }

            //alternate between walking images to simulate animation
            if(walking==1) {
                userCharacter.setFill(new ImagePattern(walkEast1));
                walking = 2;
            }
            else if (walking==2){
                userCharacter.setFill(new ImagePattern(walkEast2));
                walking=1;
            }
            isWallIntersecting = false;     //character is not intersecting wall on valid move
        }
    }
    public void moveSouth(){
        userCharacter.setLayoutY(userCharacter.getLayoutY() + 5);

        //stop character from walking through walls
        if (userCharacter.getBoundsInParent().intersects(roomSetThree.getBoundsInParent()) ||
                userCharacter.getBoundsInParent().intersects(roomSetFour.getBoundsInParent())){
            if(!isWallIntersecting) {
                gameplayText.appendText("\nCannot move here!");
                isWallIntersecting = true;
            }
            userCharacter.setLayoutY(124);
        }
        //or from walking out bottom of map
        else if (userCharacter.getLayoutY() > 213) {
            if(!isWallIntersecting) {
                gameplayText.appendText("\nCannot move here!");
                isWallIntersecting = true;
            }
            userCharacter.setLayoutY(213);
        }
        else {
            //valid move down
            if(!duplicateText.equals("South")) {    //only display direction once in gamePlay text
                duplicateText = "South";
                gameplayText.appendText("\nYou head South!");
            }

            //alternate between walking images to simulate animation
            if(walking==1) {
                userCharacter.setFill(new ImagePattern(walkSouth1));
                walking = 2;
            }
            else if(walking==2){
                userCharacter.setFill(new ImagePattern(walkSouth2));
                walking = 1;
            }
            isWallIntersecting = false;     //character is not intersecting wall on valid move
        }
    }
    public void moveWest(){
        userCharacter.setLayoutX(userCharacter.getLayoutX() - 5);

        //stop character from walking through walls
        if (userCharacter.getBoundsInParent().intersects(roomSetOne.getBoundsInParent()) ||
                userCharacter.getBoundsInParent().intersects(roomSetThree.getBoundsInParent())){
            if(!isWallIntersecting) {
                gameplayText.appendText("\nCannot move here!");
                isWallIntersecting = true;
            }
            userCharacter.setLayoutX(151);
        }
        //or from walking out left side of map
        else if (userCharacter.getLayoutX() < 2) {
            if(!isWallIntersecting) {
                gameplayText.appendText("\nCannot move here!");
                isWallIntersecting = true;
            }
            userCharacter.setLayoutX(2);
        }
        else {
            //valid move to left
            if(!duplicateText.equals("West")) {     //only display direction once in gamePlay text
                duplicateText = "West";
                gameplayText.appendText("\nYou head West!");
            }

            //alternate between walking images to simulate animation
            if(walking==1) {
                userCharacter.setFill(new ImagePattern(walkWest1));
                walking = 2;
            }
            else if(walking==2) {
                userCharacter.setFill(new ImagePattern(walkWest2));
                walking = 1;
            }
            isWallIntersecting = false;     //character is not intersecting wall on valid move
        }
    }

    public void displayPlayerStats(){
        playerStats.setText("HP: " + newCharacter.getHitPoints() + "\n" +
                "Strength: " + newCharacter.getStrength() + "\n" +
                "Dexterity: " + newCharacter.getDexterity() + "\n" +
                "Intelligence: " + newCharacter.getIntelligence() + "\n" +
                "Gold: " + newCharacter.getGold());
    }
    public void displayNPCStats(){      //displays NPC stats
        npcStats.setText("HP: " + listOfRooms.get(roomNumber).newNPC.getHitPoints() + "\n" +
                "Strength: " + listOfRooms.get(roomNumber).newNPC.getStrength() + "\n" +
                "Dexterity: " + listOfRooms.get(roomNumber).newNPC.getDexterity() + "\n" +
                "Intelligence: " + listOfRooms.get(roomNumber).newNPC.getIntelligence());
    }
    public void displayBossStats(){     //displays boss stats
        npcStats.setText("HP: " + finalBoss.getHitPoints() + "\n" +
                "Strength: " + finalBoss.getStrength() + "\n" +
                "Dexterity: " + finalBoss.getDexterity() + "\n" +
                "Intelligence: " + finalBoss.getIntelligence());
    }

    public void bossFight() {
        if ((rollTwentySidedDie() + rollTwentySidedDie()) >= finalBoss.getDexterity()){     //rolls two 20 sided dice to beat boss' dex
            finalBoss.setHitPoints(finalBoss.getHitPoints()-(newCharacter.getStrength()/3));    //successful attack deals Strength/3 damage
            gameplayText.appendText("\nSuccessful attack, you dealt " + newCharacter.getStrength() / 3 + " damage!");       //update gamePlay text
            displayBossStats();     //display boss stats

            if (finalBoss.getHitPoints()>0){        //boss not dead
                bossAttack();       //boss attacks
            }
            else if (finalBoss.getHitPoints() <= 0) {       //boss is dead
                finalBoss.setHitPoints(0);      //set boss' hitPoints to 0 if went negative
                displayBossStats();     //display boss stats

                //end game text
                gameplayText.appendText("""     
                        \nYour final blow to the beast knocks it back into the wall.
                        It crashes through the brick and the room is illuminated by sunlight!
                        You climb over the monster to your freedom! Congratulations on escaping the Bad RPG Dungeon!""");
                userCharacter.setVisible(false);        //remove character image from screen (escaped dungeon)
                enemy.setFill(new ImagePattern(bossDead));      //display defeated boss
                checkHighScore = true;      //user won game, compare gold total against high score list
                hideAllButtons();        //disable all buttons
            }
        } else {
            gameplayText.appendText("\nYour attack missed!");        //user attack misses
            bossAttack();       //boss attacks back
        }
    }
    public void bossAttack() {
        if (rollTwentySidedDie() >= newCharacter.getDexterity()){       //boss rolls 20 sided die to beat character's dex
            newCharacter.setHitPoints(newCharacter.getHitPoints()-(finalBoss.getStrength()/3));     //deal boss' Strength/3 damage to character
            gameplayText.appendText("\nThe enormous monster successfully attacked back, dealing "+ finalBoss.getStrength()/3 +" damage!");  //update gamePlay text
            checkGameOver();        //check if attack killed player
            displayPlayerStats();   //update display of player stats
        }
        else{
            gameplayText.appendText("\nYou blocked the enormous monster's attack!");     //boss attack miss
            //userCharacter.setFill(new ImagePattern(guardRight));
        }
    }
    public void monsterFight() {
        //display correct attack image after guard image displayed
        if (attackSide.equals("left")){
            userCharacter.setFill(new ImagePattern(attackLeft));
        }
        else if (attackSide.equals("right")){
            userCharacter.setFill(new ImagePattern(attackRight));
        }

        if (rollTwentySidedDie() >= listOfRooms.get(roomNumber).newNPC.getDexterity()) {    //roll 20 sided die to beat NPC's dex
            listOfRooms.get(roomNumber).newNPC.setHitPoints(listOfRooms.get(roomNumber).newNPC.getHitPoints() - (newCharacter.getStrength() / 3));  //deals Strength/3 damage to NPC
            gameplayText.appendText("\nSuccessful attack, you dealt " + newCharacter.getStrength() / 3 + " damage!");       //update gamePlay text

            displayNPCStats();  //update NPC stats

            if (listOfRooms.get(roomNumber).newNPC.getHitPoints() > 0) {    //attack did not kill NPC
                monsterAttack();        //NPC attacks back
            } else if (listOfRooms.get(roomNumber).newNPC.getHitPoints() <= 0) {    //attack did kill monster
                listOfRooms.get(roomNumber).newNPC.setHitPoints(0);     //set NPC hitPoints to 0 in case went negative
                gameplayText.appendText("\nYou defeated the monster!");     //update gamePlay text

                if (roomNumber != 12) {        //do not collect gold from sleep attack NPC
                    enemy.setFill(new ImagePattern(gold));
                    gameplayText.appendText("\nThis room is now empty except for " + listOfRooms.get(roomNumber).getGold() + " gold, " +
                            "you add it to your collection!");        //update gamePlay text

                    enemy.setFill(new ImagePattern(gold));
                    newCharacter.setGold(listOfRooms.get(roomNumber).getGold());    //update character gold total

                    displayPlayerStats();   //update player stats
                    closeDoor(roomNumber);  //remove and hide door used for collision detection, display gray "closed door" instead
                }
                else {
                    roomNumber = saveRoomNum;   //return to normal room number if "extra room" was indexed for sleep NPC
                    enemy.setVisible(false);    //hide NPC after sleep attack
                }


                userCharacter.setFill(new ImagePattern(walkSouth1));
                enableMoveButtons();        //allow character to move again after fighting
                npcStats.setText("");       //clear NPC stats
            }
        } else {
            gameplayText.appendText("\nAttack missed!");        //user attack misses
            monsterAttack();        //NPC attacks
        }
    }
    public void monsterAttack() {
        if (rollTwentySidedDie() >= newCharacter.getDexterity()){       //NPC rolls 20 sided die to beat character's dex
                        newCharacter.setHitPoints(newCharacter.getHitPoints()-(listOfRooms.get(roomNumber).newNPC.getStrength()/3));    //attack deals NPC Strength/3 damage to character
            gameplayText.appendText("\nThe monster successfully attacked back, dealing "+ listOfRooms.get(roomNumber).newNPC.getStrength()/3 +" damage!");      //update gamePlay text

            checkGameOver();        //check if attack killed character
            displayPlayerStats();   //update player stats
        }
        else{
            gameplayText.appendText("\nYou blocked the monster's attack!");      //monster attack missed
            //display left/right guarding image
            if(guardSide.equals("left")) {
                userCharacter.setFill(new ImagePattern(guardLeft));
                attackSide="left";
            }
            else if(guardSide.equals("right")){
                userCharacter.setFill(new ImagePattern(guardRight));
                attackSide="right";
            }
        }
    }

    public void closeDoor(int roomNumber){
        doors[roomNumber].setVisible(false);        //hide door
        doors[roomNumber].setLayoutX(0);            //move to location user cannot interact with
        doors[roomNumber].setLayoutY(0);
        checkBossDoor();        //check if all 12 rooms have been cleared
    }
    public void checkBossDoor(){
        for(int i = 0; i<12; i++){      //cycle through doors
            if(!doors[i].isVisible()){  //check if door is not visible (cleared)
                roomsCleared++;     //add to cleared rooms total
            }
        }

        if(roomsCleared==12){       //if all 12 rooms have been cleared
            treasureChest.setLayoutX(182);  //move treasure chest to middle of dungeon map
            treasureChest.setLayoutY(105);
            treasureChest.setFill(new ImagePattern(chest)); //set treasure chest image
            treasureChest.setVisible(true);     //make treasure chest visible
            treasureChest.setOpacity(0);
            gameplayText.appendText("\nYou have successfully cleared all the rooms, " +
                    "a treasure chest appears in the middle of the dungeon!");      //update gamePlay text

            //chest "appears"
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(3), treasureChest);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        }
        else{roomsCleared=0;}   //reset cleared room count for next loop check
    }
    public void openTreasureChest(){
        treasureChest.setFill(new ImagePattern(chestOpen)); //change treasure chest image to opened version
        doorKey.setFill(new ImagePattern(key)); //set key image
        doorKey.setVisible(true);       //display key image
        hasKey = true;      //key is "added" to inventory for boss door
        newCharacter.setHitPoints(50);      //boost character's hitPoints
        newCharacter.setStrength(newCharacter.getStrength()*2);     //double character's Strength
        newCharacter.setIntelligence(newCharacter.getIntelligence()*2);     //double character's Intelligence
        newCharacter.setDexterity(newCharacter.getDexterity()*2);       //double character's Dexterity

    }
    public void checkSleep(){
        if (newCharacter.getHitPoints()>=20){       //only enable sleep button if character has full health (or boosted from treasure chest)
            sleepButton.setDisable(true);
        }
    }
    public void checkGameOver() {
        if (newCharacter.getHitPoints()<=0){    //check if character has died
            newCharacter.setHitPoints(0);       //set hitPoints to zero if negative
            gameplayText.appendText("\nYou died! Game over...");  //update gamePlay text
            hideAllButtons();        //disable all buttons upon death
            userCharacter.setFill(new ImagePattern(dead));        //display dead character image
            playAgain.setVisible(true);     //display button to start game over
        }
    }

    public void enableMoveButtons(){
        //allow movement
        northButton.setDisable(false);
        eastButton.setDisable(false);
        southButton.setDisable(false);
        westButton.setDisable(false);
        searchButton.setDisable(true);
        sleepButton.setDisable(false);

        //disable buttons used during attacks
        attackButton.setDisable(true);
        fleeButton.setDisable(true);
    }
    public void disableMoveButtons(){
        //don't allow movement during attack
        northButton.setDisable(true);
        eastButton.setDisable(true);
        southButton.setDisable(true);
        westButton.setDisable(true);
        searchButton.setDisable(true);
        sleepButton.setDisable(true);

        //only allow attack or flee
        attackButton.setDisable(false);
        fleeButton.setDisable(false);
    }
    public void disableAllButtons(){
        //disable all game play buttons
        northButton.setDisable(true);
        eastButton.setDisable(true);
        southButton.setDisable(true);
        westButton.setDisable(true);
        searchButton.setDisable(true);
        sleepButton.setDisable(true);
        attackButton.setDisable(true);
        fleeButton.setDisable(true);
    }
    public void hideAllButtons(){
        //game over hide game play buttons
        northButton.setVisible(false);
        eastButton.setVisible(false);
        southButton.setVisible(false);
        westButton.setVisible(false);
        searchButton.setVisible(false);
        sleepButton.setVisible(false);
        attackButton.setVisible(false);
        fleeButton.setVisible(false);

        //show save score button if boss was defeated to compare score to previous high scores
        if(checkHighScore) {
            userInitials.setVisible(true);
            userInitials.setDisable(false);
            saveScore.setVisible(true);
            playAgain.setVisible(true);
        }
    }

    public int rollTwentySidedDie(){
        return (int)(Math.random()*20+1);       //roll 20 sided die for attacks
    }
    public void initializeScreen() throws IOException {
        //initial screen button layout

        //movement buttons
        northButton.setDisable(false);
        northButton.setVisible(true);
        eastButton.setDisable(false);
        eastButton.setVisible(true);
        southButton.setDisable(false);
        southButton.setVisible(true);
        westButton.setDisable(false);
        westButton.setVisible(true);

        //other game play buttons
        attackButton.setDisable(true);
        attackButton.setVisible(true);
        fleeButton.setDisable(true);
        fleeButton.setVisible(true);
        searchButton.setDisable(true);
        searchButton.setVisible(true);
        sleepButton.setDisable(true);
        sleepButton.setVisible(true);

        //misc images
        enemy.setVisible(false);
        treasureChest.setVisible(false);
        doorKey.setVisible(false);
        treasureChest.setLayoutX(8);    //reset treasure chest location if shown in previous game
        treasureChest.setLayoutY(10);

        //post game buttons
        playAgain.setVisible(false);
        saveScore.setVisible(false);
        saveScore.setDisable(true);
        userInitials.setVisible(false);

        //set text areas
        gameplayText.setPromptText("Welcome to the game!");
        gameplayText.setEditable(false);
        userInitials.clear();   //clear user intials from prior saved game
        userInitials.setPromptText("Enter initials");

        //refresh high scores to include new high scores
        highScoresDisplay.setVisible(true);
        highScoresDisplay.setEditable(false);
        readHighScores();

        //set character to first position
        userCharacter.setFill(new ImagePattern(walkSouth1));
        userCharacter.setLayoutX(187);
        userCharacter.setLayoutY(211);
        userCharacter.setVisible(true);
        displayPlayerStats();       //display character's stats
    }
    public void setDoorArray(){
        //add FXML doors to Array of rectangles 'doors'
        doors[0]=doorOne;
        doors[1]=doorTwo;
        doors[2]=doorThree;
        doors[3]=doorFour;
        doors[4]=doorFive;
        doors[5]=doorSix;
        doors[6]=doorSeven;
        doors[7]=doorEight;
        doors[8]=doorNine;
        doors[9]=doorTen;
        doors[10]=doorEleven;
        doors[11]=doorTwelve;
    }
    public void resetDoorFXMLs(){
        //returns FXML door rectangles to correct game play location
        doorOne.setLayoutX(25);
        doorOne.setLayoutY(85);
        doorOne.setVisible(true);
        doorTwo.setLayoutX(95);
        doorTwo.setLayoutY(85);
        doorTwo.setVisible(true);
        doorThree.setLayoutX(136);
        doorThree.setLayoutY(40);
        doorThree.setVisible(true);
        doorFour.setLayoutX(229);
        doorFour.setLayoutY(40);
        doorFour.setVisible(true);
        doorFive.setLayoutX(275);
        doorFive.setLayoutY(85);
        doorFive.setVisible(true);
        doorSix.setLayoutX(340);
        doorSix.setLayoutY(85);
        doorSix.setVisible(true);
        doorSeven.setLayoutX(340);
        doorSeven.setLayoutY(139);
        doorSeven.setVisible(true);
        doorEight.setLayoutX(275);
        doorEight.setLayoutY(139);
        doorEight.setVisible(true);
        doorNine.setLayoutX(229);
        doorNine.setLayoutY(185);
        doorNine.setVisible(true);
        doorTen.setLayoutX(136);
        doorTen.setLayoutY(185);
        doorTen.setVisible(true);
        doorEleven.setLayoutX(95);
        doorEleven.setLayoutY(139);
        doorEleven.setVisible(true);
        doorTwelve.setLayoutX(25);
        doorTwelve.setLayoutY(139);
        doorTwelve.setVisible(true);

        //"lock" boss door
        finalDoor.setFill(Color.RED);
    }
    public void populateRooms(){
        listOfRooms.clear();        //clear old room array in case playing again
        for (int i = 0; i < 13; i++) {    //13th room for sleep attack NPC
            Rooms newRoom = new Rooms(difficultyLevel);    //create new instance of Rooms class
            newRoom.newNPC.setDifficulty(difficultyLevel);
            listOfRooms.add(newRoom);   //add to listOfRooms array
        }
        setDoorArray();     //add FXML door rectangles to array for collision detection
    }
    public void displayEnemy(){
        //moves enemy image to room location user is currently searching
        switch(roomNumber+1){
            case 1 -> {     //display NPC if room 1 guarded
                enemy.setLayoutX(23);
                enemy.setLayoutY(59);
                enemy.setFill(new ImagePattern(npcAttackRight));
                userCharacter.setFill(new ImagePattern(attackLeft));
                guardSide="left";
            }
            case 2 -> {     //display NPC if room 2 guarded
                enemy.setLayoutX(91);
                enemy.setLayoutY(59);
                enemy.setFill(new ImagePattern(npcAttackRight));
                userCharacter.setFill(new ImagePattern(attackLeft));
                guardSide="left";
            }
            case 3 -> {     //display NPC if room 3 guarded
                enemy.setLayoutX(109);
                enemy.setLayoutY(33);
                enemy.setFill(new ImagePattern(npcAttackRight));
                userCharacter.setFill(new ImagePattern(attackLeft));
                guardSide="left";
            }
            case 4 -> {     //display NPC if room 4 guarded
                enemy.setLayoutX(248);
                enemy.setLayoutY(33);
                enemy.setFill(new ImagePattern(npcAttackLeft));
                userCharacter.setFill(new ImagePattern(attackRight));
                guardSide="right";
            }
            case 5 -> {     //display NPC if room 5 guarded
                enemy.setLayoutX(271);
                enemy.setLayoutY(59);
                enemy.setFill(new ImagePattern(npcAttackLeft));
                userCharacter.setFill(new ImagePattern(attackRight));
                guardSide="right";
            }
            case 6 -> {     //display NPC if room 6 guarded
                enemy.setLayoutX(336);
                enemy.setLayoutY(59);
                enemy.setFill(new ImagePattern(npcAttackLeft));
                userCharacter.setFill(new ImagePattern(attackRight));
                guardSide="right";
            }
            case 7 -> {     //display NPC if room 7 guarded
                enemy.setLayoutX(336);
                enemy.setLayoutY(150);
                enemy.setFill(new ImagePattern(npcAttackLeft));
                userCharacter.setFill(new ImagePattern(attackRight));
                guardSide="right";
            }
            case 8 -> {     //display NPC if room 8 guarded
                enemy.setLayoutX(271);
                enemy.setLayoutY(150);
                enemy.setFill(new ImagePattern(npcAttackLeft));
                userCharacter.setFill(new ImagePattern(attackRight));
                guardSide="right";
            }
            case 9 -> {     //display NPC if room 9 guarded
                enemy.setLayoutX(249);
                enemy.setLayoutY(178);
                enemy.setFill(new ImagePattern(npcAttackLeft));
                userCharacter.setFill(new ImagePattern(attackRight));
                guardSide="right";
            }
            case 10 -> {        //display NPC if room 10 guarded
                enemy.setLayoutX(109);
                enemy.setLayoutY(178);
                enemy.setFill(new ImagePattern(npcAttackRight));
                userCharacter.setFill(new ImagePattern(attackLeft));
                guardSide="left";
            }
            case 11 -> {        //display NPC if room 11 guarded
                enemy.setLayoutX(91);
                enemy.setLayoutY(151);
                enemy.setFill(new ImagePattern(npcAttackRight));
                userCharacter.setFill(new ImagePattern(attackLeft));
                guardSide="left";
            }
            case 12 -> {        //display NPC if room 12 guarded
                enemy.setLayoutX(21);
                enemy.setLayoutY(151);
                enemy.setFill(new ImagePattern(npcAttackRight));
                userCharacter.setFill(new ImagePattern(attackLeft));
                guardSide="left";
            }
            case 13 ->{     //display NPC if sleep attack
                //prevents images from intersecting walls
                if (userCharacter.getLayoutX()<32 || (userCharacter.getLayoutX()>138&&userCharacter.getLayoutX()<185)){
                    enemy.setLayoutX(userCharacter.getLayoutX()+30);
                    enemy.setLayoutY(userCharacter.getLayoutY());
                    enemy.setFill(new ImagePattern(npcAttackLeft));
                    userCharacter.setFill(new ImagePattern(attackRight));
                    guardSide="right";
                }else {
                    enemy.setLayoutX(userCharacter.getLayoutX() - 40);
                    enemy.setLayoutY(userCharacter.getLayoutY());
                    enemy.setFill(new ImagePattern(npcAttackRight));
                    userCharacter.setFill(new ImagePattern(attackLeft));
                    guardSide="left";
                }
            }
        }
        enemy.setVisible(true);
    }

    @FXML
    public void onEasyButtonPress(ActionEvent actionEvent) {
        //enemy HP will be random 2-6 with other stats double HP value
        difficultyLevel = 1;
        difficultyMenu.setVisible(false);

        //populate rooms with NPCs and gold based on difficulty level
        populateRooms();
        enableMoveButtons();
        finalBoss.setDifficulty(difficultyLevel);
        checkSleep();
    }
    @FXML
    public void onMediumButtonPress(ActionEvent actionEvent) {
        difficultyLevel = 2;
        difficultyMenu.setVisible(false);
        populateRooms();
        enableMoveButtons();
        checkSleep();
        finalBoss.setDifficulty(difficultyLevel);
    }
    @FXML
    public void onHardButtonPress(ActionEvent actionEvent) {
        difficultyLevel = 3;
        difficultyMenu.setVisible(false);
        populateRooms();
        enableMoveButtons();
        checkSleep();
        finalBoss.setDifficulty(difficultyLevel);
    }
}
