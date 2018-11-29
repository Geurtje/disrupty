package com.geuso.disrupty.disruption

import assertk.assert
import assertk.assertions.isEqualTo
import com.geuso.disrupty.ns.traveloption.DisruptionStatus
import com.geuso.disrupty.ns.traveloption.TravelOption
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class DisruptionEvaluationStatusTest(
        private val disruptionStatus: DisruptionStatus,
        private val isDisrupted: Boolean
) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun errorCode() = listOf(
                arrayOf(DisruptionStatus.UNKNOWN, false),
                arrayOf(DisruptionStatus.ACCORDING_TO_PLAN, false),
                arrayOf(DisruptionStatus.CHANGED, false),
                arrayOf(DisruptionStatus.DELAYED, false),
                arrayOf(DisruptionStatus.NEW, false),
                arrayOf(DisruptionStatus.NOT_OPTIMAL, false),
                arrayOf(DisruptionStatus.NOT_POSSIBLE, true),
                arrayOf(DisruptionStatus.PLAN_CHANGED, false)
        )
    }

    @Test
    fun `Assert DisruptionStatus disrupted or not based on status`() {
        println("Testing that DisruptionStatus $disruptionStatus is disrupted: $isDisrupted")
        val travelOption = TravelOption(null, 2, true, disruptionStatus)
        val disruptionCheckResult = DisruptionEvaluator.getDisruptionCheckResultFromTravelOptions(listOf(travelOption))

        assert(disruptionCheckResult.isDisrupted, "DisruptionCheckResult isDisrupted").isEqualTo(isDisrupted)
    }
}
