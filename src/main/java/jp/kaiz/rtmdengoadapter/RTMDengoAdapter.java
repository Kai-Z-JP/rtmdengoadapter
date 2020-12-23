package jp.kaiz.rtmdengoadapter;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;

@Mod(modid = RTMDengoAdapter.MODID, name = RTMDengoAdapter.NAME, version = RTMDengoAdapter.VERSION)
public class RTMDengoAdapter {
    public static final String MODID = "rtmdengoadapter";
    public static final String NAME = "RTM Dengo Adapter";
    public static final String VERSION = "1.7.10_0.3";

    public static boolean ATSA_LOADED;

    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        try {
            FMLCommonHandler.instance().bus().register(new GamePad());
            FMLCommonHandler.instance().bus().register(this);
        } catch (LWJGLException e) {
            logger.log(Level.WARN, "Failed to initialize Controllers");
        }
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ATSA_LOADED = Loader.isModLoaded("ATSAssistMod");
    }
}
