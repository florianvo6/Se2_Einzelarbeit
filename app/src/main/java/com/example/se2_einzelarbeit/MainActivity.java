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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText matNummer;
    private Button button1;
    private String result;
    private TextView response;

    private Button button2;
    private List<Integer[]> resultGGT;

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

        //declare view variables
        matNummer = findViewById(R.id.input1);
        button1 = findViewById(R.id.button1);
        response = findViewById(R.id.textView5);
        button2 = findViewById(R.id.button2);

        //Define the two buttons with clickListener, which detect any click events and execute the code
        //inside the  onClick() method

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //calling method sendMatNumberToServer()
                sendMatNumberToServer();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save the result list with the integer arrays in resultGGT
                resultGGT = calculateGgt();
                //build the resultString output
                StringBuilder output = new StringBuilder();
                if (resultGGT.isEmpty()) {
                    output.append("No common divisors of pairs greater than 1 found");
                } else {
                    output.append("Indices of pairs with ggt: ");
                    //formating the String-Output by iterating through the List and using String.format
                    //each iteration will give us a Integer Array with length 2
                    for (Integer[] pair : resultGGT) {
                        output.append(String.format("(%d, %d) ", pair[0], pair[1]));
                    }
                }
                response.setText(output.toString());
            }
        });

    }

    private void sendMatNumberToServer() {
        //get the String value of matNummer
        String matrikelNummer = matNummer.getText().toString();

        //check if input is an number of 8 digits, !(TextUtils.isDigitsOnly()) to check if the input
        //contains not only digits

        if (matrikelNummer.length() != 8 || !TextUtils.isDigitsOnly(matrikelNummer)) {
            Toast.makeText(MainActivity.this, "Die Matrikelnummer muss eine 8-stellige Zahl sein", Toast.LENGTH_SHORT).show();
            return;
        }

        //create a new thread to perform network operations in the background and not in the main thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //crate a new socket to conect to the server
                    Socket socket = new Socket("se2-submission.aau.at", 20080);
                    //create a new BufferedWriter to write to the socket's output stream
                    BufferedWriter stream1 = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                    //send the matrikelNummer string to the server
                    stream1.write(matrikelNummer);
                    stream1.newLine();
                    stream1.flush();

                    //create a new BufferedReader to read from the socket's input stream
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    //read the response from the server and store it in result
                    result = reader.readLine();


                    //create a new Runnable to update the UI-changes on the main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        // set the text of the response TextView to the result string
                        public void run() {
                            response.setText(result);
                        }
                    });
                    socket.close();
                }  catch (Exception e) {
                    //in cause of errors the Exception message will be displayed
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //calculate the GCD of each pair of digits in the matrikelNummer string
    private List<Integer[]> calculateGgt(){
        String matrikelNummer = matNummer.getText().toString();

        //create a new ArrayList to store the results
        List<Integer[]> result = new ArrayList<>();

        //iterate over each digit in the matrikelNummer string
        for (int i = 0; i < matrikelNummer.length(); i++) {
            //iterate over each digit after the current digit
            for (int j = i+1; j < matrikelNummer.length(); j++){
                //convert the current and next digits to integers
                int a = Integer.parseInt(matrikelNummer.substring(i, i+1));
                int b = Integer.parseInt(matrikelNummer.substring(j, j+1));
                //calculate the gcd of the two integers
                int gcd = gcd(a, b);
                //if the gcd is greater than 1, add the indices of the digits to the result list
                if (gcd > 1) {
                    result.add(new Integer[]{i, j});
                }
            }
        }
        return result;
    }

    private static int gcd(int a, int b) {
        //if the second integer is 0, return the first integer as the gcd
        if (b == 0) {
            return a;
        }
        // recursive call of the gcd() method with the second integer as a and the remainder of the first
        // integer divided by the second integer as b
        return gcd(b, a % b);
    }
}