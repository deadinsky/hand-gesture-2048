package thomas.dedinsky.hand_gesture_2048;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;

public class GameBlock extends GameBlockAbstract {
    private int movementCount = 0, id, canMerge = 1;
    private static int BLOCK_LAG, BLOCK_STEP, counterId;
    private int myCoordX, myCoordY, targetX, targetY, PHONE_LIMIT, BLOCK_WIDTH, BLOCK_MOVE;
    protected int absX, absY, offsetX, offsetY, score = 2;
    protected TextView number;
    GameLoopTask.GameDirection myDir = GameLoopTask.GameDirection.NO_MOVEMENT, lastDir = GameLoopTask.GameDirection.NO_MOVEMENT;
    
    public GameBlock (Context tempContext, GameBlock[][] gameBlocks, TextView testText, int phoneLimit) {
        super(tempContext, gameBlocks, testText, phoneLimit);
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

    public void setBlockDirection(GameLoopTask.GameDirection newDir) {
        if (myDir == GameLoopTask.GameDirection.NO_MOVEMENT && lastDir != newDir) {
            myDir = newDir;
            movementCount = 0;
        }
    }

    public static void initialize() {
        counterId = 0;
    }

    public void updateColour() {
        canMerge = 1;
        //Depending on the score, I modify the colour and text offset
        switch (score) {
            case 1:
                break;
            case 2:
                this.setColorFilter(Color.argb(127, 0, 255, 255));
                break;
            case 4:
                this.setColorFilter(Color.argb(127, 255, 0, 255));
                break;
            case 8:
                this.setColorFilter(Color.argb(127, 255, 255, 0));
                break;
            case 16:
                this.setColorFilter(Color.argb(127, 0, 0, 255));
                offsetX = BLOCK_WIDTH/10;
                break;
            case 32:
                this.setColorFilter(Color.argb(127, 0, 255, 0));
                offsetX = BLOCK_WIDTH/12;
                break;
            case 64:
                this.setColorFilter(Color.argb(127, 255, 0, 0));
                break;
            case 128:
                this.setColorFilter(Color.argb(127, 0, 0, 0));
                offsetX = BLOCK_WIDTH/12;
                offsetY = BLOCK_WIDTH/6;
                number.setTextSize(BLOCK_WIDTH/6);
                break;
            case 256:
                this.setColorFilter(Color.argb(127, 255, 255, 0));
                break;
            default:
                //error
                break;
        }
        
        number.setText(Integer.toString(score));
        number.setX(myCoordX+offsetX);
        number.setY(myCoordY+offsetY);
    }

    public void updateBlockDirection(GameBlock[][] gameBlocks, LinkedList<GameBlock> deadBlocks) {
        if (myDir != GameLoopTask.GameDirection.NO_MOVEMENT) { //If there is movement...
            movementCount++;
            if (movementCount == 1) {
                int oldX = absX, oldY = absY;
                //Same basic function, accounting for different directions
                //More or less checking where a block can go, and if it can merge
                if (myDir == GameLoopTask.GameDirection.LEFT) {
                    absX = 0;
                    while (gameBlocks[absX][absY] != null && absX != oldX) {
                        if (gameBlocks[absX][absY].score == gameBlocks[oldX][absY].score && ((gameBlocks[absX+1][absY] == null && (gameBlocks[absX+2][absY] == null || gameBlocks[absX+2][absY].id == this.id)) || gameBlocks[absX+1][absY].id == this.id) && gameBlocks[absX][absY].canMerge == 1) {
                            gameBlocks[absX][absY].score *= 2;
                            gameBlocks[absX][absY].canMerge = 0;
                            deadBlocks.add(this);
                            gameBlocks[oldX][absY] = null;
                            break;
                        } else {
                            absX++;
                        }
                    }
                }
                if (myDir == GameLoopTask.GameDirection.RIGHT) {
                    absX = 3;
                    while (gameBlocks[absX][absY] != null && absX != oldX) {
                        if (gameBlocks[absX][absY].score == gameBlocks[oldX][absY].score && ((gameBlocks[absX-1][absY] == null && (gameBlocks[absX-2][absY] == null || gameBlocks[absX-2][absY].id == this.id)) || gameBlocks[absX-1][absY].id == this.id) && gameBlocks[absX][absY].canMerge == 1) {
                            gameBlocks[absX][absY].score *= 2;
                            gameBlocks[absX][absY].canMerge = 0;
                            deadBlocks.add(this);
                            gameBlocks[oldX][absY] = null;
                            break;
                        } else {
                            absX--;
                        }
                    }
                }
                
                if (myDir == GameLoopTask.GameDirection.UP) {
                    absY = 0;
                    while (gameBlocks[absX][absY] != null && absY != oldY) {
                        if (gameBlocks[absX][absY].score == gameBlocks[absX][oldY].score && ((gameBlocks[absX][absY+1] == null && (gameBlocks[absX][absY+2] == null || gameBlocks[absX][absY+2].id == this.id)) || gameBlocks[absX][absY+1].id == this.id) && gameBlocks[absX][absY].canMerge == 1) {
                            gameBlocks[absX][absY].score *= 2;
                            gameBlocks[absX][absY].canMerge = 0;
                            deadBlocks.add(this);
                            gameBlocks[absX][oldY] = null;
                            break;
                        } else {
                            absY++;
                        }
                    }
                }
                if (myDir == GameLoopTask.GameDirection.DOWN) {
                    absY = 3;
                    while (gameBlocks[absX][absY] != null && absY != oldY) {
                        if (gameBlocks[absX][absY].score == gameBlocks[absX][oldY].score && ((gameBlocks[absX][absY-1] == null && (gameBlocks[absX][absY-2] == null || gameBlocks[absX][absY-2].id == this.id)) || gameBlocks[absX][absY-1].id == this.id) && gameBlocks[absX][absY].canMerge == 1) {
                            gameBlocks[absX][absY].score *= 2;
                            gameBlocks[absX][absY].canMerge = 0;
                            deadBlocks.add(this);
                            gameBlocks[absX][oldY] = null;
                            break;
                        } else {
                            absY--;
                        }
                    }
                }
                //Updates the block position internally
                if (gameBlocks[oldX][oldY] != null) {
                    gameBlocks[oldX][oldY] = null;
                    gameBlocks[absX][absY] = this;
                }
                targetX = absX * BLOCK_MOVE;
                targetY = absY * BLOCK_MOVE;
            }
            //Same idea, different directions
            //Inching forward to the desired location on the board until it gets there, then it stops moving
            if (myDir == GameLoopTask.GameDirection.LEFT) {
                if (myCoordX - BLOCK_STEP <= targetX) {
                    myCoordX = targetX;
                    myDir = GameLoopTask.GameDirection.NO_MOVEMENT;
                } else {
                    myCoordX -= BLOCK_STEP;
                }
            }
            if (myDir == GameLoopTask.GameDirection.RIGHT) {
                if (myCoordX + BLOCK_STEP >= targetX) {
                    myCoordX = targetX;
                    myDir = GameLoopTask.GameDirection.NO_MOVEMENT;
                } else {
                    myCoordX += BLOCK_STEP;
                }
            }
            if (myDir == GameLoopTask.GameDirection.UP) {
                if (myCoordY - BLOCK_STEP <= targetY) {
                    myCoordY = targetY;
                    myDir = GameLoopTask.GameDirection.NO_MOVEMENT;
                } else {
                    myCoordY -= BLOCK_STEP;
                }
            }
            if (myDir == GameLoopTask.GameDirection.DOWN) {
                if (myCoordY + BLOCK_STEP >= targetY) {
                    myCoordY = targetY;
                    myDir = GameLoopTask.GameDirection.NO_MOVEMENT;
                } else {
                    myCoordY += BLOCK_STEP;
                }
            }
            this.setX(myCoordX);
            this.setY(myCoordY);
            number.setX(myCoordX+offsetX);
            number.setY(myCoordY+offsetY);
        }
    }
}
