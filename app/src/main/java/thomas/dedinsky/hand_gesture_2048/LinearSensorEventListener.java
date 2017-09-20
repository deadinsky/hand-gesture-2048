package thomas.dedinsky.hand_gesture_2048;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;

public class LinearSensorEventListener implements SensorEventListener {
    
    final int DELAY_COUNT = 40; final double THRESHOLD = 5.4; final int C = 6;
    final int X_FOCUS = 1; final int Y_FOCUS = 2; final int Z_FOCUS = 3; final int STOP_FOCUS = -1; final int NO_FOCUS = 0;
    TextView multiView;
    float x, y, z; //max values
    float filterX = 0, filterY = 0, filterZ = 0; //max values
    int gestureCount = 0, gestureControl = 0, posNeg = 0;
    String direction = "NONE";

    //initialization
    public LinearSensorEventListener(TextView multiViewTemp) {
        multiView = multiViewTemp;
        /*multiView.setTextColor(Color.BLACK);
        multiView.setTextSize(85);
        multiView.setY(790);
        multiView.setText("NONE");*/
    }

    public void onAccuracyChanged(Sensor s, int i) {
    }

    
    //function that returns the direction of the most recent movement
    public String direction() {
        if (filterX > 0) {
            MainActivity.glt.setDirection(GameLoopTask.GameDirection.RIGHT);
            return "RIGHT";
        }
        if (filterX < 0) {
            MainActivity.glt.setDirection(GameLoopTask.GameDirection.LEFT);
            return "LEFT";
        }
        if (filterY > 0) {
            MainActivity.glt.setDirection(GameLoopTask.GameDirection.UP);
            return "UP";
        }
        if (filterY < 0) {
            MainActivity.glt.setDirection(GameLoopTask.GameDirection.DOWN);
            return "DOWN";
        }
        if (filterZ > 0) return "FRONT";
        if (filterZ < 0) return "BACK";
        if (gestureCount > 0) return direction;
        return "NONE";
    }

    //An updated version of the low pass filter model
    public float calculateNewFilteredReading(float filter, float next, int gControl) {
        //If the filter passes from positive to negative or vice versa I flag it
        if ((filter + (next - filter) / C) * posNeg < 0 && posNeg != 0) {
            if (gControl == gestureControl) {
                gestureControl = STOP_FOCUS;
            }
        }
        //If the filter crossed the x-axis recently or if it's under a certain threshold, I make the filter equal 0
        if (gestureControl == STOP_FOCUS || (Math.abs(next) < THRESHOLD && Math.abs(filter) < THRESHOLD)) {
            return -1 * filter;
        }
        //Otherwise I do regular filtering
        return (next - filter) / C;
    }

    public void registerInput() {
        //If a filter isn't 0 and it isn't because it crossed the x-axis recently.
        if ((filterX != 0 || filterY != 0 || filterZ != 0) && gestureControl >= 0) {
            //If this is a new reading...
            if (gestureCount == NO_FOCUS) {
                //I reset the x-axis flag and the input timeout
                posNeg = 0;
                gestureCount = DELAY_COUNT;
                //Depending on which input is most prominent I only focus/show on it
                if (Math.abs(filterX) >= Math.abs(filterY) && Math.abs(filterX) >= Math.abs(filterZ)) {
                    gestureControl = X_FOCUS;
                    posNeg = (int)(filterX / Math.abs(filterX));
                } else if (Math.abs(filterY) >= Math.abs(filterZ)) {
                    gestureControl = Y_FOCUS;
                    posNeg = (int)(filterY / Math.abs(filterY));
                } else {
                    gestureControl = Z_FOCUS;
                    posNeg = (int)(filterZ / Math.abs(filterZ));
                }
                //I get the direction too!
                direction = direction();
            }
            gestureCount--;
            //If I'm focusing on X, I set Y and Z to zero. Same with the other focuses
            if (gestureControl == X_FOCUS) {
                filterY = filterZ = 0;
            } else if (gestureControl == Y_FOCUS) {
                filterX = filterZ = 0;
            } else if (gestureControl == Z_FOCUS){
                filterX = filterY = 0;
            } else {
                filterX = filterY = filterZ = 0;
            }
        } else {
            //If it goes to this case and it's because it crossed the x-axis, I set the filters to 0
            filterX = filterY = filterZ = 0;
            if (gestureCount > 0) {
                gestureCount--;
            } else {
                //If the timeout period is over, I reset my filter control mechanisms
                gestureControl = NO_FOCUS;
                posNeg = 0;
            }
        }
    }

    public void utilizesData() {
        //If it hit a max point
        if (Math.abs(filterX) > x) {
            x = Math.abs(filterX);
        }
        if (Math.abs(filterY) > y) {
            y = Math.abs(filterY);
        }
        if (Math.abs(filterZ) > z) {
            z = Math.abs(filterZ);
        }
        //multiView.setText(direction);
    }

    public void reset() {
        filterX = filterY = filterZ = x = y = z = gestureControl = gestureCount = posNeg = 0;
        direction = "NONE";
    }

    
    public void onSensorChanged(SensorEvent se) {
        //I add the low pass filter results
        filterX += calculateNewFilteredReading(filterX, se.values[0], 1);
        filterY += calculateNewFilteredReading(filterY, se.values[1], 2);
        filterZ += calculateNewFilteredReading(filterZ, se.values[2], 3);
        registerInput();
        utilizesData();
    }
}
