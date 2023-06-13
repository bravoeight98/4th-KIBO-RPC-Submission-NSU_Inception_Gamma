package jp.jaxa.iss.kibo.rpc.sampleapk;

import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

import java.util.List;
import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.android.gs.MessageType;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.opencv.core.Mat;

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {
    @Override
    protected void runPlan1(){
        // the mission starts
        api.startMission();
        int loop_counter = 0;

        while (true){
            // get the list of active target id
            List<Integer> list = api.getActiveTargets();

            // move to a point
            Point point = new Point(10.4d, -10.1d, 4.47d);
            Quaternion quaternion = new Quaternion(0f, 0f, 0f, 1f);
            api.moveTo(point, quaternion, false);


            // get a camera image
            Mat image = api.getMatNavCam();

            // irradiate the laser
            api.laserControl(true);

            // take active target snapshots
            int target_id = 0;
            api.takeTargetSnapshot(target_id);

            /* ************************************************ */
            /* write your own code and repair the ammonia leak! */
            /* ************************************************ */



            // get remaining active time and mission time
            List<Long> timeRemaining = api.getTimeRemaining();

            // check the remaining milliseconds of mission time
            if (timeRemaining.get(1) < 60000){
                break;
            }

            loop_counter++;
            if (loop_counter == 2){
                break;
            }
        }
        // turn on the front flash light
        api.flashlightControlFront(0.05f);

        // get QR code content
        String mQrContent = yourMethod();

        // turn off the front flash light
        api.flashlightControlFront(0.00f);

        // notify that astrobee is heading to the goal
        api.notifyGoingToGoal();

        /* ********************************************************** */
        /* write your own code to move Astrobee to the goal positiion */
        /* ********************************************************** */

        //Move to point4
        Point point4 = new Point(10.51d, -6.7185d, 5.1804d);
        Quaternion quaternion4 = new Quaternion(0f, 0f, -1f, 0f);
        api.moveTo(point4, quaternion4, true);

        // get a camera image
        Mat image4 = api.getMatNavCam();

        // irradiate the laser
        api.laserControl(true);

        // take active target snapshots
        int target_id4 = 4;
        api.takeTargetSnapshot(target_id4);

        //Move to point 5
        Point point5 = new Point(11.114f, -7.9756f, 5.3393f);
        Quaternion quaternion5 = new Quaternion(-0.5f, -0.5f, -0.5f, 0.5f);
        api.moveTo(point5, quaternion5, true);
        //Log.i("point5","here");

        // get a camera image
        Mat image5 = api.getMatNavCam();

        // irradiate the laser
        api.laserControl(true);

        // take active target snapshots
        int target_id5 = 5;
        api.takeTargetSnapshot(target_id5);

        //Move to point 3
        Point point3 = new Point(10.71f, -7.7f, 4.48f);
        Quaternion quaternion3 = new Quaternion(0f, 0.707f, 0f, 0.707f);
        api.moveTo(point3, quaternion3, true);
        //Log.i("point3","here");

        // get a camera image
        Mat image3 = api.getMatNavCam();

        // irradiate the laser
        api.laserControl(true);

        // take active target snapshots
        int target_id3 = 3;
        api.takeTargetSnapshot(target_id3);


        //Move to point 2
        Point point2 = new Point(10.612f, -9.0709f, 4.48f);
        Quaternion quaternion2 = new Quaternion(0.5f, 0.5f, -0.5f, 0.5f);
        api.moveTo(point2, quaternion2, true);
        //Log.i("point2","here");

        // get a camera image
        Mat image2 = api.getMatNavCam();

        // irradiate the laser
        api.laserControl(true);

        // take active target snapshots
        int target_id2 = 2;
        api.takeTargetSnapshot(target_id2);

        //Move to point 6
        Point point6 = new Point(11.355f, -8.9929f, 4.7818f);
        Quaternion quaternion6 = new Quaternion(0f, 0f, 0f, 1f);
        api.moveTo(point6, quaternion6, true);
        //Log.i("point6","here");

        // get a camera image
        Mat image6 = api.getMatNavCam();

        // irradiate the laser
        api.laserControl(true);

        // take active target snapshots
        int target_id6 = 6;
        api.takeTargetSnapshot(target_id6);

        //Move to point QR
        Point pointQR = new Point(11.381944f, -8.566172f, 3.76293f);
        Quaternion quaternionQR = new Quaternion(0f, 0f, 0f, 1f);
        api.moveTo(pointQR, quaternionQR, true);
        //Log.i("pointQR","here");

        Mat imageQR = api.getMatNavCam();

        String qrCodeData = decodeQRCode(imageQR);

        if (qrCodeData != null) {
            System.out.println("QR Code data: " + qrCodeData);
        } else {
            System.out.println("Failed to decode QR Code.");
        }

        // turn on the front flash light
        api.flashlightControlFront(0.05f);
        // get QR code content

        // turn off the front flash light
        api.flashlightControlFront(0.00f);

        // take active target snapshots
        int target_idQR = 50;
        api.takeTargetSnapshot(target_idQR);

        //Move to point 1
        Point point1 = new Point(11.2746f, -9.92284f, 5.2988f);
        Quaternion quaternion1 = new Quaternion(0f, 0f, -0.707f, 0.707f);
        api.moveTo(point1, quaternion1, true);
        //Log.i("point1","here");

        // get a camera image
        Mat image1 = api.getMatNavCam();

        // irradiate the laser
        api.laserControl(true);

        // take active target snapshots
        int target_id1 = 1;
        api.takeTargetSnapshot(target_id1);

        //Move to point Goal
        Point pointG = new Point(11.143f, -6.7607f, 4.9654f);
        Quaternion quaternionG = new Quaternion(0f, 0f, -0.707f, 0.707f);
        api.moveTo(pointG, quaternionG, true);
        //Log.i("Goal","here");

        // get a camera image
        //Mat imageG = api.getMatNavCam();

        // irradiate the laser
        //api.laserControl(true);

        // take active target snapshots
        //int target_idG = 100;
        //api.takeTargetSnapshot(target_idG);

        // send mission completion
        api.reportMissionCompletion(qrCodeData);
    }

    @Override
    protected void runPlan2(){
        // write your plan 2 here
    }

    @Override
    protected void runPlan3(){
        // write your plan 3 here
    }

    // You can add your method
    private String yourMethod(){
        return "your method";
    }

    public static String decodeQRCode(Mat qrCodeImage) {
        try {
            Mat grayImage = new Mat();
            Imgproc.cvtColor(qrCodeImage, grayImage, Imgproc.COLOR_BGR2GRAY);

            Mat binImage = new Mat();
            Imgproc.threshold(grayImage, binImage, 128, 255, Imgproc.THRESH_BINARY);

            MatOfByte matOfByte = new MatOfByte();
            Imgcodecs.imencode(".jpg", binImage, matOfByte);
            byte[] byteArray = matOfByte.toArray();

            LuminanceSource source = new BufferedImageLuminanceSource(ImageIO.read(new ByteArrayInputStream(byteArray)));
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

            Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap);
            return new String(qrCodeResult.getRawBytes(), StandardCharsets.UTF_8);
        } catch (IOException | ReaderException e) {
            e.printStackTrace();
        }

        return null;
    }

}
