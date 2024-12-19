package com.example.lab_6;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private Button downloadButton, viewButton, deleteButton;
    private EditText journalIdInput;
    private File targetFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        journalIdInput = findViewById(R.id.journalIdInput);
        downloadButton = findViewById(R.id.downloadButton);
        viewButton = findViewById(R.id.viewButton);
        deleteButton = findViewById(R.id.deleteButton);

        showPopupWindow();

        downloadButton.setOnClickListener(v -> {
            String journalId = journalIdInput.getText().toString().trim();
            if (journalId.isEmpty()) {
                Toast.makeText(MainActivity.this, "Введите ID журнала", Toast.LENGTH_SHORT).show();
                return;
            }
            String fileUrl = "https://ntv.ifmo.ru/file/journal/" + journalId + ".pdf";
            String fileName = "journal_" + journalId + ".pdf";
            new DownloadFileTask(MainActivity.this).execute(fileUrl, fileName);
        });

        viewButton.setOnClickListener(v -> {
            if (targetFile != null && targetFile.exists()) {
                Uri pdfUri = FileProvider.getUriForFile(MainActivity.this, MainActivity.this.getPackageName() + ".fileprovider", targetFile);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(pdfUri, "application/pdf");
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Grant permission to read the URI
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Файл не найден", Toast.LENGTH_SHORT).show();
            }
        });

        deleteButton.setOnClickListener(v -> {
            if (targetFile.exists()) {
                targetFile.delete();
                viewButton.setEnabled(false);
                deleteButton.setEnabled(false);
                Toast.makeText(MainActivity.this, "Файл удален", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPopupWindow() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean dontShowAgain = prefs.getBoolean("dontShowAgain", false);

        if (!dontShowAgain) {
            journalIdInput.post(() -> {
                View popupView = LayoutInflater.from(this).inflate(R.layout.popup_layout, null);
                final PopupWindow popupWindow = new PopupWindow(popupView,
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT,
                        true
                );

                CheckBox dontShowCheckBox = popupView.findViewById(R.id.checkbox);
                Button okButton = popupView.findViewById(R.id.ok_button);

                okButton.setOnClickListener(v -> {
                    if (dontShowCheckBox.isChecked()) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("dontShowAgain", true);
                        editor.apply();
                    }
                    popupWindow.dismiss();
                });

                popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
            });
        }
    }

    private class DownloadFileTask extends AsyncTask<String, Void, Boolean> {

        private Context context;
        private File folder;

        public DownloadFileTask(Context context) {
            this.context = context;
            folder = new File(context.getExternalFilesDir(null), "Journals");
            if (!folder.exists()) {
                folder.mkdirs();
            }
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            String fileUrl = urls[0];
            String fileName = urls[1];
            targetFile = new File(folder, fileName);

            try {
                URL url = new URL(fileUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    FileOutputStream outputStream = new FileOutputStream(targetFile);

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    outputStream.close();
                    inputStream.close();
                    return true;
                } else {
                    InputStream errorStream = connection.getErrorStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
                    String firstLine = reader.readLine();
                    if (firstLine.contains("<!DOCTYPE html>")) {
                        return false;
                    }
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(context, "Файл загружен", Toast.LENGTH_SHORT).show();
                viewButton.setEnabled(true);
                deleteButton.setEnabled(true);
            } else {
                Toast.makeText(context, "Файл не найден", Toast.LENGTH_SHORT).show();
            }
        }
    }
}