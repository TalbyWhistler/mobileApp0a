package ca.mohawk.testo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity3 extends AppCompatActivity {
    String tag = "===loggingTag===";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        setConvoHistory();
        updateConvoHistory();

    }

    public static String[] messages = new String[4];
    public static Boolean[] sender = new Boolean[4];

    public static TextView[] convoOutput = new TextView[4];

    public void sendButton(View view)
    {
        Log.d(tag,"Send button");
        String inputText;
        EditText input = findViewById(R.id.editTextText);
        inputText=input.getText().toString();
        input.setText("");
        Log.d(tag,inputText);

        for (int i=0;i<=2;i++)
        {
            messages[i]=messages[i+1];
            sender[i]=sender[i+1];
        }
        messages[3]=inputText;
        sender[3]=true;
        updateConvoHistory();
    }

    public void updateConvoHistory()
    {
        for (int i=0;i<4;i++)
        {
            convoOutput[i].setText(messages[i]);
            if (sender[i]==true)
            {
                convoOutput[i].setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                
            }
            else
            {
                convoOutput[i].setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            }
        }
    }

    public void exitButton(View view)
    {
        Log.d(tag,"Exit");
        finish();
    }

    public void setConvoHistory()
    {
       // Boolean[] sender = new Boolean[4];
       // String[] messages = new String[4];

        convoOutput[0]=findViewById(R.id.textView3);
        convoOutput[1]=findViewById(R.id.textView4);
        convoOutput[2]=findViewById(R.id.textView5);
        convoOutput[3]=findViewById(R.id.textView18);

        sender[0]=false;
        sender[1]=true;
        sender[2]=false;
        sender[3]=true;

        messages[0]="Hey it's me.";
        messages[1]="Hi are you still awake?";
        messages[2]="Yes I'm still awake";
        messages[3]="Well go back to bed!";

        for (int i=0;i<4;i++)
        {
            convoOutput[i].setText(messages[i]);
        }
    }
}