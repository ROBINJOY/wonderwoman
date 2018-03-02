package com.roninaks.tech.shesos;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class Home extends Activity {
    public final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    ListView listView ;
    ArrayList<String> StoreContacts, ContactsName ;
    ArrayAdapter<String> arrayAdapter ;
    Cursor cursor ;
    String name, phonenumber ;
    TextView textView1, textView2;
    List<ContactModel> contact;
    List<ContactModel> temp;
    int limit = 0;
    NumberPicker number_picker;
    SearchView searchView;
    public HashMap<String, String> dict = new HashMap<String, String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
//        filter.addAction(Intent.ACTION_SCREEN_OFF);
//        filter.addAction(Intent.ACTION_USER_PRESENT);
//        filter.addAction(Intent.ACTION_SCREEN_ON);
//        KeyPressRecieve keyPressRecieve = new KeyPressRecieve();
//        LocalBroadcastManager.getInstance(this).registerReceiver(keyPressRecieve, filter);



        searchView = (SearchView) findViewById(R.id.contact_search);
        Button button = findViewById(R.id.submit_button);
        button.setVisibility(View.INVISIBLE);

        number_picker = (NumberPicker) findViewById(R.id.num_picker);
        number_picker.setMaxValue(10);
        number_picker.setMinValue(1);

        enablePermission();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(checkContactReadPermission()){
            readContacts();
        }
        KeyPressService.home = this;
        Context context = this.getBaseContext();
        context.startService(new Intent(this, KeyPressService.class));

    }
//    public void proceed(View view) throws InterruptedException {
//        enablePermission();
//        Thread.sleep(5000);
//        //Intent intent = new Intent(this, ContactsGet.class);
//        //startActivity(intent);
//        readContacts();
//    }

    public boolean checkContactReadPermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("Contact Permission", "Permission is granted");
                return true;
            } else {

                Log.e("Contact Permission", "Permission is revoked once");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 0);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Contact Permission", "Permission is granted");
            return true;
        }
    }
    public void readContacts(){

        listView = findViewById(R.id.listview);
        contact = new ArrayList<>();
        StoreContacts = new ArrayList<String>();
        ContactsName = new ArrayList<String>();

        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");
        int i = 0;
        while (cursor.moveToNext()) {

            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            StoreContacts.add(name + " "  + ":" + " " + phonenumber);
            ContactsName.add(name);
            contact.add(new ContactModel(name,phonenumber,false, i++));
        }
        temp = contact;
        cursor.close();

        CustomListAdapter arrayAdapter = new CustomListAdapter(
                this,
                R.layout.listview,
                temp
        );

        Button button = findViewById(R.id.submit_button);
        button.setVisibility(View.VISIBLE);
        number_picker.setVisibility(View.VISIBLE);


        number_picker.setOnValueChangedListener(new NumPicker());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                limit = number_picker.getValue();
                if (temp.get(position).isChecked){
                    dict.remove(String.valueOf(position));
                    Log.e("Selected Position","" + temp.get(position).pos);
                    contact.get(temp.get(position).pos).isChecked = false;
                    CheckBox checkBox = view.findViewById(R.id.contact_checkBox);
                    checkBox.setChecked(false);
                }
                else {

                    if (dict.size() == limit) {
                        Toast.makeText(Home.this, "Select max "+limit+" contacts", Toast.LENGTH_LONG).show();
                    }
                    else {
                        CheckBox checkBox = view.findViewById(R.id.contact_checkBox);
                        checkBox.setChecked(true);
                        Log.e("Selected Position","" + temp.get(position).pos);
                        dict.put(String.valueOf(position), temp.get(position).contact_no);
                        contact.get(temp.get(position).pos).isChecked = true;
                    }
                }

//                CheckBox checkBox = findViewById(R.id.contact_checkBox);
//                checkBox.setChecked(true);

                //Toast.makeText(Home.this, "dict is " + dict, Toast.LENGTH_LONG).show();

            }

        });
        listView.setAdapter(arrayAdapter);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dict.size() > 0) {
                            limit = number_picker.getValue();
                            if (dict.size() == limit){
                                String emgncy_contacts = "";
                                Iterator it = dict.entrySet().iterator();
                                while (it.hasNext()) {
                                    Map.Entry pair = (Map.Entry)it.next();
                                    emgncy_contacts = emgncy_contacts.concat(pair.getValue() + ";");
                                }
                                SharedPreferences stored_numbers = getSharedPreferences("label", 0);
                                SharedPreferences.Editor mEditor = stored_numbers.edit();
                                mEditor.putString("shesosno", emgncy_contacts).commit();
                                Intent intent = new Intent(Home.this, ContactsGet.class);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(Home.this, "Select "+limit+" contacts", Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            Toast.makeText(Home.this, "Select atleast one contact", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
        searchView.setVisibility(View.VISIBLE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterList(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        });


    }

    public void filterList(String query){
        if(query.isEmpty()){
            temp = contact;
            listView.setAdapter(new CustomListAdapter(this,R.layout.listview,
                    temp));
        }
        else{
            temp = new ArrayList<>();
            for(int i=0;i< contact.size(); i++){
                if(contact.get(i).getContact_name().toLowerCase().contains(query.toLowerCase())){
                    temp.add(contact.get(i));
                }
            }

            listView.setAdapter(new CustomListAdapter(this,R.layout.listview,
                    temp));
        }
    }
    private class NumPicker implements NumberPicker.OnValueChangeListener {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            //get new value and convert it to String
            //if you want to use variable value elsewhere, declare it as a field
            //of your main function
//            String value = "" + newVal;
            int range = newVal;
        }
    }

    public void enablePermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    //        Handling permission response
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
//                    Intent intent = new Intent(this, ContactsGet.class);
//                    startActivity(intent);
                    readContacts();
                    //Toast.makeText(this, "SUCCESS", Toast.LENGTH_LONG).show();

                }
                else {
                    //Toast.makeText(this, "PERMISSION DENIED", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

            }
            case 100: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Message Permission","Permission is granted");
                    //send sms here call your method
                    SharedPreferences mPrefs = getSharedPreferences("label", 0);
                    String phoneNo = mPrefs.getString("shesosno", "");
                    String msg_uid = mPrefs.getString("shesosuid", "");
                    String msg_content = mPrefs.getString("shesosmsg", "Please help, I am in danger.");
                    String msg = "UID: " + msg_uid + "\n" + "MESSAGE: "+ msg_content;
                    new KeyPressService().sendSMS(phoneNo,msg);
                } else {
                    Log.e("Message Permission","Permission is revoked");
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)){
//            Toast.makeText(this, "Volume Pressed", Toast.LENGTH_LONG).show();
//        }
//        if((keyCode == KeyEvent.KEYCODE_POWER))
//            Log.e("Power button", "Power Button pressed " + countPower++);
//        return true;
//    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("Test Pause","Paused");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Test Pause", "Resumed");
    }
}
