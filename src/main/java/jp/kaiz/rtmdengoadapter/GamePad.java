package jp.kaiz.rtmdengoadapter;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.entity.train.EntityTrainBase;
import jp.ngt.rtm.entity.train.util.TrainState;
import jp.ngt.rtm.event.RTMKeyHandlerClient;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.logging.log4j.Level;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class GamePad {
    private Controller control;
    private int lastNotchLevel, lastBrakeLevel;
    private int currentLevel = 0;
    private GamePadAdapter gamePadAdapter;
    private final List<Controller> controllers = new ArrayList<>();

    private boolean isPressHorn, isPressDoorR, isPressDoorL;

    public GamePad() throws LWJGLException {
        Controllers.create();

        for (int i = 0; i < Controllers.getControllerCount(); i++) {
            Controller controller = Controllers.getController(i);
            this.controllers.add(controller);
            String controllerName = controller.getName();
            RTMDengoAdapter.logger.log(Level.INFO, "Detected controller(" + i + "): " + controllerName);
            switch (controllerName) {
                case "ELECOM JC-PS201U series":
                    this.control = controller;
                    this.gamePadAdapter = new DPadAsButtonGamePadAdapter();
                    return;
                case "ELECOM JC-PS101U series":
                case "ELECOM JC-PS102U series":
                case "PS  Converter    ":
                    this.control = controller;
                    this.gamePadAdapter = new DPadAsAxisGamePadAdapter();
                    return;
                case "Generic   USB  Joystick  ":
                    this.control = controller;
                    this.gamePadAdapter = new SanYingGamePadAdapter();
                    return;
                case "One Handle MasCon for Nintendo Switch":
                case "One Handle MasCon for Nintendo Switch Exclusive Edition":
                    this.control = controller;
                    this.gamePadAdapter = new SwitchOneHandleAdapter();
                    return;
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (this.control == null || this.gamePadAdapter == null) {
                return;
            }

            EntityTrainBase train = this.getEntityTrainBase();
            if (train == null) {
                this.isPressHorn = false;
                this.isPressDoorR = false;
                this.isPressDoorL = false;
                return;
            }

            this.control.poll();

            int notchLevel = this.gamePadAdapter.getNotch(this.control, this.lastNotchLevel);
            if (this.lastNotchLevel != notchLevel) {
                this.lastNotchLevel = notchLevel;
            }

            int brakeLevel = this.gamePadAdapter.getBrake(this.control, this.lastBrakeLevel);
            if (this.lastBrakeLevel != brakeLevel) {
                this.lastBrakeLevel = brakeLevel;
            }

            int level = (brakeLevel < 0) ? brakeLevel : notchLevel;
            if (this.currentLevel != level) {
                this.currentLevel = level;
                TrainNotchController.setNotch(level);
            }

            if (this.gamePadAdapter.isHorn(this.control)) {
                if (!this.isPressHorn) {
                    try {
                        this.isPressHorn = true;
                        Method m = RTMKeyHandlerClient.class.getDeclaredMethod("playSound", EntityPlayer.class, byte.class);
                        m.setAccessible(true);
                        m.invoke(RTMKeyHandlerClient.INSTANCE, Minecraft.getMinecraft().thePlayer, RTMCore.KEY_Horn);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                this.isPressHorn = false;
            }

            int nowDoorStateData = train.getTrainStateData(TrainState.TrainStateType.State_Door.id);
            boolean dir = train.getTrainDirection() == 0;
            boolean r = (nowDoorStateData & 1) == 1;
            boolean l = (nowDoorStateData & 2) == 2;
            boolean stateR = dir ? l : r;
            boolean stateL = dir ? r : l;
            boolean flag = false;

            if (this.gamePadAdapter.isDoorR(this.control)) {
                if (!this.isPressDoorR) {
                    this.isPressDoorR = true;
                    stateR ^= true;
                    flag = true;
                }
            } else {
                this.isPressDoorR = false;
            }

            if (this.gamePadAdapter.isDoorL(this.control)) {
                if (!this.isPressDoorL) {
                    this.isPressDoorL = true;
                    stateL ^= true;
                    flag = true;
                }
            } else {
                this.isPressDoorL = false;
            }
            int newDoorStateData = ((stateR ? 1 : 0) << 1 | (stateL ? 1 : 0));

            if (flag) {
                train.setTrainStateData(TrainState.TrainStateType.State_Door.id, (byte) newDoorStateData);
                train.syncTrainStateData(TrainState.TrainStateType.State_Door.id, (byte) newDoorStateData);
            }
        }
    }

    private EntityTrainBase getEntityTrainBase() {
        Entity entity = Minecraft.getMinecraft().thePlayer.ridingEntity;
        if (entity instanceof EntityTrainBase) {
            return (EntityTrainBase) entity;
        }
        return null;
    }

    public boolean isControllerConnected() {
        return this.control == null || this.gamePadAdapter == null;
    }

    public int getCurrentLevel() {
        return this.currentLevel;
    }

    public int getNotchLevel() {
        return this.lastNotchLevel;
    }

    public int getBrakeLevel() {
        return this.lastBrakeLevel;
    }
}
