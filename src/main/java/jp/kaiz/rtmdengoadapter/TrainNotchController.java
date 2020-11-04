package jp.kaiz.rtmdengoadapter;

import jp.kaiz.atsassistmod.api.ControlTrain;
import jp.ngt.rtm.entity.train.EntityTrainBase;
import jp.ngt.rtm.entity.train.util.TrainState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public class TrainNotchController {
    public static void setNotch(int notch) {
        Entity entity = Minecraft.getMinecraft().thePlayer.ridingEntity;
        if (entity instanceof EntityTrainBase) {
            if (((EntityTrainBase) entity).isControlCar()) {
                if (RTMDengoAdapter.ATSA_LOADED) {
                    ControlTrain.setControllerNotch(notch);
                } else {
                    ((EntityTrainBase) entity).setTrainStateData(TrainState.TrainStateType.State_Notch.id, (byte) notch);
                    ((EntityTrainBase) entity).syncTrainStateData(TrainState.TrainStateType.State_Notch.id, (byte) notch);
                }
            }
        }
    }
}
