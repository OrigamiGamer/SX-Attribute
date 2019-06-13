package github.saukiya.sxattribute.data;

import github.saukiya.sxattribute.SXAttribute;
import github.saukiya.sxattribute.util.Message;
import github.saukiya.sxattribute.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Saukiya
 */
public class RandomStringManager {
    private final File file = new File(SXAttribute.getInst().getDataFolder(), "RandomString");

    private final Map<String, List<String>> map = new HashMap<>();
    private final SXAttribute plugin;

    public RandomStringManager(SXAttribute plugin) throws IOException {
        this.plugin = plugin;
        loadData();
    }

    /**
     * 获取随机字符串数据
     *
     * @return Set
     */
    public Set<Map.Entry<String, List<String>>> entrySet() {
        return map.entrySet();
    }

    /**
     * 处理随机文本
     *
     * @param string  被随机的文本
     * @param lockMap 存储固定值的Map
     * @return 处理后的Map
     */
    public String processRandomString(String string, Map<String, String> lockMap) {
        if (string != null) {
            // 固定随机
            List<String> replaceLockStringList = getStringList("<l:", ">", string);
            for (String str : replaceLockStringList) {
                String randomStr = lockMap.get(str);
                if (randomStr != null) {
                    string = string.replace("<l:" + str + ">", randomStr);
                } else {
                    randomStr = getRandomString(str, lockMap);
                    if (!randomStr.equals("%DeleteLore%")) {
                        // 记录到LockMap中
                        lockMap.put(str, randomStr);
                    }
                    string = string.replace("<l:" + str + ">", randomStr);
                }
            }
            // 普通随机
            List<String> replaceStringList = getStringList("<s:", ">", string);
            for (String str : replaceStringList) {
                string = string.replaceFirst("<s:" + str + ">", getRandomString(str, lockMap));
            }
            // 数字随机
            List<String> replaceIntList = getStringList("<r:", ">", string);
            for (String str : replaceIntList) {
                String[] strSplit = str.split("_");
                if (strSplit.length > 1) {
                    int i1 = Integer.valueOf(strSplit[0]);
                    int i2 = Integer.valueOf(strSplit[1]) + 1;
                    string = string.replaceFirst("<r:" + str + ">", String.valueOf(SXAttribute.getRandom().nextInt((i2 - i1) < 1 ? 1 : (i2 - i1)) + i1));
                }
            }
            // 小数随机
            List<String> replaceDoubleList = getStringList("<d:", ">", string);
            for (String str : replaceDoubleList) {
                String[] strSplit = str.split("_");
                if (strSplit.length > 1) {
                    double d1 = Double.valueOf(strSplit[0]);
                    double d2 = Double.valueOf(strSplit[1]);
                    string = string.replaceFirst("<d:" + str + ">", SXAttribute.getDf().format(SXAttribute.getRandom().nextDouble() * (d2 - d1) + d1));
                }
            }
            // 日期随机
            List<String> replaceTimeList = getStringList("<t:", ">", string);
            if (replaceTimeList.size() > 0) {
                for (String str : replaceTimeList) {
                    String addTime = str + "000";
                    long time = System.currentTimeMillis() + Long.valueOf(addTime);
                    string = string.replaceFirst("<t:" + str + ">", TimeUtil.getSdf().format(time));
                }
            }
        }
        return string;
    }

    /**
     * 获取字符组
     *
     * @param name    String
     * @param lockMap Map
     * @return String
     */
    private String getRandomString(String name, Map<String, String> lockMap) {
        List<String> randomList = map.get(name);
        if (randomList != null) {
            String str1 = randomList.get(SXAttribute.getRandom().nextInt(randomList.size()));
            if (lockMap != null) {
                List<String> replaceLockStringList = getStringList("<l:", ">", str1);
                for (String str : replaceLockStringList) {
                    String randomStr = lockMap.get(str);
                    if (randomStr != null) {
                        str1 = str1.replace("<l:" + str + ">", randomStr);
                    } else {
                        randomStr = getRandomString(str, lockMap);
                        if (!randomStr.equals("%DeleteLore%")) {
                            lockMap.put(str, randomStr);
                        }
                        str1 = str1.replace("<l:" + str + ">", randomStr);
                    }
                }
            }
            List<String> replaceStringList = getStringList("<s:", ">", str1);
            for (String str2 : getStringList("<s:", ">", str1)) {
                str1 = str1.replaceFirst("<s:" + str2 + ">", getRandomString(str2, lockMap));
            }
            return str1;
        }
        return "%DeleteLore%";
    }


