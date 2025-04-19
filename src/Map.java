// name surname: Mustafa Sahin
// student ID: 2023400162

import java.awt.*;

public class Map {

//----------------------------------------------------------------------------------------------------
//DATA FIELDS

    //coordinate arrays

    // keyCodes are KeyEvent.VK_RIGHT, KeyEvent.VK_A etc.
    // Obstacles List (formant is int[] = {xLeftDown , yLeftDown, xRightUp, yRightUp}
    private int[][] obstacles = {
            new int[]{0, 120, 120, 270}, new int[]{0, 270, 168, 330},
            new int[]{0, 330, 30, 480}, new int[]{0, 480, 180, 600},
            new int[]{180, 570, 680, 600}, new int[]{270, 540, 300, 570},
            new int[]{590, 540, 620, 570}, new int[]{680, 510, 800, 600},
            new int[]{710, 450, 800, 510}, new int[]{740, 420, 800, 450},
            new int[]{770, 300, 800, 420}, new int[]{680, 240, 800, 300},
            new int[]{680, 300, 710, 330}, new int[]{770, 180, 800, 240},
            new int[]{0, 0, 800, 150}, new int[]{560, 150, 800, 180},
            //    ^ this array is changed, its y0 value is 0 instead of 120
            //    this is for the collision functions to work properly.
            //    the player falls below the stage because the floor obstacle is too thin
            //    now it is thick enough so the player does not fall
            //    the bottom screen is drawn over this obstacle, so no unintended objects
            new int[]{530, 180, 590, 210}, new int[]{530, 210, 560, 240},
            new int[]{320, 150, 440, 210}, new int[]{350, 210, 440, 270},
            new int[]{220, 270, 310, 300}, new int[]{360, 360, 480, 390},
            new int[]{530, 310, 590, 340}, new int[]{560, 400, 620, 430},
            //    the ghost obstacles that is on top of exitPipe, so the player cannot reach there
            new int[]{720, 215, 740, 240}, new int[]{740, 210, 770, 240} //not drawn
    };
    // Button Coordinates
    private int[] button = new int[]{400, 390, 470, 410};
    // Button Floor Coordinates
    private int[] buttonFloor = new int[]{400, 390, 470, 400};
    // Start Pipe Coordinates for Drawing
    private int[][] startPipe = {new int[]{115, 450, 145, 480},
            new int[]{110, 430, 150, 450}};
    // Exit Pipe Coordinates for Drawing
    private int[][] exitPipe = {new int[]{720, 175, 740, 215},
            new int[]{740, 180, 770, 210}};
    // Coordinates of spike areas
    private int[][] spikes = {
            new int[]{30, 333, 50, 423}, new int[]{121, 150, 207, 170},
            new int[]{441, 150, 557, 170}, new int[]{591, 180, 621, 200},
            new int[]{750, 301, 769, 419}, new int[]{680, 490, 710, 510},
            new int[]{401, 550, 521, 570},
            new int[]{121, 0, 207, 150}, new int[]{441, 0, 557, 150}}; //added spikes(below the current spikes)
    //these two spike areas are below the lowest two spikes, they are not drawn
    //these are here because the player do not intersect with the small spike area when it has a high speed
    //now the spike area is thick enough for the player not skip its area when it moves

    // How much spike image should be rotated for each spike, in angles
    private int[] spikeAngles = new int[]{90, 180, 180, 180, 270, 0, 0};
    // Door Coordinates
    private double[] door = new double[]{685, 180, 700, 240};
    private final double doorFloor = door[1];
    private final static double DOOR_SPEED = 1.0;

    // stage and player that define a map
    private Stage stage;
    private Player player;
    // the time step that is used in movements
    private final static double TIME_STEP = Game.TIME_STEP;

    //x and y boundaries of environment
    private final static int[] X_SCALE = new int[]{0, 800};
    private final static int[] Y_SCALE = new int[]{150, 600};

    //button-door interaction variables
    private int buttonPressNum = 0;
    private boolean isButtonPressed = false;
    private boolean isDoorOpen = false;

    //color variables
    private final Color doorColor = new Color(2, 113, 4);
    private final Color pipeColor = new Color(221, 174, 7);
    private final Color buttonFloorColor = new Color(61, 61, 60);
    private final Color buttonColor = new Color(237, 1, 2);

    //player's half lengths
    private final static double HALFWIDTH = Player.WIDTH/2;
    private final static double HALFHEIGHT = Player.HEIGHT/2;

