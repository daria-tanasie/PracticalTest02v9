package ro.pub.cs.systems.eim.practicaltest02v9;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02v9MainActivity extends AppCompatActivity {

    private EditText serverPortET;
    private EditText clientPortET;
    private EditText addressET;
    private EditText wordET;
    private EditText nrET;
    private TextView def;
    private ServerThread serverThread;

    private final ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();
    private class ConnectButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String serverPort = serverPortET.getText().toString();
            if (serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e("[PracticalTest02]", "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }

    }

    private final GetCommButtonClickListener getCommButtonClickListener = new GetCommButtonClickListener();
    private class GetCommButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String word = wordET.getText().toString();
            String clientAddress = addressET.getText().toString();
            String clientPort = clientPortET.getText().toString();
            String nr = nrET.getText().toString();
            if (clientAddress.isEmpty() || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            ClientThread clientThread = new ClientThread(
                    clientAddress, Integer.parseInt(clientPort), word, def, nr);
            clientThread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_practical_test02v9_main);

        serverPortET = findViewById(R.id.server_port_edit_text);
        Button connectButton = findViewById(R.id.connect_button);
        connectButton.setOnClickListener(connectButtonClickListener);


        clientPortET = findViewById(R.id.client_port_edit_text);
        addressET = findViewById(R.id.client_address_edit_text);
        wordET = findViewById(R.id.word_edit_text);
        nrET = findViewById(R.id.nr_edit_text);
        Button getAutoButton = findViewById(R.id.get_com_button);
        getAutoButton.setOnClickListener(getCommButtonClickListener);
        def = findViewById(R.id.def_text_view);

    }

    @Override
    protected void onDestroy() {
        Log.i("[PracticalTest02]", "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }

}