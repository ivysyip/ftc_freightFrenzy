# Multi-Threading
We used multi-threading during autonomous period to do multiple tasks at the same time to increase efficiency </br></br>
We mainly used it for the autonomous period as we found that there is a significant lag while using in the Tele-Operated period. We used [runToPosition](https://ftctechnh.github.io/ftc_app/doc/javadoc/com/qualcomm/robotcore/hardware/DcMotor.RunMode.html) mode on motors as a fake multi-threading solution instead.
