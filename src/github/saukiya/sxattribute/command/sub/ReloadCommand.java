package github.saukiya.sxattribute.command.sub;

import github.saukiya.sxattribute.SXAttribute;
import github.saukiya.sxattribute.command.SubCommand;
import github.saukiya.sxattribute.event.SXReloadEvent;
import github.saukiya.sxattribute.util.Config;
import github.saukiya.sxattribute.util.Message;
import github.saukiya.sxattribute.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * 重载指令
 *
 * @author Saukiya
 */
public class ReloadCommand extends SubCommand {

    public ReloadCommand() {
        super("reload");
    }

    @Override
    public void onCommand(SXAttribute plugin, CommandSender sender, String[] args) {
        long oldTimes = System.currentTimeMillis();
        try {
            Config.loadConfig();
            Message.loadMessage();
            plugin.getRandomStringManager().loadData();
            plugin.getItemDataManager().loadItemData();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            sender.sendMessage("§cIO Error");
            return;
        }
        TimeUtil.getSdf().reload();
        plugin.getAttributeManager().onAttributeReload();
        plugin.getAttributeManager().loadDefaultAttributeData();
        plugin.getSlotDataManager().loadData();
        int size = 0;
        d1:
        for (UUID uuid : new ArrayList<>(plugin.getAttributeManager().getEntityDataMap().keySet())) {
            for (World world : Bukkit.getWorlds()) {
                for (Entity entity : world.getEntities()) {
                    if (entity.getUniqueId().equals(uuid)) {
                        // 找到了耶 不清理
                        continue d1;
                    }
                }
            }
            // 全部循环没找到 清除
            plugin.getAttributeManager().clearEntityData(uuid);
            size++;
        }

        if (size > 0) {
            sender.sendMessage(Message.getMsg(Message.ADMIN__CLEAR_ENTITY_DATA, String.valueOf(size)));
        }
        sender.sendMessage(Message.getMsg(Message.ADMIN__PLUGIN_RELOAD));
        SXAttribute.getInst().getLogger().info("Reloading Time: " + (System.currentTimeMillis() - oldTimes) + " ms");
        Bukkit.getPluginManager().callEvent(new SXReloadEvent(sender));
    }
}