    /**
     * 获取变量
     *
     * @param prefix 前缀
     * @param suffix 后缀
     * @param string 被读取的字符串
     * @return 被前后缀包围的列表 (不包括前后缀)
     */
    public List<String> getStringList(String prefix, String suffix, String string) {
        List<String> stringList = new ArrayList<>();
        if (string.contains(prefix)) {
            String[] args = string.split(prefix);
            if (args.length > 1 && args[1].contains(suffix)) {
                for (int i = 1; i < args.length && args[i].contains(suffix); i++) {
                    stringList.add(args[i].split(suffix)[0]);
                }
            }
        }
        return stringList;
    }

    /**
     * 加载随机字符串数据
     *
     * @throws IOException IOException
     */
    public void loadData() throws IOException {
        map.clear();
        if (!file.exists() || Objects.requireNonNull(file.listFiles()).length == 0) {
            createDefaultRandom();
        }
        loadRandom(file);
        SXAttribute.getInst().getLogger().info("Loaded " + map.size() + " RandomString");
    }

    /**
     * 遍历读取随机字符串数据
     *
     * @param files File
     */
    @SuppressWarnings("unchecked")
    private void loadRandom(File files) {
        for (File file : Objects.requireNonNull(files.listFiles())) {
            if (file.isDirectory()) {
                loadRandom(file);
            } else {
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                for (String name : yml.getKeys(false)) {
                    if (map.containsKey(name)) {
                        SXAttribute.getInst().getLogger().info("不要重复随机字符组名: " + file.getName().replace("plugins" + File.separator + SXAttribute.getInst().getName() + File.separator, "") + File.separator + name + " !");
                    }
                    if (yml.get(name) instanceof String) {
                        map.put(name, Collections.singletonList(yml.getString(name)));
                    } else if (yml.get(name) instanceof List) {
                        List<String> list = new ArrayList<>();
                        for (Object obj : yml.getList(name)) {
                            if (obj instanceof List) {
                                List<String> objList = (List<String>) obj;
                                StringBuilder str = new StringBuilder(objList.size() > 0 ? objList.get(0) : "");
                                for (int i = 1; i < objList.size(); i++) {
                                    str.append("/n").append(objList.get(i));
                                }
                                list.add(str.toString());
                            } else {
                                list.add(obj.toString());
                            }
                        }
                        map.put(name, list);
                    }
                }
            }
        }
    }

