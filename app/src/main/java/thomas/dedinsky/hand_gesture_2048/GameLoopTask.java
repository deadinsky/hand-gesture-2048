package thomas.dedinsky.hand_gesture_2048;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedList;

public class GameLoopTask {
    
    public enum GameDirection {UP, DOWN, LEFT, RIGHT, NO_MOVEMENT};
    public GameDirection myDir = GameDirection.NO_MOVEMENT;
    private Activity myActivity;
    private RelativeLayout myRL;
    private Context myContext;
    private GameBlock[][] gameBlocks;
    private LinkedList<GameBlock> deadBlocks;
    private TextView[][] gameTexts;
    private int moveCheck = 0;
    public GameLoopTask (Activity tempActivity, RelativeLayout tempRL, Context tempContext) {
        //initialization
        myActivity = tempActivity;
        myRL = tempRL;
        myContext = tempContext;
        gameBlocks = new GameBlock[4][4];
        deadBlocks = new LinkedList();
        gameTexts = new TextView[4][4];
        GameBlock.initialize(); //start the unique id system
        createBlock();
    }
    public void run() {
        myActivity.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        //If there is movement, and depending on the movement, I assess each block in a different order
                        //This is to properly work with the collision and game mechanics in general
                        if (myDir != GameDirection.NO_MOVEMENT) {
                            if (myDir == GameDirection.LEFT) {
                                for (int x = 0; x < 4; x++) {
                                    for (int y = 0; y < 4; y++) {
                                        if (gameBlocks[x][y] != null) {
                                            gameBlocks[x][y].updateBlockDirection(gameBlocks, deadBlocks);
                                        }
                                    }
                                }
                            }
                            if (myDir == GameDirection.RIGHT) {
                                for (int x = 3; x > -1; x--) {
                                    for (int y = 0; y < 4; y++) {
                                        if (gameBlocks[x][y] != null) {
                                            gameBlocks[x][y].updateBlockDirection(gameBlocks, deadBlocks);
                                        }
                                    }
                                }
                            }
                            if (myDir == GameDirection.UP) {
                                for (int x = 0; x < 4; x++) {
                                    for (int y = 0; y < 4; y++) {
                                        if (gameBlocks[x][y] != null) {
                                            gameBlocks[x][y].updateBlockDirection(gameBlocks, deadBlocks);
                                        }
                                    }
                                }
                            }
                            if (myDir == GameDirection.DOWN) {
                                for (int x = 0; x < 4; x++) {
                                    for (int y = 3; y > -1; y--) {
                                        if (gameBlocks[x][y] != null) {
                                            gameBlocks[x][y].updateBlockDirection(gameBlocks, deadBlocks);
                                        }
                                    }
                                }
                            }
                            //I still have to account for the blocks that are about to be killed
                            for (GameBlock deadBlock: deadBlocks) {
                                deadBlock.updateBlockDirection(gameBlocks, deadBlocks);
                            }
                            GameDirection testEnd = GameDirection.NO_MOVEMENT;
                            //I check to see if all of the blocks have moved
                            for (int x = 0; x < 4; x++) {
                                for (int y = 0; y < 4; y++) {
                                    if (gameBlocks[x][y] != null && gameBlocks[x][y].myDir != GameDirection.NO_MOVEMENT) {
                                        testEnd = gameBlocks[x][y].myDir;
                                    }
                                }
                            }
                            for (GameBlock deadBlock: deadBlocks) {
                                if (deadBlock.myDir != GameDirection.NO_MOVEMENT) {
                                    testEnd = deadBlock.myDir;
                                }
                            }
                            if (testEnd == GameDirection.NO_MOVEMENT) {
                                myDir = GameDirection.NO_MOVEMENT;
                                //I update the score/colour of each block
                                for (int x = 0; x < 4; x++) {
                                    for (int y = 0; y < 4; y++) {
                                        if (gameBlocks[x][y] != null) {
                                            gameBlocks[x][y].updateColour();
                                            if (gameBlocks[x][y].score == 256) youWin();
                                        }
                                    }
                                }
                                //I kill the blocks that need to be killed
                                for (GameBlock deadBlock: deadBlocks) {
                                    deadBlock.setVisibility(View.GONE);
                                    deadBlock.number.setVisibility(View.GONE);
                                }
                                deadBlocks = new LinkedList();
                                if (moveCheck == 2) { //If there was actual movement
                                    createBlock();
                                    int blockCount = 0;
                                    for (int x = 0; x < 4; x++) {
                                        for (int y = 0; y < 4; y++) {
                                            if (gameBlocks[x][y] != null) {
                                                blockCount++;
                                            }
                                        }
                                    }
                                    //Check to see if the game is over
                                    if (blockCount > 15) {
                                        int done = 1;
                                        for (int x = 0; x < 4; x++) {
                                            for (int y = 0; y < 4; y++) {
                                                if (gameBlocks[x][y] != null) {
                                                    if ((x != 3 && gameBlocks[x+1][y] != null && gameBlocks[x][y].score == gameBlocks[x+1][y].score) || (y != 3 && gameBlocks[x][y+1] != null && gameBlocks[x][y].score == gameBlocks[x][y+1].score) || (y != 0 && gameBlocks[x][y-1] != null && gameBlocks[x][y].score == gameBlocks[x][y-1].score) || (x != 0 && gameBlocks[x-1][y] != null && gameBlocks[x][y].score == gameBlocks[x-1][y].score)) {
                                                        done = 0;
                                                    }
                                                }
                                            }
                                        }
                                        if (done == 1) gameOver();
                                    }
                                }
                            }
                        }
                        if (moveCheck < 2) {
                            moveCheck++;
                        }
                    }
                }
        );
    }
    
    public void setDirection(GameDirection newDirection) {
        moveCheck = 0; //Reset the movement check
        if (myDir == GameDirection.NO_MOVEMENT) {
            myDir = newDirection;
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (gameBlocks[i][j] != null) {
                        gameBlocks[i][j].setBlockDirection(newDirection);
                    }
                }
            }
        }
    }

    private void youWin() {
        TextView youWin = new TextView(myContext);
        youWin.setX(MainActivity.PHONE_LIMIT/16);
        youWin.setY(MainActivity.PHONE_LIMIT/8);
        youWin.setTextColor(Color.RED);
        youWin.setTextSize(MainActivity.PHONE_LIMIT/10);
        youWin.setText("YOU\nWIN");
        youWin.setGravity(View.TEXT_ALIGNMENT_CENTER);
        myRL.addView(youWin);
        youWin.bringToFront();
    }

    private void gameOver() {
        TextView gameOver = new TextView(myContext);
        gameOver.setX(MainActivity.PHONE_LIMIT/16);
        gameOver.setY(MainActivity.PHONE_LIMIT/8);
        gameOver.setTextColor(Color.RED);
        gameOver.setTextSize(MainActivity.PHONE_LIMIT/10);
        gameOver.setText("GAME\nOVER");
        gameOver.setGravity(View.TEXT_ALIGNMENT_CENTER);
        myRL.addView(gameOver);
        gameOver.bringToFront();
    }
    

    private void createBlock() {
        //Get the proper dimensions and text view for the block
        DisplayMetrics displayMetrics = new DisplayMetrics();
        myActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int blockCount = 0;
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                if (gameBlocks[x][y] != null) {
                    blockCount++;
                }
            }
        }
        //Check to see if the game is over
        if (blockCount < 16) {
            TextView testText = new TextView(myContext);
            GameBlock testBlock = new GameBlock(myContext, gameBlocks, testText, Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels));
            myRL.addView(testBlock);
            myRL.addView(testText);
            testText.bringToFront();
            //To keep track of the block/text view
            gameBlocks[testBlock.absX][testBlock.absY] = testBlock;
            gameTexts[testBlock.absX][testBlock.absY] = testText;
        }
    }
}
