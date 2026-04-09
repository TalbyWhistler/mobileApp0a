package ca.mohawk.testo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MessageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ChatMessage> messages;

    public MessageAdapter(Context context, ArrayList<ChatMessage> messages) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getCount() { return messages.size(); }

    @Override
    public Object getItem(int position) { return messages.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage message = messages.get(position);

        // check if it's sent (0) or received (1)
        int layoutRes = (message.getSentBy() == 0)
                ? R.layout.item_message_sent
                : R.layout.item_message_received;

        // inflate the correct side
        if (convertView == null || (int)convertView.getTag() != layoutRes) {
            convertView = LayoutInflater.from(context).inflate(layoutRes, parent, false);
            convertView.setTag(layoutRes);
        }

        // set the text
        TextView txtMessage = convertView.findViewById(R.id.text_view_message);
        txtMessage.setText(message.getMessage());

        return convertView;
    }
}
