package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.teamcode.thread.ThreadPool;

import org.firstinspires.ftc.teamcode.thread.TaskThread;

@TeleOp(name="Test thread design", group="Linear Opmode")
@Disabled
public class NewThreadDesign extends LinearOpMode {
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftFront = null;
    private DcMotor rightFront = null;
    private DcMotor leftBack = null;
    private DcMotor rightBack = null;
    private DcMotor turret = null;
    private DcMotor firstArm = null;
    private DcMotor secondArm = null;
    private DcMotor carousel = null;

    private CRServo intake1 = null;
    private CRServo intake2 = null;


    private Servo intakeRotater = null;

    class Inputs {
        //gamepad 1 sticks

        double gamepad1LeftStickY;
        double gamepad1RightStickY;
        double gamepad1LeftStickX;
        double gamepad1RightStickX;

        //gamepad 1 face buttons

        boolean gamepad1A;
        boolean gamepad1B;
        boolean gamepad1X;
        boolean gamepad1Y;

        // gamepad 1 dpad

        boolean gamepad1DpadUp;
        boolean gamepad1DpadDown;
        boolean gamepad1DpadLeft;
        boolean gamepad1DpadRight;

        // gamepad 1 Bumpers;
        boolean gamepad1LeftBumper;
        boolean gamepad1RightBumper;

        // gamepad 1 Triggers

        double gamepad1LeftTrigger;
        double gamepad1RightTrigger;


        //gamepad2 sticks

        double gamepad2LeftStickY;
        double gamepad2RightStickY;
        double gamepad2LeftStickX;
        double gamepad2RightStickX;

        //gamepad2 face buttons

        boolean gamepad2A;
        boolean gamepad2B;
        boolean gamepad2X;
        boolean gamepad2Y;

        // gamepad2 dpad

        boolean gamepad2DpadUp;
        boolean gamepad2DpadDown;
        boolean gamepad2DpadLeft;
        boolean gamepad2DpadRight;

        // gamepad2 Bumpers;
        boolean gamepad2LeftBumper;
        boolean gamepad2RightBumper;

        // gamepad2 Triggers

        double gamepad2LeftTrigger;
        double gamepad2RightTrigger;

    }

    class Drive {
        double y;
        double x;
        double rx;
        double baseMult;
        double slowdownBase = 1;
    }

    Inputs inputs = new Inputs();
    Drive drive = new Drive();

