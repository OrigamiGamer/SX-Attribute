package github.saukiya.sxattribute.data.condition.sub;

import github.saukiya.sxattribute.data.condition.SubCondition;
import github.saukiya.sxattribute.util.Config;
import github.saukiya.sxattribute.util.Message;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 限制等级
 *
 * @author Saukiya
 */
public class LimitLevel extends SubCondition {

    public LimitLevel(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean determine(LivingEntity entity, ItemStack item, String lore) {
        if (lore.contains(Config.getConfig().getString(Config.NAME_LIMIT_LEVEL)) && entity instanceof Player && ((Player) entity).getLevel() < Integer.valueOf(getNumber(lore))) {
            if (item != null) Message.send(entity, Message.PLAYER__NO_LEVEL_USE, getItemName(item));
            return false;
        }
        return true;
    }
}
