package com.dailycases.application;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Country {
    Long population;
    String name;
    CovidData covidData;

    Country(String name)
    {
        this.name = name;
        covidData = new CovidData();
    }
    void initializeCovidData(Context context,boolean yesterday, final VolleyCallBack callBack)
    {
        if(!InternetConnection.checkConnection(context)) {
            Toast.makeText(context, "Connection Error", Toast.LENGTH_LONG).show();
            return;
        }

        String url;
        if(name.equals("World"))
            url = "https://disease.sh/v3/covid-19/all?yesterday=" + yesterday;
        else
            url = "https://disease.sh/v3/covid-19/countries/" + name + "?yesterday=" + yesterday;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            population = response.getLong("population");
                            covidData.totalCases = response.getInt("cases");
                            covidData.todayCases = response.getInt("todayCases");
                            covidData.totalDeaths = response.getInt("deaths");
                            covidData.todayDeaths = response.getInt("todayDeaths");
                            covidData.totalRecovered = response.getInt("recovered");
                            covidData.todayRecovered = response.getInt("todayRecovered");
                            covidData.activeCases = response.getInt("active");
                            covidData.criticalCases = response.getInt("critical");

                            callBack.onSuccess();
                        } catch (JSONException e) {
                            Toast.makeText(context, "Reading Data Error", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    Toast.makeText(context, "Error Code : " + error.networkResponse.statusCode, Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(context, "Connection Error", Toast.LENGTH_LONG).show();
                }
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public static void initializeCountryNames(Context context, List<String> countryNames, final VolleyCallBack callBack)
    {
        if(!InternetConnection.checkConnection(context)) {
            Toast.makeText(context, "Connection Error", Toast.LENGTH_LONG).show();
            return;
        }
        String url = "https://disease.sh/v3/covid-19/countries";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        countryNames.add("World");
                        for(int i = 0; i < response.length();i++)
                        {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                countryNames.add(obj.getString("country"));
                            } catch (JSONException e) {
                                Toast.makeText(context, "Reading Data Error", Toast.LENGTH_LONG).show();
                            }
                        }
                        callBack.onSuccess();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try{
                            Toast.makeText(context, "Error Code : " + error.networkResponse.statusCode, Toast.LENGTH_LONG).show();
                        }catch (Exception e){
                            Toast.makeText(context, "Connection Error", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }
}
