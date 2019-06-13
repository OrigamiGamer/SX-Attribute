package github.saukiya.sxattribute.listener;

import github.saukiya.sxattribute.SXAttribute;
import github.saukiya.sxattribute.data.attribute.SXAttributeData;
import github.saukiya.sxattribute.data.attribute.AttributeType;
import github.saukiya.sxattribute.data.attribute.SubAttribute;
import github.saukiya.sxattribute.data.condition.SubCondition;
import github.saukiya.sxattribute.data.eventdata.sub.DamageData;
import github.saukiya.sxattribute.event.SXDamageEvent;
import github.saukiya.sxattribute.util.Config;
import github.saukiya.sxattribute.verision.MaterialControl;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

/**
 * @author Saukiya
 */

public class OnDamageListener implements Listener {

    private final SXAttribute plugin;

    public OnDamageListener(SXAttribute plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    void onProjectileHitEvent(EntityShootBowEvent event) {
        if (event.isCancelled()) return;
        Entity projectile = event.getProjectile();
        LivingEntity entity = event.getEntity();
        if (entity instanceof LivingEntity) {
            SXAttribute.getAPI().setProjectileData(projectile.getUniqueId(), plugin.getAttributeManager().getEntityData(entity));
            ItemStack item = event.getBow();
            if (item != null && SubCondition.isUnbreakable(item.getItemMeta())) {
                Bukkit.getPluginManager().callEvent(new PlayerItemDamageEvent((Player) entity, item, 1));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || Config.getDamageEventBlackList().contains(event.getCause().name())) {
            return;
        }
        LivingEntity defenseEntity = (event.getEntity() instanceof LivingEntity && !(event.getEntity() instanceof ArmorStand)) ? (LivingEntity) event.getEntity() : null;
        LivingEntity attackEntity = null;
        SXAttributeData defenseData;
        SXAttributeData attackData = null;
        // 当攻击者为投抛物时
        if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof LivingEntity) {
            attackEntity = (LivingEntity) ((Projectile) event.getDamager()).getShooter();
            attackData = SXAttribute.getAPI().getProjectileData(event.getDamager().getUniqueId());
        } else if (event.getDamager() instanceof LivingEntity) {
            attackEntity = (LivingEntity) event.getDamager();
        }

        // 若有一方为null 或 怪v怪的属性计算 则取消
        if (defenseEntity == null || attackEntity == null || (!Config.isDamageCalculationToEVE() && !(defenseEntity instanceof Player || attackEntity instanceof Player))) {
            return;
        }

        defenseData = plugin.getAttributeManager().getEntityData(defenseEntity);
        attackData = attackData != null ? attackData : plugin.getAttributeManager().getEntityData(attackEntity);

        EntityEquipment eq = attackEntity.getEquipment();
        ItemStack mainHand = SXAttribute.getVersionSplit()[1] > 8 ? eq.getItemInMainHand() : eq.getItemInHand();
        if (mainHand != null) {
            if (!MaterialControl.AIR.parse().equals(mainHand.getType()) && mainHand.getItemMeta().hasLore()) {
                if (attackEntity instanceof Player && !((HumanEntity) attackEntity).getGameMode().equals(GameMode.CREATIVE)) {
                    if (mainHand.getType().getMaxDurability() == 0 || SubCondition.isUnbreakable(mainHand.getItemMeta())) {
                        Bukkit.getPluginManager().callEvent(new PlayerItemDamageEvent((Player) attackEntity, mainHand, 1));
                    }
                }
            }
        }

        String defenseName = plugin.getOnHealthChangeListener().getEntityName(defenseEntity);
        String attackName = plugin.getOnHealthChangeListener().getEntityName(attackEntity);

        DamageData damageData = new DamageData(defenseEntity, attackEntity, defenseName, attackName, defenseData, attackData, event);

        for (SubAttribute attribute : SubAttribute.getAttributes()) {
            if (attribute.containsType(AttributeType.ATTACK) && attackData.isValid(attribute)) {
                attribute.eventMethod(attackData.getValues(attribute), damageData);
            } else if (attribute.containsType(AttributeType.DEFENCE) && defenseData.isValid(attribute)) {
                attribute.eventMethod(defenseData.getValues(attribute), damageData);
            }

            if (damageData.isCancelled() || damageData.getDamage() <= 0) {
                damageData.setDamage(Config.getMinimumDamage());
                break;
            }
        }
        damageData.setDamage(damageData.getDamage() > Config.getMinimumDamage() ? damageData.getDamage() : Config.getMinimumDamage());
        Bukkit.getPluginManager().callEvent(new SXDamageEvent(damageData));
    }
}
