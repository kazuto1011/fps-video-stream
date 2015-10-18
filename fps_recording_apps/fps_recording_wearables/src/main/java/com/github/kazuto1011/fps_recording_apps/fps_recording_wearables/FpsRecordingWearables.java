package com.github.kazuto1011.fps_recording_apps.fps_recording_wearables;

import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.ros.address.InetAddressFactory;
import org.ros.android.RosActivity;
import org.ros.android.view.camera.RosCameraPreviewView;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import java.net.URI;

public class FpsRecordingWearables extends RosActivity
{
    private final String TAG = "FpsRecordingWearables";
    protected int numberOfCameras;
    private Camera camera;
    private int cameraId = 0;
    private RosCameraPreviewView rosCameraPreviewView;
    private TextView textView;
    private Handler handler;
    private Context context = this;

    public FpsRecordingWearables() {
        super("FpsRecordingWarables", "FpsRecordingWearables", URI.create("http://192.168.4.77:11311"));
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);

        rosCameraPreviewView = (RosCameraPreviewView)findViewById(R.id.camera_view);
        textView = (TextView)findViewById(R.id.rec_info);

        numberOfCameras = Camera.getNumberOfCameras();
        if (numberOfCameras > 1) {
            cameraId = (cameraId + 1) % numberOfCameras;
            rosCameraPreviewView.releaseCamera();
            rosCameraPreviewView.setCamera(Camera.open(cameraId));
        }

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Toast.makeText(context, (String) msg.obj, Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        Log.d(TAG, "init camera ID:" + cameraId);
        camera = Camera.open(cameraId);

        int fps[] = new int[2];
        camera.getParameters().getPreviewFpsRange(fps);

        String info = "";
        info += "Frame rate: " + String.valueOf(fps[1]/1000);
        info += "\nWidth: " + camera.getParameters().getPreviewSize().width;
        info += "\nHeight: " + camera.getParameters().getPreviewSize().height;

        Message msg = handler.obtainMessage(0, info);
        handler.sendMessage(msg);

        rosCameraPreviewView.setCamera(camera);

        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress());
        nodeConfiguration.setMasterUri(getMasterUri());

        nodeMainExecutor.execute(rosCameraPreviewView, nodeConfiguration.setNodeName("M100"));
    }
}
