package org.team2471.frc2020

import edu.wpi.first.networktables.EntryListenerFlags
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.team2471.frc.lib.framework.use
import org.team2471.frc.lib.motion.following.driveAlongPath
import org.team2471.frc.lib.motion_profiling.Autonomi
import org.team2471.frc.lib.util.measureTimeFPGA
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
    private val cacheFile = File("/home/lvuser/autonomi.json")

    private val lyricsChooser = SendableChooser<String?>().apply {
        setDefaultOption("Country roads", "Country roads")
        addOption("take me home", "take me home")
    }

    private val testAutoChooser = SendableChooser<String?>().apply {
        setDefaultOption("None", null)
        addOption("20 Foot Test", "20 Foot Test")
        addOption("8 Foot Straight", "8 Foot Straight")
        addOption("2 Foot Circle", "2 Foot Circle")
        addOption("4 Foot Circle", "4 Foot Circle")
        addOption("8 Foot Circle", "8 Foot Circle")
        addOption("Hook Path", "Hook Path")
    }

    private val autonomousChooser = SendableChooser<suspend() -> Unit>().apply {
        setDefaultOption("5 Ball Trench Run", ::trenchRun5)
        addOption("Tests", ::testAuto)
    }

    init {
        println("Got into Autonomous' init. Hi. 222222222222222222222")
        SmartDashboard.putData("Best Song Lyrics", lyricsChooser)
        SmartDashboard.putData("Tests", testAutoChooser)
        SmartDashboard.putData("Autos", autonomousChooser)

        try {
            autonomi = Autonomi.fromJsonString(cacheFile.readText())
            println("Autonomi cache loaded.")
        } catch (_: Throwable) {
            DriverStation.reportError("Autonomi cache could not be found", false)
            autonomi = Autonomi()
        }

        NetworkTableInstance.getDefault()
            .getTable("PathVisualizer")
            .getEntry("Autonomi").addListener({ event ->
                val json = event.value.string
                if (!json.isEmpty()) {
                    val t = measureTimeFPGA {
                        autonomi = Autonomi.fromJsonString(json)
                    }
                    println("Loaded autonomi in $t seconds")

                    cacheFile.writeText(json)
                    println("New autonomi written to cache")
                } else {
                    autonomi = Autonomi()
                    DriverStation.reportWarning("Empty autonomi received from network tables", false)
                }
            }, EntryListenerFlags.kImmediate or EntryListenerFlags.kNew or EntryListenerFlags.kUpdate)
    }

    suspend fun autonomous() = use(Drive, name = "Autonomous") {
        println("Got into Auto fun autonomous. Hi. 888888888888888")

        val autoEntry = autonomousChooser.selected
        println("Got to right before invoke. Hi. 5555555555555555555555555 $autoEntry")
        autoEntry.invoke()
    }

    suspend fun testAuto() {
        val testPath = testAutoChooser.selected
        if (testPath != null) {
            val testAutonomous = autonomi["Tests"]
            val path = testAutonomous[testPath]
            Drive.driveAlongPath(path, true, 0.0)
        }
    }

    suspend fun trenchRun5() = use(Drive){
        println("Got into fun trenchRun5. Hi. 11111111111111111111111111111111111111")
        val auto = autonomi["5 Ball Trench Run"]
        if (auto != null) {
            var path = auto["Intake 2 Cells"]
            Drive.driveAlongPath(path, true)
            path = auto["Shooting Position"]
            Drive.driveAlongPath(path, false)
        }
    }

    suspend fun test8FtStraight() = use(Drive){
        val auto = autonomi["Tests"]
        if (auto != null) {
            var path = auto["8 Foot Straight"]
            Drive.driveAlongPath(path, true)
        }
    }
}