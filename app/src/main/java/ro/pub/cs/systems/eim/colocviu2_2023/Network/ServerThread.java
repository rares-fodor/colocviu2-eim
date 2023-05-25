package ro.pub.cs.systems.eim.colocviu2_2023.Network;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import cz.msebera.android.httpclient.client.ClientProtocolException;
import ro.pub.cs.systems.eim.colocviu2_2023.Constants;
import ro.pub.cs.systems.eim.colocviu2_2023.Model.WeatherData;

public class ServerThread extends Thread {

    private int port = 0;
    private ServerSocket serverSocket = null;

    private HashMap<String, WeatherData> data = null;

    public ServerThread(int port) {
        this.port = port;
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            Log.e(Constants.TAG, "An exception has occurred: " + e.getMessage());
            if (Constants.DEBUG) {
                e.printStackTrace();
            }
        }
        this.data = new HashMap<>();
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Log.i(Constants.TAG, "[SERVER THREAD] Waiting for a client invocation...");
                Socket socket = serverSocket.accept();
                Log.i(Constants.TAG, "[SERVER THREAD] A connection request was received from " + socket.getInetAddress() + ":" + socket.getLocalPort());
                CommThread commThread = new CommThread(this, socket);
                commThread.start();
            }
        } catch (ClientProtocolException clientProtocolException) {
            Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + clientProtocolException.getMessage());
            if (Constants.DEBUG) {
                clientProtocolException.printStackTrace();
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
    }

    public void stopThread() {
        interrupt();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public synchronized HashMap<String, WeatherData> getData() {
        return data;
    }

    public synchronized void setData(String city, WeatherData data) {
        this.data.put(city, data);
    }
}
