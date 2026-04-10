package ca.mohawk.testo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class MyListAdapter extends ArrayAdapter<ListItem> {
    private List<ListItem> li;
    public MyListAdapter(@NonNull Context context,
                         int resource, @NonNull List<ListItem> objects) {
        super(context, resource, objects);
        li = objects;
    }
    @Override
    public View getView(int position, View listItemView, ViewGroup parent) {
        // inflate our view
        if (listItemView == null) {
            LayoutInflater inflater = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItemView = inflater.inflate(R.layout.list_item, parent, false);
        }
        // find the specific item from our specialized list of items
        ListItem li = getItem(position);
        // using our inflated view, set the image and text
        ImageView iv = listItemView.findViewById(R.id.imageView);
        TextView tvs = listItemView.findViewById(R.id.textViewSender);
        TextView tvd = listItemView.findViewById(R.id.textViewDate);
        TextView tvm = listItemView.findViewById(R.id.textViewMessage);
        //iv.setImageResource(li.getImId());
        int lastIndex = li.getMessages().size()-1;
        tvs.setText(li.getSender());
        tvd.setText("" + li.getMessages().get(lastIndex).getDate()); // get newest message date
        tvm.setText(li.getMessages().get(lastIndex).getMessage()); // get newest message
        return listItemView;
    }
}
