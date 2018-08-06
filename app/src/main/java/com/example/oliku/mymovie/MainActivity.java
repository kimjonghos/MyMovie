package com.example.oliku.mymovie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView tvDirector, tvGenre, tvActors, tvReleased, tvPlot;
    Button btnSearch;
    ImageView ivPoster;
    EditText etTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDirector = (TextView) findViewById(R.id.tvDirector);
        tvGenre = (TextView) findViewById(R.id.tvGenre);
        tvActors = (TextView) findViewById(R.id.tvActors);
        tvReleased = (TextView) findViewById(R.id.tvReleased);
        tvPlot = (TextView) findViewById(R.id.tvPolt);

        btnSearch = (Button) findViewById(R.id.btnSearch);

        ivPoster = (ImageView) findViewById(R.id.ivPoster);
        etTitle = (EditText) findViewById(R.id.etTitle);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etTitle.getText() != null && !etTitle.getText().toString().isEmpty()) {
                    MySearchTask mst = new MySearchTask();
                    mst.execute(etTitle.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "타이틀을 입력하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class MySearchTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... values) {
            try {
                String params = "apikey=ae7c372c&t=" + values[0];
                byte[] postData = params.getBytes("UTF-8");
                URL url = new URL("http://www.omdbapi.com/?" + params);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("GET");
                //   conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                //  conn.setRequestProperty("Content-Length",String.valueOf(postData.length));
                conn.setDoInput(true);
                conn.setDoOutput(true);
                //conn.getOutputStream().write(postData);

                int responseCode = conn.getResponseCode();

                StringBuilder sb = new StringBuilder();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();
                } else {
                    sb.append("Connection failed.");
                }
                conn.disconnect();

                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void onPostExecute(String result) {
            try {


                JSONObject j = new JSONObject(result);
                tvDirector.setText(j.getString("Director"));
                tvGenre.setText(j.getString("Genre"));
                tvActors.setText(j.getString("Actors"));
                tvReleased.setText(j.getString("Released"));
                tvPlot.setText(j.getString("Plot"));
                String imageurl = j.getString("Poster");
                new DownloadImageTask(ivPoster).execute(imageurl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
