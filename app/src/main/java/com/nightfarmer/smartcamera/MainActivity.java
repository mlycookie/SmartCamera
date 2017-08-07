package com.nightfarmer.smartcamera;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    TextView result_label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        result_label = (TextView) findViewById(R.id.result_label);
    }

    public void jump(View view) {
        startActivityForResult(new Intent(this, CameraActivity.class), 1);
    }

    private static final String DIR_NAME = "AVRecSample";

    String resultFilePath = "";

    public void open(View view) {
//        final File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), DIR_NAME);
//        File file = new File(dir, "demo" + ".mp4");
        if (TextUtils.isEmpty(resultFilePath)) {
            Toast.makeText(this, "没结果", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
//获取文件file的MIME类型
        File file = new File(resultFilePath);
        String type;
        if (file.getName().endsWith(".mp4")) {
            type = "video/mp4";
        } else {
            type = "image/jpeg";
        }
//设置intent的data和Type属性。
//        Uri uri = Uri.fromFile(file);
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

//        ContentValues contentValues = new ContentValues(1);
//        contentValues.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
//        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileProvider", file);
            intent.setDataAndType(uri, type);
        } else {
            intent.setDataAndType(Uri.fromFile(file), type);
        }

//跳转
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            resultFilePath = data.getStringExtra("path");
        } else {
            resultFilePath = "";
        }
        result_label.setText("" + resultFilePath);
    }

    public void check(View view) {
        ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean permission;
        permission = ActivityCompat.checkSelfPermission(this, PERMISSIONS_STORAGE[0]) == PackageManager.PERMISSION_GRANTED;
        permission = permission && (ActivityCompat.checkSelfPermission(this, PERMISSIONS_STORAGE[1]) == PackageManager.PERMISSION_GRANTED);
        permission = permission && (ActivityCompat.checkSelfPermission(this, PERMISSIONS_STORAGE[2]) == PackageManager.PERMISSION_GRANTED);
        permission = permission && (ActivityCompat.checkSelfPermission(this, PERMISSIONS_STORAGE[3]) == PackageManager.PERMISSION_GRANTED);
        if (!permission) {
            check(null);
        }
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };
}