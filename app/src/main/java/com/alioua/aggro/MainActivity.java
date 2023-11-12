package com.alioua.aggro;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private TextView textView;
    private Button sendButton;
    private ImageView arrowAvance,arrowRecul,arrowDroit,arrowGauche;
    private static final String SERVER_IP = "192.168.4.1"; // Adresse IP de l'ESP32
    private static final int SERVER_PORT = 80;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        arrowAvance = findViewById(R.id.arrow_avance);
        arrowRecul = findViewById(R.id.arrow_recule);
        arrowDroit = findViewById(R.id.arrow_droit);
        arrowGauche = findViewById(R.id.arrow_gauche);
        //editText = findViewById(R.id.editText);
        //textView = findViewById(R.id.textView);
        //sendButton = findViewById(R.id.sendButton);

        // Communication avec l'ESP32 au lancement de l'application
        new CommunicateWithESP32().execute();
        arrowAvance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //String messageAvance =  new String("A");
                new CommunicateWithESP32().execute("A");
            }
        });
        arrowRecul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //String messageRecul =  new String("R");
                new CommunicateWithESP32().execute("R");
            }
        });
        arrowDroit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //String messageDroit =  new String("D");
                new CommunicateWithESP32().execute("D");
            }
        });
        arrowGauche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new CommunicateWithESP32().execute("G");
            }
        });
        // Action lorsque le bouton est cliqué
        /*sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editText.getText().toString();
                new CommunicateWithESP32().execute(message);
            }
        });*/
    }

    private class CommunicateWithESP32 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String response = "";

            try {
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);

                // Si un message est fourni en paramètre, envoyez-le à l'ESP32
                if (params.length > 0) {
                    sendToESP32(socket, params[0]);
                }

                // Recevoir la réponse de l'ESP32
                response = receiveFromESP32(socket);

                // Fermer la connexion
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                response = "Error: " + e.getMessage();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // Mettez à jour l'interface utilisateur avec la réponse de l'ESP32
            textView.setText(result);

            // TODO: Gérez la réponse de l'ESP32 selon les besoins de votre application
        }

        private void sendToESP32(Socket socket, String data) throws IOException {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(data);
        }

        private String receiveFromESP32(Socket socket) throws IOException {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return in.readLine();
        }
    }
}