package top.dsbbs2.whitelist.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import top.dsbbs2.common.lambda.INoThrowsRunnable;
import top.dsbbs2.whitelist.WhiteListPlugin;
import top.dsbbs2.whitelist.util.EventUtil;
import top.dsbbs2.whitelist.util.MsgUtil;
import top.dsbbs2.whitelist.util.PlayerUtil;
import top.dsbbs2.whitelist.util.ServerUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SuppressWarnings("deprecation")
public class PlayerListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) throws Throwable {
        EventUtil.checkAndCancel(e, e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlayerConsumeItem(PlayerItemConsumeEvent e) throws Throwable {
        EventUtil.checkAndCancel(e, e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlayerPickupItem(PlayerPickupItemEvent e) throws Throwable {
        EventUtil.checkAndCancel(e, e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlayerDropItem(PlayerDropItemEvent e) throws Throwable {
        EventUtil.checkAndCancel(e, e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlayerInteract(PlayerInteractEvent e) throws Throwable {
        EventUtil.checkAndCancel(e, e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlayerChat(PlayerChatEvent e) throws Throwable {
        EventUtil.checkAndCancel(e, e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlayerDamageEntity(EntityDamageByEntityEvent e) {
        try {

            if (e.getDamager() instanceof Player) {
                Player p = (Player) e.getDamager();
                EventUtil.checkAndCancel(e, p);
            }
        } catch (NoSuchFieldException e1) {
            Player p = (Player) e.getDamager();
            MsgUtil.makeDebugMsgAndSend(p.getName() + "很可能不是玩家,可能是假人!");
        } catch (Throwable e2) {

        }

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onGameModeChange(PlayerGameModeChangeEvent e) {
        INoThrowsRunnable awa=()->PlayerUtil.setInv(e.getPlayer(), true);
        try {
            if (!PlayerUtil.isInWhiteList(e.getPlayer().getUniqueId()) && !PlayerUtil.isInWhiteList(e.getPlayer().getName())) {
                Bukkit.getScheduler().runTask(WhiteListPlugin.instance, awa);
            }

        } catch (Throwable ee) {
            e.setCancelled(true);
            Bukkit.getScheduler().runTask(WhiteListPlugin.instance, awa);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onPlayerJoin(PlayerJoinEvent e) throws Throwable {
        MsgUtil.makeDebugMsgAndSend(e.getPlayer().getName()+"正在加入游戏!");
        if (ServerUtil.isOnlineStorageMode()) {
            //正版服储存模式

            //验证
            PlayerUtil.checkUUIDAndName(e.getPlayer());

            if (!PlayerUtil.isInWhiteList(e.getPlayer().getUniqueId()) && !PlayerUtil.isInWhiteList(e.getPlayer().getName())) {
                PlayerUtil.setNoWhitelistMode(e.getPlayer(), true);
            }

        } else {
            if (!PlayerUtil.isInWhiteList(e.getPlayer().getName())) {
                PlayerUtil.setNoWhitelistMode(e.getPlayer(), true);
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onCommandProcess(PlayerCommandPreprocessEvent e) throws Throwable {
        String mess = e.getMessage();
        if ( WhiteListPlugin.instance.whitelist.con.uesLoginPluginOrNot) {
            if (mess.startsWith("/login") || mess.startsWith("/register")) {
                return;
            }
        }
        EventUtil.checkAndCancel(e, e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPreLogin(PlayerPreLoginEvent e) {
        if ( !WhiteListPlugin.instance.whitelist.con.canNoWhitePlayerGetIn) {
            if (!PlayerUtil.isInWhiteList(e.getName())) {
                if (WhiteListPlugin.instance.whitelist.con.PlayerCantJoinMSG != null && !WhiteListPlugin.instance.whitelist.con.PlayerCantJoinMSG.equals("")) {
                    e.disallow(PlayerPreLoginEvent.Result.KICK_WHITELIST, WhiteListPlugin.instance.whitelist.con.PlayerCantJoinMSG);
                } else {
                    e.disallow(PlayerPreLoginEvent.Result.KICK_WHITELIST, "您目前没有白名单,无法进入,请先申请白名单!");
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleport(PlayerTeleportEvent e) throws Throwable {
        MsgUtil.makeDebugMsgAndSend("检测到玩家传送"+e.getPlayer().getName());
        EventUtil.checkAndCancel(e, e.getPlayer());

    }
}
