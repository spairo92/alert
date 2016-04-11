package com.example.spairo.myalert;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements LocationListener {

    private LocationManager lm1;
    SQLiteDatabase db;
    String longitude, latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lm1 = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //create the database
        db=openOrCreateDatabase("AlarmNumbers", Context.MODE_PRIVATE, null);
        //create a table and save contacts and mobile numbers
        db.execSQL("CREATE TABLE IF NOT EXISTS phone_numbers(name VARCHAR, mobile VARCHAR);");
        db.execSQL("DELETE FROM phone_numbers");
        db.execSQL("INSERT INTO phone_numbers VALUES('spyro','00306980434908');");
        //create table to save locations
        db.execSQL("CREATE TABLE IF NOT EXISTS locations(longitude VARCHAR, latitude VARCHAR);");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        longitude = String.valueOf(location.getLongitude());
        latitude = String.valueOf(location.getLatitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    //---------------------------------------------------------------------------------------------
    public void showmobiles(View v) //responsible for button MOB and shows contacts in the table
    {
        Cursor c=db.rawQuery("SELECT * FROM phone_numbers", null);
        if(c.getCount()==0) //if table is empty returns message 'empty database'
        {
            Toast.makeText(getApplicationContext(), "empty database" , Toast.LENGTH_LONG).show();
            return;
        }
        StringBuffer buffer=new StringBuffer();
        while(c.moveToNext())   //loops inside the table and returns all records
        {
            buffer.append("Name: "+c.getString(0)+"\n");
            buffer.append("Number: "+c.getString(1)+"\n");
        }
        Toast.makeText(getApplicationContext(), buffer.toString() , Toast.LENGTH_LONG).show();
    }
    public void showlocations(View v)   //responsible for button LOC and shows all saved locations
    {
        Cursor c=db.rawQuery("SELECT * FROM locations", null);
        if(c.getCount()==0) //if table is empty returns message 'empty database'
        {
            Toast.makeText(getApplicationContext(), "empty database" , Toast.LENGTH_LONG).show();
            return;
        }
        StringBuffer buffer=new StringBuffer();
        while(c.moveToNext())   //loops inside the table and returns all saved locations
        {
            buffer.append("longitude: "+c.getString(0)+"\n");
            buffer.append("latitude: "+c.getString(1)+"\n");
        }
        Toast.makeText(getApplicationContext(), buffer.toString() , Toast.LENGTH_LONG).show();
    }
    public void savelocation(View v)    //connected with button SAVE
    {
        //activates GPS
        lm1.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        //inserts inside the table the coordinates it gets
        db.execSQL("INSERT INTO locations VALUES('"+longitude+"','"+latitude+"');");
        Toast.makeText(getApplicationContext(), "Location coordinates saved!", Toast.LENGTH_LONG).show();
    }
    public void deletelocations(View v) //connected with button DELETE and empties the table locations
    {
        db.execSQL("DELETE FROM locations");
        Toast.makeText(getApplicationContext(), "Location records deleted!", Toast.LENGTH_LONG).show();
    }
    public void abortmessage(View v)    //connected with button ABORT
    {
        String messageToSend = "False alarm. Everything is fine";   //text is going to be send by message
        //select contacts to whom send message
        Cursor c = db.rawQuery("SELECT * FROM phone_numbers", null);
        while(c.moveToNext()) {
            String number = c.getString(1);
            SmsManager.getDefault().sendTextMessage(number, null, messageToSend, null, null);   //sends message
        }
        Toast.makeText(getApplicationContext(), "Abort message sent", Toast.LENGTH_LONG).show();
    }
    public void help(View v)    //connected with the main button HELP
    {
        lm1.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);   //activate GPS
        String messageToSend = "SOS! My coordinates are, longitude: "+longitude+" and latitude: "
                +latitude+" and I need immediate help!";   //text is going to be send by msg
        //select contacts to whom send message
        Cursor c = db.rawQuery("SELECT * FROM phone_numbers", null);
        while(c.moveToNext()) {
            String number = c.getString(1);
            SmsManager.getDefault().sendTextMessage(number, null, messageToSend, null, null);   //send message
        }
        Toast.makeText(getApplicationContext(), "SOS message sent", Toast.LENGTH_LONG).show();
    }
}
