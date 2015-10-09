package se.mah.m11k5638.p2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button addGroupBtn, openMapBtn;
    private EditText groupNameEdt;
    private ListView groupLv;
    private MapsActivity mapsActivity;
    private String ip = "195.178.232.7";
    private int port = 7117;
    private ServerConnection connection;
    private SharedPreferences sharedPreferences;
    private String userName;
    private ArrayList<Member> memberList;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeSystem();
        initiateConnection();
        initializeListeners();
        getGroups();
    }


    private void initiateConnection() {
        connection = new ServerConnection(ip, port, this);
        connection.connect();
    }

    private void initializeListeners() {
        addGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!groupNameEdt.getText().toString().equals("")){
                    createGroup(groupNameEdt.getText().toString(), userName);
                } else{
                    Toast.makeText(MainActivity.this, getText(R.string.must_enter_name),Toast.LENGTH_SHORT).show();
                }
            }
        });

        openMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                sendLocation(longitude, latitude);
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
        };
    }

    private void initializeSystem() {
        sharedPreferences = this.getSharedPreferences("P2", Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("Name", null);
        if(userName == null){
            userInfo();
        }
        mapsActivity = new MapsActivity();
        addGroupBtn = (Button)findViewById(R.id.addGroupBtn);
        openMapBtn = (Button)findViewById(R.id.openMapBtn);
        groupNameEdt = (EditText)findViewById(R.id.groupNameEdt);
        groupLv = (ListView)findViewById(R.id.groupList);
    }

    private void userInfo() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LinearLayout linearLayout = new LinearLayout(this);
        final EditText nameInput = new EditText(this);
        nameInput.setHint(getString(R.string.name));
        linearLayout.addView(nameInput);
        dialog.setView(linearLayout);

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPreferences = getSharedPreferences("P2", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Name", nameInput.getText().toString());
                editor.apply();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    public void openMap(){
        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);
    }

    public void sendLocation(double longitude, double latitude){
        String id = connection.getId();
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(stringWriter);
        try {
            writer.beginObject().name("type").value("location").name("id").value(id).name("longitude").value(String.valueOf(longitude)).name("latitude").value(String.valueOf(latitude)).endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.send(stringWriter.toString());
        Toast.makeText(this, stringWriter.toString(), Toast.LENGTH_SHORT).show();
    }

    public void getMembers(String group){
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(stringWriter);
        try {
            writer.beginObject().name("type").value("members").name("group").value(group).endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.send(stringWriter.toString());
    }

    public void getGroups(){
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(stringWriter);
        try {
            writer.beginObject().name("type").value("groups").endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.send(stringWriter.toString());
    }

    public void createGroup(String groupName, String userName){
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(stringWriter);
        try {
            writer.beginObject().name("type").value("register").name("group").value(groupName).name("member").value(userName).endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.send(stringWriter.toString());
        getGroups();
    }

    public void updateMemberList(ArrayList<Member> memberList){
        this.memberList = memberList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connection.disconnect();
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

    public void updateGroupList(ArrayList<String> groupList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, groupList);
        groupLv.setAdapter(adapter);
        groupLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                createGroup(parent.getItemAtPosition(position).toString(), userName);
                getMembers(parent.getItemAtPosition(position).toString());
            }
        });
    }
}
