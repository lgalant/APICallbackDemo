package com.lagoonapps.okhttpdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    EditText ciudad;
    TextView presion,temp,mini,maxi,humedad;
    private final OkHttpClient client = new OkHttpClient();
    private String apiURL = "http://api.openweathermap.org/data/2.5/weather?q=";
    private String apiKey ="c2e2dceb025747dde9ec074a75e2d61c";
    private String params ="&units=metric";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ciudad = (EditText) findViewById(R.id.ciudad);
        presion = (TextView) findViewById(R.id.pressure);
        temp = (TextView) findViewById(R.id.temp);
        mini = (TextView) findViewById(R.id.mini);
        maxi = (TextView) findViewById(R.id.maxi);
        humedad = (TextView) findViewById(R.id.humedad);


    }

    public void getData(View v) {
        System.out.println("Buscando datos de :" + ciudad.getText());

        String url = apiURL + ciudad.getText() + "&APPID=" + apiKey + params;
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    String respuesta =responseBody.string();
                    System.out.println(respuesta);
                    Temperatura t = parsearResultado(respuesta);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            presion.setText(String.valueOf(t.getPressure()));
                            temp.setText(String.valueOf(t.getTemp()));
                            mini.setText(String.valueOf(t.getTemp_min()));
                            maxi.setText(String.valueOf(t.getTemp_max()));
                            humedad.setText(String.valueOf(t.getHumidity()));

                        }
                                  });

                }
            }
        });

    }


    private Temperatura parsearResultado(String respuesta) {

        Temperatura t=new Temperatura();
        JSONObject all, main;
        try {
             all = new JSONObject(respuesta);
             main = all.getJSONObject("main");
             System.out.println("main:" + main.toString());

             t.setTemp((float)main.getDouble("temp"));
             t.setPressure((float)main.getDouble("pressure"));
             t.setTemp_min((float)main.getDouble("temp_min"));
             t.setTemp_max((float)main.getDouble("temp_max"));
             t.setHumidity(main.getInt("humidity"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return t;
    }

    /*
    API key:
- Your API key is c2e2dceb025747dde9ec074a75e2d61c
- Within the next couple of hours, it will be activated and ready to use
- You can later create more API keys on your account page
- Please, always use your API key in each API call

Endpoint:
- Please, use the endpoint api.openweathermap.org for your API calls
- Example of API call:
api.openweathermap.org/data/2.5/weather?q=London,uk&APPID=c2e2dceb025747dde9ec074a75e2d61c

Useful links:
- API documentation https://openweathermap.org/api
- Details of your plan https://openweathermap.org/price
- Please, note that 16-days daily forecast and History API are not available for Free subscribers
     */
}
