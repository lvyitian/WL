package top.whitecola.wlbot.util;

import java.util.Vector;

import net.mamoe.mirai.contact.Group;
import top.whitecola.wlbot.BukkitWhitelistBot;
import top.whitecola.wlbot.listener.PlayerListener;

public class GroupUtil {
	public static boolean isBotInGroup(long group) {
		if(BukkitWhitelistBot.instance.bot.getGroup(group)!=null) {
			return true;
		}
		return false;
	}
	public static boolean isInGroup(long qq,long group) {
		if(isBotInGroup(group)) {
			return BukkitWhitelistBot.instance.bot.getGroup(group).contains(qq);
		}else {
			return false;
		}
	}
	public static boolean isInConfigGroup(long group) {
		return PlayerListener.containsLong(BukkitWhitelistBot.instance.config.getConfig().useBotGroup, group);
	}
	public static boolean isQQInConfigGroup(long qq)
	{
		
		for(long i : BukkitWhitelistBot.instance.config.getConfig().useBotGroup)
			if(isBotInGroup(i)) {
				if(BukkitWhitelistBot.instance.bot.getGroup(i).contains(qq))
					return true;
			}
		return false;
	}
	public static boolean isQQInTargetConfigGroup(long qq,long group)
	{
		return isInConfigGroup(group)&&isInGroup(qq,group);
	}
	public static void sendMsgToAllGroup(String msg)
	{
		for(Group group : BukkitWhitelistBot.instance.bot.getGroups()) {
			group.sendMessage(msg);
		}

	}
	public static void sendMsgToAllTrueConfigGroup(String msg)
	{
		if(BukkitWhitelistBot.instance.getGroupFromUseBotGroupIds().isEmpty()) {
			BukkitWhitelistBot.instance.config.getConfig().allGroupUse = true;
			sendMsgToAllGroup(msg);
			return;
		}
		for(Group group : BukkitWhitelistBot.instance.getGroupFromUseBotGroupIds()) {
			if(isBotInGroup(group.getId())) {
				group.sendMessage(msg);
			}
		}
	}
	
	public static void sendMsgToGroupAotu(String msg) {
		if(BukkitWhitelistBot.instance.config.getConfig().allGroupUse||BukkitWhitelistBot.instance.getGroupFromUseBotGroupIds().isEmpty()) {
			sendMsgToAllGroup(msg);
			return;
		}else {
			sendMsgToAllTrueConfigGroup(msg);
			return;
		}
	}
	
	
	public static boolean isBotInAllConfigGroup() {
		for(long group : BukkitWhitelistBot.instance.config.getConfig().useBotGroup) {
			if(!isBotInGroup(group)) {
				return false;
			}
		}
		return true;
	}
	public static Vector<Long> getAllTrueBotInConfigGroup() {
		Vector<Long> allTrueGroup = new Vector<Long>();
		for(long group : BukkitWhitelistBot.instance.config.getConfig().useBotGroup) {
			if(isBotInGroup(group)) {
				allTrueGroup.add(group);
			}
		}
		if(!allTrueGroup.isEmpty()) {
			return allTrueGroup;
		}
		return null;
	}
}
