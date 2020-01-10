package org.team2471.FRC2020

import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.MotorControllerID
import org.team2471.frc.lib.actuators.SparkMaxID
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.Subsystem
import edu.wpi.first.networktables.NetworkTableInstance

object Shooter : Subsystem("Shoot") {
    private val shootingMotor = MotorController(SparkMaxID(Sparks.SHOOTER))
    private val shootingSlave = MotorController(SparkMaxID(Sparks.SHOOTER2))

    private val table = NetworkTableInstance.getDefault().getTable(name)
    private val rpmTable = table.getEntry("RPM")

    init {
        shootingSlave.follow(shootingMotor)
    }

    fun setPower(power: Double) {
        shootingMotor.setPercentOutput(power)
    }

    private fun setRPM(RPM: Double) {
        shootingMotor.setVelocitySetpoint(RPM)
    }

    override suspend fun default() {
        periodic {
            //setPower(OI.driveRightTrigger)
            setRPM(rpmTable.getDouble(0.0))
        }
    }
}