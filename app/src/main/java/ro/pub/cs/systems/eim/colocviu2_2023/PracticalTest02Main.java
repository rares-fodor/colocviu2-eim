package ro.pub.cs.systems.eim.colocviu2_2023;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ro.pub.cs.systems.eim.colocviu2_2023.Network.ClientThread;
import ro.pub.cs.systems.eim.colocviu2_2023.Network.ServerThread;

public class PracticalTest02Main extends AppCompatActivity {

    private EditText serverPortEditText;
    private EditText clientPortEditText;
    private EditText addressEditText;
    private EditText cityEditText;

    private Button connectButton;
    private Button getWeatherDataButton;

    private Spinner dataTypeSpinner;

    private ServerThread serverThread;
    private ClientThread clientThread;

    private TextView resultTextView;

    private ConnectButtonOnClickListener connectButtonOnClickListener = new ConnectButtonOnClickListener();
    private class ConnectButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIV] Type a server port!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }

            Log.i(Constants.TAG, "============================" + serverThread.getServerSocket().getInetAddress());
            serverThread.start();
        }
    }

    private GetWeatherDataButtonListener getWeatherDataButtonListener = new GetWeatherDataButtonListener();
    private class GetWeatherDataButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String clientAddress = addressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();
            if (clientAddress.isEmpty() || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }
            String city = cityEditText.getText().toString();
            String dataType = dataTypeSpinner.getSelectedItem().toString();
            if (city.isEmpty() || dataType.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (city / information type) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            resultTextView.setText(Constants.EMPTY_STRING);

            clientThread = new ClientThread(
                    clientAddress, Integer.parseInt(clientPort), city, dataType, resultTextView
            );
            clientThread.start();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        serverPortEditText = findViewById(R.id.server_port_edit_text);
        clientPortEditText = findViewById(R.id.client_port_edit_text);
        addressEditText = findViewById(R.id.address_edit_text);
        cityEditText = findViewById(R.id.city_edit_text);

        connectButton = findViewById(R.id.connect_button);
        connectButton.setOnClickListener(connectButtonOnClickListener);
        getWeatherDataButton = findViewById(R.id.get_weather_button);
        getWeatherDataButton.setOnClickListener(getWeatherDataButtonListener);

        dataTypeSpinner = findViewById(R.id.information_type_spinner);

        resultTextView = findViewById(R.id.result_text_view);
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}