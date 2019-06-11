package com.example.cis657_hw4;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SettingsActivity extends AppCompatActivity {

    String distUnit = "kilometers";
    String bearUnit = "degrees";
    String lat1,lat2,long1,long2;
    TextView distanceunitid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        distanceunitid = (TextView) findViewById(R.id.distanceunitid);

        //Floating action button to save changes to unit settings
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        //Button listern for action button that reacts when it is touched
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                //When it is clicked, it saves the units and user data to be sent back to the main
                Intent intent = new Intent();
                intent.putExtra("distunits",distUnit);
                intent.putExtra("bearunits",bearUnit);
                intent.putExtra("lat1",lat1);
                intent.putExtra("lat2",lat2);
                intent.putExtra("long1",long1);
                intent.putExtra("long2",long2);

                //Returns to main
                setResult(MainActivity.DIST_UNIT,intent);
                //Exits window
                finish();
            }
        });

        //Intent that was sent from main class could have data sent with it
        Intent receiveintent = getIntent();
        if(receiveintent.hasExtra("currdistunit")&&
                receiveintent.hasExtra("currbearunit")){
            distUnit = receiveintent.getStringExtra("currdistunit");
            bearUnit = receiveintent.getStringExtra("currbearunit");
            lat1 = receiveintent.getStringExtra("lat1");
            lat2 = receiveintent.getStringExtra("lat2");
            long1 = receiveintent.getStringExtra("long1");
            long2 = receiveintent.getStringExtra("long2");
        }

        //Creates a new spinner for the distance units
        Spinner distunitspinner = (Spinner) findViewById(R.id.distancespinner);

        //The 'ArrayAdapter' uses stored strings to help create the desired spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.distUnits, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Links the spinner with the ArrayAdapter variable
        distunitspinner.setAdapter(adapter);

        //Sets the display of the spinner to whatever unit is active
        if(distUnit.equals("miles")) distunitspinner.setSelection(1);
        else distunitspinner.setSelection(0);

        //Sets up the display of the spinner
        distunitspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            //If an item is selected, it is saved as the distance unit
            @Override
            public void onItemSelected(AdapterView<?>adapterView, View view, int i, long l) {
                distUnit = (String) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView){
            }
        });

        //Creates a new spinner for the bearing units
        Spinner bearunitspinner = (Spinner) findViewById(R.id.bearingspinner);

        //The 'ArrayAdapter' uses stored strings to help create the desired spinner
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.bearUnits, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Links the spinner with the ArrayAdapter variable
        bearunitspinner.setAdapter(adapter1);

        //Sets the display of the spinner to whatever unit is active
        if(bearUnit.equals("mils")) bearunitspinner.setSelection(1);
        else bearunitspinner.setSelection(0);

        //Sets up the display of the spinner
        bearunitspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            //If an item is selected, it is saved as the distance unit
            @Override
            public void onItemSelected(AdapterView<?>adapterView, View view, int i, long l) {
                bearUnit = (String) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView){

            }
        });

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//this adds a back arrow on the toolbar
    }



}
