package jp.kaiz.rtmdengoadapter;

import org.apache.commons.lang3.BooleanUtils;
import org.lwjgl.input.Controller;

public abstract class GamePadAdapter {

    public abstract boolean isHorn(Controller control);

    public abstract boolean isDoorR(Controller control);

    public abstract boolean isDoorL(Controller control);

    public int getBrake(Controller control, int lastBrakeLevel) {
        int brakeButton = BooleanUtils.toInteger(control.isButtonPressed(6))
                + BooleanUtils.toInteger(control.isButtonPressed(4)) * 2
                + BooleanUtils.toInteger(control.isButtonPressed(7)) * 4
                + BooleanUtils.toInteger(control.isButtonPressed(5)) * 8;

        switch (brakeButton) {
            case 15:
                return lastBrakeLevel;
            case 14:
                return 0;
            case 13:
                return -1;
            case 12:
                return -2;
            case 11:
                return -3;
            case 10:
                return -4;
            case 9:
                return -5;
            case 8:
                return -6;
            case 7:
            case 6:
                return -7;
            default:
                return -8;
        }
    }

    public abstract int getNotch(Controller control, int lastNotchLevel);
}
