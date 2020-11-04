package jp.kaiz.rtmdengoadapter;

import org.lwjgl.input.Controller;

public class DPadAsAxisGamePadAdapter extends GamePadAdapter {

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
        int notchAxis = (int) control.getXAxisValue();
        boolean notchButton = control.isButtonPressed(0);
        if (notchAxis == -1) {
            if (notchButton) {
                return 3;
            } else {
                if (lastNotchLevel == 3 || lastNotchLevel == 4 || lastNotchLevel == 5) {
                    return 4;
                } else {
                    return 0;
                }
            }
        } else if (notchAxis == 1) {
            return notchButton ? 1 : 2;
        } else if (notchAxis == 0) {
            return notchButton ? 5 : lastNotchLevel;
        } else {
            return lastNotchLevel;
        }
    }
}
