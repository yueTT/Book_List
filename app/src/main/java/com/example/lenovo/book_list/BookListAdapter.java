package com.example.lenovo.book_list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2017/2/22.
 */

public class BookListAdapter extends ArrayAdapter<Book> {

    public BookListAdapter(Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;

        Book currentBook = getItem(position);

        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.book_list,parent,false);
        }

        TextView name = (TextView) listItemView.findViewById(R.id.list_item_name);
        name.setText(currentBook.getmBook_Name());

        TextView author = (TextView) listItemView.findViewById(R.id.list_item_author);
        author.setText(currentBook.getmBook_Author());

        return listItemView;

    }
}
