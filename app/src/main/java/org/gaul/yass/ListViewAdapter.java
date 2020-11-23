package org.gaul.yass;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListViewAdapter extends ArrayAdapter {

    public ListViewAdapter(Context context, List<String> listItems)
    {
        super(context,0,listItems);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        //check if there is a view we could reuse or else if convertView is null inflate a new view;
        if(listItemView==null)
        {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
        }
        String curr=getItem(position).toString();
        TextView currItem=(TextView) listItemView.findViewById(R.id.item);
//        currItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        currItem.setFocusable(false);
        currItem.setText(curr);
        return listItemView;
    }
}
