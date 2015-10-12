#!/usr/bin/env python
import sys
import numpy as np

import rospy
import cv2
from sensor_msgs.msg import Image
from cv_bridge import CvBridge, CvBridgeError


class ImageRecorder:
    device_id = 0

    def __init__(self, device_id):
        self.device_id = device_id
        cv2.namedWindow("rgb%d" % self.device_id, cv2.CV_WINDOW_AUTOSIZE)
        cv2.namedWindow("depth%d" % self.device_id, cv2.CV_WINDOW_AUTOSIZE)
        self.bridge = CvBridge()
        self.rgb_subscriber = rospy.Subscriber("/xtion%d/rgb/image_raw" % self.device_id, Image, self.rgb_callback)
        self.depth_subscriber = rospy.Subscriber("/xtion%d/depth_registered/image_raw" % self.device_id, Image,
                                                 self.depth_callback)

    def rgb_callback(self, image):
        try:
            rgb_image = self.bridge.imgmsg_to_cv2(image, "bgr8")
        except CvBridgeError, e:
            print e

        cv2.imshow("rgb%d" % self.device_id, rgb_image)
        cv2.waitKey(3)

    def depth_callback(self, image):
        try:
            depth_image = self.bridge.imgmsg_to_cv2(image, "32FC1")
        except CvBridgeError, e:
            print e
        depth_array = np.array(depth_image, dtype=np.float32)
        cv2.normalize(depth_array, depth_array, 0, 1, cv2.NORM_MINMAX)

        cv2.imshow("depth%d" % self.device_id, depth_array)
        cv2.waitKey(3)


def main(args):
    rospy.init_node('xtion_subscriber', anonymous=False)
    ImageRecorder(1)
    # ImageRecorder(2)

    try:
        rospy.spin()
    except KeyboardInterrupt:
        rospy.loginfo("Shutting down")

    cv2.destroyAllWindows()


if __name__ == '__main__':
    main(sys.argv)
