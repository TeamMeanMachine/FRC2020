@file:Suppress("EXPERIMENTAL_API_USAGE")

package org.team2471.BunnyBots2019.testing

import org.team2471.BunnyBots2019.Drive
import org.team2471.BunnyBots2019.OI
import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.coroutines.suspendUntil
import org.team2471.frc.lib.framework.use
import org.team2471.frc.lib.math.round
import org.team2471.frc.lib.motion.following.drive
import org.team2471.frc.lib.motion.following.steerToAngle
import org.team2471.frc.lib.units.degrees

suspend fun Drive.steeringTests() = use(this) {
    for (module in 0..3) {
        for (quadrant in 0..8) {
            Drive.modules[module].angleSetpoint = (quadrant * 45.0).degrees
            delay(0.25)
        }
        delay(0.5)
    }
}
//

suspend fun Drive.driveTests() = use(this) {

    for (i in 0..3) {
            Drive.modules[i].setDrivePower(0.5)
            delay(1.0)
            Drive.modules[i].setDrivePower(0.0)
            delay(0.2)
    }
}

suspend fun Drive.fullTest() = use(this) {
    periodic {
        // (i in 0..3) {
         //   Drive.modules[i].setDrivePower(OI.driverController.leftThumbstickY)
            //Drive.modules[i].(Math.atan2(OI.driverController.rightThumbstickY, OI.driverController.rightThumbstickX).degrees)
        //}

        Drive.drive(OI.driveTranslation, OI.driveRotation)
    }
}

//suspend fun Drive.steeringTests() = use(this) {
//    for (i in 1..4) {
//        for (j in 0..3) {
//            Drive.modules[j].angleSetpoint = (i * 90.0).degrees
//            println("Current Angle Setpoint: " + Drive.modules[j].angleSetpoint)
//        }
//        delay(0.75)
//    }
//    println("Task completed. Hi.")
//
//
//    var iMotor = MotorController(SparkMaxID(3))
//    iMotor.current
//}
/*    iMotor.setPercentOutput(0.5)
    delay(3.0)
    iMotor.setPercentOutput(0.0)
    println("Drive test. Hi.")

    delay(1.0)
    var iMotor2 = MotorController(SparkMaxID(15))
    iMotor2.setPercentOutput(0.5)
    delay(3.0)
    iMotor2.setPercentOutput(0.0)
    println("Second drive test. Hi.")

    delay(1.0)
    var iMotor3 = MotorController(SparkMaxID(17))
    iMotor3.setPercentOutput(0.5)
    delay(3.0)
    iMotor3.setPercentOutput(0.0)
    println("Third drive test. Hi.")

    delay(1.0)
    var iMotor4 = MotorController(SparkMaxID(16))
    iMotor4.setPercentOutput(0.5)
    delay(3.0)
    iMotor4.setPercentOutput(0.0)
    println("Fourth drive test. Hi.")
*/

//    Drive.modules[0].setDrivePower(0.25)
//    val frCurrent = Drive.frontRightModule.driveMotor.testAverageAmperage(0.5, 0.25.seconds, 0.5.seconds)
//    val blCurrent = Drive.backLeftModule.driveMotor.testAverageAmperage(0.5, 0.25.seconds, 0.5.seconds)
//    val brCurrent = Drive.backRightModule.driveMotor.testAverageAmperage(0.5, 0.25.seconds, 0.5.seconds)


