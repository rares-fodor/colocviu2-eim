package ro.pub.cs.systems.eim.colocviu2_2023.Network;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import ro.pub.cs.systems.eim.colocviu2_2023.Constants;
import ro.pub.cs.systems.eim.colocviu2_2023.Utilities;

public class ClientThread extends Thread {

    private String address;
    private int port;
    private String city;
    private String dataType;
    private TextView weatherDataView;

    private Socket socket;

    public ClientThread(String address, int port, String city, String dataType, TextView weatherDataView) {
        this.address = address;
        this.port = port;
        this.city = city;
        this.dataType = dataType;
        this.weatherDataView = weatherDataView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address,port);
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            printWriter.println(city);
            printWriter.flush();
            printWriter.println(dataType);
            printWriter.flush();

            String weatherInformation;
            while ((weatherInformation = bufferedReader.readLine()) != null) {
                final String finalizedInfo = weatherInformation;
                weatherDataView.post(new Runnable() {
                    @Override
                    public void run() {
                        weatherDataView.setText(finalizedInfo);
                    }
                });
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + e.getMessage());                }
            }
        }
    }
}
