package com.example.lenovo.book_list;

/**
 * Created by Lenovo on 2017/2/17.
 */

public class Book {

    private String mBook_Name;

    private String mBook_Author;

    public Book(String mBook_Name, String mBook_Author) {
        this.mBook_Name = mBook_Name;
        this.mBook_Author = mBook_Author;
    }

    public String getmBook_Name() {
        return mBook_Name;
    }

    public String getmBook_Author() {
        return mBook_Author;
    }
}
