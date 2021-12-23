package com.dailycases.application;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public String _toString(int value)
    {
        if(value == 0)
            return "---";
        String sValue = String.valueOf(value);
        if(sValue.length() > 3)
        {
            int countDot = sValue.length() / 3;
            if(sValue.length() % 3 == 0 )
                countDot--;
            StringBuilder sb = new StringBuilder(sValue);
            for(int i = 0; i < countDot;i++)
            {
                sb.insert(sValue.length() - ((i+1)*3),",");
            }
            return sb.toString();
        }
        return sValue;
    }
    public String _toString(long value)
    {
        if(value == 0)
            return "---";
        String sValue = String.valueOf(value);
        if(sValue.length() > 3)
        {
            int countDot = sValue.length() / 3;
            if(sValue.length() % 3 == 0 )
                countDot--;
            StringBuilder sb = new StringBuilder(sValue);
            for(int i = 0; i < countDot;i++)
            {
                sb.insert(sValue.length() - ((i+1)*3),",");
            }
            return sb.toString();
        }
        return sValue;
    }

    public void setTexts(Country country)
    {
        location.setText(country.name);

        population.setText(_toString(country.population));

        total_cases.setText(_toString(country.covidData.totalCases));
        total_deaths.setText(_toString(country.covidData.totalDeaths));
        total_recovered.setText(_toString(country.covidData.totalRecovered));

        new_cases.setText(_toString(country.covidData.todayCases));
        new_deaths.setText(_toString(country.covidData.todayDeaths));
        new_recovered.setText(_toString(country.covidData.todayRecovered));

        active_cases.setText(_toString(country.covidData.activeCases));
        critical_cases.setText(_toString(country.covidData.criticalCases));
    }

    TextView total_cases;
    TextView total_deaths;
    TextView total_recovered;
    TextView new_cases;
    TextView new_deaths;
    TextView new_recovered;
    TextView active_cases;
    TextView critical_cases;
    TextView population;
    TextView location;
    Context ctx;
    Button btnLocation;
    Button btnDay;
    int checkedCountriesItem;
    int checkedDaysItem;
    List<String> countryNamesList;
    CharSequence[] countryNamesSequence;
    boolean countryNamesInitialized;
    final CharSequence[] days = { "Today", "Yesterday"};
    Country country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        location = findViewById(R.id.textView13);
        location.setPaintFlags(location.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        total_cases = findViewById(R.id.textView14);
        total_deaths = findViewById(R.id.textView15);
        total_recovered = findViewById(R.id.textView16);
        new_cases = findViewById(R.id.textView17);
        new_deaths = findViewById(R.id.textView18);
        new_recovered = findViewById(R.id.textView19);
        active_cases = findViewById(R.id.textView20);
        critical_cases = findViewById(R.id.textView21);
        population = findViewById(R.id.textView22);
        btnLocation = findViewById(R.id.button);
        btnDay = findViewById(R.id.button2);
        ctx = this.getApplicationContext();
        checkedCountriesItem = -1;
        checkedDaysItem = 0;
        countryNamesList = new ArrayList<>();
        countryNamesInitialized = false;

        country = new Country("World");
        country.initializeCovidData(ctx,false, new VolleyCallBack() {
            @Override
            public void onSuccess() {
                setTexts(country);
            }
        });


        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (countryNamesList.size() == 0) {
                    Country.initializeCountryNames(ctx, countryNamesList, new VolleyCallBack() {
                        @Override
                        public void onSuccess() {
                            countryNamesSequence = countryNamesList.toArray(new CharSequence[countryNamesList.size()]);
                            AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                            ad.setCancelable(false);
                            ad.setTitle("Select a Location");
                            ad.setSingleChoiceItems(countryNamesSequence, checkedCountriesItem, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int arg1) {
                                    checkedCountriesItem = arg1;
                                    country = new Country(countryNamesSequence[arg1].toString());
                                    boolean yesterday = false;
                                    if(checkedDaysItem == 1)
                                        yesterday = true;
                                    country.initializeCovidData(ctx,yesterday, new VolleyCallBack() {
                                        @Override
                                        public void onSuccess() {
                                            setTexts(country);
                                        }
                                    });
                                    dialogInterface.cancel();
                                }
                            });
                            ad.show();
                        }
                    });
                }
                else
                {
                    AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                    ad.setCancelable(false);
                    ad.setTitle("Select a Location");
                    ad.setSingleChoiceItems(countryNamesSequence, checkedCountriesItem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int arg1) {
                            checkedCountriesItem = arg1;
                            country = new Country(countryNamesSequence[arg1].toString());
                            boolean yesterday = false;
                            if(checkedDaysItem == 1)
                                yesterday = true;
                            country.initializeCovidData(ctx,yesterday, new VolleyCallBack() {
                                @Override
                                public void onSuccess() {
                                    setTexts(country);
                                }
                            });
                            dialogInterface.cancel();
                        }
                    });
                    ad.show();
                }
            }
        });

        btnDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(checkedCountriesItem == -1) {
                    Toast.makeText(MainActivity.this, "Select a Location First", Toast.LENGTH_LONG).show();
                    return;
                }
                AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                ad.setCancelable(false);
                ad.setTitle("Select a Day");
                ad.setSingleChoiceItems(days, checkedDaysItem,  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int arg1) {
                        checkedDaysItem = arg1;
                        boolean yesterday = false;
                        if(checkedDaysItem == 1)
                            yesterday = true;
                        country.initializeCovidData(ctx,yesterday, new VolleyCallBack() {
                            @Override
                            public void onSuccess() {
                                setTexts(country);
                            }
                        });
                        dialogInterface.cancel();
                    }
                });
                ad.show();
            }
        });
    }
}