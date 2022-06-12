package com.example.weatherforproj;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM");
    Date d = new Date();
    String dayOfTheWeek = sdf.format(d);
    TextView date;
    TextView temptext;
    TextView comp;
    ImageView iconview;
    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private final String appid = "89bbfb30a1f1c94bd6eab6a1ff9563e5";
    DecimalFormat df = new DecimalFormat("#.##");
    String[] compliments = {"Прекрасный выбор!", "Сегодня вы выглядите потрясающе!"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iconview=(ImageView) findViewById(R.id.pogodaview);
        date = (TextView) findViewById(R.id.datetext);
        date.setText(dayOfTheWeek);
        comp = (TextView) findViewById(R.id.compliment);
        comp.setText(compliments[(new Random()).nextInt(compliments.length)]);

        temptext = (TextView) findViewById(R.id.temp);

        getWeatherDetails();
    }

    public void getWeatherDetails() {
        String tempUrl = "";
        String city = "Ufa";
            tempUrl = url + "?q=" + city + "&units=metric"+"&appid=" + appid;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String output = "";
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                        String iconid = jsonObjectWeather.getString("icon");
                        JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                        int temp = jsonObjectMain.getInt("temp");
                        temptext.setText(df.format(temp)+" °C ");
                        new DownloadImageTask((ImageView) findViewById(R.id.pogodaview))
                                .execute("https://openweathermap.org/img/wn/"+iconid+"@2x.png");


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Кажется, нужно включить VPN", Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }



    }