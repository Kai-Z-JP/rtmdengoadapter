package jp.kaiz.rtmdengoadapter;

import org.lwjgl.input.Controller;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SwitchOneHandleAdapter extends GamePadAdapter {
    @Override
    public boolean isHorn(Controller control) {return control.isButtonPressed(2);}

    @Override
    public boolean isDoorR(Controller control) {return control.isButtonPressed(5);}

    @Override
    public boolean isDoorL(Controller control) {return control.isButtonPressed(4);}

    @Override
    public int getNotch(Controller control, int lastNotchLevel) {
        float notchAxis = control.getYAxisValue();
        return (notchAxis > 0) ? Math.round(notchAxis * 5) : 0;
    }

    @Override
    public int getBrake(Controller control, int lastBrakeLevel) {
        float notchAxis = control.getYAxisValue();
        int notch = new BigDecimal(notchAxis * 10 + 1).setScale(0, RoundingMode.DOWN).intValue();
        return (notchAxis < 0) ? (notch > -7) ? notch : (notch > -9) ? -7 : -8 : 0;
    }
}
