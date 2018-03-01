package com.roninaks.tech.shesos;

import android.widget.CheckBox;

/**
 * Created by robin on 22/2/18.
 */

public class ContactModel {
    String contact_name, contact_no;
    boolean isChecked;
    int pos;

    public ContactModel(String contact_name, String contact_no, boolean isChecked, int pos){
        this.contact_name = contact_name;
        this.contact_no = contact_no;
        this.isChecked = isChecked;
        this.pos = pos;

    }

    public String getContact_no(){
        return contact_no;
    }

    public String getContact_name() {
        return contact_name;
    }

    public int getPos(){return pos;}

}
