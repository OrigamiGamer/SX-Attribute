package github.saukiya.sxattribute;

import github.saukiya.sxattribute.api.SXAPI;
import github.saukiya.sxattribute.bstats.Metrics;
import github.saukiya.sxattribute.command.MainCommand;
import github.saukiya.sxattribute.data.RandomStringManager;
import github.saukiya.sxattribute.data.SlotDataManager;
import github.saukiya.sxattribute.data.attribute.SXAttributeManager;
import github.saukiya.sxattribute.data.attribute.AttributeType;
import github.saukiya.sxattribute.data.attribute.sub.attack.*;
import github.saukiya.sxattribute.data.attribute.sub.defence.*;
import github.saukiya.sxattribute.data.attribute.sub.other.EventMessage;
import github.saukiya.sxattribute.data.attribute.sub.other.ExpAddition;
import github.saukiya.sxattribute.data.attribute.sub.other.JSAttribute;
import github.saukiya.sxattribute.data.attribute.sub.other.MythicMobsDrop;
import github.saukiya.sxattribute.data.attribute.sub.update.AttackSpeed;
import github.saukiya.sxattribute.data.attribute.sub.update.Command;
import github.saukiya.sxattribute.data.attribute.sub.update.WalkSpeed;
import github.saukiya.sxattribute.data.condition.SXConditionManager;
import github.saukiya.sxattribute.data.condition.sub.*;
import github.saukiya.sxattribute.data.itemdata.ItemDataManager;
import github.saukiya.sxattribute.data.itemdata.sub.ItemGeneratorImport;
import github.saukiya.sxattribute.data.itemdata.sub.ItemGeneratorSX;
import github.saukiya.sxattribute.listener.*;
import github.saukiya.sxattribute.util.*;
import jdk.internal.dynalink.beans.StaticClass;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * SX-Attribute
 *
 * @author Saukiya
 * <p>
 * 该插件只发布与MCBBS。
 */


@Getter
public class SXAttribute extends JavaPlugin {

    @Getter
    private static int[] versionSplit = new int[3];

    @Getter
    private static Random random = new Random();

    private static SXAttribute inst;

    private static SXAPI api = new SXAPI();

    @Getter
    @Setter
    private static DecimalFormat df = new DecimalFormat("#.##");

    @Getter
    private static boolean tabooLib = false;
    @Getter
    private static boolean placeholder = false;
    @Getter
    private static boolean holographic = false;
    @Getter
    private static boolean vault = false;
    @Getter
    private static boolean rpgInventory = false;
    @Getter
    private static boolean mythicMobs = false;

    private NbtUtil nbtUtil;

    private MainCommand mainCommand;

    private SXAttributeManager attributeManager;

    private SXConditionManager conditionManager;

    private RandomStringManager randomStringManager;

    private ItemDataManager itemDataManager;

    private SlotDataManager slotDataManager;

    private OnUpdateStatsListener onUpdateStatsListener;

    private OnDamageListener onDamageListener;

    private OnHealthChangeListener onHealthChangeListener;

    @Override
    public void onLoad() {
        super.onLoad();
        inst = this;
        String version = Bukkit.getBukkitVersion().split("-")[0].replace(" ", "");
        String[] strSplit = version.split("[.]");
        IntStream.range(0, strSplit.length).forEach(i -> versionSplit[i] = Integer.valueOf(strSplit[i]));
        SXAttribute.getInst().getLogger().info("ServerVersion: " + version);
        try {
            Config.loadConfig();
            Message.loadMessage();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            SXAttribute.getInst().getLogger().warning("IO Error!");
        }
        mainCommand = new MainCommand(this);

        new Crit(this).registerAttribute();
        new Damage(this).registerAttribute();
        new HitRate(this).registerAttribute();
        new Ignition(this).registerAttribute();
        new LifeSteal(this).registerAttribute();
        new Lightning(this).registerAttribute();
        new AttackPotion(this).registerAttribute();
        new Real(this).registerAttribute();
        new Tearing(this).registerAttribute();

        new Block(this).registerAttribute();
        new Defense(this).registerAttribute();
        new Dodge(this).registerAttribute();
        new Reflection(this).registerAttribute();
        new Toughness(this).registerAttribute();

        new EventMessage(this).registerAttribute();
        new ExpAddition(this).registerAttribute();
        if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
            new MythicMobsDrop(this).registerAttribute();
        }
        new HealthRegen(this).registerAttribute();

