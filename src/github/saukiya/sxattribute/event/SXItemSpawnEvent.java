package github.saukiya.sxattribute.event;

import github.saukiya.sxattribute.data.itemdata.SubItemGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * 物品生成事件
 *
 * @author Saukiya
 */

@AllArgsConstructor
@Getter
public class SXItemSpawnEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player player;

    private SubItemGenerator ig;

    private ItemStack item;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }
}