    @Override
    public void runOpMode() {
        // Initialize ThreadPool
        ThreadPool threadPool = new ThreadPool();

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        leftFront = hardwareMap.get(DcMotor.class, "left_front");
        rightFront = hardwareMap.get(DcMotor.class, "right_front");
        leftBack = hardwareMap.get(DcMotor.class, "left_back");
        rightBack = hardwareMap.get(DcMotor.class, "right_back");
        turret = hardwareMap.get(DcMotor.class, "turret");
        firstArm = hardwareMap.get(DcMotor.class, "armBase");
        secondArm = hardwareMap.get(DcMotor.class, "armJoint");
        carousel = hardwareMap.get(DcMotor.class, "carousel");
        intakeRotater = hardwareMap.get(Servo.class,"intake");

        intake1 = hardwareMap.get(CRServo.class, "intake1");
        intake2 = hardwareMap.get(CRServo.class, "intake2");

        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        leftFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        turret.setDirection(DcMotorSimple.Direction.FORWARD);
        firstArm.setDirection(DcMotorSimple.Direction.REVERSE);
        secondArm.setDirection(DcMotorSimple.Direction.REVERSE);
        carousel.setDirection(DcMotorSimple.Direction.REVERSE);
        intakeRotater.setDirection(Servo.Direction.FORWARD);


        intake1.setDirection(CRServo.Direction.FORWARD);
        intake2.setDirection(CRServo.Direction.FORWARD);




        //intakeRotater.setPosition(0);

        //turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        firstArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        secondArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // turret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        firstArm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        secondArm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        firstArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        secondArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);



        // Wait for the game to start (driver presses PLAY)


        threadPool.registerThread(new TaskThread(new TaskThread.Actions() {
            @Override
            public void work() {
                inputs.gamepad1LeftStickX = gamepad1.left_stick_x;
                inputs.gamepad1LeftStickY = gamepad1.left_stick_y;
                inputs.gamepad1RightStickX = gamepad1.right_stick_x;
                inputs.gamepad1RightStickY = gamepad1.right_stick_y;

                inputs.gamepad1A = gamepad1.a;
                inputs.gamepad1B = gamepad1.b;
                inputs.gamepad1X = gamepad1.x;
                inputs.gamepad1Y = gamepad1.y;

                inputs.gamepad1DpadUp = gamepad1.dpad_up;
                inputs.gamepad1DpadDown = gamepad1.dpad_down;
                inputs.gamepad1DpadLeft = gamepad1.dpad_left;
                inputs.gamepad1DpadRight = gamepad1.dpad_right;


                inputs.gamepad1LeftBumper = gamepad1.left_bumper;
                inputs.gamepad1RightBumper = gamepad1.right_bumper;
                inputs.gamepad1LeftTrigger = gamepad1.left_trigger;
                inputs.gamepad1RightTrigger = gamepad1.right_trigger;

                inputs.gamepad2LeftStickX = gamepad2.left_stick_x;
                inputs.gamepad2LeftStickY = gamepad2.left_stick_y;
                inputs.gamepad2RightStickX = gamepad2.right_stick_x;
                inputs.gamepad2RightStickY = gamepad2.right_stick_y;

                inputs.gamepad2A = gamepad2.a;
                inputs.gamepad2B = gamepad2.b;
                inputs.gamepad2X = gamepad2.x;
                inputs.gamepad2Y = gamepad2.y;

                inputs.gamepad2DpadUp = gamepad2.dpad_up;
                inputs.gamepad2DpadDown = gamepad2.dpad_down;
                inputs.gamepad2DpadLeft = gamepad2.dpad_left;
                inputs.gamepad2DpadRight = gamepad2.dpad_right;


                inputs.gamepad2LeftBumper = gamepad2.left_bumper;
                inputs.gamepad2RightBumper = gamepad2.right_bumper;
                inputs.gamepad2LeftTrigger = gamepad2.left_trigger;
                inputs.gamepad2RightTrigger = gamepad2.right_trigger;







            }
        }));


        threadPool.registerThread(new TaskThread(new TaskThread.Actions() {
            @Override
            public void work() {
                double countsPerRotation = 5281.1;
                double degreesPerRotation = 180;
                double countsPerDegree = countsPerRotation / degreesPerRotation;
                double leftPower;
                double rightPower;
                double basePowerMult = 1;
                double firstArmPowerMult = .18 - (0.1 * gamepad2.right_trigger);
                double intakeRotatePos = intakeRotater.getPosition();
                double firstArmPower = gamepad2.left_stick_y;
                double baseDegrees = firstArm.getCurrentPosition() / countsPerDegree;
                firstArmPower = firstArmPower * firstArmPowerMult;





                if (gamepad1.right_bumper) {
                    drive.slowdownBase += 0.25;
                    sleep(100);
                }

                if (gamepad1.left_bumper){
                    drive.slowdownBase += -0.25;
                    sleep(100);
                }


                if (drive.slowdownBase >= 1) drive.slowdownBase = 1;
                if (drive.slowdownBase <= 0.25) drive.slowdownBase = 0.25;
                drive.baseMult = .75;

                if ((baseDegrees > 180) && (firstArmPower < 0)|| (baseDegrees < -180) || (firstArmPower > 0)) firstArmPower = 0;





                drive.y = -inputs.gamepad1LeftStickY * drive.baseMult * drive.slowdownBase;
                drive.x = inputs.gamepad1LeftStickX * drive.baseMult * drive.slowdownBase ;
                drive.rx = inputs.gamepad1RightStickX * drive.baseMult * drive.slowdownBase;

                leftBack.setPower(drive.y - drive.x - drive.rx);
                leftFront.setPower(drive.y + drive.x - drive.rx);
                rightBack.setPower(drive.y + drive.x + drive.rx);
                rightFront.setPower(drive.y - drive.x + drive.rx);

                firstArm.setPower(firstArmPower);

                if (gamepad2.dpad_up) {
                    intakeRotatePos += 0.05;
                    sleep(50);
                }
                if (gamepad2.dpad_down) {
                    intakeRotatePos -= 0.05;
                    sleep(50);
                }


                // 0.6 = 180 degrees
                if(gamepad2.dpad_left) intakeRotatePos = .3;
                if (gamepad2.dpad_right) intakeRotatePos = 0;

                if (intakeRotatePos < 0) intakeRotatePos = 0;

                if (intakeRotatePos >1) intakeRotatePos = 1;




                intakeRotater.setPosition(intakeRotatePos);

                if (gamepad2.dpad_up) armBaseGoToPos(3);





            }
        }));

        waitForStart();



        runtime.reset();


        threadPool.registerThread(new TaskThread(new TaskThread.Actions() {
            @Override
            public void work() {
                double turretPower;
                double jointPower;
                double countsPerRotation = 5281.1;
                double degreesPerRotation = 180;
                double countsPerDegree = countsPerRotation / degreesPerRotation;


                double rotate = gamepad2.left_stick_x;
                double jointArm = gamepad2.right_stick_y;

                double jointDegrees = secondArm.getCurrentPosition() / countsPerDegree;

                if (gamepad2.dpad_up) armJointGoToPos(3);



                turretPower = rotate * 0.5;



                jointPower = jointArm * 0.75;

                if ((jointDegrees > 180) && (jointPower < 0)|| (jointDegrees < -180) || (jointPower> 0)) jointPower = 0;


                turret.setPower(turretPower);


                secondArm.setPower(jointPower);
                if (gamepad1.dpad_left) rampCarouselPwr(0.1, 1);
                if (gamepad1.dpad_right) rampCarouselPwr(-0.1, -1);
                if (gamepad1.dpad_down) carousel.setPower(0);





            }
        }));
        double intakePower = 0;


        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            // start threads in pool
            threadPool.startTasks();




            if (gamepad1.a) intakePower = 1;
            if (gamepad1.b) intakePower = -1;
            if (gamepad1.x) intakePower = 0;

            if (gamepad2.a) intakePower = 1;
            if (gamepad2.b) intakePower = -1;
            if (gamepad2.x) intakePower = 0;

            intake1.setPower(intakePower);
            intake2.setPower(intakePower);



            // Show the elapsed game time and wheel power.
            //telemetry.addData("Status", "Run Time: " + runtime.toString());
            //telemetry.addData("pushed:", true);
            telemetry.addData("Motors", firstArm.getCurrentPosition());
            telemetry.update();
        }

