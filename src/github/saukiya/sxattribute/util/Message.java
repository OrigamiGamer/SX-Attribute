package github.saukiya.sxattribute.util;

import github.saukiya.sxattribute.SXAttribute;
import github.saukiya.sxattribute.data.attribute.SubAttribute;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public enum Message {

    MESSAGE_VERSION,
    PLAYER__NO_REGISTER_SLOTS,
    PLAYER__NO_LEVEL_USE,
    PLAYER__NO_ROLE,
    PLAYER__NO_USE_SLOT,
    PLAYER__OVERDUE_ITEM,
    PLAYER__EXP_ADDITION,
    PLAYER__NO_VAULT,
    PLAYER__NO_DURABILITY,
    PLAYER__SELL,
    PLAYER__BATTLE__FIRST_PERSON,
    PLAYER__HOLOGRAPHIC__HURT,
    PLAYER__HOLOGRAPHIC__HEALTH,
    INVENTORY__STATS__NAME,
    INVENTORY__STATS__HIDE_ON,
    INVENTORY__STATS__HIDE_OFF,
    INVENTORY__STATS__SKULL_NAME,
    INVENTORY__STATS__SKULL_LORE,
    INVENTORY__STATS__ATTACK,
    INVENTORY__STATS__ATTACK_LORE,
    INVENTORY__STATS__DEFENSE,
    INVENTORY__STATS__DEFENSE_LORE,
    INVENTORY__STATS__BASE,
    INVENTORY__STATS__BASE_LORE,
    INVENTORY__SELL__NAME,
    INVENTORY__SELL__SELL,
    INVENTORY__SELL__ENTER,
    INVENTORY__SELL__OUT,
    INVENTORY__SELL__NO_SELL,
    INVENTORY__SELL__LORE__DEFAULT,
    INVENTORY__SELL__LORE__FORMAT,
    INVENTORY__SELL__LORE__NO_SELL,
    INVENTORY__SELL__LORE__ALL_SELL,
    INVENTORY__REPAIR__NAME,
    INVENTORY__REPAIR__GUIDE,
    INVENTORY__REPAIR__ENTER,
    INVENTORY__REPAIR__MONEY,
    INVENTORY__REPAIR__NO_MONEY,
    INVENTORY__REPAIR__UNSUITED,
    INVENTORY__REPAIR__REPAIR,
    INVENTORY__REPAIR__LORE__ENTER,
    INVENTORY__REPAIR__LORE__MONEY,
    INVENTORY__DISPLAY_SLOTS_NAME,
    ADMIN__CLEAR_ENTITY_DATA,
    ADMIN__NO_ITEM,
    ADMIN__GIVE_ITEM,
    ADMIN__HAS_ITEM,
    ADMIN__SAVE_ITEM,
    ADMIN__SAVE_NO_TYPE,
    ADMIN__SAVE_ITEM_ERROR,
    ADMIN__NO_PERMISSION_CMD,
    ADMIN__NO_CMD,
    ADMIN__NO_FORMAT,
    ADMIN__NO_CONSOLE,
    ADMIN__PLUGIN_RELOAD,
    ADMIN__NO_ONLINE,
    COMMAND__STATS,
    COMMAND__SELL,
    COMMAND__REPAIR,
    COMMAND__GIVE,
    COMMAND__SAVE,
    COMMAND__NBT,
    COMMAND__ATTRIBUTELIST,
    COMMAND__CONDITIONLIST,
    COMMAND__RELOAD,
    REPLACE_LIST;


    private static File FILE = new File(SXAttribute.getInst().getDataFolder(), "Message.yml");

    @Getter
    private static YamlConfiguration messages;

    private static Tool tool = new Tool();

    private static void createDefaultMessage() {
        messages.set(MESSAGE_VERSION.toString(), SXAttribute.getInst().getDescription().getVersion());

        messages.set(PLAYER__NO_REGISTER_SLOTS.toString(), "&c服务器没有开启额外的槽位识别");
        messages.set(PLAYER__NO_LEVEL_USE.toString(), "&c你没有达到使用 &a{0} &c的等级要求!");
        messages.set(PLAYER__NO_ROLE.toString(), "&c你没有达到使用 &a{0} &c的职业要求!");
        messages.set(PLAYER__NO_USE_SLOT.toString(), "&7物品 &a{0} &7属于 &a{1}&7 类型!");
        messages.set(PLAYER__OVERDUE_ITEM.toString(), "&c物品 &a{0}&c 已经过期了:&a{1}");
        messages.set(PLAYER__EXP_ADDITION.toString(), "&7你的经验增加了 &6{0}&7! [&a+{1}%&7]");
        messages.set(PLAYER__NO_VAULT.toString(), "&c服务器没有启用经济系统: Vault-Economy null");
        messages.set(PLAYER__NO_DURABILITY.toString(), "&c物品 &a{0}&c 耐久度已经为零了!");
        messages.set(PLAYER__SELL.toString(), "&7出售成功! 一共出售了 &6{0}&7 个物品，总价 &6{1}&7 金币!");

        messages.set(INVENTORY__STATS__NAME.toString(), "&d&l&oSX-Attribute");
        messages.set(INVENTORY__STATS__HIDE_ON.toString(), "&a点击显示更多属性");
        messages.set(INVENTORY__STATS__HIDE_OFF.toString(), "&c点击隐藏更多属性");
        messages.set(INVENTORY__STATS__SKULL_NAME.toString(), "&6&l&o{0} 的属性");
        messages.set(INVENTORY__STATS__SKULL_LORE.toString(), Collections.singletonList("&d战斗力:&b %sx_CombatPower%"));
        messages.set(INVENTORY__STATS__ATTACK.toString(), "&a&l&o攻击属性");
        messages.set(INVENTORY__STATS__ATTACK_LORE.toString(), Arrays.asList(
                "&c攻击力:&b %sx_Damage%",
                "&cPVP攻击力:&b %sx_PvpDamage%",
                "&cPVE攻击力:&b %sx_PveDamage%",
                "&a命中几率:&b %sx_HitRate%%",
                "&6破甲几率:&b %sx_Real%%",
                "&c暴击几率:&b %sx_CritRate%%",
                "&4暴伤增幅:&b %sx_Crit%%",
                "&6吸血几率:&b %sx_LifeStealRate%%",
                "&6吸血倍率:&b %sx_LifeSteal%%",
                "&c点燃几率:&b %sx_Ignition%%",
                "&9凋零几率:&b %sx_Wither%%",
                "&d中毒几率:&b %sx_Poison%%",
                "&7致盲几率:&b %sx_Blindness%%",
                "&3减速几率:&b %sx_Slow%%",
                "&3夺食几率:&b %sx_Hunger1%%",
                "&e雷霆几率:&b %sx_Lightning%%",
                "&c撕裂几率:&b %sx_Tearing%%"
        ));
        messages.set(INVENTORY__STATS__DEFENSE.toString(), "&9&l&o防御属性");
        messages.set(INVENTORY__STATS__DEFENSE_LORE.toString(), Arrays.asList(
                "&6防御力:&b %sx_Defense%",
                "&6PVP防御力:&b %sx_PvpDefense%",
                "&6PVE防御力:&b %sx_PveDefense%",
                "&a生命上限:&b %sx_Health%/%sx_MaxHealth%",
                "&a生命恢复:&b %sx_HealthRegen%",
                "&d闪避几率:&b %sx_Dodge%%",
                "&9韧性:&b %sx_Toughness%%",
                "&c反射几率:&b %sx_ReflectionRate%%",
                "&c反射比例:&b %sx_Reflection%%",
                "&2格挡几率:&b %sx_BlockRate%%",
                "&2格挡比例:&b %sx_Block%%"
        ));
        messages.set(INVENTORY__STATS__BASE.toString(), "&9&l&o其他属性");
        messages.set(INVENTORY__STATS__BASE_LORE.toString(), Arrays.asList(
                "&e经验增幅:&b %sx_ExpAddition%%",
                "&b移速增幅:&b %sx_WalkSpeed%%",
                SXAttribute.getVersionSplit()[1] > 8 ? "&b攻速增幅:&b %sx_AttackSpeed%%" : ""
        ));
        messages.set(INVENTORY__SELL__NAME.toString(), "&6&l出售物品");
        messages.set(INVENTORY__SELL__SELL.toString(), "&e&l点击出售");
        messages.set(INVENTORY__SELL__ENTER.toString(), "&c&l确认出售");
        messages.set(INVENTORY__SELL__OUT.toString(), "&6出售完毕:&e {0} 金币");
        messages.set(INVENTORY__SELL__NO_SELL.toString(), "&c&l不可出售");
        messages.set(INVENTORY__SELL__LORE__DEFAULT.toString(), Collections.singletonList("&7&o请放入你要出售的物品"));
        messages.set(INVENTORY__SELL__LORE__FORMAT.toString(), "&b[{0}] &a{1}&7 - &7{2}&e 金币");
        messages.set(INVENTORY__SELL__LORE__NO_SELL.toString(), "&b[{0}] &4不可出售");
        messages.set(INVENTORY__SELL__LORE__ALL_SELL.toString(), "&e总金额: {0}");

        messages.set(INVENTORY__REPAIR__NAME.toString(), "&9&l修理物品");
        messages.set(INVENTORY__REPAIR__GUIDE.toString(), "&7&o待修理物品放入凹槽");
        messages.set(INVENTORY__REPAIR__ENTER.toString(), "&e&l点击修理");
        messages.set(INVENTORY__REPAIR__MONEY.toString(), "&c&l确认修理");
        messages.set(INVENTORY__REPAIR__NO_MONEY.toString(), "&c&l金额不足");
        messages.set(INVENTORY__REPAIR__UNSUITED.toString(), "&4&l不可修理");
        messages.set(INVENTORY__REPAIR__REPAIR.toString(), "&6修理成功:&e {0} 金币");
        messages.set(INVENTORY__REPAIR__LORE__ENTER.toString(), Collections.singletonList("&7&o价格: {0}/破损值"));
        messages.set(INVENTORY__REPAIR__LORE__MONEY.toString(), Arrays.asList("&c破损值: {0} 耐久", "&e价格: {1} 金币", "&7&o价格: {2}/破损值"));

        messages.set(INVENTORY__DISPLAY_SLOTS_NAME.toString(), "&9&l槽位展示");

        messages.set(PLAYER__BATTLE__FIRST_PERSON.toString(), "你");

        messages.set(PLAYER__HOLOGRAPHIC__HURT.toString(), "&c&o- {0}");
        messages.set(PLAYER__HOLOGRAPHIC__HEALTH.toString(), "&e&o+ {0}");

        messages.set(ADMIN__CLEAR_ENTITY_DATA.toString(), "&c清理了 &6{0}&c 个多余的生物属性数据!");
        messages.set(ADMIN__NO_ITEM.toString(), "&c物品不存在!");
        messages.set(ADMIN__GIVE_ITEM.toString(), "&c给予 &6{0} &a{1}&c个 &6{2}&c 物品!");
        messages.set(ADMIN__HAS_ITEM.toString(), "&c已经存在名字为&6 {0}&c 的物品!");
        messages.set(ADMIN__SAVE_ITEM.toString(), "&a物品编号 &6{0} &a成功保存");
        messages.set(ADMIN__SAVE_NO_TYPE.toString(), "&c物品编号 &4{0} &c保存失败, 请检查该生成器是否存在, 并支持保存物品");
        messages.set(ADMIN__SAVE_ITEM_ERROR.toString(), "&c物品 &4{0} &c保存出现不可预知的错误");
        messages.set(ADMIN__NO_PERMISSION_CMD.toString(), "&c你没有权限执行此指令");
        messages.set(ADMIN__NO_CMD.toString(), "&c未找到此子指令:{0}");
        messages.set(ADMIN__NO_FORMAT.toString(), "&c格式错误!");
        messages.set(ADMIN__NO_ONLINE.toString(), "&c玩家不在线或玩家不存在!");
        messages.set(ADMIN__NO_CONSOLE.toString(), "&c控制台不允许执行此指令!");
        messages.set(ADMIN__PLUGIN_RELOAD.toString(), "§c插件已重载");

        messages.set(COMMAND__STATS.toString(), "查看属性");
        messages.set(COMMAND__SELL.toString(), "打开出售界面");
        messages.set(COMMAND__REPAIR.toString(), "打开修理界面");
        messages.set(COMMAND__GIVE.toString(), "给予玩家RPG物品");
        messages.set(COMMAND__SAVE.toString(), "保存当前的物品到配置文件 [Type] - 生成器类型");
        messages.set(COMMAND__NBT.toString(), "查看当前手持物品的NBT数据");
        messages.set(COMMAND__ATTRIBUTELIST.toString(), "查看当前属性列表");
        messages.set(COMMAND__CONDITIONLIST.toString(), "查看当前条件列表");
        messages.set(COMMAND__RELOAD.toString(), "重新加载这个插件的配置");

        messages.set(REPLACE_LIST.toString() + ".Pig", "猪猪");
        messages.set(REPLACE_LIST.toString() + ".Sheep", "羊羊");
        messages.set(REPLACE_LIST.toString() + ".Rabbit", "兔兔");
        messages.set(REPLACE_LIST.toString() + ".Mule", "骡骡");
        messages.set(REPLACE_LIST.toString() + ".Skeleton", "骷髅");
        messages.set(REPLACE_LIST.toString() + ".Zombie", "僵尸");
        messages.set(REPLACE_LIST.toString() + ".Horse", "马马");
        messages.set(REPLACE_LIST.toString() + ".Cow", "牛牛");
        messages.set(REPLACE_LIST.toString() + ".Chicken", "鸡鸡");
        messages.set(REPLACE_LIST.toString() + ".Polar Bear", "熊熊");
    }

    /**
     * 检查版本更新
     *
     * @return boolean
     * @throws IOException IOException
     */
    private static boolean detectionVersion() throws IOException {
        if (!messages.getString(Message.MESSAGE_VERSION.toString(), "").equals(SXAttribute.getInst().getDescription().getVersion())) {
            messages.save(new File(FILE.toString().replace(".yml", "_" + messages.getString(Message.MESSAGE_VERSION.toString()) + ".yml")));
            messages = new YamlConfiguration();
            createDefaultMessage();
            return true;
        }
        return false;
    }

    /**
     * 加载Message类
     *
     * @throws IOException                   IOException
     * @throws InvalidConfigurationException InvalidConfigurationException
     */
    public static void loadMessage() throws IOException, InvalidConfigurationException {
        messages = new YamlConfiguration();
        if (!FILE.exists()) {
            SXAttribute.getInst().getLogger().info("Create Message.yml");
            createDefaultMessage();
            messages.save(FILE);
        } else {
            messages.load(FILE);
            if (detectionVersion()) {
                SXAttribute.getInst().getLogger().info("Update Message.yml");
                messages.save(FILE);
            } else {
                SXAttribute.getInst().getLogger().info("Find Message.yml");
            }
        }
        tool.setConfig(messages);
        SubAttribute.setFirstPerson(Message.getMsg(Message.PLAYER__BATTLE__FIRST_PERSON));
    }

    /**
     * 替换名字
     *
     * @param str String
     * @return String
     */
    public static String replaceName(String str) {
        if (str != null && messages.contains(REPLACE_LIST.toString())) {
            for (String replaceName : messages.getConfigurationSection(REPLACE_LIST.toString()).getKeys(false)) {
                if (str.equals(replaceName)) {
                    return messages.getString(REPLACE_LIST.toString() + "." + replaceName).replace("&", "§");
                }
            }
        }
        return str;
    }

    /**
     * 获取String
     *
     * @param loc  Message
     * @param args Object...
     * @return String
     */
    public static String getMsg(Message loc, Object... args) {
        return tool.getString(loc.toString(), args);
    }

    /**
     * 获取List
     *
     * @param loc  Message
     * @param args Object...
     * @return List
     */
    public static List<String> getStringList(Message loc, Object... args) {
        return tool.getStringList(loc.toString(), args);
    }

    /**
     * 发送消息给玩家
     *
     * @param entity LivingEntity
     * @param loc    Message
     * @param args   Object...
     */
    public static void send(LivingEntity entity, Message loc, Object... args) {
        tool.send(entity, loc.toString(), args);
    }

    @Override
    public String toString() {
        return name().replace("__", ".");
    }

    @Setter
    public static class Tool {

        YamlConfiguration config;

        public YamlConfiguration config() {
            return config;
        }

        public String getString(String loc, Object... args) {
            return MessageFormat.format(config.getString(loc, "Null Message: " + loc), args).replace("&", "§");
        }

        public List<String> getStringList(String loc, Object... args) {
            List<String> list = config.getStringList(loc);
            if (list.size() == 0) return Collections.singletonList("Null Message: " + loc);
            IntStream.range(0, list.size()).forEach(i -> list.set(i, MessageFormat.format(list.get(i), args).replace("&", "§")));
            return list;
        }

        public void send(LivingEntity entity, String loc, Object... args) {
            send(entity, getString(loc, args));
        }

        public static void send(LivingEntity entity, String msg) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                if (msg.contains("[ACTIONBAR]")) {
                    msg = msg.replace("[ACTIONBAR]", "");
                    if (SXAttribute.getVersionSplit()[1] >= 9 && SXAttribute.getVersionSplit()[2] >= 2) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msg));
                    }
                } else if (msg.contains("[TITLE]")) {
                    String[] titleSplit = msg.replace("[TITLE]", "").split(":");
                    if (SXAttribute.getVersionSplit()[1] >= 11 && SXAttribute.getVersionSplit()[2] >= 2) {
                        player.sendTitle(titleSplit[0], titleSplit.length > 1 ? titleSplit[1] : null, 5, 20, 5);
                    } else {
                        player.sendTitle(titleSplit[0], titleSplit.length > 1 ? titleSplit[1] : null);
                    }
                } else {
                    player.sendMessage(msg);
                }
            }
        }

        public static TextComponent getTextComponent(String msg, String command, String showText) {
            TextComponent tc = new TextComponent(msg);
            if (showText != null)
                tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7" + showText).create()));
            if (command != null) tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
            return tc;
        }

        public static void sendTextComponent(CommandSender sender, TextComponent tc) {
            if (sender instanceof Player) {
                ((Player) sender).spigot().sendMessage(tc);
            } else {
                sender.sendMessage(tc.getText());
            }
        }
    }
}