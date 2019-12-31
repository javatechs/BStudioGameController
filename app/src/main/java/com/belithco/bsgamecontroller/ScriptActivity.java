package com.belithco.bsgamecontroller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import static com.belithco.bsgamecontroller.MainActivity.*;

public class ScriptActivity extends AppCompatActivity {

//    String defaultCommand = "https://github.com/javatechs/BowlerDebug,"
//                            +"src/main/groovy/DialogExample.groovy,"
//                            +"sit,nod,wag";
//    String defaultCommand = "https://github.com/javatechs/BowlerDebug,"
//                            +"src/main/groovy/movement.groovy,"
//                            +"sit,nod,wag";
//    String defaultCommand = "file:///home/fdou/Documents/workspace/BowlerDebug/src/main/groovy/DialogExample.groovy,"
//                            +"sit,nod,wag";
    String defaultCommand = "file://movement.groovy,sit,nod,wag";

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_script);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //
        EditText editText = findViewById(R.id.commandText);
        if (null!=editText) {
            Editable editable = editText.getText();
            if (editable.length()<1) {
                editable.clear();
                editable.append(defaultCommand);
            }
        }

    }

    public void onRunScript(View view) {
        //
        EditText editText = findViewById(R.id.commandText);
        if (  (null!=editText)
           && (null!= ccs)) {
            Editable editable = editText.getText();
            ccs.send(editable.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_script, menu);
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
            case R.id.action_game_controller:
                intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}