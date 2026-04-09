package ca.mohawk.testo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    public static String desktopAddress;

    public static MainActivity context;
    public static ArrayList<ListItem> messagesReceived = new ArrayList<>();

    public static final String SHARED_PREF = "filmfone_data";
    public static final String DESKTOP_MANAGER_ADDRESS = "desktop_manager_address";

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

                while(true){
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
        String h1;

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

                int contentLength = -1;
                try {
                    contentLength = Integer.parseInt(request.split(":")[1]
                            .substring(1));
                } catch (NumberFormatException e) {

                }
                if (contentLength <= 0) {
                    return;
                }

                char[] buf = new char[contentLength + 2];
                is.read(buf, 0, contentLength + 2);

                String body = new String(buf);
                Log.d(tag, "run body: " + body);

                JSONObject jsonObject = new JSONObject(body);
                String extractedMessage = jsonObject.getString("message");
                String extractedName = jsonObject.getString("sender");

                MainActivity.this.runOnUiThread(() -> {
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
                });

                os = new PrintWriter(socket.getOutputStream(), true);

                String response =
                        "<html><head></head>" +
                                "<body>" +
                                "<h1>" + h1 + "</h1>" +
                                "</body></html>";

                os.print("HTTP/1.1 200" + "\r\n");
                os.print("Content-Type: text/html" + "\r\n");
                os.print("Content-Length: " + response.length() + "\r\n");
                os.print("\r\n");
                os.print(response + "\r\n");
                os.close();
                socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
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

    private void connect() {
        EditText nameEt = findViewById(R.id.phoneNameEditText);
        String name = nameEt.getText().toString();

        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("number", "-");
            json.put("address", phoneAddress);
        } catch (JSONException e) {
            Log.d(TAG, "connect: " + e.getMessage());
            return;
        }

        Log.d(TAG, "connect: pre thread");

        Thread t = new Thread(() -> {
            // http://192.168.40.18:8000/connect
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

            Log.d(TAG, "run: finish");
        });

        t.start();
    }

    String tag = "===sessionLogging===";
    String TAG = tag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        if (phoneAddress == null) {
            phoneAddress = getIpAddress() + ":" + HttpServerThread.PORT;
        }

        SharedPreferences sp = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);

        context = this;

        Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null) {
            Log.d(TAG, "onCreate: " + data.toString());
            SharedPreferences.Editor editor = sp.edit();
            String address = data.getQueryParameter("address");
            if (address == null) {
                throw new IllegalStateException("Invalid navigation uri");
            }

            editor.putString(DESKTOP_MANAGER_ADDRESS, address);
            editor.apply();
            desktopAddress = address;
        } else {
            desktopAddress = sp.getString(DESKTOP_MANAGER_ADDRESS, "");
        }

        if (desktopAddress.isEmpty()) {
            return;
        }

        if (firstRun) {
            firstRun = false;
            server = new HttpServerThread();
            server.start();
            connect();
        }

        TextView urlTextView = findViewById(R.id.urlTextView);
        urlTextView.setText(phoneAddress);

        Log.d(tag,"onCreate");
    }

    public void switchToMessages(View view)
    {
        Log.d(tag,"Switch to messages");
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);
    }
}