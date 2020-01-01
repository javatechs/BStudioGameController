package com.belithco.bsgamecontroller;

import android.app.Activity;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.belithco.bsgamecontroller.MainActivity.ccs;

public class ScriptListAdapter extends ArrayAdapter<ScriptModel> {
    private final Activity context;
    private ArrayList<ScriptModel> dataSet;
    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtScript;
    }

    public ScriptListAdapter(ArrayList<ScriptModel> data, @NonNull Activity context) {
        super(context, R.layout.content_script, data);
        this.context = context;
        this.dataSet = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ScriptModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        View resultView = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();

            LayoutInflater inflater = context.getLayoutInflater();
            resultView = inflater.inflate(R.layout.content_script, parent, false);
            viewHolder.txtName = (TextView) resultView.findViewById(R.id.scriptName);
            viewHolder.txtScript = (TextView) resultView.findViewById(R.id.script);
            //
            Button button = resultView.findViewById(R.id.sendButton);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText et = (EditText) ((View) view.getParent().getParent()).findViewById(R.id.script);
                    if (null != et) {
                        Editable editable = et.getText();
                        if (null != ccs) {
                            ccs.send(editable.toString());
                        }
                    }
                }
            });
            //
            ImageButton imageButton = resultView.findViewById(R.id.deleteScript);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position =(Integer)((View)view.getParent().getParent()).findViewById(R.id.scriptName).getTag();
                    ScriptListAdapter.this.dataSet.remove(position);
                    ScriptListAdapter.this.notifyDataSetChanged();
                }
            });
            resultView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
            resultView = convertView;
        }
        viewHolder.txtName.setText(dataModel.getName());
        viewHolder.txtName.setTag(position);
        viewHolder.txtScript.setText(dataModel.getText());
        return resultView;
    }
}