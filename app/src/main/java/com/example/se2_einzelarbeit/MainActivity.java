package com.example.se2_einzelarbeit;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private EditText matNummer;
    private Button button1;
    private String result;
    private TextView response;

    private Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        matNummer = findViewById(R.id.input1);
        button1 = findViewById(R.id.button1);
        response = findViewById(R.id.textView5); // TextView response initialisieren
        button2 = findViewById(R.id.button2);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMatNummerToServer();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateGgt();
            }
        });

    }

    private void sendMatNummerToServer() {
        String matrikelNummer = matNummer.getText().toString();

        if (matrikelNummer.length() != 8 || !TextUtils.isDigitsOnly(matrikelNummer)) {
            Toast.makeText(MainActivity.this, "Die Matrikelnummer muss eine 8-stellige Zahl sein", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket("se2-submission.aau.at", 20080);
                    BufferedWriter stream1 = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                    stream1.write(matrikelNummer);
                    stream1.newLine();
                    stream1.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    result = reader.readLine();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            response.setText(result);
                        }
                    });
                    socket.close();
                }  catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void calculateGgt(){
        String matrikelNummer = matNummer.getText().toString();

        //String matikelNummer in int umwandeln
        int input = Integer.parseInt(matrikelNummer);



        }
    }
}