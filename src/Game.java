// name surname: Mustafa Sahin
// student ID: 2023400162

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Game {

//----------------------------------------------------------------------------------------------------
//DATA FIELDS

    private int stageIndex;
    private final ArrayList<Stage> stages;
    private int deathNumber = 0;
    private boolean resetGame = false;

    //the time variables below are all in milliseconds
    private long startTime; //the real time when the game starts
    private final int frameDuration = 18; //the duration for each frame to last
    private int gameTime = 0; //calculated as frameEnd-startTime

    private long resetTime = 0; //the last time reset button was pressed
    public final static double TIME_STEP = 1; //implemented time step between each update to screen
    //the physical calculations use this variable, it is in seconds

    //the coordinates of the buttons:
    private final int[] restartButton = new int[]{510, 70, 590, 100};
    private final int[] resetButton = new int[]{320, 5, 480, 35};
    private final int[] helpButton = new int[]{210, 70, 290, 100};

    //the boolean values indicating the buttons' statuses
    private boolean isRestartPressed = false;
    private boolean isResetPressed = false;
    private boolean isHelpPressed = false;

    //the clue message displayed on bottom screen
    String clueMessage;

//----------------------------------------------------------------------------------------------------
//CONSTRUCTOR

    public Game(ArrayList<Stage> stages) {
        this.stages = stages;
    }

//----------------------------------------------------------------------------------------------------
//GETTERS AND SETTERS

    public int getStageIndex() {
        return stageIndex;
    }

    public void setStageIndex(int stageIndex) {
        this.stageIndex = stageIndex;
    }

    public Stage getCurrentStage() {
        return stages.get(stageIndex-1);
    }

    private void setResetTime() {
        resetTime = System.currentTimeMillis();
    }

    private void resetDeathNumber() {
        deathNumber = 0;
    }

    private void increaseDeathNumber() {
        deathNumber++;
    }

    //----------------------------------------------------------------------------------------------------
//OUTPUT(PLAY FUNCTION)

    //in the play() function, the main while loop runs. While the loop runs,
    //input is taken and output(game) is drawn.

    //in the loop the following is done in order:
    //checking if stage is passed
    //checking if reset button is pressed
    //checking if button is pressed
    //taking input(setting variables accordingly)
    //checking if player hit a spike
    //movements
    //drawin
    //time updates

    public void play() {

        //the starting time which is used to calculate gameTime
        startTime = System.currentTimeMillis();

        for (Stage currentStage : stages) {

            setStageIndex(currentStage.getStageNumber());
            //create the player and the map
            Player player = new Player(getCurrentStage().getSpawnPoint()[0], getCurrentStage().getSpawnPoint()[1]);
            Map map = new Map(getCurrentStage(), player);
            //the clue message that will be displayed on bottom screen:
            clueMessage = getCurrentStage().getClue();

            // go to the next stage if map.changeStage()
            while (!map.changeStage()) {

                //reset button pressed:
                if (resetGame) {
                    resetGame = false;
                    resetDeathNumber(); //deathNumber = 0
                    setResetTime(); //set the last time the game is reset to now
                    drawResetScreen(); //draw reset screen for 2 seconds
                    play(); //start the game method from the beginning, start from stage 1
                }

                //CHECKING COLLISIONS

                //BUTTON COLLISIONS:
                boolean lastButtonStatus = map.isButtonPressed();
                //isButtonPressed is not updated yet, this is the previous status of button.
                map.checkButtonPressed();
                //now isButtonPressed is updated, and it is the current status of button.

                //if isButtonPressed turns true from false, means the button is pressed.
                if (!lastButtonStatus && map.isButtonPressed()) {
                    map.pressButton(); //update buttonPressNum
                }
                //setDoorStatus sets the door open in accordance with the current stage's rules
                map.setDoorStatus(getStageIndex()==4);

                //INPUT TAKING
                //sets movement and button variables depending on input
                handleInput(map, currentStage, player);

                //SPIKE COLLISIONS
                double nextX = player.getX() + player.getXMoveDirection()[0]*currentStage.getVelocityX()*TIME_STEP;
                double nextY = player.getY() + player.getVelocityY()*TIME_STEP;
                //check if the next position will intersect with a spike
                if (map.checkSpikeCollision(nextX, nextY)) {
                    increaseDeathNumber();
                    map.restartStage();
                }

                //MOVING THE PLAYER
                //movePlayer moves the player if its path is not blocked
                map.movePlayer();

                //DRAWING EVERYTHING
                StdDraw.clear();
                map.draw(); //draws the environment and player
                drawBottomScreen(clueMessage); //draw the bottom screen
                StdDraw.show();

                //TIME UPDATE AND PAUSE
                gameTime = (int) (System.currentTimeMillis() - startTime); //time passed overall
                StdDraw.pause(frameDuration); //pause for the intended frame duration

                //this frame is completed, get to next frame

            }

            //this stage is passed, draw stage passed animation if this was not the last stage
            if (getStageIndex() != 5) {
                drawStagePassed();
            }

        }

        //all stages passed, the for loop ends, now the winning screen:
        while (true) {
            drawWinScreen();
            //quit if Q is pressed
            if (StdDraw.isKeyPressed(KeyEvent.VK_Q)) {
                System.exit(0);
            }
            //play again if A is pressed
            if (StdDraw.isKeyPressed(KeyEvent.VK_A)) {
                setResetTime();
                resetDeathNumber();
                play();
            }
        }

    }

//----------------------------------------------------------------------------------------------------
//INPUT

    //depending on input, changes movement or button variables
    private void handleInput(Map map, Stage currentStage, Player player) {
        int[] keyCodes = currentStage.getKeyCodes();
        int[][] directions = map.getDirections();

        //right arrow key is pressed:
        if (StdDraw.isKeyPressed(keyCodes[0])) {
            player.setLookDirection(1);
            player.setXMoveDirection(directions[0]);
        }
        //left arrow key is pressed:
        if (StdDraw.isKeyPressed(keyCodes[1])) {
            player.setLookDirection(-1);
            player.setXMoveDirection(directions[1]);
        }
        //up arrow key is pressed or stage requires constant jumping:
        if (keyCodes[2] == -1 || StdDraw.isKeyPressed(keyCodes[2])) {
            player.setYMoveDirection(directions[2]);
        }

        //mouse actions here
        double mouseX = StdDraw.mouseX();
        double mouseY = StdDraw.mouseY();

        //the input taking from mouse checks for clicking,
        // meaning the mouse should be pressed on the button and then be released
        //if the button is pressed on top of button area, the isPressed value is set true
        //when the button is released and isPressed is true, isPressed is set false and necessary action is taken
        if (StdDraw.isMousePressed()) {
            if (map.isIn(mouseX, mouseY, helpButton)) {
                isHelpPressed = true;
            }
            if (map.isIn(mouseX, mouseY, restartButton)) {
                isRestartPressed = true;
            }
            if (map.isIn(mouseX, mouseY, resetButton)) {
                isResetPressed = true;
            }
        } else { //mouse is released, if any button were pressed, act on input
            if (isRestartPressed) {
                isRestartPressed = false;
                deathNumber++;
                map.restartStage();
            }
            if (isResetPressed) {
                isResetPressed = false;
                resetTime = gameTime;
                resetGame = true;
            }
            if (isHelpPressed) {
                isHelpPressed = false;
                clueMessage = currentStage.getHelp();
            }

        }
    }

//----------------------------------------------------------------------------------------------------
//DRAW FUNCTIONS

    private void drawBottomScreen(String clue) {
        StdDraw.setFont();
        StdDraw.setPenColor(new Color(56, 93, 172)); // Color of the area
        StdDraw.filledRectangle(400, 60, 400, 60); // Drawing bottom part
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(250,85,"Help");
        StdDraw.rectangle(250,85,40,15); // Help button
        StdDraw.text(550,85,"Restart");
        StdDraw.rectangle(550,85,40,15); // Restart button
        StdDraw.text(400,20,"RESET THE GAME");
        StdDraw.rectangle(400,20,80,15); // Reset button
        StdDraw.text(700, 75, "Deaths: " + deathNumber); //death counter
        StdDraw.text(700, 50, "Stage: " + stageIndex); //stage index
        StdDraw.text(100, 50, convertTimeToString(gameTime)); //draw the time passed after converting it
        StdDraw.text(100,75, "Level: 1");
        StdDraw.text(400, 85, "Clue:");
        StdDraw.text(400, 55, clue); //the clue message, print the help or clue string whether help button was pressed or not
    }

    //draws the resetting... banner for 2 seconds when reset button is pressed
    private void drawResetScreen() {
        while ((System.currentTimeMillis() - startTime) - gameTime < 2000) { //make sure time passed is below 2000ms
            StdDraw.setPenColor(new Color(0, 113, 0));
            StdDraw.filledRectangle(400, 275, 400, 75); //draw the banner rectangle
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.setFont(new Font(StdDraw.getFont().getFontName(), StdDraw.getFont().getStyle(), 50));
            StdDraw.text(400, 275, "RESETTING THE GAME..."); //print the reset message
            StdDraw.show();
            StdDraw.pause(frameDuration);
        }
    }

    //draws the screen that will pop when a stage is passed(except if last stage is passed)
    private void drawStagePassed() {
        while (true) {
            //we do not use clear() because we want the previous stage be shown
            //animation stays for 2 seconds:
            if ((System.currentTimeMillis() - startTime) - gameTime >= 2000) {
                startTime += 2000;
                //to not alter gameTime, add 2000 milliseconds to startTime
                //so currentTime - startTime stays as it was before this animation
                break; //get to next stage
            }
            StdDraw.setPenColor(new Color(0, 113, 0));
            StdDraw.filledRectangle(400, 275, 400, 75); //draw the banner rectangle
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.setFont(new Font(StdDraw.getFont().getFontName(), StdDraw.getFont().getStyle(), 30));
            StdDraw.text(400, 300, "You Passed The Stage");
            StdDraw.text(400, 250, "But is the level over?!");

            StdDraw.show();
            StdDraw.pause(frameDuration);
        }
    }

    //draws the screen that will pop when all stages are done
    private void drawWinScreen() {
        StdDraw.clear();
        StdDraw.setPenColor(new Color(0, 113, 0));
        StdDraw.filledRectangle(400, 275, 400, 75); //draw the banner rectangle
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new Font(StdDraw.getFont().getFontName(), StdDraw.getFont().getStyle(), 30));
        StdDraw.text(400, 300, "CONGRATULATIONS YOU FINISHED THE LEVEL");
        StdDraw.text(400, 255, "PRESS A TO PLAY AGAIN");
        StdDraw.setFont();
        StdDraw.text(400, 225, "You finished with " + deathNumber + " deaths in " + convertTimeToString(gameTime));
        StdDraw.show();
        StdDraw.pause(frameDuration);
    }

//----------------------------------------------------------------------------------------------------
//TIME

    //converts given int in milliseconds into a string in the format "minutes : seconds : milliseconds"
    private String convertTimeToString(int timeElapsed) {
        int minutes = timeElapsed / 60000;
        int seconds = (timeElapsed % 60000) / 1000;
        int milliseconds = (timeElapsed % 1000) / 10;

        return String.format("%02d : %02d : %02d", minutes, seconds, milliseconds);
    }

}

