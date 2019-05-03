package com.project.txtfile_test1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String path;//저장하는 곳의 경로
    private EditText ed1;//제목 적는 EditText
    private EditText ed2;//내용 적는 EditText
    private Button bt1;//저장하기 버튼
    private Button bt2;//불러오기 버튼
    private Button bt3;//삭제하기 버튼

    private static final int MY_PERMISSION_STORAGE = 1111;//퍼미션 요구

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();//퍼미션 확인

        path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"/TEST_TEXT_WRITE";

        ed1 = (EditText)findViewById(R.id.ed1);
        ed2 = (EditText)findViewById(R.id.ed2);
        bt1 = (Button)findViewById(R.id.bt1);
        bt2 = (Button)findViewById(R.id.bt2);
        bt3 = (Button)findViewById(R.id.bt3);

        bt1.setOnClickListener(btListener);
        bt2.setOnClickListener(btListener);
        bt3.setOnClickListener(btListener);
    }
    Button.OnClickListener btListener = new Button.OnClickListener(){
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch(v.getId()){
                case R.id.bt1:
                    String ed1text = ed1.getText().toString().trim();
                    if(ed1text.length()>0){
                        onTextWriting(ed1text,ed2.getText().toString());
                    }
                    break;
                case R.id.bt2:
                    onTextRead();
                    break;
                case R.id.bt3:
                    deltext();
                    break;
            }
        }

    };
    private void onTextWriting(String title,String body){
        File file;
        file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
        file = new File(path+File.separator+title+".txt");
        try{
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter buw = new BufferedWriter(new OutputStreamWriter(fos, "UTF8"));
            buw.write(body);
            buw.close();
            fos.close();
            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
        }catch(IOException e){

        }
    }
    private void onTextRead(){
        final ArrayList<File> filelist = new ArrayList<File>();
        File files = new File(path);
        if(!files.exists()){
            files.mkdirs();
        }
        if(files.listFiles().length>0){
            for(File file : files.listFiles(new TextFileFilter())){
                filelist.add(file);
            }
        }
        CharSequence[] filename = new CharSequence[filelist.size()];
        for(int i = 0 ; i < filelist.size() ; i++){
            filename[i] = filelist.get(i).getName();
        }
        new AlertDialog.Builder(this)
                .setTitle("TEXT FILE LIST")
                .setItems(filename, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        try{
                            String body = "";
                            StringBuffer bodytext = new StringBuffer();
                            File selecttext = filelist.get(arg1);
                            FileInputStream fis = new FileInputStream(selecttext);
                            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(fis,"UTF-8"));
                            while((body = bufferReader.readLine())!=null){
                                bodytext.append(body);
                            }
                            ed1.setText(selecttext.getName());
                            ed2.setText(bodytext.toString());
                        }catch(IOException e){

                        }
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        }).show();
    }
    private void deltext(){
        final ArrayList<File> filelist = new ArrayList<File>();
        File files = new File(path);
        if(!files.exists()){
            files.mkdirs();
        }
        if(files.listFiles().length>0){
            for(File file : files.listFiles(new TextFileFilter())){
                filelist.add(file);
            }
        }
        CharSequence[] filename = new CharSequence[filelist.size()];
        for(int i = 0 ; i < filelist.size() ; i++){
            filename[i] = filelist.get(i).getName();
        }
        new AlertDialog.Builder(this)
                .setTitle("TEXT FILE LIST")
                .setItems(filename, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        filelist.get(arg1).delete();
                        deltext();
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        }).show();
    }
    class TextFileFilter implements FileFilter{
        public boolean accept(File file) {
            // TODO Auto-generated method stub
            if(file.getName().endsWith(".txt"))return true;
            return false;
        }
    }


    //permisson check
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                new AlertDialog.Builder(this)
                        .setTitle("권한 부여")
                        .setMessage("저장소 권한이 거부 되었습니다. 앱 실행을 원하시면 설정에서 권한을 직접 허용 하셔야 합니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_STORAGE);
            }
        }
    }

    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_STORAGE:
                for (int i = 0; i <grantResults.length; i++) {
                    //grantResults[] : 허용권한 = 0 , 거부권한 = -1
                    if (grantResults[i] < 0) {
                        Toast.makeText(MainActivity.this, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                break;
        }
    }
}
