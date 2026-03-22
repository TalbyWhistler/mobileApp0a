package ca.mohawk.testo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    String tag = "==sessionLogging==";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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