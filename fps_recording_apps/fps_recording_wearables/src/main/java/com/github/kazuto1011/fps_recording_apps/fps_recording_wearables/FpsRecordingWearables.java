package com.github.kazuto1011.fps_recording_apps.fps_recording_wearables;

import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import org.ros.address.InetAddressFactory;
import org.ros.android.RosActivity;
import org.ros.android.view.camera.RosCameraPreviewView;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import java.net.URI;

public class FpsRecordingWearables extends RosActivity
{
    private final String TAG = "FpsRecordingWearables";
    private Camera mCamera;
    protected int numberOfCameras;
    private int cameraId = 0;
    private RosCameraPreviewView rosCameraPreviewView;

    public FpsRecordingWearables() {
        super("FpsRecordingWearables", "FpsRecordingWearables", URI.create("http://192.168.4.77:11311"));
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

        numberOfCameras = Camera.getNumberOfCameras();
        if (numberOfCameras > 1) {
            cameraId = (cameraId + 1) % numberOfCameras;
            rosCameraPreviewView.releaseCamera();
            rosCameraPreviewView.setCamera(Camera.open(cameraId));
        }
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        Log.d(TAG, "init");
        cameraId = 0;
        rosCameraPreviewView.setCamera(Camera.open(cameraId));

        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress());
        nodeConfiguration.setMasterUri(getMasterUri());

        nodeMainExecutor.execute(rosCameraPreviewView, nodeConfiguration.setNodeName("M100"));
    }

}
