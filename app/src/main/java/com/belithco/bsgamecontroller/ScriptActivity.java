package com.belithco.bsgamecontroller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static com.belithco.bsgamecontroller.Utility.convertScriptModel;

public class ScriptActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION = 0;
    public static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;
    private static int ct = 1;
    String defaultScript =
        "[{"+
        "\"name\": \"Dialog example.\","+
        "\"text\": \"https://github.com/javatechs/BowlerDebug.git,src/main/groovy/DialogExample.groovy\""+
        "}]";

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
                ListView list = findViewById(R.id.list);
                ScriptListAdapter scriptListAdapter = (ScriptListAdapter)list.getAdapter();
                // TODO Need better 'add new'. Better title? Default Script.
                scriptListAdapter.getDataSet().add(new ScriptModel("New "+ct, ""));
                ct++;
                // Save changes to list
                scriptListAdapter.saveScriptPrefs();
                scriptListAdapter.notifyDataSetChanged();
                // TODO Reposition list to newest entry. End of list.
            }
        });
        // Get scripts from preferences
        String prefScripts = AppPreferences.getPrefStr(this, AppPreferences.PREF_SCRIPTS, defaultScript);
        // Create list from json string
        ArrayList<ScriptModel> data = convertScriptModel(prefScripts);
        ScriptListAdapter listAdapter = new ScriptListAdapter(data, this);
        ListView list = (ListView)findViewById(R.id.list);
        list.setAdapter(listAdapter);
    }

    // This method is invoked after user click buttons in permission grant popup dialog.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int grantResultsLength;
        switch (requestCode) {
            case REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION:
                grantResultsLength = grantResults.length;
                if (grantResultsLength > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,
                            this.getString(R.string.GRANTED_EXT_STORE_WRITE),
                            Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),
                            this.getString(R.string.DENIED_EXT_STORE_WRITE),
                            Toast.LENGTH_LONG).show();
                }
                break;
            case REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION:
                grantResultsLength = grantResults.length;
                if(grantResultsLength > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,
                            this.getString(R.string.GRANTED_EXT_STORE_READ),
                            Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),
                            this.getString(R.string.DENIED_EXT_STORE_READ),
                            Toast.LENGTH_LONG).show();
                }
                break;
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
            case R.id.action_load_script_file:
                ListView list = (ListView)findViewById(R.id.list);
                ScriptListAdapter scriptListAdapter = (ScriptListAdapter)list.getAdapter();
                ArrayList<ScriptModel> dataSet = Utility.loadFile(this);
                scriptListAdapter.getDataSet().clear();
                scriptListAdapter.getDataSet().addAll(dataSet);
                scriptListAdapter.notifyDataSetChanged();
                break;
            //
            case R.id.action_save_script_file:
                list = (ListView)findViewById(R.id.list);
                ArrayList<ScriptModel> data = ((ScriptListAdapter)list.getAdapter()).getDataSet();
                Utility.saveFile(this, data);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}