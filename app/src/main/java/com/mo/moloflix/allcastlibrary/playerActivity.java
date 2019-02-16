package com.mo.moloflix.allcastlibrary;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.connectsdk.core.MediaInfo;
import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.device.ConnectableDeviceListener;
import com.connectsdk.device.DevicePicker;
import com.connectsdk.discovery.CapabilityFilter;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.capability.MediaControl;
import com.connectsdk.service.capability.MediaPlayer;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.service.sessions.LaunchSession;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class playerActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView cover,cast,rewind,pause,play,stop,forword,back;
    private DevicePicker mDevicePicker;
    private ConnectableDevice mDevice;
    MediaPlayer.LaunchListener listener;
    LaunchSession mLaunchSession;
    MediaControl mMediaControl;
    MediaInfo mediaInfo;
    Animation blink , rotate;
    String mediaURL ;
    String title = "Test";
    String mimeType = "video/mp4";
    AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mediaURL = b.getString("url");
        Toast.makeText(this,mediaURL,Toast.LENGTH_LONG).show();
        cover = findViewById(R.id.cover);
        Picasso.with(this).load("http://camendesign.co.uk/code/video_for_everybody/poster.jpg")
                .transform(new CropCircleTransformation())
                .into(cover);
        rotate = AnimationUtils.loadAnimation(this,R.anim.rotate);
        blink = AnimationUtils.loadAnimation(this,R.anim.blink);
        start();
        cast = findViewById(R.id.castP);
        cast.startAnimation(blink);
        dialog = mDevicePicker.getPickerDialog("Select a device", mPickerClickListener);
        cast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDevice != null){
                    if (mDevice.isConnected()){
                        mDevice.disconnect();
                        mDevice.getMediaPlayer().closeMedia(mLaunchSession, null);
                        cast.startAnimation(blink);
                        cover.clearAnimation();
                    }else{
                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#D30808"));
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boolean wantToCloseDialog = false;
                                DiscoveryManager.getInstance().stop();
                                DiscoveryManager.getInstance().start();
                                if (wantToCloseDialog)
                                    dialog.dismiss();
                                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
                            }
                        });
                        cast.clearAnimation();
                        cover.startAnimation(rotate);
                    }
                }else {
                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#D30808"));
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boolean wantToCloseDialog = false;
                                DiscoveryManager.getInstance().stop();
                                DiscoveryManager.getInstance().start();
                                if (wantToCloseDialog)
                                    dialog.dismiss();
                                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
                            }
                        });
                        cast.clearAnimation();
                        cover.startAnimation(rotate);
                    }
                }
        });
        rewind = findViewById(R.id.rewind);
        rewind.setOnClickListener(this);
        pause = findViewById(R.id.pause);
        pause.setOnClickListener(this);
        play = findViewById(R.id.play);
        play.setOnClickListener(this);
        stop = findViewById(R.id.stop);
        stop.setOnClickListener(this);
        forword = findViewById(R.id.forward);
        forword.setOnClickListener(this);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
    }
    private void start() {
        CapabilityFilter imageFilter = new CapabilityFilter(MediaPlayer.Play_Video);
        DiscoveryManager.getInstance().setCapabilityFilters(imageFilter);
        DiscoveryManager.getInstance().start();
        mDevicePicker = new DevicePicker(this);
        mediaInfo = new MediaInfo.Builder(mediaURL, mimeType)
                .setTitle(title)
                .build();
        listener = new MediaPlayer.LaunchListener() {
            @Override
            public void onSuccess(MediaPlayer.MediaLaunchObject object) {
                // save these object references to control media playback
                mLaunchSession = object.launchSession;
                mMediaControl = object.mediaControl;
                // you will want to enable your media control UI elements here
            }

            @Override
            public void onError(ServiceCommandError error) {
                Log.d("App Tag", "Play media failure: " + error);
            }
        };
    }

    private AdapterView.OnItemClickListener mPickerClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(android.widget.AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            mDevice = (ConnectableDevice) arg0.getItemAtPosition(arg2);
            mDevice.addListener(mDeviceListener);
            mDevice.connect();
        }
    };
    private ConnectableDeviceListener mDeviceListener = new ConnectableDeviceListener() {

        @Override
        public void onPairingRequired(ConnectableDevice device,
                                      DeviceService service, DeviceService.PairingType pairingType) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onDeviceReady(ConnectableDevice device) {
            device.getMediaPlayer().playMedia(mediaInfo, true, listener);
        }

        @Override
        public void onDeviceDisconnected(ConnectableDevice device) {
            mDevice.getMediaPlayer().closeMedia(mLaunchSession, null);
            mDevice.removeListener(mDeviceListener);
            mDevice.disconnect();
        }

        @Override
        public void onConnectionFailed(ConnectableDevice device,
                                       ServiceCommandError error) {
            Toast.makeText(playerActivity.this,"Connection Failed",Toast.LENGTH_LONG).show();
            mDevice.removeListener(mDeviceListener);
            mDevice = null;
        }

        @Override
        public void onCapabilityUpdated(ConnectableDevice device,
                                        List<String> added, List<String> removed) {
            Toast.makeText(playerActivity.this,"Capability Updated",Toast.LENGTH_LONG).show();

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rewind:
                if (mDevice != null){
                    if (mDevice.isConnected()){
                        mMediaControl.rewind(null);
                        mMediaControl.getPosition(new MediaControl.PositionListener() {
                            @Override
                            public void onSuccess(Long object) {
                                mMediaControl.seek(object - 10000L,null);
                            }

                            @Override
                            public void onError(ServiceCommandError error) {

                            }
                        });
                    }
                }
            break;
            case R.id.pause:
                if (mDevice != null){
                    if (mDevice.isConnected()){
                        mMediaControl.pause(null);
//                        mMediaControl.getDuration(new MediaControl.DurationListener() {
//                            @Override
//                            public void onSuccess(Long object) {
//                                @SuppressLint("SimpleDateFormat") String s = new SimpleDateFormat("hh:mm:ss").format(new Date(object));
//                                Toast.makeText(playerActivity.this,s,Toast.LENGTH_LONG).show();
//                            }
//
//                            @Override
//                            public void onError(ServiceCommandError error) {
//
//                            }
//                        });
                    }
                }
                break;
            case R.id.play:
                if (mDevice != null){
                    if (mDevice.isConnected()){
                        mMediaControl.play(null);
                    }
                }
                break;
            case R.id.stop:
                if (mDevice != null){
                    if (mDevice.isConnected()){
                        mDevice.getMediaPlayer().closeMedia(mLaunchSession, null);
                        onBackPressed();
                    }
                }
                break;
            case R.id.forward:
                if (mDevice != null){
                    if (mDevice.isConnected()){
                        mMediaControl.fastForward(null);
                        mMediaControl.getPosition(new MediaControl.PositionListener() {
                            @Override
                            public void onSuccess(Long object) {
                                mMediaControl.seek(object + 10000L,null);
                            }

                            @Override
                            public void onError(ServiceCommandError error) {

                            }
                        });
                    }
                }
                break;
            case R.id.back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
