package com.example.cis657_hw4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.cis657_hw4.webservice.WeatherService;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static com.example.cis657_hw4.webservice.WeatherService.BROADCAST_ELEMENT;

public class MainActivity extends AppCompatActivity {

    public static final int DIST_UNIT = 1;
    public static int HISTORY_RESULT = 2;
    public static final int NEW_LOCATION_REQUEST = 3;

    Boolean begin = true;
    Location loc1 = new Location("GPS");
    Location loc2 = new Location("GPS");

    EditText lat1;
    EditText long1;
    EditText lat2;
    EditText long2;

    EditText[] inputarray = new EditText[4];
    String lat1str, lat2str, long1str, long2str;

    TextView distanceresult;
    TextView bearingresult;
    private TextView lat1_temp = null;
    private TextView lat1_sum = null;
    private TextView lat2_temp = null;
    private TextView lat2_sum = null;

    Double distance;
    Double bearing;

    String DistUnit = "kilometers";
    String BearUnit = "degrees";

    Button CalculateButton;
    Button ClearButton;
    Button SearchButton;

    private ImageView lat1_icon = null;
    private ImageView lat2_icon = null;

    DatabaseReference topRef;
    public static List<LocationLookup> allHistory;

    int count =0;
//onCreate starts up the application
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setting view to a coordinatorlayout that includes the activity_main constraint layout
        setContentView(R.layout.activity_main_coordinatorlayout);

        lat1 = (EditText) findViewById(R.id.lat1);
        long1 = (EditText) findViewById(R.id.long1);
        lat2 = (EditText) findViewById(R.id.lat2);
        long2 = (EditText) findViewById(R.id.long2);
        lat1_temp = (TextView) findViewById(R.id.Lat1_temp);
        lat1_sum = (TextView) findViewById(R.id.Lat1_des);
        lat2_temp = (TextView) findViewById(R.id.Lat2_temp);
        lat2_sum = (TextView) findViewById(R.id.Lat2_des);
        lat1_icon = (ImageView) findViewById(R.id.Lat1_image);
        lat2_icon = (ImageView) findViewById(R.id.Lat2_image);


        distanceresult = (TextView) findViewById(R.id.distanceText);
        bearingresult = (TextView) findViewById(R.id.BearingText);

        CalculateButton = (Button) findViewById(R.id.CalculateButton);
        ClearButton = (Button) findViewById(R.id.ClearButton);
        SearchButton = (Button) findViewById(R.id.searchbutton);

        //Sets an action listener for the button that gives instructions for post-button push
        CalculateButton.setOnClickListener(v -> {
            lat1.onEditorAction(EditorInfo.IME_ACTION_DONE);
            lat2.onEditorAction(EditorInfo.IME_ACTION_DONE);
            long1.onEditorAction(EditorInfo.IME_ACTION_DONE);
            long2.onEditorAction(EditorInfo.IME_ACTION_DONE);

            inputToString();

            //Informs the user of incomplete data using a "snackbar" (pops up on bottom of screen)
            if ((lat1str.length() == 0) || (lat2str.length() == 0) ||
                    (long1str.length() == 0) || (long2str.length() == 0)) {
                Snackbar.make(CalculateButton, "One or more of the text fields is incomplete",
                        Snackbar.LENGTH_LONG).show();
            }

            else {
                LocationLookup entry = new LocationLookup();
                entry.setOrigLat(Double.parseDouble(lat1str));
                entry.setOrigLong(Double.parseDouble(long1str));
                entry.setEndLat(Double.parseDouble(lat2str));
                entry.setEndLong(Double.parseDouble(long2str));
                DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
//                entry.setCalculationDate(DateTime.now());
                entry.set_key(fmt.print(DateTime.now()));
                topRef.push().setValue(entry);
                calcDistance();
            }

            WeatherService.startGetWeather(this, lat1str, long1str, "p1");
            WeatherService.startGetWeather(this, lat2str, long2str, "p2");

        });

        ClearButton.setOnClickListener(y -> {
            lat1.setText("");
            lat2.setText("");
            long1.setText("");
            long2.setText("");
            distanceresult.setText("Distance: ");
            bearingresult.setText("Bearing: ");
            lat1.onEditorAction(EditorInfo.IME_ACTION_DONE);
            lat2.onEditorAction(EditorInfo.IME_ACTION_DONE);
            long1.onEditorAction(EditorInfo.IME_ACTION_DONE);
            long2.onEditorAction(EditorInfo.IME_ACTION_DONE);
            setWeatherViews(View.INVISIBLE);

        });

