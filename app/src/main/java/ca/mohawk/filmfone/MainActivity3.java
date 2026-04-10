package ca.mohawk.filmfone;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity3 extends AppCompatActivity {
    static String tag = "===loggingTag===";
    static int index;
    public static MainActivity3 context;

    public static void setMessagesAdapter(int index) {
        if (context == null) return;

        MessageAdapter adapter = new MessageAdapter(context, MainActivity.messagesReceived.get(index).getMessages());
        ListView listView = context.findViewById(R.id.listViewItem);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        ListView listView = findViewById(R.id.listViewItem);

        context = this;

        // get the index passed from MainActivity2
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