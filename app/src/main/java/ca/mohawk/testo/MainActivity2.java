package ca.mohawk.testo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {
    String tag = "==sessionLogger==";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);
        Log.d(tag,"onCreate");

        ArrayAdapter<ListItem> adapter = new MyListAdapter(MainActivity2.this, 0, MainActivity.messagesReceived);
        Log.d(tag, "adapter = " + adapter);
        Log.d(tag, "adapter getCount() = " + adapter.getCount());
        ListView randList = findViewById(R.id.listView);
        randList.setAdapter(adapter);
        randList.setOnItemClickListener(this::onItemClick);
    }

    public void onItemClick(AdapterView parent, View v, int position, long id) {
        // Do something in response to the click
        Log.d(tag, "adapter = " + parent);
        Log.d(tag, "View = " + v);
        Log.d(tag, "id = " + id);
        Intent nextIntent = new Intent(MainActivity2.this,MainActivity3.class);
        nextIntent.putExtra("ITEM_INDEX", position);
        startActivity(nextIntent);
    }

    public void closeButton(View view)
    {
        finish();
    }
}