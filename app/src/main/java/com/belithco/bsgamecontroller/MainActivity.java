package com.belithco.bsgamecontroller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import edu.wpi.SimplePacketComs.server.device.GameControllerServer;
import io.github.controlwear.virtual.joystick.android.JoystickView;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

// NOTE! This appears to be off by 1 per specification
// https://github.com/javatechs/WiiChuck#classic-controller-mapping
// Send side should include the controller index at offset 0. This shifts sent values by 1 byte.
// On receive side, GameController.getData() returns the 1 byte shifted data values.
enum GAMECTRLR_COMM_OFFSET_CLASSIC {
    ControllerIndex(0),
    JoyXLeft(1),
    JoyYLeft(2),
    JoyXRight(3),
    JoyYRight(4),
    PadRightLeft(7),
    PadUpDown(8),
    ButtonX(9),
    ButtonY(10),
    ButtonZLeft(11),
    TriggerLeft(12),
    ButtonA(13),
    ButtonB(14),
    ButtonPlusMinus(15),
    ButtonHome(16),
    TriggerRight(18),
    ButtonZRight(19),
    ;
    private int offset;
    GAMECTRLR_COMM_OFFSET_CLASSIC(int offset) {
        this.offset = offset;
    }
    public int offset() {
        return offset;
    }
}

public class MainActivity extends AppCompatActivity {
    static final String TAG = "MainActivity";
    static public GameControllerServer gcs = null;
    static public ScriptControllerServer ccs = null;
    String defaultControllerName = "GameController_22";
    int defaultControllerID = 2;
    int controllerID = defaultControllerID;
    String controllerName = defaultControllerName;
    TextView xmit_text = null;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get controllerName from preferences
        // TODO controller name never stored in preferences
        controllerName = AppPreferences.getPrefStr(this,AppPreferences.PREF_MODEL_CONTROLLER_NAME, defaultControllerName);
        controllerID = AppPreferences.getPrefInt(this,AppPreferences.PREF_MODEL_CONTROLLER_ID, defaultControllerID);

        // Title
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(controllerName + "."+controllerID+" - " + getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);

        // Floating action bar (disabled)
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.setVisibility(View.INVISIBLE);

        // Controller Name
        EditText editText;
        editText = findViewById(R.id.controller_name);
        editText.setText(controllerName);
        // Controller ID
        editText = findViewById(R.id.controller_id);
        editText.setText(""+controllerID);

        // X,Y,A,B, Home, ZLeft, ZRight Buttons
        Button btn;
        btn = findViewById(R.id.btn_X);
        btn.setOnTouchListener(new ControllerButtonListener(GAMECTRLR_COMM_OFFSET_CLASSIC.ButtonX));
        btn = findViewById(R.id.btn_Y);
        btn.setOnTouchListener(new ControllerButtonListener(GAMECTRLR_COMM_OFFSET_CLASSIC.ButtonY));
        btn = findViewById(R.id.btn_A);
        btn.setOnTouchListener(new ControllerButtonListener(GAMECTRLR_COMM_OFFSET_CLASSIC.ButtonA));
        btn = findViewById(R.id.btn_B);
        btn.setOnTouchListener(new ControllerButtonListener(GAMECTRLR_COMM_OFFSET_CLASSIC.ButtonB));
        btn = findViewById(R.id.btn_Home);
        btn.setOnTouchListener(new ControllerButtonListener(GAMECTRLR_COMM_OFFSET_CLASSIC.ButtonHome));
        btn = findViewById(R.id.btn_zleft);
        btn.setOnTouchListener(new ControllerButtonListener(GAMECTRLR_COMM_OFFSET_CLASSIC.ButtonZLeft));
        btn = findViewById(R.id.btn_zright);
        btn.setOnTouchListener(new ControllerButtonListener(GAMECTRLR_COMM_OFFSET_CLASSIC.ButtonZRight));

        // Z Left/Right Triggers
        SeekBar seekBar;
        seekBar = findViewById(R.id.zleft_trigger);
        seekBar.setOnSeekBarChangeListener(
                new ZTriggerListener(GAMECTRLR_COMM_OFFSET_CLASSIC.TriggerLeft, R.id.zleft_trigger_value));
        seekBar = findViewById(R.id.zright_trigger);
        seekBar.setOnSeekBarChangeListener(
                new ZTriggerListener(GAMECTRLR_COMM_OFFSET_CLASSIC.TriggerRight, R.id.zright_trigger_value));