    //the direction arrays that will be used in movements
    public final static int[][] DIRECTIONS = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {0, 0}};
    // {xDirection, yDirection} : can represent right, left, up, down, stationary(no direction)

    //----------------------------------------------------------------------------------------------------
//CONSTRUCTOR
    public Map(Stage stage, Player player) {
        this.stage = stage;
        this.player = player;
    }

//----------------------------------------------------------------------------------------------------
//GETTERS AND SETTERS

    public Stage getStage() {
        return stage;
    }

    public Player getPlayer() {
        return player;
    }

    public int[][] getDirections() {
        return DIRECTIONS;
    }

    public boolean isButtonPressed() {
        return isButtonPressed;
    }

    public int getButtonPressNum() {
        return buttonPressNum;
    }
    //increases buttonPressNum
    public void pressButton() {
        buttonPressNum++;
    }

    //sets the door open depending on current stage's requirements
    public void setDoorStatus(boolean isStageFour) {
        if (isStageFour) { //door opens with the fifth press in stage four
            if (getButtonPressNum()>=5) {isDoorOpen=true;}
        } else { //door opens if the button got pressed in other stages
            if (isButtonPressed) {isDoorOpen = true;}
        }
    }

    //----------------------------------------------------------------------------------------------------
//RESTART
    //reset all the variables to their starting values
    public void restartStage() {
        isDoorOpen = false;
        buttonPressNum = 0;
        door = new double[]{685, 180, 700, 240};
        getPlayer().respawn(stage.getSpawnPoint());
    }
//----------------------------------------------------------------------------------------------------
//MOVEMENT METHODS

    //updates player's location variables x and y by velocity*timeStep depending player's moveDirections
    public void movePlayer() {
        //player's movement values

        int[] xMoveDirection = player.getXMoveDirection();
        int[] yMoveDirection = player.getYMoveDirection();
        double x = player.getX();
        double y = player.getY();
        double velocityX = getStage().getVelocityX();
        double nextX = x + xMoveDirection[0]*velocityX* TIME_STEP;
        double velocityY = player.getVelocityY();
        double nextY = y + velocityY * TIME_STEP;
        double gravity = stage.getGravity();

        //if player will not crash into walls or the door, set its nextX as its x
        if (!checkDirectionCollision(nextX, y, xMoveDirection) && !checkDoorCollision(nextX, y)) {
            player.setX(nextX);
        }
        //jump input or stage 3:
        if (yMoveDirection == DIRECTIONS[2]) {
            //for the player to be able to jump, it must be in touch with and object and its velocityY must be 0
            //for smoother animation, we check if velocityY<0.00001,
            //and also because the velocityY while standing on an object
            //is equal to gravity*timeStep(explained later), we check for velocityY-gravity*timeStep<0.00001
            //also we check standing on an object requirement with bottom direction collision function
            if ((velocityY - gravity * TIME_STEP <= 0.00001) && (checkDirectionCollision(x, nextY, DIRECTIONS[3]))) {
                jump();
            }
        }
        player.setXMoveDirection(DIRECTIONS[4]); //reset the xMoveDirection for next input
        player.setYMoveDirection(DIRECTIONS[4]); //reset the yMoveDirection for next input

        //update the y variables after jump
        velocityY = player.getVelocityY();
        nextY = y + velocityY * TIME_STEP;

        //if player is falling:
        if (velocityY<0) {
            //if player will crash to the ground, set velocityY = 0
            if (checkDirectionCollision(x, nextY, DIRECTIONS[3])) {
                player.setVelocityY(0);
            } else { //if player will not crash to ground, player will move
                player.setY(nextY);
            }
        } else { // velocityY >= 0
            //if player will crash to the ceiling, set velocityY = 0
            if (checkDirectionCollision(x, nextY, DIRECTIONS[2])) {
                player.setVelocityY(0);
            } else { //if player will not crash to the ceiling, player will move
                player.setY(nextY);
            }
        }
        fall(); //gravity's effect
        //the velocityY while the player is standing on top of an object will be set 0,
        //then fall() sets it to gravity*timeStep
        //this is useful as it keeps velocityY negative, so its nextY is not where it stands but a little below
        //this nextY will intersect with obstacles,
        //so the collision functions will detect before player collides the object
    }

    //lower velocityY by gravity*timeStep
    public void fall() {
        player.setVelocityY(player.getVelocityY()+stage.getGravity()* TIME_STEP);
    }

    //set velocityY to its max value
    public void jump() {
        player.setVelocityY(stage.getVelocityY());
    }

    //decreases the door's y coordinates by doorSpeed*timeStep if door is set to be open and is not fully opened
    //the objects are drawn on top of the door, so door's descending part is not shown on screen
    public void slideDoor() {
        if (isDoorOpen && (door[3] > doorFloor)) {
            door[1]-= DOOR_SPEED * TIME_STEP;
            door[3]-= DOOR_SPEED * TIME_STEP;
        }
    }

