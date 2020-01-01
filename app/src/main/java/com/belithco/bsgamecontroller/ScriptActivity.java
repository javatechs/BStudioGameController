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
import android.widget.ListView;

import java.util.ArrayList;

import static com.belithco.bsgamecontroller.MainActivity.*;

public class ScriptActivity extends AppCompatActivity {

    ArrayList<ScriptModel> data = null;
    static int ct = 1;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_script);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add a new script...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setDuration(2000).show();
                // TODO Need better 'add new'. Reposition list to newest entry.
                if (null!=data) {
                    data.add(new ScriptModel("New "+ct, ""));
                    ct++;
                    ListView list = findViewById(R.id.list);
                    ((ScriptListAdapter)list.getAdapter()).notifyDataSetChanged();
                }
            }
        });
        // TODO Should build list from preferences
        data = new ArrayList<ScriptModel>();
        data.add(new ScriptModel("Wag, nod, sit and speak.",
        "file://movement.groovy,Wag,nod,sit,speak"));
        data.add(new ScriptModel("Dialog example.",
        "https://github.com/javatechs/BowlerDebug,src/main/groovy/DialogExample.groovy"));
        ScriptListAdapter listAdapter = new ScriptListAdapter(data, this);
        ListView list=(ListView)findViewById(R.id.list);
        list.setAdapter(listAdapter);
    }

    public void onRunScript(View view) {
        //
        EditText editText = findViewById(R.id.script);
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