        // Pad: Down/Up, Left/Right
        // TODO might be better as single image with hit test
        btn = findViewById(R.id.btn_PadDown);
        btn.setOnTouchListener(new ControllerButtonListener(GAMECTRLR_COMM_OFFSET_CLASSIC.PadUpDown,0,128));
        btn = findViewById(R.id.btn_PadUp);
        btn.setOnTouchListener(new ControllerButtonListener(GAMECTRLR_COMM_OFFSET_CLASSIC.PadUpDown,255,128));
        btn = findViewById(R.id.btn_PadLeft);
        btn.setOnTouchListener(new ControllerButtonListener(GAMECTRLR_COMM_OFFSET_CLASSIC.PadRightLeft,0,128));
        btn = findViewById(R.id.btn_PadRight);
        btn.setOnTouchListener(new ControllerButtonListener(GAMECTRLR_COMM_OFFSET_CLASSIC.PadRightLeft,255,128));

        // Plus/Minus
        btn = findViewById(R.id.btn_Minus);
        btn.setOnTouchListener(new ControllerButtonListener(GAMECTRLR_COMM_OFFSET_CLASSIC.ButtonPlusMinus,0,128));
        btn = findViewById(R.id.btn_Plus);
        btn.setOnTouchListener(new ControllerButtonListener(GAMECTRLR_COMM_OFFSET_CLASSIC.ButtonPlusMinus,255,128));

        // Left/Right Joystick
        JoystickView joystick;
        joystick = findViewById(R.id.leftJoystick);
        joystick.setOnMoveListener(
                new JoystickListener(this,
                        GAMECTRLR_COMM_OFFSET_CLASSIC.JoyXLeft, GAMECTRLR_COMM_OFFSET_CLASSIC.JoyYLeft,
                        R.id.leftJoyAngle, R.id.leftJoyStrength));
        joystick = findViewById(R.id.rightJoystick);
        joystick.setOnMoveListener(
                new JoystickListener(this,
                        GAMECTRLR_COMM_OFFSET_CLASSIC.JoyXRight, GAMECTRLR_COMM_OFFSET_CLASSIC.JoyYRight,
                        R.id.rightJoyAngle, R.id.rightJoyStrength));

