package com.stanzione.weatherfollower;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;


public class CitiesActivity extends ActionBarActivity implements CompoundButton.OnCheckedChangeListener {

    private TextView city1, city2, city3, city4, city5;
    private Switch switch1, switch2, switch3, switch4, switch5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cities);

        city1 = (TextView) findViewById(R.id.textViewCity);
        city2 = (TextView) findViewById(R.id.textViewCity2);
        city3 = (TextView) findViewById(R.id.textViewCity3);
        city4 = (TextView) findViewById(R.id.textViewCity4);
        city5 = (TextView) findViewById(R.id.textViewCity5);

        switch1 = (Switch) findViewById(R.id.switch1);
        switch2 = (Switch) findViewById(R.id.switch2);
        switch3 = (Switch) findViewById(R.id.switch3);
        switch4 = (Switch) findViewById(R.id.switch4);
        switch5 = (Switch) findViewById(R.id.switch5);

        getActivatedCities();

        switch1.setOnCheckedChangeListener(this);
        switch2.setOnCheckedChangeListener(this);
        switch3.setOnCheckedChangeListener(this);
        switch4.setOnCheckedChangeListener(this);
        switch5.setOnCheckedChangeListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cities, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch(buttonView.getId()){
            case R.id.switch1:
                changeCityTextStyle(city1, isChecked);
                saveChange(Configs.CITY_ACTIVATED_1, isChecked);
                break;
            case R.id.switch2:
                changeCityTextStyle(city2, isChecked);
                saveChange(Configs.CITY_ACTIVATED_2, isChecked);
                break;
            case R.id.switch3:
                changeCityTextStyle(city3, isChecked);
                saveChange(Configs.CITY_ACTIVATED_3, isChecked);
                break;
            case R.id.switch4:
                changeCityTextStyle(city4, isChecked);
                saveChange(Configs.CITY_ACTIVATED_4, isChecked);
                break;
            case R.id.switch5:
                changeCityTextStyle(city5, isChecked);
                saveChange(Configs.CITY_ACTIVATED_5, isChecked);
                break;
        }

    }

    private void changeCityTextStyle(TextView changedCity, boolean isChecked){

        if(isChecked){
            changedCity.setEnabled(true);
        }
        else{
            changedCity.setEnabled(false);
        }

    }

    private void saveChange(String configId, boolean configValue){

        SharedPreferences prefs = getSharedPreferences(Configs.SHARED_CONFIGS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editPrefs = prefs.edit();

        editPrefs.putBoolean(configId, configValue);

        editPrefs.commit();

    }

    private void getActivatedCities(){

        SharedPreferences prefs = getSharedPreferences(Configs.SHARED_CONFIGS, Activity.MODE_PRIVATE);

        boolean activated = false;

        activated = prefs.getBoolean(Configs.CITY_ACTIVATED_1, true);
        changeCityTextStyle(city1, activated);
        switch1.setChecked(activated);

        activated = prefs.getBoolean(Configs.CITY_ACTIVATED_2, true);
        changeCityTextStyle(city2, activated);
        switch2.setChecked(activated);

        activated = prefs.getBoolean(Configs.CITY_ACTIVATED_3, true);
        changeCityTextStyle(city3, activated);
        switch3.setChecked(activated);

        activated = prefs.getBoolean(Configs.CITY_ACTIVATED_4, true);
        changeCityTextStyle(city4, activated);
        switch4.setChecked(activated);

        activated = prefs.getBoolean(Configs.CITY_ACTIVATED_5, true);
        changeCityTextStyle(city5, activated);
        switch5.setChecked(activated);

    }

}
