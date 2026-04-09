package ca.mohawk.testo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity3 extends AppCompatActivity {
    String tag = "===loggingTag===";
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        ListView listView = findViewById(R.id.listViewItem);

        // get the index passed from MainActivity
        index = getIntent().getIntExtra("ITEM_INDEX", 0);
        MessageAdapter adapter = new MessageAdapter(this, MainActivity.messagesReceived.get(index).getMessages());
        listView.setAdapter(adapter);

        // To show the newest (bottom) entry automatically:
        listView.setSelection(adapter.getCount() - 1);

    }

    public void sendButton(View view)
    {
        Log.d(tag,"Send button");
        String inputText;
        EditText input = findViewById(R.id.editTextText);
        inputText=input.getText().toString();

        ListItem li = MainActivity.messagesReceived.get(index);
        li.addMessage(0, inputText);
        input.setText("");

        ListView listView = findViewById(R.id.listViewItem);
        MessageAdapter adapter = new MessageAdapter(this, MainActivity.messagesReceived.get(index).getMessages());
        listView.setAdapter(adapter);
    }

    public void exitButton(View view)
    {
        Log.d(tag,"Exit");
        finish();
    }
}