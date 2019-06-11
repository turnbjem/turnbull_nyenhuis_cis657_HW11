package com.example.cis657_hw4;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;
//
//import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.parceler.Parcels;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationSearchActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE2 = 2;
    private static final String TAG = "LocationSearchActivity";

    @BindView(R.id.loc1)
    TextView location1;
    @BindView(R.id.loc2)
    TextView location2;
    @BindView(R.id.calculation_date)
    TextView calculationDateView;

    private DateTime calculationDate;
    private DatePickerDialog dpDialog;
    private LocationLookup currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_search);
        ButterKnife.bind(this);

        currentLocation = new LocationLookup();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        DateTime today = DateTime.now();

        dpDialog = new DatePickerDialog(this, this, today.getYear(), today.getMonthOfYear() - 1, today.getDayOfMonth());


        calculationDateView.setText(formatted(today));
        calculationDate = today;
    }

    @OnClick(R.id.loc1)
    public void location1Pressed() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.LAT_LNG);
        Intent intent =
                new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(this);
        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
    }

    @OnClick(R.id.loc2)
    public void location2Pressed() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.LAT_LNG);
        Intent intent =
                new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(this);
        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE2);
    }

    @OnClick({R.id.calculation_date})
    public void datePressed() {
        dpDialog.show();
    }

    @OnClick(R.id.fab)
    public void FABPressed() {
        Intent result = new Intent();
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        currentLocation.calculationDate = calculationDate;
        currentLocation._key = fmt.print(calculationDate);
        // add more code to initialize the rest of the fields
        Parcelable parcel = Parcels.wrap(currentLocation);
        result.putExtra("Location", parcel);
        setResult(MainActivity.NEW_LOCATION_REQUEST, result);
        finish();
    }

    private String formatted(DateTime d) {
        return d.monthOfYear().getAsShortText(Locale.getDefault()) + " " +
                d.getDayOfMonth() + ", " + d.getYear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place pl = Autocomplete.getPlaceFromIntent(data);
                currentLocation.origLat = pl.getLatLng().latitude;
                currentLocation.origLong = pl.getLatLng().longitude;
                location1.setText(pl.getName());

                Log.i(TAG, "onActivityResult: " + pl.getName() + "/" + pl.getAddress());

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status stat = Autocomplete.getStatusFromIntent(data);
                Log.d(TAG, "onActivityResult: ");
            } else if (requestCode == RESULT_CANCELED) {
                System.out.println("Cancelled by the user");
            }
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE2) {
            if (resultCode == RESULT_OK) {
                Place pl = Autocomplete.getPlaceFromIntent(data);
                currentLocation.endLat = pl.getLatLng().latitude;
                currentLocation.endLong = pl.getLatLng().longitude;
                location2.setText(pl.getName());

                Log.i(TAG, "onActivityResult: " + pl.getName() + "/" + pl.getAddress());

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status stat = Autocomplete.getStatusFromIntent(data);
                Log.d(TAG, "onActivityResult: ");
            } else if (requestCode == RESULT_CANCELED) {
                System.out.println("Cancelled by the user");
            }
        }else
            super.onActivityResult(requestCode, resultCode, data);
    }

//    @Override
//    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
//
//    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calculationDate = new DateTime(year, month + 1, dayOfMonth, 0, 0);
        calculationDateView.setText(formatted(calculationDate));
    }
}