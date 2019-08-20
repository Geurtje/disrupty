package com.geuso.disrupty.disruption

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.geuso.disrupty.ns.instantOf
import com.geuso.disrupty.ns.traveloption.DisruptionStatus
import com.geuso.disrupty.ns.traveloption.TravelOption
import com.geuso.disrupty.ns.traveloption.TravelOptionNotification
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner

/*
 * https://stackoverflow.com/questions/31272732/unit-testing-with-android-xmlpullparser-on-the-jvm
 * https://stackoverflow.com/questions/30629314/android-studio-with-junit-4-12-junit-version-3-8-or-later-expected
 */
@RunWith(RobolectricTestRunner::class)
class DisruptionEvaluationTest {

    companion object {
        private val sharedPreferences = Mockito.mock(SharedPreferences::class.java)
        private val context = Mockito.mock(Context::class.java)

        init {
            Mockito.`when`(sharedPreferences.getString(Mockito.eq("pref_disruption_minimum_delay_time"), Mockito.anyString()))
                    .thenReturn("2")
            Mockito.`when`(context.resources)
                    .thenReturn(Mockito.mock(Resources::class.java))
        }
    }


    @Test
    fun `Test traveloptions not disrupted`() {
        val undisruptedTravelOption = TravelOption(null, 2, true, DisruptionStatus.ACCORDING_TO_PLAN)
        val disruptionCheckResult = DisruptionEvaluator(context, sharedPreferences).getDisruptionCheckResultFromTravelOptions(listOf(undisruptedTravelOption))

        assertThat(disruptionCheckResult.isDisrupted, "DisruptionCheckResult isDisrupted").isEqualTo(false)
        assertThat(disruptionCheckResult.disruptionStatus, "DisruptionCheckResult status").isEqualTo(DisruptionStatus.ACCORDING_TO_PLAN)
        assertThat(disruptionCheckResult.message, "DisruptionCheckResult message").isNull()
    }

    @Test
    fun `Test delayed travel option is not disrupted without departure times`(){
        val undisruptedTravelOption = TravelOption(null, 2, true, DisruptionStatus.ACCORDING_TO_PLAN)
        val disruptedTravelOption = TravelOption(null, 0, false, DisruptionStatus.DELAYED)
        val disruptionCheckResult = DisruptionEvaluator(context, sharedPreferences).getDisruptionCheckResultFromTravelOptions(listOf(undisruptedTravelOption, disruptedTravelOption))

        assertThat(disruptionCheckResult.isDisrupted, "DisruptionCheckResult isDisrupted").isEqualTo(false)
        assertThat(disruptionCheckResult.disruptionStatus, "DisruptionCheckResult status").isEqualTo(DisruptionStatus.ACCORDING_TO_PLAN)
        assertThat(disruptionCheckResult.message, "DisruptionCheckResult message").isNull()
    }

    @Test
    fun `Test travel option with not severe notification is not disrupted`() {
        val travelOption = TravelOption(TravelOptionNotification(false, "this is a test message"), 2, true, DisruptionStatus.ACCORDING_TO_PLAN)
        val disruptionCheckResult = DisruptionEvaluator(context, sharedPreferences).getDisruptionCheckResultFromTravelOptions(listOf(travelOption))

        assertThat(disruptionCheckResult.isDisrupted, "DisruptionCheckResult isDisrupted").isEqualTo(false)
        assertThat(disruptionCheckResult.disruptionStatus, "DisruptionCheckResult status").isEqualTo(DisruptionStatus.ACCORDING_TO_PLAN)
        assertThat(disruptionCheckResult.message, "DisruptionCheckResult message").isNull()
    }

    @Test
    fun `Test travel option with severe notification is not disrupted when status is according to plan`(){
        val travelOption = TravelOption(TravelOptionNotification(true, "something bad is going on"), 0, true, DisruptionStatus.ACCORDING_TO_PLAN)

        val disruptionCheckResult = DisruptionEvaluator(context, sharedPreferences).getDisruptionCheckResultFromTravelOptions(listOf(travelOption))

        assertThat(disruptionCheckResult.isDisrupted, "DisruptionCheckResult isDisrupted").isEqualTo(false)
        assertThat(disruptionCheckResult.disruptionStatus, "DisruptionCheckResult status").isEqualTo(DisruptionStatus.ACCORDING_TO_PLAN)
        assertThat(disruptionCheckResult.message, "DisruptionCheckResult message").isNull()
    }

