package ro.pub.cs.systems.eim.practicaltest02v9;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

public class CommunicationThread extends Thread {
    private final ServerThread serverThread;
    private final Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            Log.i("[PracticalTest02]", "[COMMUNICATION THREAD] Waiting for parameters from client!");
            String word = bufferedReader.readLine();
            if (word == null || word.isEmpty()) {
                Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] Error receiving parameters from client!");
                return;
            }

            String result = null;

            String[] parts = word.split(" ");
            String toAnag = parts[0].trim().toLowerCase();
            int nr = Integer.parseInt(parts[1].trim());

            try {
                Log.i("[PracticalTest02]", "[COMMUNICATION THREAD] Getting the information from the webservice..." + toAnag);
                HttpClient httpClient = new DefaultHttpClient();
                String pageSourceCode = "";
                HttpGet httpGet = new HttpGet("http://www.anagramica.com/all/:" + toAnag);
                HttpResponse httpGetResponse = httpClient.execute(httpGet);
                HttpEntity httpGetEntity = httpGetResponse.getEntity();

                if (httpGetEntity != null) {
                    pageSourceCode = EntityUtils.toString(httpGetEntity);
                }

                JSONObject jsonObject = new JSONObject(pageSourceCode);
                JSONArray all = jsonObject.getJSONArray("all");
                StringBuilder finalResult = new StringBuilder();
                for (int i = 0; i < all.length(); i++) {
                    if (all.getString(i).length() <= nr) {
                        finalResult.append(all.getString(i)).append(", ");
                    }
                    Log.i("[PracticalTest02]", "[COMMUNICATION THREAD] Anagrame: " + all.getString(i));
                }

                result = finalResult.toString();
                Log.i("[PracticalTest02]", "[COMMUNICATION THREAD] Result from server: " + all.toString());
            } catch (Exception e) {
                Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] Error getting data!");
            }


            printWriter.println(result);
            printWriter.flush();
        } catch (IOException ioException) {
            Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ioException) {
                Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            }
        }
    }
}
