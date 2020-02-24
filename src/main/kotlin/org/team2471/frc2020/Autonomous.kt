package org.team2471.frc2020

import edu.wpi.first.networktables.EntryListenerFlags
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.coroutines.parallel
import org.team2471.frc.lib.framework.use
import org.team2471.frc.lib.motion.following.driveAlongPath
import org.team2471.frc.lib.motion_profiling.Autonomi
import org.team2471.frc.lib.util.measureTimeFPGA
import org.team2471.frc2020.actions.*
import java.io.File

private lateinit var autonomi: Autonomi


enum class Side {
    LEFT,
    RIGHT;

    operator fun not(): Side = when (this) {
        LEFT -> RIGHT
        RIGHT -> LEFT
    }
}

private var startingSide = Side.RIGHT


object AutoChooser {
    var cacheFile : File? = null

    private val lyricsChooser = SendableChooser<String?>().apply {
        setDefaultOption("Country roads", "Country roads")
        addOption("take me home", "take me home")
    }

    private val testAutoChooser = SendableChooser<String?>().apply {
        addOption("None", null)
        addOption("20 Foot Test", "20 Foot Test")
        addOption("8 Foot Straight", "8 Foot Straight")
        addOption("2 Foot Circle", "2 Foot Circle")
        addOption("4 Foot Circle", "4 Foot Circle")
        addOption("8 Foot Circle", "8 Foot Circle")
        addOption("Hook Path", "Hook Path")
        setDefaultOption("90 Degree Turn", "90 Degree Turn")
    }

    private val autonomousChooser = SendableChooser<String?>().apply {
        setDefaultOption("Tests", "testAuto")
        addOption("5 Ball Trench Run", "trenchRun5")
        addOption("10 Ball Shield Generator", "shieldGenerator10")
        addOption("8 Ball Trench Run", "trenchRun8")
    }

    init {
        println("Got into Autonomous' init. Hi. 222222222222222222222")
        SmartDashboard.putData("Best Song Lyrics", lyricsChooser)
        SmartDashboard.putData("Tests", testAutoChooser)
        SmartDashboard.putData("Autos", autonomousChooser)

        try {

            cacheFile = File("/home/lvuser/autonomi.json")
            if (cacheFile  != null) {
                autonomi = Autonomi.fromJsonString(cacheFile?.readText())
                println("Autonomi cache loaded.")
            } else {
                println("Autonomi failed to load!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! RESTART ROBOT!!!!!!")
            }
        } catch (_: Throwable) {
            DriverStation.reportError("Autonomi cache could not be found", false)
            autonomi = Autonomi()
        }
        println("In Auto Init. Before AddListener. Hi.")
        NetworkTableInstance.getDefault()
            .getTable("PathVisualizer")
            .getEntry("Autonomi").addListener({ event ->
                println("Automous change detected")
                val json = event.value.string
                if (!json.isEmpty()) {
                    val t = measureTimeFPGA {
                        autonomi = Autonomi.fromJsonString(json)
                    }
                    println("Loaded autonomi in $t seconds")
                    cacheFile?.writeText(json)
                    println("New autonomi written to cache")
                } else {
                    autonomi = Autonomi()
                    DriverStation.reportWarning("Empty autonomi received from network tables", false)
                }
            }, EntryListenerFlags.kImmediate or EntryListenerFlags.kNew or EntryListenerFlags.kUpdate)
    }

    suspend fun autonomous() = use(Drive, name = "Autonomous") {
        println("Got into Auto fun autonomous. Hi. 888888888888888")
        val selAuto = SmartDashboard.getString("Autos/selected", "no auto selected")
        println("Selected Auto = *****************   $selAuto ****************************")
        when (selAuto) {
            "Tests" -> testAuto()
            "5 Ball Trench Run" -> trenchRun5()
            "10 Ball Shield Generator" -> shieldGenerator10()
            "8 Ball Trench Run" -> trenchRun8()
            else -> println("No function found for ---->$selAuto<-----")
        }

//        var autoEntry = autonomousChooser.selected
//        autoEntry = ::shieldGenerator10 //delete this line after 2/18/2020
//        println("Got to right before invoke. Hi. 5555555555555555555555555 $autoEntry")
//        autoEntry.invoke()
    //    shieldGenerator10() //delete this line
    }

