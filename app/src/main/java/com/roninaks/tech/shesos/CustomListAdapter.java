package com.roninaks.tech.shesos;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by robin on 22/2/18.
 */

public class CustomListAdapter extends ArrayAdapter<ContactModel> {

    //the list values in the List of type hero
    private List<ContactModel> contact;

    //activity context
    private Context context;

    //the layout resource file for the list items
    private int resource;

    //constructor initializing the values
    public CustomListAdapter(Context context, int resource, List<ContactModel> contact) {
        super(context, resource, contact);
        this.context = context;
        this.resource = resource;
        this.contact = contact;
    }

    //this will return the ListView Item as a View
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //we need to get the view of the xml for our list item
        //And for this we need a layoutinflater
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        //getting the view
        View view = layoutInflater.inflate(resource, null, false);

        //getting the view elements of the list from the view
        TextView textViewName = view.findViewById(R.id.textViewName);
        TextView textViewNo = view.findViewById(R.id.textViewNo);
        CheckBox checkBox = view.findViewById(R.id.contact_checkBox);

        //getting the hero of the specified position
        ContactModel contactModel = contact.get(position);

        //adding values to the list item
        textViewName.setText(contactModel.getContact_name());
        textViewNo.setText(contactModel.getContact_no());
        checkBox.setChecked(contactModel.isChecked);




        return view;
    }

}