    @Test
    fun `Test travel option with severe notification is disrupted when status is not according to plan`(){
        val travelOption = TravelOption(TravelOptionNotification(true, "something bad is going on"), 0, true, DisruptionStatus.CHANGED)

        val disruptionCheckResult = DisruptionEvaluator(context, sharedPreferences).getDisruptionCheckResultFromTravelOptions(listOf(travelOption))

        assertThat(disruptionCheckResult.isDisrupted, "DisruptionCheckResult isDisrupted").isEqualTo(true)
        assertThat(disruptionCheckResult.disruptionStatus, "DisruptionCheckResult status").isEqualTo(DisruptionStatus.CHANGED)
        assertThat(disruptionCheckResult.message, "DisruptionCheckResult message").isNotNull()
    }


    @Test
    fun `Test travel option with 5 minute delay is disrupted`() {
        val travelOption = TravelOption(null, 0, true, DisruptionStatus.CHANGED,
                instantOf("2018-11-17T20:39:00+0100"),
                instantOf("2018-11-17T20:44:00+0100"),
                "+5 min"
        )

        val disruptionCheckResult = DisruptionEvaluator(context, sharedPreferences).getDisruptionCheckResultFromTravelOptions(listOf(travelOption))

        assertThat(disruptionCheckResult.isDisrupted, "DisruptionCheckResult isDisrupted").isEqualTo(true)
        assertThat(disruptionCheckResult.disruptionStatus, "DisruptionCheckResult status").isEqualTo(DisruptionStatus.CHANGED)
        assertThat(disruptionCheckResult.message, "DisruptionCheckResult message").isNull()
        assertThat(disruptionCheckResult.departureDelay, "DisruptionCheckResult departure delay string").isEqualTo("+5 min")
    }

    @Test
    fun `Test travel option with 2 minute delay is not disrupted`() {
        val travelOption = TravelOption(null, 0, true, DisruptionStatus.CHANGED,
                instantOf("2018-11-17T20:39:00+0100"),
                instantOf("2018-11-17T20:41:00+0100"),
                "+2 min"
        )

        val disruptionCheckResult = DisruptionEvaluator(context, sharedPreferences).getDisruptionCheckResultFromTravelOptions(listOf(travelOption))

        assertThat(disruptionCheckResult.isDisrupted, "DisruptionCheckResult isDisrupted").isEqualTo(false)
        assertThat(disruptionCheckResult.disruptionStatus, "DisruptionCheckResult status").isEqualTo(DisruptionStatus.ACCORDING_TO_PLAN)
        assertThat(disruptionCheckResult.message, "DisruptionCheckResult message").isNull()
        assertThat(disruptionCheckResult.departureDelay, "DisruptionCheckResult departure delay string").isNull()
    }

    @Test
    fun `Test travel option with 2 minute delay and status delayed is not disrupted`() {
        val travelOption = TravelOption(null, 0, true, DisruptionStatus.DELAYED,
                instantOf("2018-11-17T20:39:00+0100"),
                instantOf("2018-11-17T20:41:00+0100"),
                "+2 min"
        )

        val disruptionCheckResult = DisruptionEvaluator(context, sharedPreferences).getDisruptionCheckResultFromTravelOptions(listOf(travelOption))

        assertThat(disruptionCheckResult.isDisrupted, "DisruptionCheckResult isDisrupted").isEqualTo(false)
        assertThat(disruptionCheckResult.disruptionStatus, "DisruptionCheckResult status").isEqualTo(DisruptionStatus.ACCORDING_TO_PLAN)
        assertThat(disruptionCheckResult.message, "DisruptionCheckResult message").isNull()
        assertThat(disruptionCheckResult.departureDelay, "DisruptionCheckResult departure delay string").isNull()
    }


}