    suspend fun testAuto() {
        val testPath = SmartDashboard.getString("Tests/selected", "no test selected") // testAutoChooser.selected
        if (testPath != null) {
            val testAutonomous = autonomi["Tests"]
            val path = testAutonomous[testPath]
            Drive.driveAlongPath(path, true)
        }
    }

    suspend fun trenchRun5() = use(Drive, Shooter, Intake, Feeder) {
        val auto = autonomi["5 Ball Trench Run"]
        if (auto != null) {
            Intake.setPower(Intake.INTAKE_POWER)
            var path = auto["01- Intake 2 Cells"]
            Drive.driveAlongPath(path, true)
//            autoIntakeStop()
            path = auto["02- Shooting Position"]
            Drive.driveAlongPath(path, false)
            autoPrepShot(5)
        }
    }

    suspend fun shieldGenerator10() = use(Drive, Shooter, Intake, Feeder) {
        try {
            println("In sheildGenerator auto. Hi.")
            val auto = autonomi["10 Ball Shield Generator"]
            println(auto == null)
            if (true){//auto != null) {
                Intake.setPower(1.0) //Intake.INTAKE_POWER)
                Intake.extend = true
                var path = auto["01- Intake 2 Cells"]
                Drive.driveAlongPath(path, true, 0.125)
                delay(0.25)
                Intake.setPower(0.5)
                Intake.extend = false
                parallel ({
                    delay(path.duration * 0.25)
                    val rpmSetpoint = Shooter.rpmCurve.getValue(FrontLimelight.distance.asInches)
                    Shooter.rpm = rpmSetpoint
                }, {
                    path = auto["02- Shooting Position"]
                    Drive.driveAlongPath(path, false)
                })
//                parallel ({
//                    autoPrepShot(7)
//                }, {
//                    delay(2.0)
                    autoPrepShot(5)
                    Intake.setPower(1.0)
                    Intake.extend = true
                    path = auto["03- Intake 3 Cells"]
                    Drive.driveAlongPath(path, false)
//                })
                    parallel ({
                        path = auto["04- Intake 2 Cells"]
                        Drive.driveAlongPath(path, false)
                    }, {
                        delay(path.duration * 0.9)
                        Intake.extend = true
                    })
                    Intake.setPower(1.0)
                    Intake.extend = false
                    path = auto["05- Shooting Position"]
                    Drive.driveAlongPath(path, false)
                    autoPrepShot(5)
                }
        } finally {
            Shooter.stop()
            Shooter.rpmSetpoint = 0.0
            Feeder.setPower(0.0)
            Intake.extend = false
            Intake.setPower(0.0)
        }
    }

    suspend fun trenchRun8() = use(Drive, Intake, Shooter, Feeder){
        try {
            val auto = autonomi["8 Ball Trench Run"]
            if (auto != null) {
                var path = auto["Collect 1 and Shoot 4 Cells"]
                Intake.setPower(Intake.INTAKE_POWER)
                Intake.extend = true
                parallel ({
                    Drive.driveAlongPath(path, true)
                }, {
                    delay(path.duration * 0.5)
                    val rpmSetpoint = Shooter.rpmCurve.getValue(FrontLimelight.distance.asInches)
                    Shooter.rpm = rpmSetpoint
                })
                autoPrepShot(4)
                path = auto["Collect 4 Cells"]
                Drive.driveAlongPath(path, false)
                path = auto["Shoot 4 Cells"]
                parallel ({
                    Drive.driveAlongPath(path, false)
                }, {
                    Intake.extend = false
                })
                autoPrepShot(4)
            }
        } finally {
            Shooter.stop()
            Shooter.rpmSetpoint = 0.0
            Feeder.setPower(0.0)
            Intake.extend = false
            Intake.setPower(0.0)
        }
    }

    suspend fun test8FtStraight() = use(Drive) {
        val auto = autonomi["Tests"]
        if (auto != null) {
            var path = auto["8 Foot Straight"]
            Drive.driveAlongPath(path, true)
        }
    }

    suspend fun test8FtCircle() = use(Drive) {
        val auto = autonomi["Tests"]
        if (auto != null) {
            var path = auto["8 Foot Circle"]
            Drive.driveAlongPath(path, true)
        }
    }

    suspend fun test90DegreeTurn() = use(Drive) {
        val auto = autonomi["Tests"]
        if (auto != null) {
            Drive.driveAlongPath( auto["90 Degree Turn"], true, 2.0)
        }
    }
}