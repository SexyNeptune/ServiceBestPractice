package com.example.servicebestpractice;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    static final String CHANNEL_1 = "1";

    private DownloadService.DownloadBinder downloadBinder;


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            downloadBinder = (DownloadService.DownloadBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NotificationChannel channel = new NotificationChannel("1", "通道一", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
        Button startDownload = findViewById(R.id.start_download);
        Button pauseDownload = findViewById(R.id.pause_download);
        Button cancelDownload = findViewById(R.id.cancel_download);
        startDownload.setOnClickListener(this);
        pauseDownload.setOnClickListener(this);
        cancelDownload.setOnClickListener(this);
        Intent intent = new Intent(this,DownloadService.class);
        startService(intent);//启动服务
        bindService(intent,connection,BIND_AUTO_CREATE);//绑定服务
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    @Override
    public void onClick(View view) {
        if(downloadBinder ==null){
            return ;
        }
        switch(view.getId()){
            case R.id.start_download:
                String url = "https://gss2.bdstatic.com/9fo3dSag_xI4khGkpoWK1HF6hhy/baike/c0%3Dbaike80%2C5%2C5%2C80%2C26/sign=de2947a2aacc7cd9ee203c8b58684a5a/d50735fae6cd7b89ecd766cc062442a7d8330ec1.jpg";
                downloadBinder.startDownload(url);
                break;
            case R.id.pause_download:
                downloadBinder.pauseDownload();
                break;
            case R.id.cancel_download:
                downloadBinder.cancelDownload();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1:
                if(grantResults.length > 0 &&grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"拒绝访问权限将无法使程序运行",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
