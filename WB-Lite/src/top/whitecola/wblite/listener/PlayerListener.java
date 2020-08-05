package top.whitecola.wblite.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import top.dsbbs2.whitelist.WhiteListPlugin;
import top.dsbbs2.whitelist.config.struct.WhiteListConfig;
import top.dsbbs2.whitelist.config.struct.WhiteListConfig.WLPlayer;
import top.dsbbs2.whitelist.util.CommandUtil;
import top.dsbbs2.whitelist.util.PlayerUtil;
import top.whitecola.wblite.WBLite;
import top.whitecola.wlbot.util.GroupUtil;

public class PlayerListener implements Listener {
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e) {
		if(WBLite.instance.config.getConfig().playerJoinServerGroupMsg) {
			sendJoinOrLeaveServerGroupMsg(1, e.getPlayer());
		}
		WLPlayer wlp = PlayerUtil.getWLPlayerByName(e.getPlayer().getName());
		if(wlp!=null&&wlp.QQ!=-1) {
			if(!GroupUtil.isQQInGroupAuto(wlp.QQ)) {
				CommandUtil.getCommand(WhiteListPlugin.instance.childCmds, "remove").onCommand(Bukkit.getConsoleSender(), WhiteListPlugin.instance.getCommand("wl"), "wl", new String[] {"remove",e.getPlayer().getName()});
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if(WBLite.instance.config.getConfig().playerLeaveServerGroupMsg) {
			sendJoinOrLeaveServerGroupMsg(2, e.getPlayer());
		}
	}


	public void sendJoinOrLeaveServerGroupMsg(int type,Player p) {
		//type ==1 join
		WhiteListConfig.WLPlayer wlp=PlayerUtil.getWLPlayerByName(p.getName());
			if(wlp!=null)
			{
				if(wlp.QQ!=-1) {
					if(type==1) {
						GroupUtil.sendMsgToGroupAuto("玩家 "+wlp.name+"["+wlp.QQ+"] 加入了游戏.");
						GroupUtil.sendMsgToGroupAuto("目前服务器人数: "+(p.getServer().getOnlinePlayers().size()+1)+" 人.");
						return;
					}else {
						GroupUtil.sendMsgToGroupAuto("玩家 "+wlp.name+"["+wlp.QQ+"] 退出了游戏.");
						GroupUtil.sendMsgToGroupAuto("目前服务器人数: "+(p.getServer().getOnlinePlayers().size()-1)+" 人.");
						return;
					}
				}else {
					if(type==1) {
						GroupUtil.sendMsgToGroupAuto("玩家 "+p.getName()+" 加入了游戏.");
						GroupUtil.sendMsgToGroupAuto("目前服务器人数: "+(p.getServer().getOnlinePlayers().size()+1)+" 人.");
						return;
					}else {
						GroupUtil.sendMsgToGroupAuto("玩家 "+p.getName()+" 退出了游戏.");
						GroupUtil.sendMsgToGroupAuto("目前服务器人数: "+(p.getServer().getOnlinePlayers().size()-1)+" 人.");
						return;
					}
					
				}
			}else {
				if(type==1) {
					GroupUtil.sendMsgToGroupAuto("没有白名单的玩家 "+p.getName()+" 加入了游戏.");
					GroupUtil.sendMsgToGroupAuto("目前服务器人数: "+(p.getServer().getOnlinePlayers().size()+1)+" 人.");
					return;
				}else {
					GroupUtil.sendMsgToGroupAuto("没有白名单的玩家 "+p.getName()+" 退出了游戏.");
					GroupUtil.sendMsgToGroupAuto("目前服务器人数: "+(p.getServer().getOnlinePlayers().size()-1)+" 人.");
					return;
				}
			}
		
		


	}
}