        // Connect
        Switch sw = findViewById(R.id.switch_onoff);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    if (null == gcs) {
                        try {
                            gcs = new GameControllerServer(controllerName, controllerID);
                            initControllerData();
                            ccs = new ScriptControllerServer(1985);
//                            ccs.getPacket().oneShotMode();
                            gcs.addServer(ccs);
                            gcs.connect();
                        } catch (Exception e) {
                            Log.d("onoff (connect) button", "gcs.connect() failed");
                        }
                    }
                } else {
                    // The toggle is disabled
                    if (null != gcs) {
                        gcs.disconnectDeviceImp();
                        gcs = null;
                    }
                }
            }
        });
        updateDebugText();
    }

    /**
     * Initialize controller data to neutral values
     * TODO Validate that these #s are correct.
     */
    private void initControllerData(){
        int[] array = gcs.getControllerData();
        array[GAMECTRLR_COMM_OFFSET_CLASSIC.ControllerIndex.offset()] = controllerID;
        array[GAMECTRLR_COMM_OFFSET_CLASSIC.JoyXLeft.offset()] = 128;
        array[GAMECTRLR_COMM_OFFSET_CLASSIC.JoyYLeft.offset()] = 128;
        array[GAMECTRLR_COMM_OFFSET_CLASSIC.JoyXRight.offset()] = 128;
        array[GAMECTRLR_COMM_OFFSET_CLASSIC.JoyYRight.offset()] = 128;
        array[5] = 0;
        array[6] = 0;
        array[GAMECTRLR_COMM_OFFSET_CLASSIC.PadRightLeft.offset()] = 128;
        array[GAMECTRLR_COMM_OFFSET_CLASSIC.PadUpDown.offset()] = 128;
        array[GAMECTRLR_COMM_OFFSET_CLASSIC.ButtonX.offset()] = 0;
        array[GAMECTRLR_COMM_OFFSET_CLASSIC.ButtonY.offset()] = 0;
        array[GAMECTRLR_COMM_OFFSET_CLASSIC.ButtonZLeft.offset()] = 0;
        array[GAMECTRLR_COMM_OFFSET_CLASSIC.TriggerLeft.offset()] = 0;
        array[GAMECTRLR_COMM_OFFSET_CLASSIC.ButtonA.offset()] = 0;
        array[GAMECTRLR_COMM_OFFSET_CLASSIC.ButtonB.offset()] = 0;
        array[GAMECTRLR_COMM_OFFSET_CLASSIC.ButtonPlusMinus.offset()] = 128;
        array[GAMECTRLR_COMM_OFFSET_CLASSIC.ButtonHome.offset()] = 0;
        array[17] = 0;
        array[GAMECTRLR_COMM_OFFSET_CLASSIC.TriggerRight.offset()] = 0;
        array[GAMECTRLR_COMM_OFFSET_CLASSIC.ButtonZRight.offset()] = 0;
    }

    /**
     * Creates a thread which updates debug text
     */
    private void updateDebugText() {

        xmit_text = findViewById(R.id.xmit_text);
        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(50);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateDebugTextView();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    Log.d("UpdateDebugText Thread", "Interrupted!");
                }
            }
        };

        t.start();
    }

    private void updateDebugTextView() {
        if (null!=gcs) {
            int[] array = gcs.getControllerData();
            String outStr = "";
            for (int i=0; i<array.length; i++) {
                outStr += String.format("%02X ", (array[i]));
                if (i==9) {
                    outStr += "\n";
                }
            }
            // TODO Enable/disable with preferences
//            outStr = outStr +array.length;
            xmit_text.setText(outStr);
        }
    }

    /**
     * Changes controller server data for buttons:
     * X,Y,A,B, Home, ZRight, ZLeft
     */
    class ControllerButtonListener implements View.OnTouchListener {
        GAMECTRLR_COMM_OFFSET_CLASSIC offset;
        int offVal = 0;
        int onVal = 255;

        public ControllerButtonListener(GAMECTRLR_COMM_OFFSET_CLASSIC offset) {
            this.offset = offset;
        }
        public ControllerButtonListener(GAMECTRLR_COMM_OFFSET_CLASSIC offset, int onVal, int offVal) {
            this.offset = offset;
            this.onVal = onVal;
            this.offVal = offVal;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            MainActivity host = (MainActivity) view.getContext();
            if (null==host.gcs) {
                return false;
            }
            if (motionEvent.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                int[] x = host.gcs.getControllerData();
                // If we have an on/off values
                x[offset.offset()]=onVal;
                Log.d("TouchTest", "Touch down");
            } else if (motionEvent.getAction() == android.view.MotionEvent.ACTION_UP) {
                Log.d("TouchTest", "Touch up");
                int[] x = host.gcs.getControllerData();
                x[offset.offset()]=offVal;
            }
            return true;
        }
    }

    /**
     * Changes controller server data for Left/Right Z triggers.
     * Values are 0-255
     */
    class ZTriggerListener implements SeekBar.OnSeekBarChangeListener {
        GAMECTRLR_COMM_OFFSET_CLASSIC offset;
        int idZTriggerValue;
        public ZTriggerListener(GAMECTRLR_COMM_OFFSET_CLASSIC offset, int idZTriggerValue) {
            this.offset = offset;
            this.idZTriggerValue = idZTriggerValue;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            MainActivity host = (MainActivity) seekBar.getContext();
            TextView tv;
            tv = findViewById(idZTriggerValue);
            tv.setText(""+i);
            if (null==host.gcs) {
                return;
            }
            int[] x = host.gcs.getControllerData();
            x[offset.offset()]=i;
            Log.d("Seekbar", "onProgressChanged");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
    class JoystickListener implements JoystickView.OnMoveListener {
        GAMECTRLR_COMM_OFFSET_CLASSIC offsetX;
        GAMECTRLR_COMM_OFFSET_CLASSIC offsetY;
        int idJoyStrength;
        int idJoyAngle;
        MainActivity host;

        JoystickListener(MainActivity host,
                         GAMECTRLR_COMM_OFFSET_CLASSIC offsetX, GAMECTRLR_COMM_OFFSET_CLASSIC offsetY,
                         int idJoyAngle, int idJoyStrength) {
            super();
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.idJoyAngle = idJoyAngle;
            this.idJoyStrength = idJoyStrength;
            this.host = host;
        }
        @Override
        public void onMove(int angle, int strength) {
            TextView tv;
            double radians = Math.toRadians(angle);
            // Display raw joystick position
            tv = findViewById(idJoyAngle);
            tv.setText("" + angle+"°");
            tv = findViewById(idJoyStrength);
            tv.setText("" + strength+"%");

            // Is there a valid GameControllerServer?
            if (null==host.gcs) {
                return;
            }
            int[] array = host.gcs.getControllerData();
            // Sine/cos of angle produces XY on perimeter of unit circle.
            // Multiple by 128 to compute offset. 128D=80H.
            // Reduce by strength as a ratio.
            // Add 128 such that x=o,y=0 is expressed as 80H.
            double strengthRatio = strength/100.0;
            int y = (int)(((Math.sin(radians)*128.0)*strengthRatio)+128.0);
            int x = (int)(((Math.cos(radians)*128.0)*strengthRatio)+128.0);

            array[offsetX.offset()] = Math.min(255, x);
            array[offsetY.offset()] = Math.min(255, y);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
//                intent = new Intent(this, SettingsActivity.class);
//                startActivity(intent);
                return true;
            case R.id.action_command:
                intent = new Intent(this, ScriptActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
