package com.example.womenssafetyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity{
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    TextView add,call,trig;
    DatabaseHelper myDB;
    double latitude,longitude;
    private FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), AddContact.class);
                startActivity(i);
            }
        });
        call = findViewById(R.id.call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), helpline.class);
                startActivity(i);
            }
        });
        trig=findViewById(R.id.trig);
        trig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), trig.class);
                startActivity(i);
            }
        });
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED )
            {
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location!=null )
                        {
                            latitude=location.getLatitude();
                            longitude=location.getLongitude();
                        }
                    }
                });
            }
        }

        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, new LocationListener() {
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}
            @Override
            public void onProviderEnabled(String s) {}
            @Override
            public void onProviderDisabled(String s) {}
            @Override
            public void onLocationChanged(final Location location) {}
        });
        Location myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        double longitude = myLocation.getLongitude();
        double latitude = myLocation.getLatitude();
    }
    public void btn_send(View view)
    {
        int permissioncheck= ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if(permissioncheck== PackageManager.PERMISSION_GRANTED)
        {
            MyMessage();
        }
        else
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},0);
        }
    }
    public void MyMessage() {
        try {
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            PendingIntent pi= PendingIntent.getActivity(getApplicationContext(), 0, intent,0);
            int phoneNumber;
            String phn;
            myDB = new DatabaseHelper(this);
            Cursor data= myDB.getListContents();
            if(data.getCount() == 0){
                Toast.makeText(this, "There are no contents in this list!",Toast.LENGTH_LONG).show();
            }
            else{
                while(data.moveToNext()){
                    {
                      // phn = objCursor.getString(1);
                         phn=data.getString(1);
                        String msg = "I'm in Trouble. Please help me"+"  "+"http://www.google.com/maps/place/"+latitude+","+longitude;
                            SmsManager smsmanager = SmsManager.getDefault();
                            smsmanager.sendTextMessage(phn, null, msg, pi, null);

                            Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        catch(Exception e)
        {
            Toast.makeText(this,"Show values from Db:", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        int action, keycode,count=0,count1=0;

        action = event.getAction();
        keycode = event.getKeyCode();

        switch (keycode)
        {
            case KeyEvent.KEYCODE_VOLUME_UP:
                {
                if(KeyEvent.ACTION_UP == action){
                    count=1;
                  MyMessage();
                }
            }
            case KeyEvent.KEYCODE_POWER:
                if(KeyEvent.ACTION_DOWN == action){
                    count1=1;
                    MyMessage();
                }
        }
      //  if(count==1&&count1==1)
        //    MyMessage();
        return super.dispatchKeyEvent(event);
    }

}
