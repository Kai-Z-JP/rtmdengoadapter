package jp.kaiz.rtmdengoadapter;

import org.apache.commons.lang3.BooleanUtils;
import org.lwjgl.input.Controller;

public class SanYingGamePadAdapter extends GamePadAdapter {

    @Override
    public boolean isHorn(Controller control) {
        return control.isButtonPressed(0);
    }

    @Override
    public boolean isDoorR(Controller control) {
        return control.isButtonPressed(1);
    }

    @Override
    public boolean isDoorL(Controller control) {
        return control.isButtonPressed(2);
    }

    public int getNotch(Controller control, int lastNotchLevel) {
        int notchButton = BooleanUtils.toInteger(control.isButtonPressed(9))
                + BooleanUtils.toInteger(control.isButtonPressed(8)) * 2
                + BooleanUtils.toInteger(control.isButtonPressed(7)) * 4
                + 8;

        switch (notchButton) {
            case 10:
                return 0;
            case 11:
                return 1;
            case 12:
                return 2;
            case 13:
                return 3;
            case 14:
                return 4;
            case 15:
                return 5;
            default:
                return lastNotchLevel;
        }
    }

    public int getBrake(Controller control, int lastBrakeLevel) {
        int brakeButton = BooleanUtils.toInteger(control.isButtonPressed(9))
                + BooleanUtils.toInteger(control.isButtonPressed(8)) * 2
                + BooleanUtils.toInteger(control.isButtonPressed(7)) * 4
                + BooleanUtils.toInteger(control.isButtonPressed(6)) * 8;
        switch (brakeButton) {
            case 0:
                return lastBrakeLevel;
            case 1:
                return -8;
            case 2:
            case 3:
                return -7;
            case 4:
                return -6;
            case 5:
                return -5;
            case 6:
                return -4;
            case 7:
                return -3;
            case 8:
                return -2;
            case 9:
                return -1;
            default:
                return 0;
        }
    }

}
