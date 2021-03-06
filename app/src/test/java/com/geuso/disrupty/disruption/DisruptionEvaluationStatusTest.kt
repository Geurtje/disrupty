package com.geuso.disrupty.disruption

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.geuso.disrupty.ns.traveloption.DisruptionStatus
import com.geuso.disrupty.ns.traveloption.TravelOption
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.Mockito

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

                // Status codes from the new trips API
                arrayOf(DisruptionStatus.CANCELLED, true),
                arrayOf(DisruptionStatus.CHANGE_COULD_BE_POSSIBLE, false),
                arrayOf(DisruptionStatus.ALTERNATIVE_TRANSPORT, true),
                arrayOf(DisruptionStatus.DISRUPTION, true),
                arrayOf(DisruptionStatus.MAINTENANCE, false),
                arrayOf(DisruptionStatus.REPLACEMENT, false),
                arrayOf(DisruptionStatus.ADDITIONAL, false),
                arrayOf(DisruptionStatus.SPECIAL, false),
                arrayOf(DisruptionStatus.NORMAL, false),

                // Status codes from the deprecated trips API
                arrayOf(DisruptionStatus.ACCORDING_TO_PLAN, false),
                arrayOf(DisruptionStatus.CHANGED, false),
                arrayOf(DisruptionStatus.DELAYED, false),
                arrayOf(DisruptionStatus.NEW, false),
                arrayOf(DisruptionStatus.NOT_OPTIMAL, false),
                arrayOf(DisruptionStatus.NOT_POSSIBLE, true),
                arrayOf(DisruptionStatus.PLAN_CHANGED, false)
        )

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
    fun `Assert DisruptionStatus disrupted or not based on status`() {
        println("Testing that DisruptionStatus $disruptionStatus is disrupted: $isDisrupted")
        val travelOption = TravelOption(null, 2, true, disruptionStatus)
        val disruptionCheckResult = DisruptionEvaluator(context, sharedPreferences).getDisruptionCheckResultFromTravelOptions(listOf(travelOption))

        assertThat(disruptionCheckResult.isDisrupted, "DisruptionCheckResult isDisrupted").isEqualTo(isDisrupted)
    }
}
