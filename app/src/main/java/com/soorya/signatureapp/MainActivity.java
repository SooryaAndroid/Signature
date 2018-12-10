package com.soorya.signatureapp;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    private SignatureView signatureView;
    private Button saveButton, clearButton, getButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signatureView = findViewById(R.id.signature_view);
        saveButton = findViewById(R.id.button_save);
        clearButton = findViewById(R.id.button_clear);
        getButton = findViewById(R.id.button_get);
        saveButtonClick();
        clearButtonClick();
        getButtonClick();
       // signatureTouchEvent();

    }

    private void getButtonClick() {
        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("SIGN", MODE_PRIVATE);
                String val = prefs.getString("sign", null);

                Bitmap workingBitmap = Bitmap.createBitmap(StringToBitMap(val));
                Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
                signatureView.setBitmap(mutableBitmap);
            }
        });
    }

    private void clearButtonClick() {
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signatureView.clearCanvas();
            }
        });
    }

    private void saveButtonClick() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = signatureView.getSignatureBitmap();
                Log.e("bitmapss", BitMapToString(bitmap));
                SharedPreferences.Editor editor = getSharedPreferences("SIGN", MODE_PRIVATE).edit();
                editor.putString("sign", BitMapToString(bitmap));
                editor.apply();
            }
        });
    }
    @SuppressLint("ClickableViewAccessibility")
    // scrollview issue in signature
    private void signatureTouchEvent() {
        signatureView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                //signatureStat = true;
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                v.onTouchEvent(event);
                return true;
            }
        });
    }
    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] arr = baos.toByteArray();
        String result = Base64.encodeToString(arr, Base64.DEFAULT);
        return result;
    }

    public Bitmap StringToBitMap(String image) {
        try {
            byte[] encodeByte = Base64.decode(image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}
