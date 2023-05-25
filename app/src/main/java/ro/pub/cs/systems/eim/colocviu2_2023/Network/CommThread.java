package ro.pub.cs.systems.eim.colocviu2_2023.Network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Entities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;
import ro.pub.cs.systems.eim.colocviu2_2023.Constants;
import ro.pub.cs.systems.eim.colocviu2_2023.Model.WeatherData;
import ro.pub.cs.systems.eim.colocviu2_2023.Utilities;

public class CommThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[Comm Thread] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            Log.i(Constants.TAG, "[Comm Thread] Waiting for params from client");

            String city = bufferedReader.readLine();
            String dataType = bufferedReader.readLine();

            if (city == null || city.isEmpty() || dataType == null || dataType.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type!");
                return;
            }

            HashMap<String, WeatherData> data = serverThread.getData();
            WeatherData weatherData = null;

            if (data.containsKey(city)) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                weatherData = data.get(city);
            } else {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");

                HttpClient httpClient = new DefaultHttpClient();
                String pageSource = "";

                HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS + "?q=" + city + "&APPID=" + Constants.WEB_SERVICE_API_KEY + "&units=" + Constants.UNITS);

                HttpResponse httpGetResponse = httpClient.execute(httpGet);

                HttpEntity httpGetEntitiy = httpGetResponse.getEntity();
                if (httpGetEntitiy != null) {
                    pageSource = EntityUtils.toString(httpGetEntitiy);
                }

                Log.i(Constants.TAG, pageSource);

                JSONObject content = new JSONObject(pageSource);
                JSONArray weatherArray = content.getJSONArray(Constants.WEATHER);
                JSONObject weather;

                StringBuilder condition = new StringBuilder();
                for (int i = 0; i < weatherArray.length(); i++) {
                    weather = weatherArray.getJSONObject(i);
                    condition.append(weather.getString(Constants.MAIN)).append(" : ").append(weather.getString(Constants.DESCRIPTION));

                    if (i < weatherArray.length() - 1) {
                        condition.append(";");
                    }
                }

                JSONObject main = content.getJSONObject(Constants.MAIN);
                String temperature = main.getString(Constants.TEMP);
                String pressure = main.getString(Constants.PRESSURE);
                String humidity = main.getString(Constants.HUMIDITY);

                JSONObject wind = content.getJSONObject(Constants.WIND);
                String windSpeed = wind.getString(Constants.SPEED);

                weatherData = new WeatherData(
                        temperature, windSpeed, condition.toString(), pressure, humidity
                );
                serverThread.setData(city, weatherData);
            }

            if (weatherData == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Weather Forecast Information is null!");
                return;
            }

            String result = null;
            switch(dataType) {
                case Constants.ALL:
                    result = weatherData.toString();
                    break;
                case Constants.TEMPERATURE:
                    result = weatherData.getTemperature();
                    break;
                case Constants.WIND_SPEED:
                    result = weatherData.getWind();
                    break;
                case Constants.CONDITION:
                    result = weatherData.getStatus();
                    break;
                case Constants.HUMIDITY:
                    result = weatherData.getHumidity();
                    break;
                case Constants.PRESSURE:
                    result = weatherData.getPressure();
                    break;
                default:
                    result = "[COMMUNICATION THREAD] Wrong information type (all / temperature / wind_speed / condition / humidity / pressure)!";
            }
            printWriter.println(result);
            printWriter.flush();

        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } catch (JSONException jsonException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + jsonException.getMessage());
            if (Constants.DEBUG) {
                jsonException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}
