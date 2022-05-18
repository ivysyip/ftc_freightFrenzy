package org.firstinspires.ftc.teamcode.currentlyUsing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.AutonomousMethodsNewArm;
import org.firstinspires.ftc.teamcode.FreightFrenzyOpenCVPipeline;
import org.firstinspires.ftc.teamcode.thread.TaskThread;
import org.firstinspires.ftc.teamcode.thread.ThreadPool;

@Autonomous(name="Blue LeftN", group="Pushbot")

public class BlueLeftNew extends AutonomousMethodsNewArmN {

    double turretDegrees = 90;
    double drivePower = 0.8;
    public ElapsedTime runtime = new ElapsedTime();



    public void runOpMode(){
        initHardware(false);
        ThreadPool threadPool = new ThreadPool();
//        sleep(5000);
        while ( pipeline == null ) {

        }
        telemetry.addData( "Init Completed", "");
        telemetry.update();
        waitForStart();



        FreightFrenzyOpenCVPipeline.ElementPosition position = pipeline.getAnalysis();

        telemetry.addData("position =", position);
        telemetry.update();

        // move to shipping hub and place freight in correct level
        angleArm(5);  sleep(50);
        angleArm(15);
        telemetry.addData("Angle: ", getArmAngleDegree());
        moveWPID(0, -6, .2);

        rotateWormGearTurret(110);

        /**
         * LEFT -> Bottom Position
         * MIDDLE -> Middle Position
         * RIGHT -> Top Position
         */
        if (position == FreightFrenzyOpenCVPipeline.ElementPosition.LEFT) {
            // move up x degrees
            angleArm(-3);
            depositPreLoad(4);
            extendArm(1350);
            depositPreLoad(1);
            sleep(1000);
        }

        else if (position == FreightFrenzyOpenCVPipeline.ElementPosition.MIDDLE) {
            // move up x degrees
            // extend some amount
            angleArm(7.5);
            depositPreLoad(4);
            extendArm(1400);
            depositPreLoad(2);
            sleep(1000);

        }
        else if (position == FreightFrenzyOpenCVPipeline.ElementPosition.RIGHT) {
            // move up x degrees
            // extend some amount
            angleArm(20);
            depositPreLoad(4);
            extendArm(1650);
            depositPreLoad(3);
            sleep(1000);

        }
        // ***** wait until arm is finished extending ***** //


        /**
         * Starting freight Cycling
         */

        /**
         * Main Thread
         *
         * Base movements (to and from Ware house)
         */


        /**
         * Arm Thread
         *
         * Arm Movements (to and from pick up and drop off)
         */
        threadPool.registerThread( new TaskThread(new TaskThread.Actions() {
            @Override
            public void work() {
                if (isStopRequested())
                    threadPool.stopTasks();
                depositPreLoad(4);
                extendArm(1000);
                angleArm(55);
                rotateWormGearTurret(0);
                sleep(1000);
                angleArm(0);
                sleep(1000);
                extendArm(0);
                telemetry.addData("Time ", runtime);
                telemetry.addData("arm extension", armExtend.getCurrentPosition());
                telemetry.update();
                done = true;
                threadPool.stopTasks();
                sleep(750);
            }
        }));

        armToZero = true;
        baseToWareHouse = false;
        contThread = false;
        /**
         * starts the cycling tasks
         */
        threadPool.startTasks();

        //***** begin by moving the arm back to pick up position *****//
        //***** and moving base to warehouse *****//


        //**** wait until there is five seconds left
        //while (runtime.seconds() < 30) {}
        /**
         * Stop EVERYTHING you are doing
         */

        armToZero = false;
        armToDrop = false;
        baseToWareHouse = false;
        baseToZero = false;
        //*** return arm back to starting position
        //*** so teleOp automation works
        armToStart = true;
        sleep(100);
        //threadPool.stopTasks();

        //**** park in warehouse
        // relative to starting position!
        //baseGoToPos(24);

        /**
         * stop cycling now so that arm and base will move at same time
         */
        //extendArm(0);
        moveWPID(-1,28,0.8);
        moveWPID(30, 0, 0.5);
        //**** end the autonomous
        while(!done && opModeIsActive()) {

        }

        threadPool.stopTasks();
        moveWPID(0, 5, .5);
        stop();













    }




}