        new Health(this).registerAttribute();
        new WalkSpeed(this).registerAttribute();
        if (SXAttribute.getVersionSplit()[1] > 8) {
            new AttackSpeed(this).registerAttribute();
        }
        new Command(this).registerAttribute();

        File jsAttributeFiles = new File(getDataFolder(), "Attribute" + File.separator + "JavaScript");
        if (!jsAttributeFiles.exists() && SXAttribute.getVersionSplit()[1] > 8) {
            saveResource("Attribute/JavaScript/JSAttribute.js", true);
            saveResource("Attribute/SX-Attribute/JSAttribute_JS.yml", true);
        }
        if (jsAttributeFiles.exists() && jsAttributeFiles.isDirectory()) {
            ScriptEngineManager jsManager = new ScriptEngineManager();
            StaticClass arrays = StaticClass.forClass(Arrays.class);
            StaticClass sxAttributeType = StaticClass.forClass(AttributeType.class);
            StaticClass sxAttribute = StaticClass.forClass(SXAttribute.class);
            StaticClass bukkit = StaticClass.forClass(Bukkit.class);
            for (File jsFile : jsAttributeFiles.listFiles()) {
                if (jsFile.getName().endsWith(".js")) {
                    ScriptEngine engine = jsManager.getEngineByName("JavaScript");
                    engine.put("Arrays", arrays);
                    engine.put("SXAttributeType", sxAttributeType);
                    engine.put("SXAttribute", sxAttribute);
                    engine.put("Bukkit", bukkit);
                    engine.put("API", api);
                    try {
                        engine.eval(new InputStreamReader(new FileInputStream(jsFile), StandardCharsets.UTF_8));
                        JSAttribute jsAttribute = new JSAttribute(jsFile.getName().replace(".js", ""), this, engine);
                        jsAttribute.registerAttribute();
                    } catch (ScriptException | FileNotFoundException e) {
                        SXAttribute.getInst().getLogger().info("==========================================================================================");
                        e.printStackTrace();
                        SXAttribute.getInst().getLogger().warning("Error JavaScript: " + jsFile.getName());
                        SXAttribute.getInst().getLogger().info("==========================================================================================");
                    }
                }
            }
        }

        if (SXAttribute.getVersionSplit()[1] > 8) {
            new MainHand(this).registerCondition();
            new OffHand(this).registerCondition();
        }
        new Hand(this).registerCondition();
        new LimitLevel(this).registerCondition();
        new Role(this).registerCondition();
        new ExpiryTime(this).registerCondition();
        new Durability(this).registerCondition();

