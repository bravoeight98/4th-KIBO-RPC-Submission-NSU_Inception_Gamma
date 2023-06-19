package jp.jaxa.iss.kibo.rpc.sampleapk;

import android.util.Log;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;


import java.util.List;
import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.Kinematics;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;

import org.opencv.aruco.Aruco;
import org.opencv.aruco.Dictionary;

import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.CvType;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import org.opencv.imgproc.Imgproc;

import org.opencv.objdetect.QRCodeDetector;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {
    private static final double INF = Double.POSITIVE_INFINITY; // infinity value to indicate disconnection
    private final String TAG = this.getClass().getSimpleName();
    static String report = "STAY_AT_JEM"; //　Write the message corresponding to the QR here
    static int Now_place; //current location

    @Override
    protected void runPlan1(){

        //Begin mission 

        api.startMission();
        Log.i(TAG, "mission start");
        MoveToWaypoint(waypoints_config.wp1); // initial point

        //4->5->3->2->6->QR->1->Goal

        Waypoint2Number(4);
        
        // move to a point
        Point point = new Point(10.4d, -10.1d, 4.47d);
        Quaternion quaternion = new Quaternion(0f, 0f, 0f, 1f);
        api.moveTo(point, quaternion, false);
        
        //point laser
        api.laserControl(true);
        api.laserControl(false);

        Waypoint2Number(5);
        
        //point laser
        api.laserControl(true);
        api.laserControl(false);
        Waypoint2Number(4);
        
        //point laser
        api.laserControl(true);
        api.laserControl(false);
        Waypoint2Number(3);
        
        //point laser
        api.laserControl(true);
        api.laserControl(false);
        Waypoint2Number(6);
        
        //point laser
        api.laserControl(true);
        api.laserControl(false);        


        MoveToWaypoint(waypoints_config.wp2); // QR point

        //Change value 
        Now_place = 9;

        //Scan QR
        Mat image = new Mat();
        image = api.getMatNavCam();
        api.saveMatImage(image,"wp2.png");
        String report = read_QRcode(image);

        //Begin Search
        //Long ActiveTime = Time.get(0); //Remaining time in current phase in milliseconds
        //Long MissionTime = Time.get(1); //Mission Remaining Time (ms)
        //List<Long> Time = api.getTimeRemaining();

        Waypoint2Number(1);
        api.laserControl(true);
        api.laserControl(false);   

        while (api.getTimeRemaining().get(1) >(5-4.0)*60*1000){
            GoTarget(api.getActiveTargets(),Now_place);
        }
        Log.i(TAG,"go to goal");
        MoveToWaypoint(waypoints_config.goal_point);

        api.notifyGoingToGoal();
        api.reportMissionCompletion(report);


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
    private void moveToWrapper(double pos_x, double pos_y, double pos_z,
                               double qua_x, double qua_y, double qua_z,
                               double qua_w){
        final Point point = new Point((float)pos_x, (float)pos_y, (float)pos_z);
        final Quaternion quaternion = new Quaternion((float)qua_x, (float)qua_y,
                (float)qua_z, (float)qua_w);
        api.moveTo(point, quaternion, true);
    }

    private void relativeMoveToWrapper(double pos_x, double pos_y, double pos_z,
                                       double qua_x, double qua_y, double qua_z,
                                       double qua_w) {
        final Point point = new Point((float)pos_x, (float)pos_y, (float)pos_z);
        final Quaternion quaternion = new Quaternion((float) qua_x, (float) qua_y,
                (float) qua_z, (float) qua_w);
        Result result = api.relativeMoveTo(point, quaternion, true);

        // Output whether the movement command is operating normally with the Status enum type For details, please refer to the following URL
        // https://github.com/nasa/astrobee_android/blob/a8560ab0270ac281d8eadeb48645f4224582985e/astrobee_api/api/src/main/java/gov/nasa/arc/astrobee/Result.java
        if(result.hasSucceeded()){
            String str = result.getStatus().toString();
            Log.i(TAG, "[relativeMoveToWrapper]:"+str);
        }else{
            Log.w(TAG, " api.relativeMoveTo Error : result.hasSucceeded()=false");
        }
    }


    private void MoveToWaypoint(Waypoint name){

        final int LOOP_MAX = 10;

        int count = 0;
        while(count < LOOP_MAX){
            final Point point = new Point(
                    (float)(name.posX + name.avoidX*count),
                    (float)(name.posY + name.avoidY*count),
                    (float)(name.posZ + name.avoidZ*count)  );
            final Quaternion quaternion = new Quaternion(
                    (float)name.quaX,
                    (float)name.quaY,
                    (float)name.quaZ,
                    (float)name.quaW    );

            Result result = api.moveTo(point, quaternion, true);
            ++count;

            if(result.hasSucceeded()){
                break;
            }
            Log.w(TAG, "Failed to move , retry");
        }
    }

    private void Print_AR(List<Mat> corners, Mat markerIds) {
        for (int n = 0; n < 4; n++) {
            Log.i(TAG, "markerIds:" + Arrays.toString(markerIds.get(n,0)));
            Log.i(TAG, "top left:" + Arrays.toString(corners.get(n).get(0, 0)));
            Log.i(TAG, "top right:" + Arrays.toString(corners.get(n).get(0, 1)));
            Log.i(TAG, "bottom right:" + Arrays.toString(corners.get(n).get(0, 2)));
            Log.i(TAG, "bottom left:" + Arrays.toString(corners.get(n).get(0, 3)));
        }
    }

    //find the bottom right marker
    private int findBottomRight(List<Mat> corners){
        Log.i(TAG,"start findBottomRight");
        // out = function return
        int out = 0;
        int temp = 0;

        //corners.get(n).get(0, 0) -> Get the xy coordinates of the bottom right corner of the nth marker
        for(int n=0; n<4; n++){
            Log.i(TAG,"Loop" + n );
            // Use the Pythagorean theorem that the largest number is the farthest
            // a^2 + b^2 = c^2
            double[] ab = corners.get(n).get(0,2);
            int c = (int)ab[0] * (int)ab[0] + (int)ab[1] * (int)ab[1];
            if(temp < c ){
                temp = c;
                out = n;
                Log.i(TAG,"change");
            }
        }
        // The bottom right (farthest) returns the number of the array
        Log.i(TAG,"finish findBottomRight");
        return out;
    }

    // Kinematics Github
    // https://github.com/nasa/astrobee_android/blob/a8560ab0270ac281d8eadeb48645f4224582985e/astrobee_api/api/src/main/java/gov/nasa/arc/astrobee/Kinematics.java
    private void LoggingKinematics(){
        //Kinematics no Log
        Kinematics kinematics = api.getRobotKinematics();
        Log.i(TAG, "[LoggingKinematics]: state" + kinematics.getConfidence().toString());
        Log.i(TAG, "[LoggingKinematics]: absolute coordinates" + kinematics.getPosition().toString());
        Log.i(TAG, "[LoggingKinematics]: direction coordinates" + kinematics.getOrientation().toString());
        Log.i(TAG, "[LoggingKinematics]: Line speed" + kinematics.getLinearVelocity().toString());      // 線速度
        Log.i(TAG, "[LoggingKinematics]: angular velocity" + kinematics.getAngularVelocity().toString());     // 角速度
        Log.i(TAG, "[LoggingKinematics]: acceleration" + kinematics.getLinearAcceleration().toString());  // 加速度
    }

    private  void GoTarget(List<Integer> ActiveTargets, int Now_place){
        int index = ActiveTargets.size();
        int i = 0;
        //Currently, it is designed to go in ascending order of numbers
        // ----- Replace the contents of ActiveTarget so that you can move at the shortest distance Put the necessary items here or in another function ------------

        while(i < index){
            Log.i(TAG, "Let's go " + ActiveTargets.get(i).toString());
            List<Integer> route = getShortestPath(Now_place,ActiveTargets.get(i));
            for(int n = 1; n<route.size();n++){
                Log.i(TAG, "Let's go to node " +route.get(n).toString());
                Waypoint2Number(route.get(n));
            }
            api.laserControl(true);
            api.takeTargetSnapshot(ActiveTargets.get(i));
            ++i;
        }
    }

    public static double[] dijkstra(double[][] A, int start) {
        int n = A.length; // number of vertices
        double[] distances = new double[n]; // shortest distance from the starting point to each vertex
        boolean[] visited = new boolean[n]; // vertex visit state

        // Initialize the distances array and set vertices other than the starting point to infinity
        Arrays.fill(distances, INF);
        distances[start] = 0.0;

        for (int i = 0; i < n; i++) {
            // Find the unvisited vertex with the smallest distance
            double minDist = INF;
            int minIndex = -1;

            for (int j = 0; j < n; j++) {
                if (!visited[j] && distances[j] < minDist) {
                    minDist = distances[j];
                    minIndex = j;
                }
            }

            // if not found, exit
            if (minIndex == -1) {
                break;
            }

            // mark the found vertex as visited
            visited[minIndex] = true;

            // Update the distance of adjacent vertices
            for (int j = 0; j < n; j++) {
                if (!visited[j] && A[minIndex][j] != INF) {
                    double distance = distances[minIndex] + A[minIndex][j];
                    if (distance < distances[j]) {
                        distances[j] = distance;
                    }
                }
            }
        }

        return distances;
    }

    public static List<Integer> getShortestPath(int start, int end) {
        double[][] A = adjacency_matrix.graph;
        double[] distances = dijkstra(A, start);
        List<Integer> path = new ArrayList<>();

        if (distances[end] == INF) {
            return path; // Returns an empty list if unreachable
        }

        //Restore shortest path from end point to start point
        int current = end;
        path.add(current);
        while (current != start) {
            for (int prev = 0; prev < A.length; prev++) {
                if (A[prev][current] != INF && distances[current] == distances[prev] + A[prev][current]) {
                    current = prev;
                    path.add(0, current);
                    break;
                }
            }
        }

        return path;
    }

    private String[] StringArray2DoubleArray(double[] array){
        String[] stringArray = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            stringArray[i] = Double.toString(array[i]);
        }
        return stringArray;
    }

    private void Waypoint2Number(int n){
        Now_place = n; //Change current position
        switch (n){
            case 1:
                MoveToWaypoint(waypoints_config.point1);
                break;
            case 2:
                MoveToWaypoint(waypoints_config.point2);
                break;
            case 3:
                MoveToWaypoint(waypoints_config.point3);
                break;
            case 4:
                MoveToWaypoint(waypoints_config.point4);
                break;
            case 5:
                MoveToWaypoint(waypoints_config.point5);
                break;
            case 6:
                MoveToWaypoint(waypoints_config.point6);
                break;
            case 7:
                MoveToWaypoint(waypoints_config.goal_point);
                break;
            case 8:
                MoveToWaypoint(waypoints_config.wp1);
                break;
            case 9:
                MoveToWaypoint(waypoints_config.wp2);
                break;
        }
    }

    /**
     * FUNCTIONs ABOUT QRCODE
     */
    private String read_QRcode(Mat image){
        String QRcode_content = "";
        try{
            api.saveMatImage(image,"QR.png");
            Mat mini_image = new Mat(image, new Rect(700, 360, 240, 240)); // The value here is the area to clip
            api.saveMatImage(mini_image,"QR_mini.png");

            MatOfPoint2f points = new MatOfPoint2f();
            Mat straight_qrcode = new Mat();
            QRCodeDetector qrc_detector = new QRCodeDetector();
            Boolean detect_success = qrc_detector.detect(mini_image, points);
            Log.i(TAG,"detect_success is " + detect_success.toString());

            QRcode_content = qrc_detector.detectAndDecode(mini_image, points, straight_qrcode);
            Log.i(TAG,"QRCode_content is " + QRcode_content);
            if(QRcode_content != null){
                Mat straight_qrcode_gray = new Mat();
                straight_qrcode.convertTo(straight_qrcode_gray, CvType.CV_8UC1);
                api.saveMatImage(straight_qrcode_gray,"QR_binary.png");
            }

        } catch(Exception e){
            ;
        }
        /**
         * QRCode_CONTENT to REPORT_MESSEGE
         */
        switch(QRcode_content){
            case "JEM":
                QRcode_content = "STAY_AT_JEM";
                break;
            case "COLUMBUS":
                QRcode_content = "GO_TO_COLUMBUS";
                break;
            case "RACK1":
                QRcode_content = "CHECK_RACK_1";
                break;
            case "ASTROBEE":
                QRcode_content = "I_AM_HERE";
                break;
            case "INTBALL":
                QRcode_content = "LOOKING_FORWARD_TO_SEE_YOU";
                break;
            case "BLANK":
                QRcode_content = "NO_PROBLEM";
                break;
            default:
                QRcode_content = "";
                break;
        }
        return QRcode_content;
    }
}