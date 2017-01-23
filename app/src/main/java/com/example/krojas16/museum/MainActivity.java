package com.example.krojas16.museum;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.Utils;

import java.util.List;
import java.util.UUID;

public class MainActivity extends Activity {

    private BeaconManager museumBeaconManager;
    private Region exhibitionRegion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        museumBeaconManager = new BeaconManager(this);
        exhibitionRegion = new Region("The Hall of Biodiversity", UUID.fromString("D0D3FA86-CA76-45EC-9BD9-6AF42E29F23E"), null, null);


        Button start = (Button) findViewById(R.id.button_start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                museumBeaconManager.setRangingListener(new BeaconManager.RangingListener() {
                    @Override
                    public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
                        if(!beacons.isEmpty()){
                            Beacon beaconInRange = beacons.get(0);
                            double proximity = Utils.computeAccuracy(beaconInRange);
                            String distance = Utils.proximityFromAccuracy(proximity).toString();
                            Log.d("DISTANCE to BEACON: ", distance);

                            String location = "";
                            if(distance.equals("NEAR")){
                                    location = "YOU ARE ABOUT TO ENTER THE HALL OF BIODIVERSITY";
                                    showDialog(location);
                            }else if (distance.equals("IMMEDIATE")){
                                setContentView(R.layout.hall_biodiversity);
                                TextView description = (TextView)findViewById(R.id.description);
                                description.setText("The Hall of Biodiversity presents a vivid portrait of the beauty and abundance of life on Earth, highlighting both biodiversity and the factors that threaten it.\n" +
                                        "Ecological biodiversity is illustrated by a 2,500-square-foot walk-through diorama that depicts part of the Dzanga-Sangha rain forest, one of Earth’s most diverse ecosystems. Featuring more than 160 species of flora and fauna, the diorama uses video and sound to re-create the ecosystem at dawn, at an elephant clearing, and degraded by human intervention along a road.\n" +
                                        "The hall’s Spectrum of Life exhibit showcases the diversity of life resulting from 3.5 billion years of evolution. More than 1,500 specimens and models, from microorganisms to terrestrial and aquatic giants, are organized into 28 groups along the 100-foot-long installation.\n" +
                                        "Underscoring threats to biodiversity, a timeline of the five previous mass extinctions includes examples of species lost. A nearby display case features examples of extinct and threatened species, including the long-extinct Dodo bird and the threatened Siberian tiger. A multi-screen video installation provides a tour of nine ecosystems and explores perils to preservation, and a regularly updated BioBulletin video features the latest in biodiversity research.");
                                museumBeaconManager.stopRanging(exhibitionRegion);
                            }

                        }
                    }
                });

                startRanging();

            }
        });

    }

    private void startRanging(){

        museumBeaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                museumBeaconManager.startRanging(exhibitionRegion);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //museumBeaconManager.stopRanging(exhibitionRegion);

    }

    public void showDialog(String location){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Museum of Natural History");
        alertDialog.setMessage(location);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        startRanging();
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
        museumBeaconManager.stopRanging(exhibitionRegion);
    }


}