        // Stopping threads at the end
        threadPool.stopTasks();
    }

    public void rampCarouselPwr(double initialPwr, double maxPwr) {
        carousel.setPower(initialPwr);
        if (initialPwr < 0) {
            double curPower = initialPwr;
            while (curPower >= maxPwr) {
                curPower -= 0.0025;
                carousel.setPower(curPower);
            }

        } else {
            double curPower = initialPwr;
            while (curPower <= maxPwr) {
                curPower += 0.0025;
                carousel.setPower(curPower);
            }
        }
    }

    public void turnSecondArmDegrees(double degrees, double power) {
        double countsPerRotation = 5281.1;
        double degreesPerRotation = 180;

        double countsPerDegree = countsPerRotation / degreesPerRotation;
        double target = degrees * countsPerDegree;

        resetArmJointEncoders();

        while (Math.abs(firstArm.getCurrentPosition() - target) > 100) {
            firstArm.setPower(power);
        }

        firstArm.setPower(0);

        resetArmJointEncoders();

    }

    public void turnFirstArmDegrees(double degrees, double power) {
        double countsPerRotation = 5281.1;
        double degreesPerRotation = 180;

        double countsPerDegree = countsPerRotation / degreesPerRotation;
        double target = degrees * countsPerDegree;

        resetArmBaseEncoders();


        while (Math.abs(firstArm.getCurrentPosition() - target) > 100) {
            firstArm.setPower(power);
        }

        firstArm.setPower(0);


        resetArmBaseEncoders();

    }

    public void resetArmBaseEncoders() {
        firstArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        firstArm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);



    }

    public void resetArmJointEncoders() {


        secondArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        secondArm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    }



    public void armBaseGoToPos (double pos){

        double countsPerRotation = 5281.1;
        double degreesPerRotation = 180;
        double countsPerDegree = countsPerRotation / degreesPerRotation;

        double degrees = 0;
        if (pos == 1){
            degrees = 0;


        }

        if (pos == 2) {
            degrees = 0;


        }

        if (pos == 3){
            degrees = 180;






        }

        double target = countsPerDegree * degrees;


        while ((Math.abs(firstArm.getCurrentPosition()) - target) < 0){
            firstArm.setPower(0.3);
            telemetry.addData("target :", target);
            telemetry.addData("first arm pos:", firstArm.getCurrentPosition());
            telemetry.update();

        }

        firstArm.setPower(0);





    }


    public void armJointGoToPos (double pos){double countsPerRotation = 5281.1;
        double degreesPerRotation = 180;
        double countsPerDegree = countsPerRotation / degreesPerRotation;

        double degrees = 0;
        if (pos == 1){
            degrees = 0;


        }

        if (pos == 2) {
            degrees = 0;


        }

        if (pos == 3){
            degrees = 90;






        }

        double target = countsPerDegree * degrees;


        while ((Math.abs(secondArm.getCurrentPosition()) - target) < 0){
            secondArm.setPower(-0.3);
            //  telemetry.addData("target: ", target);
            //  telemetry.addData("cur pos:", secondArm.getCurrentPosition());
            //  telemetry.update();

        }

        secondArm.setPower(0);
        intakeRotater.setPosition(.3);





    }


    public void universalArmBaseMoveTo(int pos) {
        double countsPerRotation = 5281.1;
        double degreesPerRotation = 180;
        double countsPerDegree = countsPerRotation / degreesPerRotation;

        double degrees = 0;
        if (pos == 0) {
            degrees = 50;
        }
        if (pos == 1) {
            degrees = 10;
        }

        if (pos == 2) {
            degrees = 45;
        }

        if (pos == 3) {
            degrees =50;
        }
        if (pos == 4) {
            degrees = 0;
        }
        double target = (countsPerDegree * degrees);

        if (target > firstArm.getCurrentPosition()) {
            while ((firstArm.getCurrentPosition() - target) < 0) {
                telemetry.addData("target position", target);
                telemetry.addData("current position", firstArm.getCurrentPosition());
                telemetry.update();
                firstArm.setPower(-0.8);
                sleep(50);

            }
            firstArm.setPower(0);
        }
        else {
            while ((firstArm.getCurrentPosition() - target) > 0) {
                telemetry.addData("target position", target);
                telemetry.addData("current position", firstArm.getCurrentPosition());

                telemetry.update();

                firstArm.setPower(0.8);
                sleep(50);

            }
            firstArm.setPower(0);
        }

    }
    public void universalArmJointMoveTo(int pos) {
        double countsPerRotation = 5281.1;
        double degreesPerRotation = 180;
        double countsPerDegree = countsPerRotation / degreesPerRotation;



        double degrees = 0;
        if (pos == 0) {
            degrees = 20;
        }
        if (pos == 1) {
            degrees = 110;
        }

        if (pos == 2) {
            degrees = 75;
        }

        if (pos == 3) {
            degrees = 90;
        }
        if (pos == 4) {
            degrees = -5;
        }
        double target = (countsPerDegree * degrees);

        if (target > secondArm.getCurrentPosition()) {
            while ((secondArm.getCurrentPosition() - target) < 0) {
                telemetry.addData("target position", target);
                telemetry.addData("current position", secondArm.getCurrentPosition());
                telemetry.update();
                secondArm.setPower(1);
                sleep(50);

            }
            secondArm.setPower(0);
        }
        else {
            while ((secondArm.getCurrentPosition() - target) > 0) {
                telemetry.addData("target position", target);
                telemetry.addData("current position", secondArm.getCurrentPosition());

                telemetry.update();

                secondArm.setPower(-1);
                sleep(50);

            }
            secondArm.setPower(0);
        }

    }

    public double [] settleArm (){






        return null;
    }



}

