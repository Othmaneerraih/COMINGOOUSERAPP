package com.comingoo.driver.fousa;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VoipCallingActivity extends AppCompatActivity {

    public static Activity activity;
    String driverId = "";
    String clientId = "";
    String callerName = "";
    String clientImage = "";
    private Call call;
    private CountDownTimer countDownTimer;
    private AudioManager audioManager;
    private SinchClient sinchClient;
    private ImageView iv_back_voip_one;
    private TextView callState,caller_name,tv_name_voip_one;
    private CircleImageView iv_user_image_voip_one,iv_cancel_call_voip_one,iv_mute,iv_loud,iv_recv_call_voip_one;

    private static final String APP_KEY = "185d9822-a953-4af6-a780-b0af1fd31bf7";
    private static final String APP_SECRET = "ZiJ6FqH5UEWYbkMZd1rWbw==";
    private static final String ENVIRONMENT = "sandbox.sinch.com";

    boolean isLoud = false;

    private Handler mHandler = new Handler();
    private int mHour, mMinute; // variables holding the hour and minute
    public Runnable mUpdate = new Runnable() {

        @Override
        public void run() {
            mMinute += 1;
            // just some checks to keep everything in order
            if (mMinute >= 60) {
                mMinute = 0;
                mHour += 1;
            }
            if (mHour >= 24) {
                mHour = 0;
            }
            // or call your method
            caller_name.setText(mHour + ":" + mMinute);
            mHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_voip_one);
        activity = this;

        iv_back_voip_one = (ImageView)findViewById(R.id.iv_back_voip_one);
        iv_user_image_voip_one = (CircleImageView)findViewById(R.id.iv_user_image_voip_one);
        iv_cancel_call_voip_one = (CircleImageView)findViewById(R.id.iv_cancel_call_voip_one);
        iv_recv_call_voip_one = (CircleImageView)findViewById(R.id.iv_recv_call_voip_one);
        caller_name = (TextView)findViewById(R.id.callerName);
        callState = (TextView)findViewById(R.id.callState);

        iv_mute = (CircleImageView)findViewById(R.id.iv_mute);
        iv_loud = (CircleImageView)findViewById(R.id.iv_loud);
        tv_name_voip_one = (TextView)findViewById(R.id.tv_name_voip_one);

        iv_loud.setBackgroundColor(Color.WHITE);
        iv_loud.setCircleBackgroundColor(Color.WHITE);
        iv_mute.setBackgroundColor(Color.WHITE);
        iv_mute.setCircleBackgroundColor(Color.WHITE);

        iv_recv_call_voip_one.setVisibility(View.GONE);
        iv_mute.setVisibility(View.GONE);
        iv_loud.setVisibility(View.VISIBLE);

        driverId = getIntent().getStringExtra("driverId");
        clientId = getIntent().getStringExtra("clientId");
        callerName = getIntent().getStringExtra("clientName");
        clientImage = getIntent().getStringExtra("clientImage");


        if (ContextCompat.checkSelfPermission(VoipCallingActivity.this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(VoipCallingActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(VoipCallingActivity.this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.READ_PHONE_STATE},
                    1);
        }

        caller_name.setVisibility(View.VISIBLE);
        caller_name.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        caller_name.setTypeface(null, Typeface.NORMAL);      // for Normal Text

//        caller_name.setText(callerName+ " vous appelle");
        tv_name_voip_one.setText(callerName);
        if(clientImage != null ){
            if (!clientImage.isEmpty())
            Picasso.get().load(clientImage).into(iv_user_image_voip_one);
        }
        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(driverId)
                .applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET)
                .environmentHost(ENVIRONMENT)
                .build();

        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.start();

        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());

        iv_back_voip_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        iv_cancel_call_voip_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(call != null){
                    call.hangup();
//                    iv_recv_call_voip_one.setVisibility(View.VISIBLE);
//                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_cancel_call_voip_one.getLayoutParams();
//                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
//                    params.setMargins(0,0,150,60);
                    finish();
                }

            }
        });

        iv_recv_call_voip_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!clientId.isEmpty() && !driverId.isEmpty()){
                    try{
//                        if (call == null) {
                            call = sinchClient.getCallClient().callUser(clientId);
//                            call.addCallListener(new SinchCallListener());
                        if (call != null) call.answer();
//                        } else {
//                            call.hangup();
//                            iv_recv_call_voip_one.setVisibility(View.VISIBLE);
//                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_cancel_call_voip_one.getLayoutParams();
//                            params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
//                            params.setMargins(0,0,150,60);
//                        }
                    }catch(NullPointerException e){
                        e.printStackTrace();
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }


            }
        });

        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setSpeakerphoneOn(false);
        audioManager.setMicrophoneMute(false);

        iv_loud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLoud) {
                    audioManager.setSpeakerphoneOn(true);
                    iv_loud.setImageResource(R.drawable.clicked_speaker_bt);
                    isLoud = true;
                } else {
                    iv_loud.setImageResource(R.drawable.speaker_bt);
                    audioManager.setSpeakerphoneOn(false);
                    isLoud = false;
                }
            }
        });

        iv_mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mute();
            }
        });

        startTimer();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(2000, 1000) {
            // 500 means, onTick function will be called at every 500 milliseconds

            @Override
            public void onTick(long leftTimeInMilliseconds) {

            }
            @Override
            public void onFinish() {
                try{
                    if(sinchClient != null){
                        if(!clientId.isEmpty() && !driverId.isEmpty()){
                            try{
                                if (call == null) {
                                    call = sinchClient.getCallClient().callUser(clientId);
                                    call.addCallListener(new VoipCallingActivity.SinchCallListener());
                                } else {
                                    call.hangup();
                                    iv_recv_call_voip_one.setVisibility(View.VISIBLE);
                                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_cancel_call_voip_one.getLayoutParams();
                                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                                    params.setMargins(0,0,150,60);
                                }
                            }catch(NullPointerException e){
                                e.printStackTrace();
                            } catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                } catch(NullPointerException e){
                    e.printStackTrace();
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }.start();

    }

    private void mute(){
        if (audioManager.isMicrophoneMute() == false) {
            audioManager.setMicrophoneMute(true);
            iv_mute.setImageResource(R.drawable.clicked_mute);
        } else {
            audioManager.setMicrophoneMute(false);
            iv_mute.setImageResource(R.drawable.mute_bt);
        }
    }

    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            call = null;
            SinchError a = endedCall.getDetails().getError();
//            iv_mute.setVisibility(View.GONE);
//            iv_loud.setVisibility(View.GONE);
//            caller_name.setVisibility(View.GONE);
//            callState.setText("");
//            iv_recv_call_voip_one.setVisibility(View.VISIBLE);
//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_cancel_call_voip_one.getLayoutParams();
//            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
//            params.setMargins(0,0,150,60);
//            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
//            mute();
            mHandler.removeCallbacks(mUpdate);
            finish();
        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            callState.setText("connected");
            caller_name.setVisibility(View.VISIBLE);
            iv_mute.setVisibility(View.VISIBLE);
            iv_loud.setVisibility(View.VISIBLE);
            iv_recv_call_voip_one.setVisibility(View.GONE);
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_cancel_call_voip_one.getLayoutParams();
            params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            params.setMargins(0,0,450,60);


            mHandler.postDelayed(mUpdate, 1000);
        }

        @Override
        public void onCallProgressing(Call progressingCall) {
//            caller_name.setText("0 : "+progressingCall.getDetails().getDuration()+"");
//            caller_name.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
//            iv_mute.setVisibility(View.VISIBLE);
//            iv_loud.setVisibility(View.VISIBLE);
//            caller_name.setTypeface(null, Typeface.BOLD);
//            iv_recv_call_voip_one.setVisibility(View.GONE);

//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_cancel_call_voip_one.getLayoutParams();
//            params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
//            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//            params.setMargins(0,0,450,60);

caller_name.setVisibility(View.GONE);
            callState.setText("ringing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
        }
    }

    private class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {
            call = incomingCall;
            Toast.makeText(VoipCallingActivity.this, "incoming call", Toast.LENGTH_SHORT).show();
//            call.answer();
            call.addCallListener(new SinchCallListener());
        }
    }
}


