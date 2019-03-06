package com.example.smsverification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.smsverification.databinding.MainActivityBinding;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

//Q+u2Yul6RQ6-App Signature

public class MainActivity extends AppCompatActivity implements GetMessage {
    private int RC_HINT = 2;
    MainActivityBinding mainActivityBinding;

    public static GetMessage getMessage;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        mainActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        getMessage = this;

       /* AppSignatureHelper appSignatureHelper=new AppSignatureHelper(this);
        System.out.println("My app Signature"+appSignatureHelper.getAppSignatures());*/

        //get user number
        mainActivityBinding.buttonget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    hintRequest();
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });

        mainActivityBinding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOTP(getApplicationContext());
            }
        });
    }

    public void hintRequest() throws IntentSender.SendIntentException {
        HintRequest hintRequest = new HintRequest.Builder()
                .setHintPickerConfig(new CredentialPickerConfig.Builder().setShowCancelButton(true).build())
                .setPhoneNumberIdentifierSupported(true)
                .build();
        GoogleApiClient apiClient = new GoogleApiClient.Builder(getBaseContext()).addApi(Auth.CREDENTIALS_API).build();

        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(apiClient, hintRequest);
        startIntentSenderForResult(intent.getIntentSender(), RC_HINT, null, 0, 0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_HINT) {
            if (data != null) {
                Credential cred = data.getParcelableExtra(Credential.EXTRA_KEY);
                if (cred != null) {
                    final String unformattedPhone = cred.getId();
                    //mainActivityBinding.textView.setText(unformattedPhone);
                    mainActivityBinding.editText.setText(unformattedPhone);
                }
            }
        }
    }

    public void getOTP(Context mContext) {
        SmsRetrieverClient client = SmsRetriever.getClient(mContext);

        // Starts SmsRetriever, waits for ONE matching SMS message until timeout
        // (5 minutes).
        Task<Void> task = client.startSmsRetriever();

        // Listen for success start Task
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });

        //Listen failure of the start Task.
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public void myMessage(String msg) {
        final String arg[] = msg.split(" ");
        mainActivityBinding.textView.setText(arg[2]);
        handler.post(new Runnable() {
            @Override
            public void run() {
                mainActivityBinding.arg0.setText(""+arg[2].charAt(0));
                mainActivityBinding.arg1.setText(""+arg[2].charAt(1));
                mainActivityBinding.arg2.setText(""+arg[2].charAt(2));
                mainActivityBinding.arg3.setText(""+arg[2].charAt(3));
                mainActivityBinding.arg4.setText(""+arg[2].charAt(4));
                mainActivityBinding.arg5.setText(""+arg[2].charAt(5));
            }
        });

    }
}
