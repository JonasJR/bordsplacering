package se.mah.m11k5638.p2;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Jonas on 07/10/15.
 */
public class ServerConnection {
    private ThreadManager threadManager;
    private MainActivity mainActivity;
    private Socket socket;
    private InetAddress address;
    private int port;
    private String ip;
    private InputStream is;
    private DataInputStream dis;
    private OutputStream os;
    private DataOutputStream dos;
    private Recieve recieve;

    public String getId() {
        return id;
    }

    private String id;

    public ServerConnection(String ip, int port, MainActivity mainActivity) {
        this.ip = ip;
        this.port = port;
        this.mainActivity = mainActivity;
        threadManager = new ThreadManager();
    }

    public void connect() {
        threadManager.start();
        threadManager.execute(new Connection());
    }

    public void disconnect() {
        threadManager.execute(new Disconnect());
    }

    public void send(String arg) {
        threadManager.execute(new Send(arg));
    }

    public class Connection implements Runnable{

        @Override
        public void run() {
            try {
                address = InetAddress.getByName(ip);
                socket = new Socket(address, port);
                is = socket.getInputStream();
                dis = new DataInputStream(is);
                os = socket.getOutputStream();
                dos = new DataOutputStream(os);
                recieve = new Recieve();
                recieve.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class Disconnect implements Runnable {
        @Override
        public void run() {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class Send implements Runnable {

        String message;

        public Send(String message){
            this.message = message;
        }
        @Override
        public void run() {
            try {
                dos.writeUTF(message);
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class Recieve extends Thread {
        @Override
        public void run() {
            String arg;
            try {
                while(recieve != null){
                    arg = (String)dis.readUTF();
                    resolveMessage(arg);
                }
            } catch (IOException e) {
                recieve = null;
                e.printStackTrace();
            }
        }
    }

    public void resolveMessage(final String response){
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String message = response;
                try {
                    JSONObject jObj = new JSONObject(message);
                    String type = jObj.getString("type");

                    if (type.equals("groups")) {
                        recevieGroups(jObj);
                    } else if (type.equals("locations")) {
                        receiveLocations(jObj);
                    } else if (type.equals("members")) {
                        receiveMembers(jObj);
                    } else if (type.equals("register")) {
                        recieveRegistration(jObj);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void receiveMembers(JSONObject jObj) throws JSONException {
        JSONArray jArray = jObj.getJSONArray("members");
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < jArray.length(); i++) {
            String n;

            JSONObject jRealObject = jArray.getJSONObject(i);
            n = jRealObject.getString("member");

            list.add(n);
        }
        Toast.makeText(mainActivity,list.toString(),Toast.LENGTH_SHORT).show();
    }

    private void recieveRegistration(JSONObject jObj) throws JSONException {
        id = jObj.getString("id");
    }

    private void receiveLocations(JSONObject jObj) throws JSONException {
        JSONArray jArray = jObj.getJSONArray("location");
        ArrayList<Member> memberList = new ArrayList<>();
        for (int i = 0; i < jArray.length(); i++) {
            JSONObject jRealObject = jArray.getJSONObject(i);
            Member member = new Member(jRealObject.getString("member"), Double.parseDouble(jRealObject.getString("longitude")), Double.parseDouble(jRealObject.getString("latitude")));
            memberList.add(member);
        }
        mainActivity.updateMemberList(memberList);
    }

    private void recevieGroups(JSONObject jObj) throws JSONException {
        JSONArray jArray = jObj.getJSONArray("groups");
        ArrayList<String> groupList = new ArrayList<>();
        for (int i = 0; i < jArray.length(); i++) {
            JSONObject jRealObject = jArray.getJSONObject(i);
            groupList.add((jRealObject.getString("group")));
        }
        mainActivity.updateGroupList(groupList);
    }
}
