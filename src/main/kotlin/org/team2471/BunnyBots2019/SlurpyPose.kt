package org.team2471.BunnyBots2019

import org.team2471.frc.lib.units.Angle
import org.team2471.frc.lib.units.degrees

data class SlurpyPose(val shoulderAngle: Angle, val wristAngle: Angle) {
    companion object {
        val START_POSE = SlurpyPose((-53).degrees, 48.degrees)
        val SCORING_POSE = SlurpyPose((-80).degrees, 66.degrees)
        val SAFETY_POSE = SlurpyPose((-5).degrees, 180.degrees)
        val GROUND_POSE = SlurpyPose(5.degrees, 68.degrees)
        val OLD_STEAL_POSE = SlurpyPose(90.degrees, 37.degrees)
        //val ARM_OUT_POSE = Pose((-90).degrees, (-90).degrees)
        val HIGH_STEAL_POSE = SlurpyPose(84.3.degrees, (10.0).degrees)
        val LOW_STEAL_POSE = SlurpyPose(36.8.degrees, 34.9.degrees)
    }
}

data class BintakePose(val angle: Angle) {
    companion object {
        val SAFETY_POSE = BintakePose(206.degrees)
        val INTAKE_POSE = BintakePose(150.degrees)
        val SCORING_POSE = BintakePose(40.degrees)
        val SPITTING_POSE = BintakePose(135.degrees)
    }
}
// arm: 2.7  wrist: 10.4
// arm: -46 wrist: 36