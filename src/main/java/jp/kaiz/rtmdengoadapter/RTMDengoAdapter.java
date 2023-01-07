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
    public static final String VERSION = "1.7.10_0.5";

    public static boolean ATSA_LOADED;

    public static Logger logger;

    @Mod.Instance(MODID)
    public static RTMDengoAdapter INSTANCE;

    private GamePad gamePad;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        try {
            this.gamePad = new GamePad();
            FMLCommonHandler.instance().bus().register(this.gamePad);
        } catch (LWJGLException e) {
            logger.log(Level.WARN, "Failed to initialize Controllers");
        }
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ATSA_LOADED = Loader.isModLoaded("ATSAssistMod");
    }

    /**
     * 対応コントローラーが接続されているかどうかを返却します。
     * ノッチ・ブレーキレベルを読み取る前に呼び出し、コントローラーが接続されているかを確認してください。
     *
     * @return コントローラーが接続されているか
     */
    public static boolean isControllerConnected() {
        return INSTANCE.gamePad != null && INSTANCE.gamePad.isControllerConnected();
    }

    /**
     * ノッチ及びブレーキを統合したレベルを返却します。
     * ワンハンドルマスコンを搭載している車両向けの値です。
     * 実際に接続されているコントローラーがツーハンドルタイプの場合も機能します。
     * このメソッドの値を使用する前に、{@link RTMDengoAdapter#isControllerConnected} でコントローラーが接続されていることを確認してください。
     * コントローラーが接続されていない場合、0を返却します。
     *
     * @return -8から5の範囲のノッチ・ブレーキを統合したレベル
     */
    public static int getCurrentLevel() {
        return INSTANCE.gamePad != null ? INSTANCE.gamePad.getCurrentLevel() : 0;
    }

    /**
     * ノッチ単体のレベルを返却します。
     * ツーハンドルマスコンを搭載している車両向けの値です。
     * 実際に接続されているコントローラーがワンハンドルタイプの場合も機能します。
     * このメソッドの値を使用する前に、{@link RTMDengoAdapter#isControllerConnected} でコントローラーが接続されていることを確認してください。
     * コントローラーが接続されていない場合、0を返却します。
     *
     * @return 0から5の範囲のノッチ単体のレベル
     */
    public static int getNotchLevel() {
        return INSTANCE.gamePad != null ? INSTANCE.gamePad.getNotchLevel() : 0;
    }

    /**
     * ブレーキ単体のレベルを返却します。
     * ツーハンドルマスコンを搭載している車両向けの値です。
     * 実際に接続されているコントローラーがワンハンドルタイプの場合も機能します。
     * このメソッドの値を使用する前に、{@link RTMDengoAdapter#isControllerConnected} でコントローラーが接続されていることを確認してください。
     * コントローラーが接続されていない場合、0を返却します。
     *
     * @return -8から0の範囲のブレーキ単体のレベル
     */
    public static int getBrakeLevel() {
        return INSTANCE.gamePad != null ? INSTANCE.gamePad.getBrakeLevel() : 0;
    }
}
