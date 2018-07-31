package com.example.nervian.opencvone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private final int SELECT_PHOTO = 1;
    private ImageView ivImage, ivImageProcessed;
    Mat src;
    static int ACTION_MODE = 0;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Android specific code

        ivImage = (ImageView) findViewById(R.id.ivImage);

        ivImageProcessed = (ImageView) findViewById(R.id.ivImageProcessed);

        Intent intent = getIntent();

        if (intent.hasExtra("ACTION_MODE"))

        {

            ACTION_MODE = intent.getIntExtra("ACTION_MODE", 0);

        }

        if (OpenCVLoader.initDebug()) {
            Toast.makeText(getApplicationContext(), "OpenCV loaded successfully", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getApplicationContext(), "OpenCV not loaded", Toast.LENGTH_SHORT).show();
        }




    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public void selectPicture(View view) {
        {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

            photoPickerIntent.setType("image/*");

            startActivityForResult(photoPickerIntent, SELECT_PHOTO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (resultCode == RESULT_OK) {
            try {
//Code to load image into a Bitmap and convert it to a Mat for processing.
                final Uri imageUri = imageReturnedIntent.getData();
                final InputStream imageStream =
                        getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage =
                        BitmapFactory.decodeStream(imageStream);
                src = new Mat(selectedImage.getHeight(),
                        selectedImage.getWidth(), CvType.CV_8UC4);
                Utils.bitmapToMat(selectedImage, src);
                Imgproc.medianBlur(src, src, 43);

                Bitmap processedImage = Bitmap.createBitmap(src.cols(),
                        src.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(src, processedImage);
                ivImage.setImageBitmap(selectedImage);
                ivImageProcessed.setImageBitmap(processedImage);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

    }
}

