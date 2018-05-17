package jp.techacademy.rei.tanaka.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    ImageView imageView;
    Cursor cursor;

    Timer mTimer;
    double mTimerSec = 0.0;
    Handler mHandler = new Handler();

    Button backButton;
    Button nextButton;
    Button startStopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getContentsInfo();
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            getContentsInfo();
        }

        final Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cursor.isFirst()) {
                    cursor.moveToLast();
                    int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    Long id = cursor.getLong(fieldIndex);
                    Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                    imageView = findViewById(R.id.imageView);
                    imageView.setImageURI(imageUri);
                } else {
                    cursor.moveToPrevious();
                    int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    Long id = cursor.getLong(fieldIndex);
                    Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                    imageView = findViewById(R.id.imageView);
                    imageView.setImageURI(imageUri);
                }
            }
                                      });

        final Button nextButton =findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cursor.isLast()) {
                    cursor.moveToFirst();
                    int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    Long id = cursor.getLong(fieldIndex);
                    Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                    imageView = findViewById(R.id.imageView);
                    imageView.setImageURI(imageUri);
                } else {
                    cursor.moveToNext();
                    int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    Long id = cursor.getLong(fieldIndex);
                    Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                    imageView = findViewById(R.id.imageView);
                    imageView.setImageURI(imageUri);
                }
            }
        });

        final Button startStopButton =findViewById(R.id.startStopButton);
        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if (mTimer != null) {
                    mTimer.cancel();
                    mTimer = null;
                    startStopButton.setText("再生");
                    backButton.setEnabled(true);
                    nextButton.setEnabled(true);
                } else if (mTimer == null) {
                    mTimer = new Timer();
                    startStopButton.setText("停止");
                    backButton.setEnabled(false);
                    nextButton.setEnabled(false);
                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mTimerSec += 2.0;

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (cursor.isLast()) {
                                        cursor.moveToFirst();
                                        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                                        Long id = cursor.getLong(fieldIndex);
                                        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                                        imageView = findViewById(R.id.imageView);
                                        imageView.setImageURI(imageUri);
                                    } else {
                                        cursor.moveToNext();
                                        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                                        Long id = cursor.getLong(fieldIndex);
                                        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                                        imageView = findViewById(R.id.imageView);
                                        imageView.setImageURI(imageUri);
                                    }
                                }
                            });
                        }
                    }, 2000, 2000);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                } else {
                    Toast.makeText(this, "許可が無いと利用できません", Toast.LENGTH_LONG).show();
                    backButton =findViewById(R.id.backButton);
                    backButton.setEnabled(false);

                    startStopButton = findViewById(R.id.startStopButton);
                    startStopButton.setEnabled(false);

                    nextButton = findViewById(R.id.nextButton);
                    nextButton.setEnabled(false);
                }
                break;
            default:
                break;
        }
    }

    private void getContentsInfo(){
        ContentResolver resolver = getContentResolver();
         cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            imageView = findViewById(R.id.imageView);
            imageView.setImageURI(imageUri);
        }
    }

  @Override
    protected void onDestroy() {
      super.onDestroy();
      cursor.close();
  }

}
