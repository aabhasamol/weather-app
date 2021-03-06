package com.example.findtheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    EditText cityName;
    TextView cityData;
    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls){
            String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            try
            {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in= urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data= reader.read();
                while (data!=-1)
                {
                    char current= (char) data;
                    result+= current;
                    data= reader.read();
                }
                return result;
            }catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo=jsonObject.getString("weather");
                JSONArray array= new JSONArray(weatherInfo);
                String message="";
                for(int i=0; i<array.length(); i++)
                {
                    JSONObject jsonPart= array.getJSONObject(i);
                    String main=jsonPart.getString("main");
                    String description=jsonPart.getString("description");
                    if(!main.equals("") && !description.equals(""))
                    {
                        message+=main+ " : "+description+"\n";
                    }
                }
                if(!message.equals(""))
                {
                    cityData.setText(message);
                }

            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_SHORT).show();
            }

        }
    }
    public void weather(View view)
    {
        try{
                String encodedCityName= URLEncoder.encode(cityName.getText().toString(), "UTF-8");
                DownloadTask task=new DownloadTask();
                task.execute("https://openweathermap.org/data/2.5/weather?q="+encodedCityName+"&appid=439d4b804bc8187953eb36d2a8c26a02");
                InputMethodManager mgr=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(cityName.getWindowToken(),0);
        }catch(Exception e)
        {
            e.printStackTrace();
            Toast.makeText(this, "Could not find weather", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityName = findViewById(R.id.cityName);
        cityData = findViewById(R.id.cityData);
    }
}
