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
import org.apache.commons.lang3.BooleanUtils;
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
            RTMDengoAdapter.logger.log(Level.INFO, "Detected controller(" + i + "): " + controller.getName());
            switch (controllerName) {
                case "ELECOM JC-PS201U series":
                    this.control = controller;
                    this.gamePadAdapter = new DPadAsButtonGamePadAdapter();
                    return;
                case "ELECOM JC-PS101U series":
                case "PS  Converter    ":
                    this.control = controller;
                    this.gamePadAdapter = new DPadAsAxisGamePadAdapter();
                    return;
                case "Generic   USB  Joystick  ":
                    this.control = controller;
                    this.gamePadAdapter = new SanYingGamePadAdapter();
                    return;
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
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

            byte nowDoorStateData = train.getTrainStateData(TrainState.TrainStateType.State_Door.id);
            boolean r = (nowDoorStateData & 1) == 1;
            boolean l = (nowDoorStateData & 2) == 2;

            if (this.gamePadAdapter.isDoorR(this.control)) {
                if (!this.isPressDoorR) {
                    this.isPressDoorR = true;
                    r = !r;
                }
            } else {
                this.isPressDoorR = false;
            }

            if (this.gamePadAdapter.isDoorL(this.control)) {
                if (!this.isPressDoorL) {
                    this.isPressDoorL = true;
                    l = !l;
                }
            } else {
                this.isPressDoorL = false;
            }
            byte newDoorStateData = (byte) (BooleanUtils.toInteger(r) + BooleanUtils.toInteger(l) * 2);

            if (nowDoorStateData != newDoorStateData) {
                train.setTrainStateData(TrainState.TrainStateType.State_Door.id, newDoorStateData);
                train.syncTrainStateData(TrainState.TrainStateType.State_Door.id, newDoorStateData);
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
}
