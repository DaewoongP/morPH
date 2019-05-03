package com.example.voice_inputoutput;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ScrollView;
import java.util.ArrayList;
import java.util.Locale;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;

import android.speech.tts.TextToSpeech;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


public class MainActivity extends AppCompatActivity {

    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 5);
            toast("권한주세용");
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);
        final TextView txt = new TextView(this);
        txt.setText("\n");
        txt.setTextSize(18);
        layout.addView(txt);

        Button input = new Button(this);
        input.setText("음성 입력");
        input.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                inputVoice(txt);
            }
        });
        layout.addView(input);

        ScrollView scroll = new ScrollView(this);
        scroll.addView(layout);
        setContentView(scroll);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                tts.setLanguage(Locale.KOREAN);
            }
        });
    }
    private void replyAnswer(String input, TextView txt){
        try{
            if(input.equals("안녕")){
                txt.append("[앱] 누구세요?;\n");
                tts.speak("누구세요?",TextToSpeech.QUEUE_FLUSH,null);
            }
        }catch (Exception e) {
            toast(e.toString());
        }

    }
    public void inputVoice(final TextView txt){
        try {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

            final SpeechRecognizer stt = SpeechRecognizer.createSpeechRecognizer(this);
            stt.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    toast("음성 입력 시작...");
                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {

                }

                @Override
                public void onEndOfSpeech() {
                    toast("음성 입력 종료");
                }

                @Override
                public void onError(int error) {
                    toast("오류 발생 :"+error);
                }

                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> result = (ArrayList<String>) results.get(SpeechRecognizer.RESULTS_RECOGNITION);
                    txt.append("[나]"+result.get(0)+"\n");
                    replyAnswer(result.get(0), txt);
                    stt.destroy();
                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });
            stt.startListening(intent);

        } catch (Exception e){
            toast(e.toString());
        }

    }

    private void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