        SearchButton.setOnClickListener(y -> {
            Intent newLocation = new Intent(MainActivity.this, LocationSearchActivity.class);
            startActivityForResult(newLocation, NEW_LOCATION_REQUEST);
        });

        Places.initialize(getApplicationContext(),"PLACE API HERE!");
        allHistory = new ArrayList<LocationLookup>();
    }

    private void setWeatherViews(int visible) {
        lat1_icon.setVisibility(visible);
        lat2_icon.setVisibility(visible);
        lat1_temp.setVisibility(visible);
        lat2_temp.setVisibility(visible);
        lat1_sum.setVisibility(visible);
        lat2_sum.setVisibility(visible);
    }

    private BroadcastReceiver weatherReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Main Activity", "onReceive: " + intent);
            Bundle bundle = intent.getExtras();
            double temp = bundle.getDouble("TEMPERATURE");
            String summary = bundle.getString("SUMMARY");
            String icon = bundle.getString("ICON").replaceAll("-", "_");
            String key = bundle.getString("KEY");
            int resID = getResources().getIdentifier(icon , "drawable", getPackageName());
            setWeatherViews(View.VISIBLE);
            if (key.equals("p1"))  {
                lat1_sum.setText(summary);
                lat1_temp.setText(Double.toString(temp));
                lat1_icon.setImageResource(resID);
                lat1_icon.setVisibility(View.VISIBLE);
            } else {
                lat2_sum.setText(summary);
                lat2_temp.setText(Double.toString(temp));
                lat2_icon.setImageResource(resID);
            }
        }
    };



    @Override
    public void onResume(){
        super.onResume();
        allHistory.clear();
        topRef = FirebaseDatabase.getInstance().getReference();
        topRef.addChildEventListener (chEvListener);
        //topRef.addValueEventListener(valEvListener);
        IntentFilter weatherFilter = new IntentFilter(BROADCAST_ELEMENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(weatherReceiver, weatherFilter);
        setWeatherViews(View.INVISIBLE);
    }

    @Override
    public void onPause(){
        super.onPause();
        topRef.removeEventListener(chEvListener);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(weatherReceiver);
    }


    //Creates a menu in the coordinator layout
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //This function is triggered when a menu item is selected, and operates accordingly
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.action_settings) {

            inputToString();

            //New intent is made to move open a new screen (activity) for settings screen
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);

            //The ".putsExtra" command stores information that is transferred to the new activity
            intent.putExtra("currdistunit",DistUnit);
            intent.putExtra("currbearunit",BearUnit);
            intent.putExtra("lat1",lat1str);
            intent.putExtra("lat2",lat2str);
            intent.putExtra("long1",long1str);
            intent.putExtra("long2",long2str);

            //This function is used when the current activity anticipates a result from the newly
            //opened activity to be returned.
            startActivityForResult(intent,DIST_UNIT);
            return true;
        }
        else if(item.getItemId() == R.id.action_history) {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivityForResult(intent, HISTORY_RESULT );
            return true;
        }
        return false;
    }


    //If a result is returned, this function is activated and stores the returned data
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if(resultCode == DIST_UNIT) {
            DistUnit = data.getStringExtra("distunits");
            BearUnit = data.getStringExtra("bearunits");

            lat1.setText(data.getStringExtra("lat1"));
            lat2.setText(data.getStringExtra("lat2"));
            long1.setText(data.getStringExtra("long1"));
            long2.setText(data.getStringExtra("long2"));
        }
        else if (resultCode == HISTORY_RESULT) {
//            String[] vals = data.getStringArrayExtra("item");
//////            this.lat1.setText(vals[0]);
//////            this.long1.setText(vals[1]);
//////            this.lat2.setText(vals[2]);
//////            this.long2.setText(vals[3]);
//////            this.calcDistance();  // code that updates the calcs.

            Parcelable parcel = data.getParcelableExtra("item");
            LocationLookup loc = Parcels.unwrap(parcel);
            Log.d("MainActivity","Retrieved: (" + loc.origLat +","+ loc.origLong +","
                    + loc.endLat +","+ loc.endLong +")");

            this.lat1.setText(""+loc.origLat);
            this.long1.setText(""+loc.origLong);
            this.lat2.setText(""+loc.endLat);
            this.long2.setText(""+loc.endLong);
            lat1str=""+loc.origLat;
            long1str=""+loc.origLong;
            lat2str=""+loc.endLat;
            long2str=""+loc.endLong;
//            LocationLookup entry = new LocationLookup();
//            entry.setOrigLat(Double.parseDouble(lat1str));
//            entry.setOrigLong(Double.parseDouble(long1str));
//            entry.setEndLat(Double.parseDouble(lat2str));
//            entry.setEndLong(Double.parseDouble(long2str));
//            DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
//            entry.set_key(fmt.print(DateTime.now()));
//            topRef.push().setValue(entry);
            this.calcDistance();  // code that updates the calcs.
        }
        else if (resultCode == NEW_LOCATION_REQUEST) {
            if (data != null && data.hasExtra("Location")){
                Parcelable parcel = data.getParcelableExtra("Location");
                LocationLookup loc = Parcels.unwrap(parcel);
                Log.d("MainActivity","New Location: (" + loc.origLat +","+ loc.origLong +","
                        + loc.endLat +","+ loc.endLong +")");

                this.lat1.setText(""+loc.origLat);
                this.long1.setText(""+loc.origLong);
                this.lat2.setText(""+loc.endLat);
                this.long2.setText(""+loc.endLong);
                lat1str=""+loc.origLat;
                long1str=""+loc.origLong;
                lat2str=""+loc.endLat;
                long2str=""+loc.endLong;
                LocationLookup entry = new LocationLookup();
                entry.setOrigLat(Double.parseDouble(lat1str));
                entry.setOrigLong(Double.parseDouble(long1str));
                entry.setEndLat(Double.parseDouble(lat2str));
                entry.setEndLong(Double.parseDouble(long2str));
//                entry.setCalculationDate(loc.calculationDate);
                DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
                entry.set_key(fmt.print(loc.calculationDate));
                topRef.push().setValue(entry);
                this.calcDistance();  // code that updates the calcs.
            }
            else
                super.onActivityResult(requestCode, resultCode, data);
        }


        inputToString();

        //recalculates the distance/bearing information with updated units, if applicable
        if ((lat1str.length() != 0) && (lat2str.length() != 0) &&
                (long1str.length() != 0) && (long2str.length() != 0)) {
            calcDistance();
        }
    }


    void calcDistance(){
        loc1.setLatitude(Double.parseDouble(lat1str));
        loc1.setLongitude(Double.parseDouble(long1str));
        loc2.setLatitude(Double.parseDouble(lat2str));
        loc2.setLongitude(Double.parseDouble(long2str));

        BigDecimal BDdist = new BigDecimal(Double.toString((loc1.distanceTo(loc2)/1000)));

        if(DistUnit.equals("miles"))
            BDdist = new BigDecimal(Double.toString(((loc1.distanceTo(loc2)/1000)*.621371)));

        BDdist = BDdist.setScale(2, RoundingMode.HALF_UP);
        distance = BDdist.doubleValue();

        BigDecimal BDbear = new BigDecimal(Double.toString(loc1.bearingTo(loc2)));

        if(BearUnit.equals("mils"))
            BDbear = new BigDecimal(Double.toString(loc1.bearingTo(loc2)*17.77777));

        BDbear = BDbear.setScale(2, RoundingMode.HALF_UP);
        bearing = BDbear.doubleValue();




       distanceresult.setText("Distance: "+distance+" "+DistUnit);
       bearingresult.setText("Bearing: "+bearing+" "+BearUnit);
    }

    void inputToString(){

        lat1str = lat1.getText().toString();
        lat2str = lat2.getText().toString();
        long1str = long1.getText().toString();
        long2str = long2.getText().toString();
    }

    private ChildEventListener chEvListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            LocationLookup entry = (LocationLookup) dataSnapshot.getValue(LocationLookup.class);
            //entry._key = dataSnapshot.getKey();
            allHistory.add(entry);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            LocationLookup entry = (LocationLookup) dataSnapshot.getValue(LocationLookup.class);
            List<LocationLookup> newHistory = new ArrayList<LocationLookup>();
            for (LocationLookup t : allHistory) {
                if (!t._key.equals(dataSnapshot.getKey())) {
                    newHistory.add(t);
                }
            }
            allHistory = newHistory;
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


}
