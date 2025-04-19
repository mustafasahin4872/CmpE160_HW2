// name surname: Mustafa Sahin
// student ID: 2023400162

import java.awt.Color;

public class Stage {

//----------------------------------------------------------------------------------------------------,d
//DATA FIELDS

    private int stageNumber;
    private double gravity;
    private double velocityX;
    private double velocityY;
    private int rightCode;
    private int leftCode;
    private int upCode;
    private String clue;
    private  String help;
    //for every Stage object, create a random color
    private final Color color = new Color((int)(Math.random()*256), (int)(Math.random()*256), (int)(Math.random()*256));
    private final static double[] SPAWN_POINT = new double[]{130, 440};

//----------------------------------------------------------------------------------------------------
//CONSTRUCTOR

    Stage(
            double gravity, double velocityX, double velocityY, int stageNumber,
            int rightCode, int leftCode, int upCode, String clue, String help
    ) {
        this.gravity = gravity;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.stageNumber = stageNumber;
        this.rightCode = rightCode;
        this.leftCode = leftCode;
        this.upCode = upCode;
        this.clue = clue;
        this.help = help;
    }

//----------------------------------------------------------------------------------------------------
//SETTERS AND GETTERS

    public int getStageNumber() {
        return stageNumber;
    }

    public double getGravity() {
        return gravity;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public int[] getKeyCodes() {
        return new int[]{rightCode, leftCode, upCode};
    }

    public String getClue() {
        return clue;
    }

    public String getHelp() {
        return help;
    }

    public Color getColor() {
        return color;
    }

    public double[] getSpawnPoint() {
        return SPAWN_POINT;
    }

}