//----------------------------------------------------------------------------------------------------
//COLLISION FUNCTIONS

    //checks if the player will collide to a spike
    public boolean checkSpikeCollision(double nextX, double nextY) {
        for (int[] spike : spikes) {
            if (checkCollision(nextX, nextY, spike)) {return true;}
        }
        return false;
    }

    //checks if player will collide to a door
    public boolean checkDoorCollision(double nextX, double nextY) {
        return checkCollision(nextX, nextY, door);
    }

    //checks if the player is in the domain of exitPipe[1]
    public boolean changeStage() {
        return checkCollision(player.getX(), player.getY(), exitPipe[1]);
    }
    //updates isButtonPressed if player collides with button
    public void checkButtonPressed() {
        isButtonPressed = checkCollision(player.getX(), player.getY(), button);
    }

    //the fundamental collision functions:

    //indicates if the next position intersects with a region
    public boolean checkCollision(double nextX, double nextY, int[] obstacle) {
        //if any corner gets into region, return true
        return (isIn(nextX+ HALFWIDTH, nextY + HALFHEIGHT, obstacle) ||
                isIn(nextX- HALFWIDTH, nextY + HALFHEIGHT, obstacle) ||
                isIn(nextX- HALFWIDTH, nextY+HALFHEIGHT, obstacle) ||
                isIn(nextX+ HALFWIDTH, nextY-HALFHEIGHT, obstacle)) ||
                nextX<X_SCALE[0] || nextX>X_SCALE[1] ||
                nextY<Y_SCALE[0] || nextY>Y_SCALE[1]; //check if player gets out of stage as well
    }
    //takes input array as double[]
    public boolean checkCollision(double nextX, double nextY, double[] obstacle) {
        return (isIn(nextX + HALFWIDTH, nextY + HALFHEIGHT, obstacle) ||
                isIn(nextX - HALFWIDTH, nextY + HALFHEIGHT, obstacle) ||
                isIn(nextX -HALFWIDTH, nextY + HALFHEIGHT, obstacle) ||
                isIn(nextX + HALFWIDTH, nextY - HALFHEIGHT, obstacle)) ||
                nextX<X_SCALE[0] || nextX>X_SCALE[1] ||
                nextY<Y_SCALE[0] || nextY>Y_SCALE[1];
    }

    /*the next collision function gives an idea to us which side of the player will collide into an obstacle

checks if the bottom, top, rightmost or leftmost points of player will collide into an obstacle.
checks the side of the player depending on the direction array
{0, 1} means top, {0, -1} means bottom, {1, 0} means rightmost and {-1, 0} means leftmost

      for example,
      checkDirectionCollision(nextX, nextY, DIRECTIONS[0]) checks right side collision.
      checks if isIn(nextX + HALFWIDTH, twoYValues, obstacle) for every obstacle in obstacles
      this checking is repeated for 2 twoYValues: nextY + HALFHEIGHT and, nextY - HALFHEIGHT
      so the function checks for the 2 corners of the side being evaluated.

    ____________  <------ (nextX + HALFWIDTH, nextY + HALFHEIGHT)
    |          |
    |          |
    |          |
    |__________|  <------ (nextX + HALFWIDTH, nextY - HALFWIDTH)

     */
    public boolean checkDirectionCollision(double nextX, double nextY, int[] direction) {
        for (int[] obstacle : obstacles) {
            if (direction[1] != 0) { //vertical check (top-bottom)
                if (
                        isIn(nextX - HALFWIDTH, nextY + direction[1]* HALFHEIGHT, obstacle) ||
                        isIn(nextX + HALFWIDTH, nextY + direction[1]* HALFHEIGHT, obstacle)
                ) {return true;}

                if (direction[1] == 1) {if (nextY>Y_SCALE[1]) {return true;}} //check y being out of bounds
                else {if (nextY<Y_SCALE[0]) {return true;}} //check y being out of bounds
            }
            if (direction[0] != 0){ //horizontal check (left-right)
                if (
                        isIn(nextX + direction[0]* HALFWIDTH, nextY+ HALFHEIGHT, obstacle) ||
                        isIn(nextX + direction[0]* HALFWIDTH, nextY- HALFHEIGHT, obstacle)
                ) {return true;}

                if (direction[0] == 1) {if (nextX>X_SCALE[1]) {return true;}} //check x being out of bounds
                else {if (nextX<X_SCALE[0]) {return true;}} //check x being out of bounds
            }
        }
        return false;
    }

    //checks if given (x, y) resides in the rectangle whose two corners are given
    public boolean isIn(double x, double y, int[] coordinates) {
        return x>coordinates[0] && x<coordinates[2] && y>coordinates[1] && y<coordinates[3];
    }

    public boolean isIn(double x, double y, double[] coordinates) {
        return x>coordinates[0] && x<coordinates[2] && y>coordinates[1] && y<coordinates[3];
    }