        ItemDataManager.registerGenerator(new ItemGeneratorImport(this));
        ItemDataManager.registerGenerator(new ItemGeneratorSX(this));
    }

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().isPluginEnabled("TabooLib")) {
            tabooLib = true;
        } else {
            SXAttribute.getInst().getLogger().warning("No Find TabooLib!");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            placeholder = true;
            new Placeholders();
        } else {
            SXAttribute.getInst().getLogger().warning("No Find PlaceholderAPI!");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            try {
                MoneyUtil.setup();
                vault = true;
            } catch (NullPointerException e) {
                SXAttribute.getInst().getLogger().warning("No Find Vault-Economy!");
            }
        } else {
            SXAttribute.getInst().getLogger().warning("No Find Vault!");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            holographic = true;
        } else {
            SXAttribute.getInst().getLogger().warning("No Find HolographicDisplays!");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
            mythicMobs = true;
            Bukkit.getPluginManager().registerEvents(new OnMythicmobsSpawnListener(this), this);
        } else {
            SXAttribute.getInst().getLogger().warning("No Find MythicMobs!");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("RPGInventory")) {
            rpgInventory = true;
        } else {
            SXAttribute.getInst().getLogger().warning("No Find RPGInventory!");
        }

        new Metrics(this);
        try {
            nbtUtil = new NbtUtil();
            randomStringManager = new RandomStringManager(this);
            itemDataManager = new ItemDataManager(this);
        } catch (IOException e) {
            e.printStackTrace();
            SXAttribute.getInst().getLogger().warning("IO Error!");
            this.setEnabled(false);
            return;
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
            SXAttribute.getInst().getLogger().warning("Reflection Error!");
            this.setEnabled(false);
            return;
        }

        attributeManager = new SXAttributeManager();
        conditionManager = new SXConditionManager(this);
        slotDataManager = new SlotDataManager();
        onUpdateStatsListener = new OnUpdateStatsListener(this);
        onDamageListener = new OnDamageListener(this);
        onHealthChangeListener = new OnHealthChangeListener(this);

        if (!Config.getConfig().getString(Config.DAMAGE_EVENT_PRIORITY, "HIGH").equals("HIGH")) {
            for (Method method : OnDamageListener.class.getDeclaredMethods()) {
                if (method.getName().equals("onEntityDamageByEntityEvent")) {
                    try {
                        EventPriority priority = EventPriority.valueOf(Config.getConfig().getString(Config.DAMAGE_EVENT_PRIORITY));
                        EventHandler eventHandler = method.getAnnotation(EventHandler.class);
                        InvocationHandler invHandler = Proxy.getInvocationHandler(eventHandler);
                        Field field = invHandler.getClass().getDeclaredField("memberValues");
                        field.setAccessible(true);
                        Map<String, Object> memberValues = (Map<String, Object>) field.get(invHandler);
                        memberValues.put("priority", EventPriority.LOW);
                        SXAttribute.getInst().getLogger().info("EditDamageEventPriority: " + priority.name());

                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        SXAttribute.getInst().getLogger().warning("EditDamageEventPriority ERROR!");
                        e.printStackTrace();
                        this.setEnabled(false);
                        return;
                    }
                    break;
                }
            }

        }

        Bukkit.getPluginManager().registerEvents(new OnBanShieldInteractListener(), this);
        Bukkit.getPluginManager().registerEvents(onUpdateStatsListener, this);
        Bukkit.getPluginManager().registerEvents(onDamageListener, this);
        Bukkit.getPluginManager().registerEvents(onHealthChangeListener, this);
        Bukkit.getPluginManager().registerEvents(new OnItemSpawnListener(), this);
        mainCommand.setup("sxAttribute");
        SXAttribute.getInst().getLogger().info("Author: Saukiya");
        if (Config.getConfig().getBoolean(Config.QAQ)) {
            Bukkit.getConsoleSender().sendMessage("§c");
            Bukkit.getConsoleSender().sendMessage("§c   ______  __             ___   __  __       _ __          __");
            Bukkit.getConsoleSender().sendMessage("§c  / ___/ |/ /            /   | / /_/ /______(_) /_  __  __/ /____");
            Bukkit.getConsoleSender().sendMessage("§c  \\__ \\|   /   ______   / /| |/ __/ __/ ___/ / __ \\/ / / / __/ _ \\");
            Bukkit.getConsoleSender().sendMessage("§c ___/ /   |   /_____/  / ___ / /_/ /_/ /  / / /_/ / /_/ / /_/  __/");
            Bukkit.getConsoleSender().sendMessage("§c/____/_/|_|           /_/  |_\\__/\\__/_/  /_/_.___/\\__,_/\\__/\\___/");
            Bukkit.getConsoleSender().sendMessage("§c");
        }
    }

    @Override
    public void onDisable() {
        attributeManager.onAttributeDisable();
        conditionManager.onConditionDisable();
        onHealthChangeListener.cancel();
    }

    public static SXAttribute getInst() {
        return inst;
    }

    public static SXAPI getAPI() {
        return api;
    }
}