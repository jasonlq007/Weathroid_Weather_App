package com.kluglos.weathroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    TextView txtString,autoText;
    public String url = "https://api.openweathermap.org/data/2.5/weather?q=";
    public String city = "";
    public String id = "&appid=148203bbf82d36f9e31007279c13f7f0";
    public String finalUrl = "";
    private TextView txtminTemp, txtmaxTemp, txtTmp, txtStatus, txtDesc, textPressure, textHumidity, txtWindSpeed, txtCity;
    private ConstraintLayout mainBG;
    private ImageView weatherIcon;
    public static String responseString = "";
    private Button searchBtn;

    private final OkHttpClient client = new OkHttpClient ();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );
        initView ();
        String json_return_by_the_function="";
        String[] fruits = {"Rome", "Delhi", "Mangalore", "Bangalore", "Kolkata", "Mumbai", "Chennai", "Hyderabad", "Berlin","Hamburg", "Frankfurt",
        "Stuttgart","Munich","Canberra","Perth","Brisbane","Tasmania","Cologne","Dusseldorf","Madrid","Lisbon","Balkunje","Mulki"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, fruits);
        AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        actv.setThreshold(1);//will start working from first character
        actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        actv.setTextColor( Color.RED);


        searchBtn.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
            finalUrl=url+autoText.getText ().toString ()+id;
              getWeatherData ();
            }
        } );
    }
    private void initView()
    {
        txtString = findViewById ( R.id.textstring );
        autoText = findViewById ( R.id.autoCompleteTextView );
        searchBtn = findViewById ( R.id.searchBtn );
        txtDesc=findViewById ( R.id.txtdesc );
        txtmaxTemp=findViewById ( R.id.txtTempMax );
        txtminTemp=findViewById ( R.id.txtTempMin );
        txtStatus=findViewById ( R.id.txtStatus );
        txtTmp=findViewById ( R.id.txtTemp );
        textHumidity=findViewById ( R.id.txtHum );
        textPressure=findViewById ( R.id.txtPress );
        txtWindSpeed=findViewById ( R.id.txtWindSpeed );
        weatherIcon=findViewById ( R.id.weatherIcon );
        mainBG=findViewById ( R.id.mainBackGround );
        txtCity=findViewById ( R.id.txtCity );

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
    private void getWeatherData() {
        class WeatherTask extends AsyncTask<Void, Void,Void> {
            WeatherTask()
            {

            }

            @Override
            protected Void doInBackground(Void... voids) {
                Request request = new Request.Builder()
                        .url(finalUrl)
                        .get()
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "Your Token")
                        .addHeader("cache-control", "no-cache")
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    String serverResponse = response.body().string();
                    responseString=serverResponse;

                }

                catch (IOException e) {
                    Log.v("###","error");
                    e.printStackTrace ();
                }
            return null;
            }

            @Override
            protected void onPostExecute(Void voids) {
                super.onPostExecute(voids);
                txtString.setText ( responseString );
                try {
                    JSONObject obj = new JSONObject(responseString);
                    setWeatherData ( obj, responseString );
                    JSONArray weatherarr = obj.getJSONArray ("weather");
                    JSONObject weatherObj = weatherarr.getJSONObject(0);
                    String status = weatherObj.getString("main");
                    String description = weatherObj.getString("description");

                    changeWeatherBackground ( description );
                    changeWeatherIcon ( description );


                }
                catch (JSONException e) {
                    e.printStackTrace ();
                }


            }
        }

        WeatherTask gt = new WeatherTask ();
        gt.execute();

    }
    private void setWeatherData(JSONObject obj, String responseString) throws JSONException
    {

        String tempString = obj.getString ("main");
        JSONObject tempobj = new JSONObject(tempString);
        String  temp_min = tempobj.getString("temp_min");
        Float temp;
        String temp_max = tempobj.getString("temp_max");
        String temps = tempobj.getString("temp");
        String pressure = tempobj.getString("pressure");
        String humidity = tempobj.getString("humidity");
        String windString = obj.getString ("wind");
        JSONObject windObject = new JSONObject(windString);
        String  windSpeed = windObject.getString("speed");
        JSONArray weatherarr = obj.getJSONArray ("weather");
        JSONObject weatherObj = weatherarr.getJSONObject(0);
        String status = weatherObj.getString("main");
        String description = weatherObj.getString("description");
        String icon=weatherObj.getString ( "icon" );
        String  city_name = obj.getString("name");
        temp=Float.parseFloat ( temp_min )-(float)273.15;
        txtminTemp.setText ( "Min Temperature: "+String.valueOf ( temp ) );
        temp=Float.parseFloat ( temp_max )-(float)273.15;
        txtmaxTemp.setText ( "Max Temperature: "+String.valueOf ( temp ) );
        temp=Float.parseFloat ( temps )-(float)273.15;
        txtCity.setText ( "City: "+autoText.getText () );
        txtTmp.setText ( "Current Temperature: "+String.valueOf ( temp ) );
        txtDesc.setText ( "Wather Description: "+String.valueOf ( status ) );
        txtStatus.setText ( "Current Status: "+String.valueOf ( description ) );
        textPressure.setText ( "Pressure: "+String.valueOf ( pressure ) );
        textHumidity.setText ( "Humidty: "+String.valueOf ( humidity ) );
        txtWindSpeed.setText ( "Wind Speed: "+ String.valueOf ( windSpeed ) );
    }
    private void changeWeatherIcon(String description)
    {
        String weatherIconUrl="";
        if(description.toLowerCase ().contains ( "rain" )) weatherIconUrl=ConstantUtils.DESC_RAIN;
        else if(description.toLowerCase ().contains ( "snow" )) weatherIconUrl=ConstantUtils.DESC_SNOW;
        else if(description.toLowerCase ().contains ( "cloud" )) weatherIconUrl=ConstantUtils.DESC_FEW_CLOUDS;
        else if(description.toLowerCase ().contains ( "mist" ))weatherIconUrl=ConstantUtils.DESC_MIST;
        else if(description.toLowerCase ().contains ( "sun" )) weatherIconUrl=ConstantUtils.DESC_CLEAR_SKY;
        else if(description.toLowerCase ().contains ( "thunder" )) weatherIconUrl=ConstantUtils.DESC_THUNDERSTORM;
        else if(description.toLowerCase ().contains ( "storm" )) weatherIconUrl=ConstantUtils.DESC_THUNDERSTORM;
        else if(description.toLowerCase ().contains ( "haze" )) weatherIconUrl=ConstantUtils.DESC_MIST;
        else weatherIconUrl=ConstantUtils.DESC_CLEAR_SKY;

        new DownloadImageTask((ImageView) findViewById(R.id.weatherIcon))
                .execute(weatherIconUrl);
    }
    private void changeWeatherBackground(String description)
    {
        if(description.toLowerCase ().contains ( "rain" )) mainBG.setBackgroundResource ( R.drawable.rainy );
        else if(description.toLowerCase ().contains ( "snow" )) mainBG.setBackgroundResource ( R.drawable.snowy );
        else if(description.toLowerCase ().contains ( "cloud" )) mainBG.setBackgroundResource ( R.drawable.cloudy);
        else if(description.toLowerCase ().contains ( "mist" )) mainBG.setBackgroundResource ( R.drawable.mist );
        else if(description.toLowerCase ().contains ( "haze" )) mainBG.setBackgroundResource ( R.drawable.mist );
        else if(description.toLowerCase ().contains ( "sun" )) mainBG.setBackgroundResource ( R.drawable.sunny );
        else if(description.toLowerCase ().contains ( "thunder" )) mainBG.setBackgroundResource ( R.drawable.thunderstorm );
        else if(description.toLowerCase ().contains ( "storm" )) mainBG.setBackgroundResource ( R.drawable.thunderstorm );
        else mainBG.setBackgroundResource ( R.drawable.weatherbg );

    }


}





