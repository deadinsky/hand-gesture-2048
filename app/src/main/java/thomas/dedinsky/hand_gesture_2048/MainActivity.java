package thomas.dedinsky.hand_gesture_2048;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    static final int CLOCK_SPEED = 40;
    public static GameLoopTask glt;
    private LinearSensorEventListener linearListen;
    static public int PHONE_WIDTH = 0, PHONE_HEIGHT = 0, PHONE_LIMIT = 0;
    ImageView background;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Finding out what the phone width and height are
        RelativeLayout r = (RelativeLayout) findViewById(R.id.base);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        PHONE_WIDTH = displayMetrics.widthPixels; PHONE_HEIGHT = displayMetrics.heightPixels;
        PHONE_LIMIT = Math.min(PHONE_HEIGHT, PHONE_WIDTH);
        LinearLayout.LayoutParams xy = new LinearLayout.LayoutParams(PHONE_LIMIT, PHONE_LIMIT);
        //Setting the background to the gameboard
        background = new ImageView(this);
        background.setLayoutParams(xy);
        background.setImageResource(R.drawable.gameboard);
        r.addView(background);
        sensorMaker(r);
        //Game Loop
        Timer myGameLoop = new Timer();
        glt = new GameLoopTask(this, (RelativeLayout) findViewById(R.id.base), getApplicationContext());
        TimerTask myGameLoopTask = new TimerTask() {
            @Override
            public void run() {
                glt.run();
            }
        };
        myGameLoop.schedule(myGameLoopTask, CLOCK_SPEED, CLOCK_SPEED);

    }

    public void sensorMaker(RelativeLayout l) {
        final SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //I'm making my linear acceleration sensor
        TextView linearView = new TextView(getApplicationContext());
        l.addView(linearView);
        linearListen = new LinearSensorEventListener(linearView);

        sensorManager.registerListener(linearListen, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_GAME);
    }
}