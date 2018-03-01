package com.roninaks.tech.shesos;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by robin on 25/2/18.
 */

public class DialogUID extends Dialog implements View.OnClickListener {
    public SetUID s ;
    public Dialog d;
    public Button setid;
    public EditText et;


    public DialogUID(SetUID a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.s = a;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_uid);
        setid = findViewById(R.id.uid_button);
        setid.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.uid_button:
                et = findViewById(R.id.uid);
                String getID = et.getText().toString();
                if (getID.isEmpty()){
                    Toast.makeText(SetUID.getContext(), "Enter the unique id to continue", Toast.LENGTH_LONG).show();

                }
                else {
                    Toast.makeText(SetUID.getContext(), "SUCCESS", Toast.LENGTH_LONG).show();
//                    SharedPreferences stored_uid = PreferenceManager.getDefaultSharedPreferences(SetUID.getContext());
                    SharedPreferences stored_uid = SetUID.getContext().getSharedPreferences("label", 0);
                    SharedPreferences.Editor mEditor = stored_uid.edit();
                    mEditor.putString("shesosuid", getID).commit();
                    Intent intent = new Intent(SetUID.getContext(), Home.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    SetUID.getContext().startActivity(intent);
                    dismiss();
                }
                break;
            default:
                break;
        }

    }
}


