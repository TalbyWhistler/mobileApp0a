package ca.mohawk.testo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {
    public static String phoneAddress;
    public static String phoneName;
    public static String desktopAddress;

    public static MainActivity context;
    public static ArrayList<ListItem> messagesReceived = new ArrayList<>();

    public static final String SHARED_PREF = "filmfone_data";
    public static final String DESKTOP_MANAGER_ADDRESS = "desktop_manager_address";
    public static final String PHONE_NAME = "phone_name";

    public static HttpServerThread server;
    public static boolean firstRun = true;

    private class HttpServerThread extends Thread {
        ServerSocket httpServerSocket;
        final static int PORT = 8888;

        @Override
        public void run() {
            Socket socket = null;

            try {
                httpServerSocket = new ServerSocket(PORT);

                while (true) {
                    socket = httpServerSocket.accept();

                    HttpResponseThread httpResponseThread =
                            new HttpResponseThread(socket);
                    httpResponseThread.start();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public class HttpResponseThread extends Thread {
        Socket socket;

        HttpResponseThread(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {
            BufferedReader is;
            PrintWriter os;
            String request;

            try {
                is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                request = is.readLine();

                while (request != null && !request.contains("Content-Length")) {
                    request = is.readLine();
                    Log.d(tag, "run: " + request);
                }

                int contentLength;

                try {
                    assert request != null;
                    contentLength = Integer.parseInt(request.split(":")[1]
                            .substring(1));
                } catch (NumberFormatException e) {
                    Log.d(TAG, "run: content length: " + e.getMessage());
                    return;
                }

                if (contentLength <= 0) {
                    Log.d(TAG, "run: no content");
                    return;
                }

                char[] buf = new char[contentLength + 2];
                is.read(buf, 0, contentLength + 2);

                String body = new String(buf);
                Log.d(tag, "run body: " + body);

                JSONObject jsonObject = new JSONObject(body);
                String extractedMessage = jsonObject.getString("message");
                String extractedName = jsonObject.getString("sender");

                MainActivity.this.runOnUiThread(() ->
                    appendMessage(extractedMessage, extractedName)
                );

                os = new PrintWriter(socket.getOutputStream(), true);

                String response = "{\"status\": \"Message received\"}";

                os.print("HTTP/1.1 200" + "\r\n");
                os.print("Content-Type: text/json" + "\r\n");
                os.print("Content-Length: " + response.length() + "\r\n");
                os.print("\r\n");
                os.print(response + "\r\n");
                os.close();
            } catch (IOException e) {
                Log.d(TAG, "run: io: " + e.getMessage());
            } catch (JSONException e) {
                Log.d(TAG, "run: json: " + e.getMessage());
                throw new RuntimeException(e);
            } finally {
                try {
                    if (socket != null) {
                        Log.d(TAG, "run: socket close");
                        socket.close();
                    }
                } catch (IOException e) {
                    Log.d(TAG, "run: io after request: " + e.getMessage());
                }
            }
        }
    }

    private void appendMessage(String extractedMessage, String extractedName) {
        // add body to list
        // check if the sender already exists, if they do add message to their list,
        // if they don't make new sender
        int index = 0;
        boolean senderExists = false;
        for (ListItem item : messagesReceived) {
            if (item.getSender().equalsIgnoreCase(extractedName)) {
                index = messagesReceived.indexOf(item);
                messagesReceived.get(index).addMessage(1,extractedMessage);
                senderExists = true;
            }
        }
        if (!senderExists) {
            ListItem li = new ListItem(extractedName, extractedMessage);
            messagesReceived.add(li);
        }

        MainActivity2.setMessagesAdapter();
        MainActivity3.setMessagesAdapter(index);
    }

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        Log.d(tag, "getIpAddress: " + inetAddress.getHostAddress());
                        ip = inetAddress.getHostAddress();
                        if (ip.contains(":")) {
                            ip = "localhost";
                        }

                        Log.d(TAG, "getIpAddress: ip: " + ip);
                        return ip;
                    }

                }

            }
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }

    public void firstConnect(View view) {
        EditText nameEt = findViewById(R.id.phoneNameEditText);
        String name = nameEt.getText().toString();

        if (name.isBlank() || desktopAddress.isBlank()) {
            Log.d(TAG, "firstConnect: name: " + name);
            Log.d(TAG, "firstConnect: desktopAddress: " + desktopAddress);
            Log.d(TAG, "firstConnect: missing name and/or address");
            return;
        }

        SharedPreferences sp = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PHONE_NAME, name);
        editor.apply();

        phoneName = name;
        connect();
    }

    private void connect() {
        JSONObject json = new JSONObject();
        try {
            Log.d(TAG, "connect: name: " + phoneName);
            json.put("name", phoneName);
            json.put("number", "-");
            json.put("address", phoneAddress);
        } catch (JSONException e) {
            Log.d(TAG, "connect: " + e.getMessage());
            return;
        }

        Log.d(TAG, "connect: pre thread");

        Thread t = new Thread(() -> {
            try {
                Log.d(TAG, "connect: ");
                URL url = new URL(desktopAddress + "/connect");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                Log.d(TAG, "connect: open");
                connection.setDoOutput(true);
                connection.setRequestProperty("Accept-Charset", "UTF-8");
                connection.setRequestProperty("Content-type", "application/json");

                // Write to the connection
                OutputStream output = connection.getOutputStream();
                Log.d(TAG, "connect: outputStream");
                output.write(json.toString().getBytes(StandardCharsets.UTF_8));
                output.close();
                Log.d(TAG, "connect: close");

                int status = connection.getResponseCode();
                Log.d(TAG, "run: " + status);
            } catch (MalformedURLException e) {
                Log.d(TAG, "connect: m: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "connect: io: " + e.getMessage());
            } catch (Exception e) {
                Log.d(TAG, "connect: other: " + e.getMessage());
            }

            MainActivity.this.runOnUiThread(() -> {
                EditText et = findViewById(R.id.phoneNameEditText);
                Button connectButton = findViewById(R.id.connectButton);

                et.setVisibility(View.GONE);
                connectButton.setVisibility(View.GONE);
            });

            Log.d(TAG, "run: finish");
        });

        t.start();
    }

    protected void onInit() {
        if (!firstRun) return;
        firstRun = false;
        Log.d(TAG, "onInit");

        SharedPreferences sp = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);

        phoneName = sp.getString(PHONE_NAME, "");
        desktopAddress = sp.getString(DESKTOP_MANAGER_ADDRESS, "");

        server = new HttpServerThread();
        server.start();

        phoneAddress = getIpAddress() + ":" + HttpServerThread.PORT;
        if (desktopAddress.isEmpty()) {
            Log.d(TAG, "onCreate: desktop address empty");
            return;
        }

        if (!phoneName.isBlank()) {
            connect();
        }
    }

    String tag = "===sessionLogging===";
    String TAG = tag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        onInit();

        // Web link with desktop address
        Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null && desktopAddress.isEmpty()) {
            SharedPreferences sp = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            Log.d(TAG, "onInit with data: " + data);
            SharedPreferences.Editor editor = sp.edit();
            String address = data.getQueryParameter("address");
            if (address == null) {
                throw new IllegalStateException("Invalid navigation uri");
            }

            Log.d(TAG, "onInit: query" + address);
            editor.putString(DESKTOP_MANAGER_ADDRESS, address);
            editor.apply();
            desktopAddress = address;
        }

        EditText et = findViewById(R.id.phoneNameEditText);
        Button connectButton = findViewById(R.id.connectButton);
        if (phoneName != null && !phoneName.isBlank()) {
            et.setVisibility(View.GONE);
            connectButton.setVisibility(View.GONE);
        }

        context = this;

        Log.d(tag,"onCreate");
    }

    public void switchToMessages(View view)
    {
        Log.d(tag,"Switch to messages");
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);
    }
}