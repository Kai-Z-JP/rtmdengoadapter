package jp.kaiz.rtmdengoadapter;

import org.apache.commons.lang3.BooleanUtils;
import org.lwjgl.input.Controller;

public class DPadAsButtonGamePadAdapter extends GamePadAdapter {

    @Override
    public boolean isHorn(Controller control) {
        return control.isButtonPressed(3);
    }

    @Override
    public boolean isDoorR(Controller control) {
        return control.isButtonPressed(8);
    }

    @Override
    public boolean isDoorL(Controller control) {
        return control.isButtonPressed(9);
    }

    public int getNotch(Controller control, int lastNotchLevel) {
        int brakeButton = BooleanUtils.toInteger(control.isButtonPressed(0))
                + BooleanUtils.toInteger(control.isButtonPressed(15)) * 2
                + BooleanUtils.toInteger(control.isButtonPressed(13)) * 4;

        switch (brakeButton) {
            case 6:
                return 0;
            case 5:
                return 1;
            case 4:
                return 2;
            case 3:
                return 3;
            case 2:
                return 4;
            case 1:
                return 5;
            default:
                return lastNotchLevel;
        }
    }
}
