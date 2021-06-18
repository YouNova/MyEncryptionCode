package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.textView);
        String variableData = "Data to encrypt here";
        String finalResult = "Before encryption:\n\n";

        //create a file
        try {
            File file = new File("FileName.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //write in text file
        writeToTxt(variableData);

        //read from text file
        finalResult += readFromTxt() + "\n\nAfter encryption:\n\n";

        //encryption -> read file to string>convert to byte>generate a key>encrypt with hashed key>convert to string>write to text file
        try {
            String encryptedOutput = encryptFunct();
            writeToTxt(encryptedOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //read from text file
        finalResult += readFromTxt() + "\n\nAfter decryption:\n\n";

        //decryption
        try {
            String decryptedOutput = decryptFunc();
            writeToTxt(decryptedOutput);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        //read from text file
        finalResult += readFromTxt();

        textView.setText(finalResult);
    }

    private String decryptFunc() throws Exception {
        String decryptInputText = readFromTxt();
        SecretKeySpec decryptionKey = hashKeyGenerator();
        @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE,decryptionKey);
        byte[] decVal = Base64.decode(decryptInputText,Base64.DEFAULT);
        byte[] decryptedOutput = cipher.doFinal(decVal);
        return new String(decryptedOutput);
    }

    private String encryptFunct() throws Exception {
        String encryptInputText = readFromTxt();
        SecretKeySpec encryptionKey = hashKeyGenerator();
        @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE,encryptionKey);
        byte[] encVal = cipher.doFinal(encryptInputText.getBytes());
        return Base64.encodeToString(encVal,Base64.DEFAULT);
    }

    private void writeToTxt(String prescription) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("FileName.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(prescription);
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFromTxt() {
        String result = "";
        InputStream inputStream = null;
        try {
            inputStream = openFileInput("FileName.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (inputStream != null) {

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String tempString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while (true) {
                try {
                    if ((tempString = bufferedReader.readLine()) == null) {
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                stringBuilder.append(tempString).append("\n");

            }

            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            result = stringBuilder.toString();
        }
        return result;
    }

    private SecretKeySpec hashKeyGenerator() throws Exception {
        byte[] inputData = "your 10digitEncryptionKeyHere".getBytes();
        byte[] outputData;
        MessageDigest shaVal = MessageDigest.getInstance("SHA-256");
        shaVal.update(inputData);
        outputData = shaVal.digest();
        return new SecretKeySpec(outputData, "AES");
    }
}

