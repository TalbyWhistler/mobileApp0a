package ca.mohawk.testo;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.http.HttpEngine;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownServiceException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {
    public static MainActivity context;
    private static String address;

    private class HttpServerThread extends Thread {
        ServerSocket httpServerSocket;
        static final int HttpServerPORT = 8888;

        @Override
        public void run() {
            Socket socket = null;

            try {
                httpServerSocket = new ServerSocket(HttpServerPORT);

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

    private class HttpResponseThread extends Thread {
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

                    contentLength = Integer.parseInt(request.split(":")[1].substring(1));
                } catch (NumberFormatException e) {

                }
                if (contentLength <= 0) {
                    return;
                }

                char buf[] = new char[contentLength + 2];
                is.read(buf, 0, contentLength + 2);

                String body = new String(buf);
                Log.d(tag, "run body: " + body);

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

                MainActivity.this.runOnUiThread(() -> {
                    MainActivity _this = MainActivity.context;
                    TextView url = _this.findViewById(R.id.urlTextView);
                    url.setText(body);
                });
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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
                        break;
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

    public void connect(View view) {
        EditText et = findViewById(R.id.urlEditText);
        String serverAddress = et.getText().toString();
        String json = "{" +
                "    \"name\": \"john\"," +
                "    \"number\": \"Hello there\"," +
                "    \"address\": \"" + address + "\"" +
                "}";

        Thread t = new Thread(() -> {
            // http://192.168.40.117:8000/connect
            try {
                URL url = new URL(serverAddress);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Accept-Charset", "UTF-8");
                connection.setRequestProperty("Content-type", "application/json");

                // Write to the connection
                OutputStream output = connection.getOutputStream();
                output.write(json.getBytes(StandardCharsets.UTF_8));
                output.close();

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

        if (address == null) {
            address = getIpAddress() + ":" + HttpServerThread.HttpServerPORT;
        }

        context = this;

        HttpServerThread httpServerThread = new HttpServerThread();
        httpServerThread.start();

        TextView urlTextView = findViewById(R.id.urlTextView);
        urlTextView.setText(address);

        Log.d(tag,"onCreate");
        appendTime();
    }

    public void appendTime()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date date= new Date();
        String newStr = formatter.format(date).toString();
       // String newStr = "D'oh";
        TextView tView = findViewById(R.id.textView2);
        tView.setText(newStr);
    }

    public void switchToMessages(View view)
    {
        Log.d(tag,"Switch to messages");
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);
    }
}