package com.example.cis657_hw4;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.parceler.Parcels;


public class HistoryActivity extends AppCompatActivity
        implements HistoryFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    @Override
    public void onListFragmentInteraction(LocationLookup item) {
//        System.out.println("Interact!");
//        Intent intent = new Intent();
//        Double[] vals = {item.origLat, item.origLong, item.endLat, item.endLong};
//        intent.putExtra("item", vals);
//        setResult(MainActivity.HISTORY_RESULT,intent);
//        finish();

        Intent result = new Intent();
        // add more code to initialize the rest of the fields
        Parcelable parcel = Parcels.wrap(item);
        result.putExtra("item", parcel);
        setResult(MainActivity.HISTORY_RESULT, result);
        finish();

    }

}
