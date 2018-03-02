package com.roninaks.tech.shesos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class SetUID extends Activity {
    public static Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_uid);

        mContext = getBaseContext();

        SharedPreferences stored_uid = getSharedPreferences("label", 0);
        String mString = stored_uid.getString("shesosuid", "");
        Log.e("UID", "uid"+mString);
//        Toast.makeText(SetUID.this, ""+mString, Toast.LENGTH_LONG).show();

        if (mString.isEmpty()){
            DialogUID dialogBox = new DialogUID(SetUID.this);
            dialogBox.show();
        }
        else {
            Intent intent = new Intent(SetUID.this, Home.class);
            startActivity(intent);
        }

    }
    public static Context getContext() {
        return mContext;
    }
}