//---------------------------------------------------------------------------------------
//DRAW FUNCTIONS

    //the main draw function who draws all the map's components and the player
    public void draw() {

        //draw the door
        slideDoor(); //slide the door if it should be open and is not fully open
        StdDraw.setPenColor(doorColor);
        drawRectangle(door);

        //draw obstacles without the last two ghost obstacles on top of pipe:
        int[][] obstaclesDrawn = new int[obstacles.length-2][];
        System.arraycopy(obstacles, 0, obstaclesDrawn, 0, obstacles.length-2);
        //copied the obstacles without the last two elements into new obstaclesDrawn
        StdDraw.setPenColor(stage.getColor());
        drawArrayedRectangles(obstaclesDrawn);

        //draw button if it is not pressed
        if (!isButtonPressed) {
            StdDraw.setPenColor(buttonColor);
            drawRectangle(button);
        }

        //draw button floor
        StdDraw.setPenColor(buttonFloorColor);
        drawRectangle(buttonFloor);

        //draw the player
        player.draw(stage.getStageNumber());

        //draw the pipes
        StdDraw.setPenColor(pipeColor);
        drawArrayedRectangles(startPipe);
        drawArrayedRectangles(exitPipe);

        //draw the spikes
        drawSpikes();
    }

    //draws multiple rectangles whose coordinates are given in an array
    private static void drawArrayedRectangles(int[][] array) {
        for (int[] rectangle : array) {drawRectangle(rectangle);}
    }

    //draws a rectangle whose two corner coordinates are given in an array
    //in format {x0, y0, x1, y1}, the coordinates of bottom left and top right points
    private static void drawRectangle(int[] rectangle) {
        StdDraw.filledRectangle((rectangle[0]+rectangle[2])/2.0, (rectangle[1]+rectangle[3])/2.0,
                (rectangle[2]-rectangle[0])/2.0, (rectangle[3]-rectangle[1])/2.0);
    }

    //takes input rectangle array as a double array
    private static void drawRectangle(double[] rectangle) {
        StdDraw.filledRectangle((rectangle[0]+rectangle[2])/2.0, (rectangle[1]+rectangle[3])/2.0,
                (rectangle[2]-rectangle[0])/2.0, (rectangle[3]-rectangle[1])/2.0);
    }

    //draws spike images to given coordinates
    //the spike image is cropped. and it is scaled and rotated depending on the spike's position on map
    //we use spikeAngles array to rotate the image
    private void drawSpikes() {
        for (int i = 0; i < spikeAngles.length; i++) {

            int scaledWidth;
            int scaledHeight;
            int[] spikedArea = spikes[i];

            //the width and heights are chosen whether the angle is 90/270 or 0/180
            if (spikeAngles[i] == 90 || spikeAngles[i] == 270) {
                scaledWidth = spikedArea[3]-spikedArea[1]; //spike's new width
                scaledHeight = spikedArea[2]-spikedArea[0]; //spike's new height
            } else {
                scaledWidth = spikedArea[2]-spikedArea[0]; //spike's new width
                scaledHeight = spikedArea[3]-spikedArea[1]; //spike's new height
            }

            StdDraw.picture((spikedArea[0]+spikedArea[2])/2.0, (spikedArea[1]+spikedArea[3])/2.0,
                    "misc/Spikes.png",
                    scaledWidth,
                    scaledHeight,
                    spikeAngles[i]);
        }
    }
}

