package com.github.kazuto1011.fps_recording_apps.fps_recording_moverio;

import android.os.Bundle;

import org.ros.android.RosActivity;
import org.ros.node.NodeMainExecutor;

public class FpsRecordingMoverio extends RosActivity
{
    public FpsRecordingMoverio() {
        super("FpsRecordingMoverio", "FpsRecordingMoverio");
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {

    }
}
