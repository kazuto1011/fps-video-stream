import numpy as np
import cv2
import rospy as rp
from sensor_msgs.msg import Image
from cv_bridge import CvBridge, CvBridgeError

__author__ = 'kazuto'

RGB_TOPIC_NAME = "/xtion%d/rgb/image_raw"
DEPTH_TOPIC_NAME = "/xtion%d/depth_registered/image_raw"


class RGBDRecorder:
    def __init__(self, device_id):
        rp.loginfo("Initialization %d" % device_id)
        self.device_id = device_id
        self.bridge = CvBridge()

        cv2.namedWindow("rgb%d" % self.device_id, cv2.CV_WINDOW_AUTOSIZE)
        self.rgb_subscriber = rp.Subscriber(RGB_TOPIC_NAME % self.device_id, Image, self.rgb_callback)

        cv2.namedWindow("depth%d" % self.device_id, cv2.CV_WINDOW_AUTOSIZE)
        self.depth_subscriber = rp.Subscriber(DEPTH_TOPIC_NAME % self.device_id, Image, self.depth_callback)

    def rgb_callback(self, image):
        try:
            rgb_image = self.bridge.imgmsg_to_cv2(image, "bgr8")
        except CvBridgeError, e:
            print e

        cv2.imshow("rgb%d" % self.device_id, rgb_image)
        cv2.waitKey(3)

    def depth_callback(self, image):
        try:
            depth_image = np.asarray(self.bridge.imgmsg_to_cv2(image, 'passthrough'))
        except CvBridgeError, e:
            print e

        cv2.normalize(depth_image, depth_image, 0, 1, cv2.NORM_MINMAX)
        cv2.imshow("depth%d" % self.device_id, depth_image)
        cv2.waitKey(3)


class NodeMain:
    def __init__(self):
        rp.init_node('rgbd_recorder', anonymous=False)
        rp.on_shutdown(self.shutdown)

        RGBDRecorder(1)
        # RGBDRecorder(2)

        rp.spin()
        cv2.destroyAllWindows()

    @staticmethod
    def shutdown():
        rp.loginfo("Shutting down")


if __name__ == '__main__':
    try:
        NodeMain()
    except rp.ROSInterruptException:
        rp.loginfo("Terminated")
