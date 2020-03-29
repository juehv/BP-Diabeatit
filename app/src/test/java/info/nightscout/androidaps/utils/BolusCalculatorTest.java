package info.nightscout.androidaps.utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import info.AAPSMocker;
import info.nightscout.androidaps.MainApp;
import info.nightscout.androidaps.data.ConstraintChecker;
import info.nightscout.androidaps.data.IobTotal;
import info.nightscout.androidaps.data.Profile;
import info.nightscout.androidaps.interfaces.Constraint;
import info.nightscout.androidaps.interfaces.PumpInterface;
import info.nightscout.androidaps.plugins.configBuilder.ConfigBuilderPlugin;
import info.nightscout.androidaps.diabeatit.bolus.BolusCalculator;
import info.nightscout.androidaps.diabeatit.bolus.BolusCalculatorBuilder;
import info.nightscout.androidaps.plugins.iob.iobCobCalculator.GlucoseStatus;
import info.nightscout.androidaps.plugins.pump.mdi.MDIPlugin;
import info.nightscout.androidaps.plugins.treatments.TreatmentsPlugin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MainApp.class, GlucoseStatus.class, ConfigBuilderPlugin.class, TreatmentsPlugin.class, ConstraintChecker.class})
public class BolusCalculatorTest {
    private static final double PUMP_BOLUS_STEP = 0.1;

    private Profile setupProfile(Double targetLow, Double targetHigh, Double insulinSensitivityFactor, Double insulinToCarbRatio) {
        Profile profile = mock(Profile.class);
        when(profile.getTargetLow()).thenReturn(targetLow);
        when(profile.getTargetHigh()).thenReturn(targetHigh);
        when(profile.getIsf()).thenReturn(insulinSensitivityFactor);
        when(profile.getIc()).thenReturn(insulinToCarbRatio);

        PowerMockito.mockStatic(GlucoseStatus.class);
        when(GlucoseStatus.getGlucoseStatusData()).thenReturn(null);

        PowerMockito.mockStatic(TreatmentsPlugin.class);
        TreatmentsPlugin treatment = mock(TreatmentsPlugin.class);
        IobTotal iobTotalZero = new IobTotal(System.currentTimeMillis());
        when(treatment.getLastCalculationTreatments()).thenReturn(iobTotalZero);
        when(treatment.getLastCalculationTempBasals()).thenReturn(iobTotalZero);
        PowerMockito.mockStatic(MainApp.class);
        when(TreatmentsPlugin.getPlugin()).thenReturn(treatment);

        AAPSMocker.mockConfigBuilder();
        PumpInterface pump = MDIPlugin.getPlugin();
        pump.getPumpDescription().bolusStep = PUMP_BOLUS_STEP;
        when(ConfigBuilderPlugin.getPlugin().getActivePump()).thenReturn(pump);

        AAPSMocker.mockConstraintsChecker();
        Mockito.doAnswer(invocation -> {
            Constraint<Double> constraint = invocation.getArgument(0);
            return constraint;
        }).when(AAPSMocker.constraintChecker).applyBolusConstraints(any(Constraint.class));

        return profile;
    }

    @Test
    /** Should calculate the same bolus when different blood glucose but both in target range */
    public void shouldCalculateTheSameBolusWhenBGsInRange() throws Exception {
        Profile profile = setupProfile(4d, 8d, 20d, 12d);

        BolusCalculatorBuilder builder = new BolusCalculatorBuilder();
        builder.setProfile(profile);
        builder.setCarbs(20);
        builder.setCOB(0.0);
        builder.setBG(4.2);
        builder.setCorrection(0d);
        builder.setUseBg(true);
        builder.setUseCob(true);
        builder.setIncludeBolusIOB(true);
        builder.setIncludeBasalIOB(true);
        builder.setUseTrend(false);
        BolusCalculator cal = builder.build();

        Double bolusFor42 = cal.getCalculatedTotalInsulin();
        cal.setBG(5.4);
        Double bolusFor54 = cal.getCalculatedTotalInsulin();

        Assert.assertEquals(bolusFor42, bolusFor54);
    }

    @Test
    public void shouldCalculateHigherBolusWhenHighBG() throws Exception {
        Profile profile = setupProfile(4d, 8d, 20d, 12d);

        BolusCalculatorBuilder builder = new BolusCalculatorBuilder();
        builder.setProfile(profile);
        builder.setCarbs(20);
        builder.setCOB(0.0);
        builder.setBG(9.8);
        builder.setCorrection(0d);
        builder.setUseBg(true);
        builder.setUseCob(true);
        builder.setIncludeBolusIOB(true);
        builder.setIncludeBasalIOB(true);
        builder.setUseTrend(false);
        BolusCalculator cal = builder.build();

        Double highBolus = cal.getCalculatedTotalInsulin();

        cal.setBG(5.4);
        Double bolusInRange = cal.getCalculatedTotalInsulin();

        Assert.assertTrue(highBolus > bolusInRange);
    }

    @Test
    public void shouldCalculateLowerBolusWhenLowBG() throws Exception {
        Profile profile = setupProfile(4d, 8d, 20d, 12d);

        BolusCalculatorBuilder builder = new BolusCalculatorBuilder();
        builder.setProfile(profile);
        builder.setCarbs(20);
        builder.setCOB(0.0);
        builder.setBG(3.6);
        builder.setCorrection(0d);
        builder.setUseBg(true);
        builder.setUseCob(true);
        builder.setIncludeBolusIOB(true);
        builder.setIncludeBasalIOB(true);
        builder.setUseTrend(false);
        BolusCalculator cal = builder.build();

        Double lowBolus = cal.getCalculatedTotalInsulin();

        cal.setBG(5.4);
        Double inRangeBolus = cal.getCalculatedTotalInsulin();

        Assert.assertTrue(lowBolus < inRangeBolus);
    }
}
