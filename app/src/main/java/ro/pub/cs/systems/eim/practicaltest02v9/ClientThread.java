package ro.pub.cs.systems.eim.practicaltest02v9;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {
    private final String address;
    private final int port;
    private final String word;
    private final String nr;

    private Socket socket;
    private TextView resultTV;

    public ClientThread(String address, int port, String word, TextView resultTV, String nr) {
        this.address = address;
        this.port = port;
        this.word = word;
        this.resultTV = resultTV;
        this.nr = nr;
    }

    @Override
    public void run() {
        String error = "Error";
        try {
            socket = new Socket(address, port);
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            String all = word + " " + nr;
            printWriter.println(all);
            printWriter.flush();

            String definition = bufferedReader.readLine();
            if (resultTV != null) {
                resultTV.post(new Runnable() {
                    @Override
                    public void run() {
                        resultTV.setText(definition);
                    }
                });
            }
            Log.i("[PracticalTest02]", "[CLIENT THREAD] Received definition: " + definition);

        } catch (IOException ioException) {
            Log.e("[PracticalTest02]", "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e("[PracticalTest02]", "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    resultTV.post(new Runnable() {
                        @Override
                        public void run() {
                            resultTV.setText(error);
                        }
                    });
                }
            }
        }
    }
}
