package com.geuso.disrupty.ns.disruption

import assertk.assert
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import com.geuso.disrupty.disruption.DisruptionEvaluator
import com.geuso.disrupty.ns.traveloption.DisruptionStatus
import com.geuso.disrupty.ns.traveloption.TravelOption
import com.geuso.disrupty.ns.traveloption.TravelOptionNotification
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/*
 * https://stackoverflow.com/questions/31272732/unit-testing-with-android-xmlpullparser-on-the-jvm
 * https://stackoverflow.com/questions/30629314/android-studio-with-junit-4-12-junit-version-3-8-or-later-expected
 */
@RunWith(RobolectricTestRunner::class)
class DisruptionEvaluationTest {

    @Test
    fun testUndisruptedEvaluation() {
        val undisruptedTravelOption = TravelOption(null, 2, true, DisruptionStatus.ACCORDING_TO_PLAN)
        val disruptionCheckResult = DisruptionEvaluator.getDisruptionCheckResultFromTravelOptions(listOf(undisruptedTravelOption))

        assert(disruptionCheckResult.isDisrupted, "DisruptionCheckResult isDisrupted").isEqualTo(false)
        assert(disruptionCheckResult.disruptionStatus, "DisruptionCheckResult status").isEqualTo(DisruptionStatus.ACCORDING_TO_PLAN)
        assert(disruptionCheckResult.message, "DisruptionCheckResult message").isEqualTo(null)
    }

    @Test
    fun testDisruptedEvaluation(){
        val undisruptedTravelOption = TravelOption(null, 2, true, DisruptionStatus.ACCORDING_TO_PLAN)
        val disruptedTravelOption = TravelOption(null, 0, false, DisruptionStatus.DELAYED)
        val disruptionCheckResult = DisruptionEvaluator.getDisruptionCheckResultFromTravelOptions(listOf(undisruptedTravelOption, disruptedTravelOption))

        assert(disruptionCheckResult.isDisrupted, "DisruptionCheckResult isDisrupted").isEqualTo(true)
        assert(disruptionCheckResult.disruptionStatus, "DisruptionCheckResult status").isEqualTo(DisruptionStatus.DELAYED)
        assert(disruptionCheckResult.message, "DisruptionCheckResult message").isEqualTo(null)
    }

    @Test
    fun testNotSevereNotificationNotReportedAsDisruption() {
        val travelOption = TravelOption(TravelOptionNotification(false, "this is a test message"), 2, true, DisruptionStatus.ACCORDING_TO_PLAN)
        val disruptionCheckResult = DisruptionEvaluator.getDisruptionCheckResultFromTravelOptions(listOf(travelOption))

        assert(disruptionCheckResult.isDisrupted, "DisruptionCheckResult isDisrupted").isEqualTo(false)
        assert(disruptionCheckResult.disruptionStatus, "DisruptionCheckResult status").isEqualTo(DisruptionStatus.ACCORDING_TO_PLAN)
        assert(disruptionCheckResult.message, "DisruptionCheckResult message").isEqualTo(null)
    }

    @Test
    fun testSevereNotificationNotReportedAsDisruptionWhenStatusIsGood() {
        val travelOption = TravelOption(TravelOptionNotification(true, "something bad is going on"), 0, true, DisruptionStatus.ACCORDING_TO_PLAN)

        val disruptionCheckResult = DisruptionEvaluator.getDisruptionCheckResultFromTravelOptions(listOf(travelOption))

        assert(disruptionCheckResult.isDisrupted, "DisruptionCheckResult isDisrupted").isEqualTo(false)
        assert(disruptionCheckResult.disruptionStatus, "DisruptionCheckResult status").isEqualTo(DisruptionStatus.ACCORDING_TO_PLAN)
        assert(disruptionCheckResult.message, "DisruptionCheckResult message").isNotEqualTo(null)
    }




}