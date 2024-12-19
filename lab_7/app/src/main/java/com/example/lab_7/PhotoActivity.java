package com.example.lab_7;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.OutputStream;

public class PhotoActivity extends AppCompatActivity {

    private static final int REQUEST_PHOTO_CAPTURE = 3;
    private static final int REQUEST_PHOTO_PICK = 4;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        Button buttonCapturePhoto = findViewById(R.id.button_capture_photo);
        Button buttonChoosePhoto = findViewById(R.id.button_choose_photo);
        imageView = findViewById(R.id.imageView_photo);
        Button buttonBackToMain = findViewById(R.id.button_back_to_main);
        buttonCapturePhoto.setOnClickListener(view -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_PHOTO_CAPTURE);
            } else {
                Toast.makeText(this, "Камера недоступна", Toast.LENGTH_SHORT).show();
            }
        });

        buttonChoosePhoto.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_PHOTO_PICK);
        });

        buttonBackToMain.setOnClickListener(view -> {
            Intent intent = new Intent(PhotoActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_PHOTO_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imageView.setImageBitmap(imageBitmap);
                savePhotoToGallery(imageBitmap);
                Toast.makeText(this, "Фотография сохранена в галерею", Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_PHOTO_PICK) {
                Uri selectedImageUri = data.getData();
                imageView.setImageURI(selectedImageUri);
            }
        }
    }

    private void savePhotoToGallery(Bitmap bitmap) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "Photo_" + System.currentTimeMillis() + ".jpg");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}