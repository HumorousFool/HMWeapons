package io.github.humorousfool.hmweapons.listener;

import io.github.humorousfool.hmweapons.items.CustomItem;
import io.github.humorousfool.hmweapons.items.ItemRegistry;
import io.github.humorousfool.hmweapons.items.preset.PresetEffect;
import io.github.humorousfool.hmweapons.util.ItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class ItemListener implements Listener
{
    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        if(event.getHand() == null) return;
        String id = ItemUtil.getItem(event.getPlayer().getInventory().getItem(event.getHand()));
        if(id == null) return;

        for(CustomItem item : ItemRegistry.items.values())
        {
            if(item.id.equals(id))
            {
                PresetEffect.EventContext ctx = new PresetEffect.EventContext(item.id, event.getHand(), item.effects, 0);
                for(int i = 0; i < item.effects.size(); i++)
                {
                    ctx.setEffectIndex(i);
                    PresetEffect effect = item.effects.get(i);
                    if(effect.flags.shouldStop(effect.onInteract(event, ctx))) return;
                    i = ctx.getEffectIndex();
                }

                return;
            }
        }
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event)
    {
        if(event.isCancelled()) return;

        if(!(event.getEntity().getShooter() instanceof Player player)) return;

        EquipmentSlot[] slots = new EquipmentSlot[] {EquipmentSlot.HAND, EquipmentSlot.OFF_HAND};
        for(EquipmentSlot slot : slots)
        {
            String id = ItemUtil.getItem(player.getInventory().getItem(slot));
            if(id == null) continue;

            for(CustomItem item : ItemRegistry.items.values())
            {
                if(item.id.equals(id))
                {
                    PresetEffect.EventContext ctx = new PresetEffect.EventContext(item.id, slot, item.effects, 0);
                    for(int i = 0; i < item.effects.size(); i++)
                    {
                        ctx.setEffectIndex(i);
                        PresetEffect effect = item.effects.get(i);
                        if(effect.flags.shouldStop(effect.onProjectileLaunch(event, ctx))) break;
                        i = ctx.getEffectIndex();
                    }

                    break;
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event)
    {
        if(event.isCancelled()) return;

        if(!(event.getEntity().getShooter() instanceof Player player)) return;

        EquipmentSlot[] slots = new EquipmentSlot[] {EquipmentSlot.HAND, EquipmentSlot.OFF_HAND};
        for(EquipmentSlot slot : slots)
        {
            String id = ItemUtil.getItem(player.getInventory().getItem(slot));
            if(id == null) continue;

            for(CustomItem item : ItemRegistry.items.values())
            {
                if(item.id.equals(id))
                {
                    PresetEffect.EventContext ctx = new PresetEffect.EventContext(item.id, slot, item.effects, 0);
                    for(int i = 0; i < item.effects.size(); i++)
                    {
                        ctx.setEffectIndex(i);
                        PresetEffect effect = item.effects.get(i);
                        if(effect.flags.shouldStop(effect.onProjectileHit(event, ctx))) break;
                        i = ctx.getEffectIndex();
                    }

                    break;
                }
            }
        }
    }
}
