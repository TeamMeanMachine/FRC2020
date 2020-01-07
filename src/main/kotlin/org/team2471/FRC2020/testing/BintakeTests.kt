package org.team2471.FRC2020.testing

import org.team2471.FRC2020.Bintake
import org.team2471.FRC2020.OI
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.units.degrees

suspend fun Bintake.pivotTest() {
    periodic {
        Bintake.angle = (OI.driveTranslation.y * (-45) + 150).degrees
        println("Bintake Angle: $angle")
    }
}