package comingoo.vone.tahae.comingoodriver;

import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VoipCallingActivity extends AppCompatActivity {

    String driverId = "";
    String clientId = "";
    String callerName = "";
    private Call call;
    private SinchClient sinchClient;
    private ImageView iv_back_voip_one;
    private TextView callState,caller_name;
    private CircleImageView iv_user_image_voip_one,iv_cancel_call_voip_one;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_voip_one);

        iv_back_voip_one = (ImageView)findViewById(R.id.iv_back_voip_one);
        iv_user_image_voip_one = (CircleImageView)findViewById(R.id.iv_user_image_voip_one);
        iv_cancel_call_voip_one = (CircleImageView)findViewById(R.id.iv_cancel_call_voip_one);
        caller_name = (TextView)findViewById(R.id.callerName);
        callState = (TextView)findViewById(R.id.callState);

        driverId = getIntent().getStringExtra("driverId");
        clientId = getIntent().getStringExtra("clientId");
        callerName = getIntent().getStringExtra("clientName");


        if (ContextCompat.checkSelfPermission(VoipCallingActivity.this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(VoipCallingActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(VoipCallingActivity.this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.READ_PHONE_STATE},
                    1);
        }

        caller_name.setText(callerName);

        if(!driverId.isEmpty()){
            sinchClient = Sinch.getSinchClientBuilder()
                    .context(VoipCallingActivity.this)
                    .userId(driverId)
                    .applicationKey("04ae7d45-1084-4fb5-9d7c-08d82527d191")
                    .applicationSecret("TfJrquo6qEmkV8DG/EXQPg==")
                    .environmentHost("clientapi.sinch.com")
//                    .applicationKey(resources.getString(R.string.sinch_app_key))
//                    .applicationSecret(resources.getString(R.string.sinch_app_secret))
//                    .environmentHost(resources.getString(R.string.sinch_envirentmnet_host))
                    .build();
            sinchClient.setSupportCalling(true);
            sinchClient.start();
            sinchClient.startListeningOnActiveConnection();
        }




//                sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());





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
                }

            }
        });

        iv_user_image_voip_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!clientId.isEmpty()){
                    if (call == null) {
                        call = sinchClient.getCallClient().callUser(clientId);
                        call.addCallListener(new SinchCallListener());
                    }
                }
            }
        });

    }

    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            //call ended by either party

            call = null;
            callState.setText("");
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        }

        @Override
        public void onCallEstablished(final Call establishedCall) {
            //incoming call was picked up
            callState.setText("connected");
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }

        @Override
        public void onCallProgressing(Call progressingCall) {
            //call is ringing
            callState.setText("ringing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            //don't worry about this right now
        }
    }

    private class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {
            //Pick up the call!
        }
    }
}


