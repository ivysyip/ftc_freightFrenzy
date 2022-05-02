package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.currentlyUsing.AutonomousMethodsNewArmN;
import org.java_websocket.framing.FramedataImpl1;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.*;


@TeleOp(name="OpenCVAlignment", group="Linear Opmode")
@Disabled
public class OpenCVAutoAlignmentTest extends LinearOpMode {
    private DcMotorEx turret = null;
    private DcMotorEx armAngle = null;
    private DcMotorEx armExtend = null;
    private Servo intakeRotater = null;
    private DcMotor leftFront = null;
    private DcMotor rightFront = null;
    private DcMotor leftBack = null;
    private DcMotor rightBack = null;

    OpenCvCamera webcam;
    CameraImagePipeline pipeline;
    private static final int TARGET_OBJECT_TO_FIND = 2;
    private static final double DEFAULT_X_DIST_TO_HUB = -2.5;
    private static double X_DIST_TO_HUB = 0;
    private static final double Y_DIST_TO_HUB = 36.0;
    private static final double PIXELS_PER_IN = 990.0/17.5;
    private static final double Z_DIST_TO_HUB = 5.0;
    private static boolean Dropped = false;
    static final double COUNTS_PER_MOTOR_REV = 537.6;// eg: TETRIX Motor Encoder
    static final double DRIVE_GEAR_REDUCTION = 1;     // This is < 1.0 if geared UP
    static final double WHEEL_DIAMETER_INCHES = 3.937;   // For figuring circumference - 100mm
    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);

    @Override
    public void runOpMode() {
        leftFront = hardwareMap.get(DcMotor.class, "left_front");
        rightFront = hardwareMap.get(DcMotor.class, "right_front");
        leftBack = hardwareMap.get(DcMotor.class, "left_back");
        rightBack = hardwareMap.get(DcMotor.class, "right_back");
        leftFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);

        turret = hardwareMap.get(DcMotorEx.class, "turret");
        turret.setDirection(DcMotorSimple.Direction.FORWARD);
        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        armAngle = hardwareMap.get(DcMotorEx.class, "armAngle");
        armAngle.setDirection(DcMotorSimple.Direction.REVERSE);
        armAngle.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armAngle.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        armExtend = hardwareMap.get(DcMotorEx.class, "armExtend");
        armExtend.setDirection(DcMotorSimple.Direction.FORWARD);
        armExtend.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armExtend.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        intakeRotater = hardwareMap.get(Servo.class, "intakeRotater");
        intakeRotater.setDirection(Servo.Direction.FORWARD);

        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftFront.setPower(0);
        leftBack.setPower(0);
        rightFront.setPower(0);
        rightBack.setPower(0);
        turret.setPower(0);
        armExtend.setPower(0);
        armAngle.setPower(0);

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);

        // We set the viewport policy to optimized view so the preview doesn't appear 90 deg
        // out when the RC activity is in portrait. We do our actual image processing assuming
        // landscape orientation, though.
        webcam.setViewportRenderingPolicy(OpenCvCamera.ViewportRenderingPolicy.OPTIMIZE_VIEW);
        pipeline = new CameraImagePipeline();
        webcam.setPipeline(pipeline);

        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                webcam.startStreaming(1280, 720, OpenCvCameraRotation.UPRIGHT);
            }

            public void onError(int n) {

            }
        });

        waitForStart();
        while (opModeIsActive()) {
            moveWPID(0, 2, .05);
            ArrayList<CameraImagePipeline.AnalyzedBarcodes> barcodes = pipeline.getDetectedBarcodes();

            if (barcodes.isEmpty()) {
                telemetry.addLine("No objects detected");
            }
            else {
                int objectFound = 0;
                double object_x_pos[] = new double[10];
                for (CameraImagePipeline.AnalyzedBarcodes barcode : barcodes) {
                    if (barcode.points[1].y > 500) {
                        telemetry.addLine(String.format("Object: (%.2f,%.2f) (%.2f,%.2f)",
                                barcode.points[1].x, barcode.points[1].y,
                                barcode.points[3].x, barcode.points[3].y));
                        if (objectFound < TARGET_OBJECT_TO_FIND)
                            object_x_pos[objectFound] = barcode.points[1].x;
                        objectFound++;
                    }
                }

                double barcode_pos = getListMax(object_x_pos);

                X_DIST_TO_HUB = DEFAULT_X_DIST_TO_HUB + (barcode_pos / PIXELS_PER_IN);
                X_DIST_TO_HUB = Math.round(X_DIST_TO_HUB/0.62) * 0.62;

                double angle = 0;
                if (X_DIST_TO_HUB >= 0) {
                    angle = Math.atan(Y_DIST_TO_HUB / X_DIST_TO_HUB);
                    angle = Math.toDegrees(angle);
                    angle = 180.0 - angle;
                }
                else {
                    angle = Math.atan(Y_DIST_TO_HUB/ Math.abs(X_DIST_TO_HUB));
                    angle = Math.toDegrees(angle);
                }

                rotateTurret(angle);

                double hypotenuse = Math.sqrt(Math.pow(Y_DIST_TO_HUB, 2) + Math.pow(X_DIST_TO_HUB, 2));
                angleArm();

                double extension = Math.sqrt(Math.pow(Z_DIST_TO_HUB, 2) + Math.pow(hypotenuse, 2));
                extendArm(extension);
                telemetry.addData("extension", 40*extension);
                telemetry.update();

                /*if (Dropped)
                    armToZero();*/

                sleep(500);
            }
        }
    }

    public void rotateTurret(double angle) {
        double countsPerMotorRotation = 145.1;
        double motorRotationsPerTurret = 28;
        double countsPerDegree = (countsPerMotorRotation * motorRotationsPerTurret)/360;

        turret.setTargetPosition((int)countsPerDegree * (int)angle);
        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turret.setPower(1);
        sleep(100);
        if (Math.abs(turret.getCurrentPosition() - turret.getTargetPosition()) < 10) {
            turret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            turret.setPower(0);
        }
    }

    public void angleArm() {
        double countsPerMotorRotation = 537.7;
        double motorRotationsPerRotation = 28;
        double countsPerDegreee = (countsPerMotorRotation * motorRotationsPerRotation) / 360;

        armAngle.setTargetPosition((int)(countsPerDegreee * 20));
        armAngle.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        armAngle.setPower(0.8);
        sleep(50);

        if (armAngle.getCurrentPosition() >= (int)(countsPerDegreee * 20)) {
            armAngle.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            armAngle.setPower(0);
        }
    }

    public void extendArm(double length) {
        armExtend.setTargetPosition((int)(40*length));
        armExtend.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        armExtend.setPower(0.6);
        telemetry.addData("currentPos", armExtend.getCurrentPosition());
        telemetry.update();
        sleep(50);
        if (Math.abs(armExtend.getCurrentPosition()-armExtend.getTargetPosition()) < 10) {
            sleep(100);
            Dropped = true;
        }
    }

    public double getListMax(double [] list) {
        double max = 0;

        for (double x : list) {
            if (x > max) {
                max = x;
            }
        }

        return max;
    }

    public void moveWPID(double targetXInches, double targetYInches, double maxPwr) {


        leftFront.setPower(0);
        rightFront.setPower(0);

        rightBack.setPower(0);
        leftBack.setPower(0);


        double targetXCount = targetXInches * COUNTS_PER_INCH;
        double targetYCount = targetYInches * COUNTS_PER_INCH;
        // get starting X and Y position from encoders
        // and solving from equation

        double initialLFPos = leftFront.getCurrentPosition();
        double initialLBPos = leftBack.getCurrentPosition();
        double initialRFPos = rightFront.getCurrentPosition();
        double initialRBPos = rightBack.getCurrentPosition();

        double targetLFPos = initialLFPos + (targetYCount + targetXCount);
        double targetLBPos = initialLBPos + (targetYCount - targetXCount);
        double targetRBPos = initialRBPos + (targetYCount + targetXCount);
        double targetRFPos = initialRFPos + (targetYCount - targetXCount);






        // adding Count + initial
        // double targetXPos = targetXCount + initialXPos;
        // double targetYPos = targetYCount + initialYPos;


        leftFront.setTargetPosition((int)targetLFPos);
        leftBack.setTargetPosition((int)targetLBPos);
        rightBack.setTargetPosition((int)targetRBPos);
        rightFront.setTargetPosition((int)targetRFPos);

        leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        leftFront.setPower(maxPwr);
        leftBack.setPower(maxPwr);
        rightBack.setPower(maxPwr);
        rightFront.setPower(maxPwr);

        while (leftFront.isBusy() || leftBack.isBusy() ||
                rightBack.isBusy() || rightFront.isBusy()){
            double errorLF = Math.abs(targetLFPos - leftFront.getCurrentPosition()),
                    errorLB = Math.abs(targetLBPos - leftBack.getCurrentPosition()),
                    errorRF = Math.abs(targetRFPos - rightFront.getCurrentPosition()),
                    errorRB = Math.abs(targetRBPos - rightBack.getCurrentPosition());

            if (errorLF < 30 || errorLB < 30 || errorRF < 30 || errorRB < 30) {
                leftFront.setTargetPosition(leftFront.getCurrentPosition());
                leftBack.setTargetPosition(leftBack.getCurrentPosition());
                rightFront.setTargetPosition(rightFront.getCurrentPosition());
                rightBack.setTargetPosition(rightBack.getCurrentPosition());
            }
        }

        sleep(250);
        leftFront.setPower(0);
        rightFront.setPower(0);

        rightBack.setPower(0);
        leftBack.setPower(0);




    }
}
