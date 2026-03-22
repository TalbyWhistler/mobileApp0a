package ca.mohawk.testo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity2 extends AppCompatActivity {
    String tag = "==sessionLogger==";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);
        Log.d(tag,"onCreate");
        setTextHistory();
        TextView tvClick = (TextView)findViewById(R.id.textView7);
        tvClick.setOnClickListener(this::textView7OnClick);
        tvClick = (TextView)findViewById(R.id.textView6);
        tvClick.setOnClickListener(this::textView7OnClick);
        tvClick = (TextView)findViewById(R.id.textView11);
        tvClick.setOnClickListener(this::textView7OnClick);
        ImageView iv = findViewById(R.id.imageView);
        iv.setOnClickListener(this::textView7OnClick);
       // getCardInfo();
       // fillScroll();
    }

    public void fillScroll()
    {
      //  TextView tview = findViewById(R.id.textView3);
      //  String inText = tview.getText().toString();
        String outText = "This is some new out text";
        String messageOut = "";
        for(int i=0;i<50;i++)
        {
            messageOut+="And another line";
        }
      //  tview.setText(inText+" "+outText+" "+messageOut);
    }
    public void testScroll(Context context)
    {
        ScrollView sv = new ScrollView(context);
    }

    public void closeButton(View view)
    {
        finish();
    }

    public void textView7OnClick(View view)
    {
        Log.d(tag,"textview 7 onclick");
        Intent nextIntent = new Intent(MainActivity2.this,MainActivity3.class);
        startActivity(nextIntent);
    }

    public void setTextHistory()
    {
       // Variable arrays for programmable data
        String[] callers = new String[4];
        String[] timeStamps = new String[4];
        String[] phoneNumbers = new String[4];
        Bitmap[] callerPictures = new Bitmap[4];


        // Variable arrays for output widgets
        TextView[] callerOutput = new TextView[4];
        TextView[] timestampOutput = new TextView[4];
        TextView[] phoneNumbersOutput = new TextView[4];
        ImageView[] callerImagesOutput = new ImageView[4];

        // Previous four caller names
        callers[0]="Samantha";
        callers[1]="Greg";
        callers[2]="Big Jimbo";
        callers[3]="Mysterious Stranger";

        // Previous four caller timestampes
        timeStamps[0]="12:01am 03/21/26";
        timeStamps[1]="11:53pm 03/20/26";
        timeStamps[2]="10:29pm 03/20/26";
        timeStamps[3]="9:20pm 03/20/26";

        // Previous four caller phone numbers
        phoneNumbers[0]="905-555-5436";
        phoneNumbers[1]="905-556-5436";
        phoneNumbers[2]="905-557-5436";
        phoneNumbers[3]="905-558-5436";

        // Previous four caller pictures
        callerImagesOutput[0]=findViewById(R.id.imageView);
        callerImagesOutput[1]=findViewById(R.id.imageView5);
        callerImagesOutput[2]=findViewById(R.id.imageView6);
        callerImagesOutput[3]=findViewById(R.id.imageView8);

        callerImagesOutput[0].setImageResource(R.drawable.betty);
        callerImagesOutput[1].setImageResource(R.drawable.cherry);
        callerImagesOutput[2].setImageResource(R.drawable.jughead);
        callerImagesOutput[3].setImageResource(R.drawable.ethyl);


        callerOutput[0]=findViewById(R.id.textView11);
        callerOutput[1]=findViewById(R.id.textView8);
        callerOutput[2]=findViewById(R.id.textView12);
        callerOutput[3]=findViewById(R.id.textView15);

        timestampOutput[0]=findViewById(R.id.textView7);
        timestampOutput[1]=findViewById(R.id.textView10);
        timestampOutput[2]=findViewById(R.id.textView14);
        timestampOutput[3]=findViewById(R.id.textView17);

        phoneNumbersOutput[0]=findViewById(R.id.textView6);
        phoneNumbersOutput[1]=findViewById(R.id.textView9);
        phoneNumbersOutput[2]=findViewById(R.id.textView13);
        phoneNumbersOutput[3]=findViewById(R.id.textView16);



        for(int i=0;i<4;i++)
        {
            callerOutput[i].setText(callers[i]);
            timestampOutput[i].setText(timeStamps[i]);
            phoneNumbersOutput[i].setText(phoneNumbers[i]);
        }
    }

    public void getCardInfo()
    {

     //   CardView cView = findViewById(R.id.cardView);
     //   TextView tView = findViewById(R.id.textView4);
       // TextView tView5 = findViewById(R.id.textView5);
       // ImageView iView = findViewById(R.id.imageView6);
       // Button bView = findViewById(R.id.button);
        //* display doesn't need to be variable in size, just one active and some dummies
        // click on the active to open the conversation screen
        // style the contact cards for real life.
    }
}