package com.example.maxmcarthur.myweather;

import android.app.ProgressDialog;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WeatherActivity extends AppCompatActivity implements WeatherFragment.OnFragmentInteractionListener{
    private ViewPager mViewPager;
    private ArrayList<HashMap<String, String>> weatherData;
    private final String[] DEFAULT_CITY = new String[] {"San Francisco", "CA"};
    private final String API_KEY = "5bc8a0d198203a20";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        weatherData = new ArrayList<HashMap<String, String>>();

        TextView city = (TextView)findViewById(R.id.cityName);
        city.setText(DEFAULT_CITY[0] + ", " + DEFAULT_CITY[1]);
        refreshWeatherData(DEFAULT_CITY);

        mViewPager = (ViewPager) findViewById(R.id.pager);
    }

    private void initializeAdapter() {
        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (weatherData.size() == 0) {
                    return new Fragment();
                }
                Map<String, String> reqCity = weatherData.get(position);
                return WeatherFragment.newInstance(reqCity.get("day"), reqCity.get("forecast"), reqCity.get("icon"));
            }

            @Override
            public int getCount() {
                // working with 10 day forecast
                return 10;
            }
        });
    }

    private void refreshWeatherData(String[] city) {

        final ProgressDialog dialog = ProgressDialog.show(WeatherActivity.this, "MyWeather", "Loading forecast..", true);

        String city_name = city[0].replace(" ", "_");
        String url = "http://api.wunderground.com/api/" + API_KEY + "/forecast10day/q/" + city[1] + "/" + city_name + ".json";

        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray forecast = response.getJSONObject("forecast").getJSONObject("txt_forecast").getJSONArray("forecastday");

                    Log.d("JSONResponse", forecast.toString());

                    weatherData.clear();

                    for (int i = 0; i < forecast.length(); i++) {
                        HashMap<String, String> day = new HashMap<String, String>();
                        day.put("day", forecast.getJSONObject(i).getString("title"));
                        day.put("forecast", forecast.getJSONObject(i).getString("fcttext"));
                        day.put("icon", forecast.getJSONObject(i).getString("icon_url"));
                        weatherData.add(day);
                    }
                    initializeAdapter();
                    dialog.dismiss();
                } catch (JSONException e) {
                    Log.e("JSONResponse", "Malformed JSON Response");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        AppSingleton.newInstance(getApplicationContext()).addRequest(request, "WEATHER_REQUEST");
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
