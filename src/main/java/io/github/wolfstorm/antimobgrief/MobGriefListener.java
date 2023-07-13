package io.github.wolfstorm.antimobgrief;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

public class MobGriefListener implements Listener {
    private final AntiMobGriefPlugin plugin;

    public MobGriefListener(AntiMobGriefPlugin antiMobGriefPlugin) { this.plugin = antiMobGriefPlugin; }

    @EventHandler
    public void onCreeperExplode(EntityExplodeEvent creeperGoBoomEvt){
        if(creeperGoBoomEvt.getEntityType() == EntityType.CREEPER && !plugin.getCreeperGriefStatus()){
            creeperGoBoomEvt.blockList().clear();
        }
    }

    @EventHandler
    public void onEntityDestroyEntity(EntityDamageByEntityEvent dmgEvt){
        if(!dmgEvt.getEntityType().isAlive() || dmgEvt.getEntityType() == EntityType.ARMOR_STAND){
            switch(dmgEvt.getDamager().getType()) {
                case CREEPER:
                    if (!plugin.getCreeperGriefStatus()) {
                        dmgEvt.setCancelled(true);
                    }
                    break;
                case FIREBALL:
                    if(!plugin.getGhastGriefStatus()) {
                        dmgEvt.setCancelled(true);
                    }
                    break;
                //case ENTITYTYPE:
            }
        }
    }

    @EventHandler
    public void onHangingBreak(HangingBreakByEntityEvent hangEvt){
        switch(hangEvt.getRemover().getType()){
            case CREEPER:
                if (!plugin.getCreeperGriefStatus()) {
                    hangEvt.setCancelled(true);
                }
                break;
            case GHAST:
                if(!plugin.getGhastGriefStatus()) {
                    hangEvt.setCancelled(true);
                }
                break;
            case FIREBALL:
            default:
                break;

        }
    }

    @EventHandler
    public void onZombieDoorBreak(EntityBreakDoorEvent brkDoorEvt) {
        if(!plugin.getDoorGriefStatus()){
            brkDoorEvt.setCancelled(true);
        }
    }

    @EventHandler
    public void onEndermanPickup(EntityChangeBlockEvent blkChangeEvt) {
        if (blkChangeEvt.getEntityType() == EntityType.ENDERMAN && !plugin.getEndermanGriefStatus()) {
            blkChangeEvt.setCancelled(true);
        }
    }

    @EventHandler
    public void onRavagerDestroy(EntityChangeBlockEvent blkChangeEvt) {
        if (blkChangeEvt.getEntityType() == EntityType.RAVAGER && !plugin.getRavagerGriefStatus()) {
            blkChangeEvt.setCancelled(true);
        }
    }

}