    /**
     * 创建默认数据
     *
     * @throws IOException IOException
     */
    private void createDefaultRandom() throws IOException {
        YamlConfiguration yml = new YamlConfiguration();
        SXAttribute.getInst().getLogger().info("Create Item/Default.yml");
        yml.set("DefaultLore", Arrays.asList(
                "&7&o是由什么材质做成的呢?",
                "&7&o源于诅咒的力量",
                "&7&o火焰重塑、泯灭了这里，就这样诞生了",
                "&7&o难以想象他到底隐藏了多大的能量"
        ));
        yml.set("DefaultPrefix", Arrays.asList(
                "&c令人兴奋之",
                "&c煞胁之",
                "&e兴趣使然之",
                "&e初心者之",
                "&e丝质之",
                "&e精灵之"
        ));
        yml.set("DefaultSuffix", Arrays.asList(
                "&e淦",
                "&e武",
                "&e衡"
        ));
        yml.set("普通基数", "<d:1_1.1>");
        yml.set("优秀基数", "<d:1.3_1.4>");
        yml.set("史诗基数", "<d:1.7_1.8>");
        yml.set("普通属判", Arrays.asList("%DeleteLore%", "%DeleteLore%", "%DeleteLore%", ""));
        yml.set("优秀属判", Arrays.asList("%DeleteLore%", ""));
        yml.set("史诗属判", Arrays.asList("%DeleteLore%", "", "", ""));
        yml.set("普通颜色", Arrays.asList("<s:好丑Color>", "<s:好丑Color>", "<s:好丑Color>", "<s:好看Color>"));
        yml.set("优秀颜色", Arrays.asList("<s:好丑Color>", "<s:好看Color>"));
        yml.set("史诗颜色", Arrays.asList("<s:好丑Color>", "<s:好看Color>", "<s:好看Color>", "<s:好看Color>"));
        yml.set("品质", Arrays.asList("普通", "普通", "普通", "普通", "普通", "普通", "普通", "优秀", "优秀", "史诗"));
        yml.set("职业", Arrays.asList("射手", "战士", "剑士"));
        yml.set("射手附魔", Arrays.asList(
                Arrays.asList("ARROW_DAMAGE:<r:0_2>", "ARROW_INFINITE:<r:0_1>"),
                Arrays.asList("ARROW_DAMAGE:<r:0_2>", "ARROW_FIRE:<r:0_2>"),
                Arrays.asList("ARROW_DAMAGE:<r:0_2>", "DURABILITY:<r:0_1>")
        ));
        yml.set("战士附魔", Arrays.asList(
                Arrays.asList("DAMAGE_ALL:<r:0_2>", "FIRE_ASPECT:<r:0_1>"),
                Arrays.asList("DAMAGE_ARTHROPODS:<r:0_2>", "KNOCKBACK:<r:0_1>"),
                Arrays.asList("DAMAGE_UNDEAD:<r:0_2>", "LOOT_BONUS_MOBS:<r:0_1>")
        ));
        yml.set("剑士附魔", Arrays.asList(
                Arrays.asList("DAMAGE_ALL:<r:0_2>", "FIRE_ASPECT:<r:0_1>"),
                Arrays.asList("DAMAGE_ALL:<r:0_2>", "KNOCKBACK:<r:0_1>"),
                Arrays.asList("DAMAGE_ALL:<r:0_2>", "LOOT_BONUS_MOBS:<r:0_1>")
        ));
        yml.set("射手ID", "261");
        yml.set("战士ID", "<s:战士<l:品质>ID>");
        yml.set("剑士ID", "<s:剑士<l:品质>ID>");
        yml.set("战士普通ID", "258");
        yml.set("战士优秀ID", "286");
        yml.set("战士史诗ID", "279");
        yml.set("剑士普通ID", "267");
        yml.set("剑士优秀ID", "283");
        yml.set("剑士史诗ID", "276");
        yml.set("材质", Arrays.asList("&01", "&01", "&02", "&03", "&04", "&05", "&06", "&07", "&08", "&09", "&010", "&011"));
        yml.set("优秀职判", "");
        yml.set("优秀职判", "");
        yml.set("普通Color", "&7");
        yml.set("优秀Color", "&a");
        yml.set("史诗Color", "&5");
        yml.set("普通宝石孔", "&a&l『&7武石槽&a&l』");
        yml.set("优秀宝石孔", "&a&l『&7武石槽&a&l』&a&l『&7武石槽&a&l』");
        yml.set("史诗宝石孔", "&a&l『&7武石槽&a&l』&a&l『&7武石槽&a&l』&a&l『&7武石槽&a&l』");
        yml.set("史诗绑判", "");
        yml.set("优秀介判", "");
        yml.set("史诗介判", "");
        yml.set("好看Color", Arrays.asList("&a", "&b", "&c", "&4", "&d", "&1", "&3", "&9"));
        yml.set("好丑Color", Arrays.asList("&1", "&8", "&7", "&5", "&3", "&2"));
        yml.set("攻随一", Arrays.asList("命中几率", "失明几率", "缓慢几率", "凋零几率", "夺食几率"));
        yml.set("攻随二", Arrays.asList("雷霆几率", "破甲几率", "撕裂几率"));
        yml.set("防随一_模板未使用", Arrays.asList("反射比例", "格挡比例", "韧性", "移动速度"));
        yml.set("防随二_模板未使用", Arrays.asList("反射比例", "格挡比例", "闪避几率"));
        yml.set("防随三_模板未使用", Arrays.asList("生命恢复", "生命上限", "PVP防御力", "PVE防御力"));
        yml.save(new File(file, "DefaultRandom.yml"));
        yml = new YamlConfiguration();
        yml.set("攻一-10", Arrays.asList(
                Arrays.asList(
                        "<s:<l:品质>颜色>暴击几率: +<c:<r:20_30> * <s:<l:品质>基数>>%",
                        "<s:<l:品质>颜色>暴击伤害: +<c:<r:20_30> * <s:<l:品质>基数>>%"
                ),
                Arrays.asList(
                        "<s:<l:品质>颜色>攻击速度: +<c:<r:20_30> * <s:<l:品质>基数>>%",
                        "<s:<l:品质>颜色>点燃几率: +<c:<r:20_30> * <s:<l:品质>基数>>%"
                ),
                Arrays.asList(
                        "<s:<l:品质>颜色>吸血几率: +<c:<r:20_30> * <s:<l:品质>基数>>%",
                        "<s:<l:品质>颜色>吸血倍率: +<c:<r:20_30> * <s:<l:品质>基数>>%"
                )
        ));
        yml.set("攻二-10", Arrays.asList("<s:<l:品质>属判><s:<l:品质>颜色><s:攻随一>: +<c:<d:8_9> * <s:<l:品质>基数>>%"));
        yml.set("攻三-10", Arrays.asList("<s:<l:品质>属判><s:<l:品质>颜色><s:攻随二>: +<c:<d:4_5> * <s:<l:品质>基数>>%"));
        yml.save(new File(file, "10Level" + File.separator + "Random.yml"));
    }
}