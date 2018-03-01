package com.roninaks.tech.shesos;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ContactsGet extends Activity {
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_get);
        editText = findViewById(R.id.emgncy_msg);

        SharedPreferences stored_msg = getSharedPreferences("label", 0);
        String mString = stored_msg.getString("shesosmsg", "Please help, I am in danger");
        editText.setText(mString);

        Button button = findViewById(R.id.final_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg_string = editText.getText().toString();
                SharedPreferences stored_msg = getSharedPreferences("label", 0);
                SharedPreferences.Editor mEditor = stored_msg.edit();
                mEditor.putString("shesosmsg", msg_string).commit();
                DialogBox dialogBox = new DialogBox(ContactsGet.this);
                dialogBox.show();

            }
        });

    }

}
