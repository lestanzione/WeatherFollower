package com.stanzione.weatherfollower;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends ActionBarActivity {

    private static String TAG = MainActivity.class.getSimpleName();

    private RelativeLayout backgroundLayout;
    private TableLayout tableCities;
    private ProgressBar progressBar;
    private Button startWebservice;
    private Button otherButton;
    private TextView testLabel;

    private String[] citiesToSearch;

    private HTTPRequestOperation asyncOperation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");

        backgroundLayout = (RelativeLayout) findViewById(R.id.backgroundLayout);
        tableCities = (TableLayout) findViewById(R.id.tableCitiesResult);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        startWebservice = (Button) findViewById(R.id.startWebservice);
        otherButton = (Button) findViewById(R.id.otherButton);
        testLabel = (TextView) findViewById(R.id.testLabel);

        setBackground();

        startWebservice.setVisibility(View.INVISIBLE);
        otherButton.setVisibility(View.INVISIBLE);
        testLabel.setVisibility(View.INVISIBLE);

        startWebservice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startOperation();

            }
        });

        otherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                testLabel.setText("Working");

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        startOperation();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_edit_cities) {

            Intent intent = new Intent(this, CitiesActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setBackground(){

        int currentHour = new Date().getHours();

        Log.d(TAG, "Current hour: " + currentHour);

        if(currentHour < 18){
            backgroundLayout.setBackgroundResource(R.drawable.bg_day);
        }
        else{
            backgroundLayout.setBackgroundResource(R.drawable.bg_night);
        }

    }

    private void startOperation(){

        if(asyncOperation == null){
            asyncOperation = new HTTPRequestOperation();
        }

        Log.d(TAG, asyncOperation.getStatus().toString());

        if(asyncOperation.getStatus() == AsyncTask.Status.PENDING){
            Log.d(TAG, "Starting operation..");

            citiesToSearch = getActivatedCities();

            Log.d(TAG, "Activated cities:");

            for(int i=0; i<citiesToSearch.length; i++){
                Log.d(TAG, citiesToSearch[i]);
            }

            Log.d(TAG, "Total of activated cities: " + citiesToSearch.length);

            progressBar.setVisibility(View.VISIBLE);
            asyncOperation.execute(citiesToSearch);
        }
        else if(asyncOperation.getStatus() == AsyncTask.Status.RUNNING){
            Log.d(TAG, "The operation is still running..");
        }
        else if(asyncOperation.getStatus() == AsyncTask.Status.FINISHED){
            Log.d(TAG, "The operation has finished, starting again..");

            citiesToSearch = getActivatedCities();

            Log.d(TAG, "Activated cities:");

            for(int i=0; i<citiesToSearch.length; i++){
                Log.d(TAG, citiesToSearch[i]);
            }

            Log.d(TAG, "Total of activated cities: " + citiesToSearch.length);

            progressBar.setVisibility(View.VISIBLE);
            asyncOperation = new HTTPRequestOperation();
            asyncOperation.execute(citiesToSearch);
        }

    }

    private String[] getActivatedCities(){

        ArrayList<String> cities = new ArrayList<String>();

        SharedPreferences prefs = getSharedPreferences(Configs.SHARED_CONFIGS, Activity.MODE_PRIVATE);

        boolean activated = false;

        if(prefs.getBoolean(Configs.CITY_ACTIVATED_1, true)){
            cities.add(getResources().getString(R.string.string_city_1));
        }
        if(prefs.getBoolean(Configs.CITY_ACTIVATED_2, true)){
            cities.add(getResources().getString(R.string.string_city_2));
        }
        if(prefs.getBoolean(Configs.CITY_ACTIVATED_3, true)){
            cities.add(getResources().getString(R.string.string_city_3));
        }
        if(prefs.getBoolean(Configs.CITY_ACTIVATED_4, true)){
            cities.add(getResources().getString(R.string.string_city_4));
        }
        if(prefs.getBoolean(Configs.CITY_ACTIVATED_5, true)){
           cities.add(getResources().getString(R.string.string_city_5));
        }

        String[] returnArr = new String[cities.size()];
        return cities.toArray(returnArr);

    }

    private class HTTPRequestOperation extends AsyncTask<String, Integer, ArrayList<CityResult>> {

        private String TAG = HTTPRequestOperation.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute");
        }

        @Override
        protected ArrayList<CityResult> doInBackground(String... cities) {

            ArrayList<CityResult> citiesResult = new ArrayList<CityResult>();
            CityResult cityResult;

            try {

                for(String city : cities) {

                    Log.d(TAG, "Starting search for city: " + city);

                    String cityHtml = getCityResultHTML(city);

                    if (cityHtml == null) {
                        Log.e(TAG, "Could not get weather info for city: " + city);
                        continue;
                    }

                    String currentTemperature = getCurrentTemperature(cityHtml);

                    if (currentTemperature == null) {
                        Log.e(TAG, "Could not get weather info for city: " + city);
                        continue;
                    }

                    Log.d(TAG, city + ": " + currentTemperature);

                    cityResult = new CityResult(city, currentTemperature);
                    citiesResult.add(cityResult);

                }

            } finally {

            }
/*
            for (int i = 0; i < 5; i++) {

                Log.d(TAG, "doInBackground: " + i);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
*/
            return citiesResult;

        }

        private String getCityResultHTML(String city){

            if(city.equals("São Paulo"))
                city = "558/saopaulo-sp";
            else if(city.equals("Rio de Janeiro"))
                city = "321/riodejaneiro-rj";
            else if(city.equals("Curitiba"))
                city = "271/curitiba-pr";
            else if(city.equals("Florianópolis"))
                city = "377/florianopolis-sc";
            else if(city.equals("Salvador"))
                city = "56/salvador-ba";

            String cityHtml = "";

            try{
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet("http://www.climatempo.com.br/previsao-do-tempo/cidade/" + city);
                HttpResponse response = client.execute(request);

                InputStream in = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder str = new StringBuilder();
                String line = null;
                while((line = reader.readLine()) != null)
                {
                    str.append(line);
                }
                in.close();
                cityHtml = str.toString();

                return cityHtml;

            } catch (ClientProtocolException e) {
                e.printStackTrace();
                return null;

            } catch (IOException e) {
                e.printStackTrace();
                return null;

            }

        }

        private String getCurrentTemperature(String html){

            String temperature = null;

            String[] arr = html.split("temp-momento\">");
            if(arr.length > 1){
                String[] temperatureArr = arr[1].split("</span>");
                if(temperatureArr.length > 1){
                    temperature = temperatureArr[0];
                }
            }

            return temperature;

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

        }

        @Override
        protected void onPostExecute(ArrayList<CityResult> citiesResult) {
            testLabel.setText("Executed!");

            tableCities.removeAllViews();
            tableCities.setAlpha(0.75f);

            createTableHeader();

            for(int i=0; i<citiesResult.size(); i++){

                Log.d(TAG, "" + i);

                TableRow cityRow = new TableRow(MainActivity.this);
                cityRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));

                TextView cityNameView = new TextView(MainActivity.this);
                cityNameView.setText(citiesResult.get(i).getCityName());
                cityNameView.setTextSize(20);
                cityNameView.setPadding(10, 0, 0, 0);

                TextView currentTemperatureView = new TextView(MainActivity.this);
                currentTemperatureView.setText(citiesResult.get(i).getCurrentTemperature());
                currentTemperatureView.setTextSize(20);
                currentTemperatureView.setPadding(30, 0, 5, 0);

                if(i%2 == 0)
                    cityRow.setBackgroundResource(R.drawable.table_line_odd);
                else
                    cityRow.setBackgroundResource(R.drawable.table_line_even);

                cityRow.addView(cityNameView);
                cityRow.addView(currentTemperatureView);

                tableCities.addView(cityRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));

            }

            createTableFooter();

            progressBar.setVisibility(View.INVISIBLE);

        }

        private void createTableHeader(){

            TableRow headerRow = new TableRow(MainActivity.this);
            headerRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            TextView cityNameTitleView = new TextView(MainActivity.this);
            cityNameTitleView.setText(getResources().getString(R.string.table_header_city));
            cityNameTitleView.setTextSize(25);
            cityNameTitleView.setPadding(10, 0, 0, 0);
            cityNameTitleView.setTextColor(Color.WHITE);

            TextView currentTemperatureTitleView = new TextView(MainActivity.this);
            currentTemperatureTitleView.setText(getResources().getString(R.string.table_header_temperature));
            currentTemperatureTitleView.setTextSize(25);
            currentTemperatureTitleView.setPadding(30, 0, 5, 0);
            currentTemperatureTitleView.setTextColor(Color.WHITE);

            headerRow.setBackgroundResource(R.drawable.table_header);

            headerRow.addView(cityNameTitleView);
            headerRow.addView(currentTemperatureTitleView);

            tableCities.addView(headerRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

        }

        private void createTableFooter(){

            TableRow footerRow = new TableRow(MainActivity.this);
            footerRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            TextView cityNameTitleView = new TextView(MainActivity.this);
            cityNameTitleView.setText("");
            cityNameTitleView.setTextSize(20);
            cityNameTitleView.setPadding(10, 0, 0, 0);
            cityNameTitleView.setTextColor(Color.WHITE);

            TextView currentTemperatureTitleView = new TextView(MainActivity.this);
            currentTemperatureTitleView.setText("");
            currentTemperatureTitleView.setTextSize(20);
            currentTemperatureTitleView.setPadding(30, 0, 5, 0);
            currentTemperatureTitleView.setTextColor(Color.WHITE);

            footerRow.addView(cityNameTitleView);
            footerRow.addView(currentTemperatureTitleView);

            footerRow.setBackgroundResource(R.drawable.table_footer);

            tableCities.addView(footerRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

        }

    }

}
