package com.example.lenovo.book_list;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String BOOKS_URL = "https://api.douban.com/v2/book/search?q=";

    private EditText editText;

    private String searchTitle = "";

    private StringBuffer searchUrl = new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button search = (Button) findViewById(R.id.search);

        search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                editText = (EditText)findViewById(R.id.edit_text);
                searchTitle = editText.getText().toString();
                searchUrl.append(BOOKS_URL);
                searchUrl.append(searchTitle);
                myAsyncTask task = new myAsyncTask();
                task.execute();
            }
        });
    }


    public class myAsyncTask extends AsyncTask<URL,Void,ArrayList<Book>> {

        private final String LOG_TAG = MainActivity.class.getSimpleName();

        //private String inputUrl;

        //private Context context;

        private URL createURL(String str){
            URL url = null;
            try {
                url = new URL(str);
            }catch (MalformedURLException e){
                Log.e(LOG_TAG,"error with creating URL",e);
                return null;
            }
            return url;
        }

        private String readFormStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null){
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = bufferedReader.readLine();
                while (line != null){
                    output.append(line);
                    line = bufferedReader.readLine();
                }
            }
            return output.toString();
        }

        private String makeHttpRequest(URL url)throws IOException{
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.connect();
                if (urlConnection.getResponseCode()==200){
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFormStream(inputStream);
                }else {
                    Log.e(LOG_TAG,"error response code"+urlConnection.getResponseCode());
                }
            }catch (IOException e){
                Log.e(LOG_TAG,"problem retrieving the book json results by urlconnection",e);
            }finally {
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
                if (inputStream != null){
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        private ArrayList<Book> extractFeatureFromJson(String str){
            StringBuilder author = new StringBuilder();
            ArrayList<Book> books = new ArrayList<Book>();
            try {
                JSONObject baseJsonResponse = new JSONObject(str);
                JSONArray items = baseJsonResponse.getJSONArray("books");
                for (int i =0; i < items.length(); i++){
                    JSONObject itemsObject = items.getJSONObject(i);
                    String title = itemsObject.getString("title");
                    JSONArray authors = itemsObject.getJSONArray("author");
                    for (int j=0; j<authors.length(); j++){
                        author.append("  "+authors.getString(j));
                    }
                    String author_total = author.toString();
                    Book book = new Book(title,author_total);
                    books.add(book);
                }
            }catch (JSONException e){
                Log.e(LOG_TAG,"problem parsing the book json results",e);
            }
            return books;
        }

        @Override
        protected ArrayList<Book> doInBackground(URL... params) {
            URL url = createURL(searchUrl.toString());
            String jsonResponse = "";
            try{
                jsonResponse = makeHttpRequest(url);
            }catch (IOException e){
                Log.e(LOG_TAG,"makehttprequest has problem",e);
            }
            ArrayList<Book> books = extractFeatureFromJson(jsonResponse);
            return books;
        }

        @Override
        protected void onPostExecute(ArrayList<Book> books) {
            if (books == null){
                return;
            }
            BookListAdapter adapter = new BookListAdapter(MainActivity.this,books);
            ListView bookList = (ListView) MainActivity.this.findViewById(R.id.book_listview);
            bookList.setAdapter(adapter);
            searchUrl.setLength(0);
        }
    }


}
