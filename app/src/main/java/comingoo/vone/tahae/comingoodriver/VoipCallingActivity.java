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
import android.widget.Toast;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchError;
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

    private static final String APP_KEY = "04ae7d45-1084-4fb5-9d7c-08d82527d191";
    private static final String APP_SECRET = "TfJrquo6qEmkV8DG/EXQPg==";
    private static final String ENVIRONMENT = "sandbox.sinch.com";

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
        clientId = getIntent().getStringExtra("clientId");//"RHiU2GIxm2ZIlU4GBGgKFZWxk4J3";//getIntent().getStringExtra("clientId");
        callerName = getIntent().getStringExtra("clientName");


        if (ContextCompat.checkSelfPermission(VoipCallingActivity.this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(VoipCallingActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(VoipCallingActivity.this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.READ_PHONE_STATE},
                    1);
        }

        caller_name.setText(callerName);

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

        sinchClient.getCallClient().addCallClientListener(new VoipCallingActivity.SinchCallClientListener());

        iv_cancel_call_voip_one.setEnabled(false);





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
                        call.addCallListener(new VoipCallingActivity.SinchCallListener());
//                        button.setText("Hang Up");
                        iv_cancel_call_voip_one.setEnabled(true);
                    } else {
                        call.hangup();
                    }
                }
            }
        });

    }

    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            call = null;
            SinchError a = endedCall.getDetails().getError();
//            button.setText("Call");
            iv_cancel_call_voip_one.setEnabled(false);
            callState.setText("");
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            callState.setText("connected");
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }

        @Override
        public void onCallProgressing(Call progressingCall) {
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
            call.answer();
            call.addCallListener(new VoipCallingActivity.SinchCallListener());
//            button.setText("Hang Up");
            iv_cancel_call_voip_one.setEnabled(true);
        }
    }
}


