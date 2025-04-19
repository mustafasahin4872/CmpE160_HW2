// name surname: Mustafa Sahin
// student ID: 2023400162

public class Player {

//----------------------------------------------------------------------------------------------------
//DATA FIELDS

    private double x;
    private double y;
    public final static double WIDTH = 20.0;
    public final static double HEIGHT = 20.0;
    private double velocityY = 0.0;
    //the player's look direction, it is +1 or -1 whether it is turned left or right
    private int lookDirection = 1;
    //the player's move direction, initially stationary. directions are explained in map class
    private int[] xMoveDirection = new int[]{0, 0};
    private int[] yMoveDirection = new int[]{0, 0};

//----------------------------------------------------------------------------------------------------
//CONSTRUCTOR

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
    }

//----------------------------------------------------------------------------------------------------
//SETTERS AND GETTERS

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }

    public void setLookDirection(int lookDirection) {
        this.lookDirection = lookDirection;
    }

    public void setXMoveDirection(int[] xMoveDirection) {
        this.xMoveDirection = xMoveDirection;
    }

    public void setYMoveDirection(int[] yMoveDirection) {this.yMoveDirection = yMoveDirection;}

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public int[] getXMoveDirection() {
        return xMoveDirection;
    }
    public int[] getYMoveDirection() {
        return yMoveDirection;
    }

    //----------------------------------------------------------------------------------------------------
//HELPERS

    //resets all of player's variables to their start values
    public void respawn(double[] spawnPoint) {
        setX(spawnPoint[0]);
        setY(spawnPoint[1]);
        setLookDirection(1);
        setXMoveDirection(new int[]{0, 0});
        setVelocityY(0);
    }

    //draws player in accordance with the stage's requirements
    public void draw(int stageIndex) {

        //turn the opposite direction of movement in stage 2
        if (stageIndex == 2) {
            if (lookDirection == -1) {
                StdDraw.picture(x, y, "misc/ElephantRight.png", WIDTH, HEIGHT);
            } else {
                StdDraw.picture(x, y, "misc/ElephantLeft.png", WIDTH, HEIGHT);
            }
        } else { //draw the player looking right if it last moved rightwards, left if the opposite-
            if (lookDirection == 1) {
                StdDraw.picture(x, y, "misc/ElephantRight.png", WIDTH, HEIGHT);
            } else {
                StdDraw.picture(x, y, "misc/ElephantLeft.png", WIDTH, HEIGHT);
            }
        }
    }
}
