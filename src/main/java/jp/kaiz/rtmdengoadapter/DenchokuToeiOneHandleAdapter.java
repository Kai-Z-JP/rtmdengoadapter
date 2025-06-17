package jp.kaiz.rtmdengoadapter;

import org.lwjgl.input.Controller;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DenchokuToeiOneHandleAdapter extends GamePadAdapter {
    @Override
    public boolean isHorn(Controller control) {
        return false;
    }

    @Override
    public boolean isDoorR(Controller control) {
        return false;
    }

    @Override
    public boolean isDoorL(Controller control) {
        return false;
    }

    @Override
    public int getNotch(Controller control, int lastNotchLevel) {
        float notchAxis = control.getXAxisValue();
        int notch = new BigDecimal((notchAxis + 1) / 2 * -12).setScale(0, RoundingMode.HALF_UP).intValue() + 4;
        return (notch > 0) ? (notch == 4) ? 5 : notch : 0;
    }

    @Override
    public int getBrake(Controller control, int lastBrakeLevel) {
        if(!control.isButtonPressed(0)) return -8;
        float notchAxis = control.getXAxisValue();
        int notch = new BigDecimal((notchAxis + 1) / 2 * -12).setScale(0, RoundingMode.HALF_UP).intValue() + 4;
        return (notch < 0) ? notch : 0;
    }
}
