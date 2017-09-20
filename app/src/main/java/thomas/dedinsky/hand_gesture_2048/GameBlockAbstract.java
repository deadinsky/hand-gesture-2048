package thomas.dedinsky.hand_gesture_2048;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;

abstract class GameBlockAbstract extends android.support.v7.widget.AppCompatImageView {

    private int movementCount = 0, id, canMerge = 1;
    private static int BLOCK_LAG, BLOCK_STEP, counterId;
    private int myCoordX, myCoordY, targetX, targetY, PHONE_LIMIT, BLOCK_WIDTH, BLOCK_MOVE;
    protected int absX, absY, offsetX, offsetY, score = 2;
    protected TextView number;
    GameLoopTask.GameDirection myDir = GameLoopTask.GameDirection.NO_MOVEMENT, lastDir = GameLoopTask.GameDirection.NO_MOVEMENT;

    public GameBlockAbstract(Context tempContext, GameBlock[][] gameBlocks, TextView testText, int phoneLimit) {
        super(tempContext);
        number = testText;
        id = counterId; //unique id system
        counterId++;
        //Get the boundaries of the phone/block
        PHONE_LIMIT = phoneLimit;
        BLOCK_WIDTH = PHONE_LIMIT * 146 / 575;
        BLOCK_MOVE = PHONE_LIMIT * 143 / 575;
        //Offset for the textView
        offsetX = BLOCK_WIDTH/4;
        offsetY = 0;
        int size = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (gameBlocks[i][j] != null) {
                    size++;
                }
            }
        }
        if ((int)(Math.random() * 5) > 3) {
            score = 4;
        }

        //Accounting for java lag
        BLOCK_LAG = (13 - (int)(Math.pow(size, 0.4) * 2))/2;
        BLOCK_STEP = BLOCK_MOVE / BLOCK_LAG;
        int stop = 0;
        //Make sure I don't place it in an existing block spot
        if (size < 16) {
            while (stop == 0) {
                stop = 1;
                absX = (int) (Math.random() * 4);
                absY = (int) (Math.random() * 4);
                if (gameBlocks[absX][absY] != null) {
                    stop = 0;
                }
            }
            myCoordX = BLOCK_MOVE * absX;
            myCoordY = BLOCK_MOVE * absY;
            //Set all of the kinks for the block
            LinearLayout.LayoutParams xy = new LinearLayout.LayoutParams(BLOCK_WIDTH, BLOCK_WIDTH);
            this.setLayoutParams(xy);
            this.setX(myCoordX);
            this.setY(myCoordY);
            if (score == 2) {
                this.setColorFilter(Color.argb(127, 0, 255, 255));
            } else {
                this.setColorFilter(Color.argb(127, 255, 0, 255));
            }
            this.setImageResource(R.drawable.gameblock);
            //Set all of the kinks for the text
            number.setX(myCoordX+offsetX);
            number.setY(myCoordY+offsetY);
            number.setTextColor(Color.BLACK);
            number.setTextSize(BLOCK_WIDTH/4);
            number.setText(Integer.toString(score));
            number.setGravity(View.TEXT_ALIGNMENT_CENTER);
        }
    }

    public abstract void setBlockDirection(GameLoopTask.GameDirection newDir);

    public static void initialize() {
        counterId = 0;
    }

    public abstract void updateColour();

    public abstract void updateBlockDirection(GameBlock[][] gameBlocks, LinkedList<GameBlock> deadBlocks);
}
