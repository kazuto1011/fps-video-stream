package com.github.kazuto1011.fps_recording_apps.fps_recording_wearables;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.namespace.NameResolver;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

import sensor_msgs.CompressedImage;

/**
 * Created by kazuto on 15/10/15.
 */
public class ImagePublisher extends AbstractNodeMain{

    private Publisher<CompressedImage> publisher;
    private Publisher<std_msgs.String> test_publisher;
    private std_msgs.String msg;

    public GraphName getDefaultNodeName() {
        return GraphName.of("fps_recording_wearables/image_publisher");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        super.onStart(connectedNode);

        NameResolver resolver = connectedNode.getResolver().newChild("wearable");
        publisher = connectedNode.newPublisher(resolver.resolve("rgb/compressed"), CompressedImage._TYPE);
        test_publisher = connectedNode.newPublisher(resolver.resolve("string"), std_msgs.String._TYPE);

        connectedNode.executeCancellableLoop(new CancellableLoop() {
            @Override
            protected void loop() throws InterruptedException {
                msg = test_publisher.newMessage();
                msg.setData("hello, I'm Vuzix M100");
                test_publisher.publish(msg);
            }
        });
    }

    @Override
    public void onShutdown(Node node) {
        super.onShutdown(node);
    }
}
