package ro.pub.cs.systems.eim.practicaltest02v9;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

public class ServerThread extends Thread {
    private ServerSocket serverSocket = null;

    private String data;

    private HashMap<String, Long> clientTimers = new HashMap<>();

    public ServerThread(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException ioException) {
            Log.e("[PracticalTest02]", "An exception has occurred: " + ioException.getMessage());
        }
        this.data = "";
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public synchronized void setData(String word) {
        this.data = word;
    }


    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Log.i("[PracticalTest02]", "[SERVER THREAD] Waiting for a client invocation...");
                Socket socket = serverSocket.accept();
                Log.i("[PracticalTest02]", "[SERVER THREAD] A connection request was received from " + socket.getInetAddress() + ":" + socket.getLocalPort());
                CommunicationThread communicationThread = new CommunicationThread(this, socket);
                communicationThread.start();
            }
        } catch (IOException ioException) {
            Log.e("[PracticalTest02]", "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
        }
    }

    public void stopThread() {
        interrupt();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                Log.e("[PracticalTest02]", "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
            }
        }
    }
}
