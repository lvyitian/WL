package top.whitecola.wlbot.listener;

import java.util.Objects;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.At;
import top.dsbbs2.common.lambda.INoThrowsRunnable;
import top.dsbbs2.whitelist.WhiteListPlugin;
import top.dsbbs2.whitelist.config.struct.WhiteListConfig.WLPlayer;
import top.dsbbs2.whitelist.util.CommandUtil;
import top.dsbbs2.whitelist.util.PlayerUtil;
import top.whitecola.wlbot.BotListener;
import top.whitecola.wlbot.BukkitWhitelistBot;
import top.whitecola.wlbot.QQCommandSender;
import top.whitecola.wlbot.TimePeriod;
import top.whitecola.wlbot.util.GroupUtil;

@SuppressWarnings("deprecation")
public class PlayerListener implements Listener {
	public static boolean containsLong(long[] arr,long obj)
	{
		for(long i : arr)
			if(i==obj)
				return true;
		return false;
	}
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		if(BukkitWhitelistBot.instance.config.getConfig().isPlayerGotWhitelistThenExcuteCommand)
			if (BukkitWhitelistBot.instance.storage.getConfig().offlinePlayerRecords.parallelStream().anyMatch(i->i.toPlayer().equals(e.getPlayer()))) {
				BukkitWhitelistBot.instance.config.getConfig().playerGotWhitelistThenExcuteCommands.stream().forEach(i->Bukkit.dispatchCommand(Bukkit.getConsoleSender(), i.replace("${%player_name%}", e.getPlayer().getName())));
				BukkitWhitelistBot.instance.storage.getConfig().offlinePlayerRecords.removeIf(i->i.toPlayer().equals(e.getPlayer()));
				INoThrowsRunnable.invoke(BukkitWhitelistBot.instance.storage::saveConfig);
			}
		if(BukkitWhitelistBot.instance.config.getConfig().canPlayersGetTheirLotteries) {
			if(Bukkit.getOnlineMode() || BukkitWhitelistBot.instance.config.getConfig().canPlayerOfOfflineServerGetRandomLotteriesItem) {
				if(BukkitWhitelistBot.instance.storage.getConfig().alreadyGetRewardPlayers.parallelStream().noneMatch(i->Objects.equals(i.toPlayer(), e.getPlayer())))
				{
					BukkitWhitelistBot.instance.config.getConfig().giveReward("@a[name="+e.getPlayer().getName()+"]");
					BukkitWhitelistBot.instance.storage.getConfig().alreadyGetRewardPlayers.add(WLPlayer.fromPlayer(e.getPlayer(), -1));
					INoThrowsRunnable.invoke(BukkitWhitelistBot.instance.storage::saveConfig);
					new BukkitRunnable() {

						@Override
						public void run() {
							e.getPlayer().sendMessage("§a§l"+TimePeriod.now().toString()+"好 "+e.getPlayer().getName());
							e.getPlayer().sendMessage("§a§l我们为你准备了一些随机礼物，已经发送到您的背包，希望您能在这个服务器玩得愉快 : D");
							e.getPlayer().sendMessage("§a§l您现在只需要申请一个白名单 即可加入服务器.");

						}
					}.runTaskLater(BukkitWhitelistBot.instance, 10);

				}
			}
		}

		//////
		if(BukkitWhitelistBot.instance.config.getConfig().isAddWhitelistConfirm) {
			BukkitWhitelistBot.instance.confirmGUIs.forEach(i->{
				if(i.p.equals(e.getPlayer())) {
					i.open();
					e.getPlayer().sendMessage("请选择!");
				}
			});





			//			try {
			//				for(RequestPlayer rp : BukkitWhitelistBot.instance.storage.getConfig().confrimAddWhitelistRequest) {
			//					if(rp.mcName.equalsIgnoreCase(e.getPlayer().getName())) {
			//						ConfirmGUI confirmgui = new ConfirmGUI(e.getPlayer(), rp);
			//						confirmgui.open();
			//						BukkitWhitelistBot.instance.confirmGUIs.add(confirmgui);
			//						INoThrowsRunnable.invoke(BukkitWhitelistBot.instance.storage::saveConfig);
			//						break;
			//					}
			//				}
			//			}catch (Exception ex) {
			//				ex.printStackTrace();
			//				e.getPlayer().kickPlayer("出现未预料的异常,请联系管理员!");
			//			}
		}

	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getCurrentItem()==null) {
			return;
		}
		BukkitWhitelistBot.instance.confirmGUIs.forEach(i->{
			if(i.p.equals(e.getWhoClicked()) && i.inv.equals(e.getInventory())) {
				e.setCancelled(true);
				if(e.getCurrentItem().equals(i.items.get(0))) {
					//拒绝
					i.rp.group.sendMessage(new At(i.rp.sender).plus("您要申请白名单的玩家ID"+e.getWhoClicked().getName()+",在确认时,已拒绝绑定!"));
					i.close();
					BukkitWhitelistBot.instance.confirmGUIs.remove(i);
					e.getWhoClicked().sendMessage("§c已拒绝!");
					e.getWhoClicked().getWorld().playSound(e.getWhoClicked().getLocation(), Sound.BLOCK_ANVIL_PLACE, 200, 0);
				}else if(e.getCurrentItem().equals(i.items.get(1))) {
					//同意

					i.rp.group.sendMessage(new At(i.rp.sender).plus("您要申请白名单的玩家ID"+e.getWhoClicked().getName()+",在确认时,已同意绑定,即将为您添加白名单!"));
					i.close();

					try {

						CommandUtil.getCommand(WhiteListPlugin.instance.childCmds, "add").onCommand(
								new QQCommandSender(i.rp.sender, i.rp.group.getId()),
								WhiteListPlugin.instance.getCommand("wl"), "wl",
								new String[] { "add",
										i.p.getName(),
										new StringBuilder().append(i.rp.sender.getId()).toString() });
						if (!Bukkit.getOfflinePlayer(
								i.p.getName())
								.isOnline()) {
							BukkitWhitelistBot.instance.storage.getConfig().offlinePlayerRecords
							.add(PlayerUtil.getWLPlayerByQQ(i.rp.sender.getId()));
							INoThrowsRunnable.invoke(BukkitWhitelistBot.instance.storage::saveConfig);
						} else {
							BukkitWhitelistBot.instance.config
							.getConfig().playerGotWhitelistThenExcuteCommands
							.stream()
							.forEach(a -> Bukkit.dispatchCommand(
									Bukkit.getConsoleSender(),
									a.replace("${%player_name%}",
											(CharSequence) i.p.getName())));
						}
					} catch (Throwable exc) {
						exc.printStackTrace();
					}
					BukkitWhitelistBot.instance.confirmGUIs.remove(i);
					e.getWhoClicked().sendMessage("§a已同意!");
					e.getWhoClicked().getWorld().playSound(e.getWhoClicked().getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 200, 0);
				}
			}
		});


	}




	@EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=false)
	public void onCloseInventory(InventoryCloseEvent e) {
		if(BukkitWhitelistBot.instance.config.getConfig().isAddWhitelistConfirm) {
			BukkitWhitelistBot.instance.confirmGUIs.forEach(i->{
				if(i.p.equals(e.getPlayer())) {
					Bukkit.getScheduler().runTask(BukkitWhitelistBot.instance, ()->i.open());
					e.getPlayer().sendMessage("请先选择!");
					e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 100, 0);
				}
			});
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {




	}
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e) {
		Player player = e.getPlayer();
		if(!PlayerUtil.isInWhiteList(player)) {
			if(BukkitWhitelistBot.instance.config.getConfig().allGroupUse) {
				GroupUtil.sendMsgToAllGroup("一个没有白名单的玩家 :"+player.getName()+"加入了服务器!");
				GroupUtil.sendMsgToAllGroup("可以使用 .申请白名单 <id> 来申请白名单!");
				if(Bukkit.getServer().getOnlineMode()) {
					for(Group group : BukkitWhitelistBot.instance.bot.getGroups().parallelStream().filter(i->containsLong(BukkitWhitelistBot.instance.config.getConfig().useBotGroup,i.getId())).collect(Collectors.toList())) {
						group.getMembers().parallelStream().filter(i->i.getNameCard().equals(player.getName())&&WhiteListPlugin.instance.whitelist.getConfig().players.parallelStream().noneMatch(i2->i2.QQ==i.getId())).forEach(i->group.sendMessage(new At(i)));
					}
				}
			}else {
				GroupUtil.sendMsgToAllTrueConfigGroup("一个没有白名单的玩家 :"+player.getName()+"加入了服务器!");
				GroupUtil.sendMsgToAllTrueConfigGroup("可以使用 .申请白名单 <id> 来申请白名单!");
				if(Bukkit.getServer().getOnlineMode()) {
					for(Group group : BukkitWhitelistBot.instance.getGroupFromUseBotGroupIds()) {
						group.getMembers().parallelStream().filter(i->i.getNameCard().equals(player.getName())&&WhiteListPlugin.instance.whitelist.getConfig().players.parallelStream().noneMatch(i2->i2.QQ==i.getId())).forEach(i->group.sendMessage(new At(i)));
					}
				}
			}
			//			for(Group group : BukkitWhitelistBot.instance.bot.getGroups().parallelStream().filter(i->containsLong(BukkitWhitelistBot.instance.config.getConfig().useBotGroup,i.getId())).collect(Collectors.toList())) {
			//				group.sendMessage("一个没有白名单的玩家 :"+player.getName()+"加入了服务器!");
			//				group.sendMessage("可以使用 .申请白名单 <id> 来申请白名单!");
			//				if(Bukkit.getServer().getOnlineMode())
			//				  group.getMembers().parallelStream().filter(i->i.getNameCard().equals(player.getName())&&WhiteListPlugin.instance.whitelist.getConfig().players.parallelStream().noneMatch(i2->i2.QQ==i.getId())).forEach(i->group.sendMessage(new At(i)));
			//			}
		}else {

			if(BukkitWhitelistBot.instance.config.getConfig().joinServerOrLeaveServerMsgSendToGroup) {
				GroupUtil.sendMsgToGroupAotu(e.getPlayer().getName()+"加入了服务器!");
			}


			try {
				//				if(WhiteListPlugin.instance!=null&&WhiteListPlugin.instance.whitelist!=null&&WhiteListPlugin.instance.whitelist.getConfig()!=null&&WhiteListPlugin.instance.whitelist.getConfig().players!=null&&!GroupUtil.isQQInConfigGroup(WhiteListPlugin.instance.whitelist.getConfig().players.parallelStream().filter(i->i!=null&&(ServerUtil.isOnlineStorageMode()?i.uuid!=null&&i.uuid.equals(player.getUniqueId()):i.name!=null&&i.name.equals(player.getName()))).map(i->i.QQ).findFirst().orElse(null))) {

				if(PlayerUtil.isInWhiteList(e.getPlayer())) {
					WLPlayer wlp = PlayerUtil.getWLPlayerByName(e.getPlayer().getName());
					if(wlp==null) {
						return;
					}
					if(!GroupUtil.isQQInConfigGroup(wlp.QQ)) {
						CommandUtil.getCommand(WhiteListPlugin.instance.childCmds, "remove").onCommand(Bukkit.getConsoleSender(), WhiteListPlugin.instance.getCommand("wl"), "wl", new String[] {"remove",player.getName()});
						e.getPlayer().sendMessage("由于没有在QQ群内找到您,您的白名单已过期,请重新申请!");
						if(BukkitWhitelistBot.instance.config.getConfig().allGroupUse) {
							GroupUtil.sendMsgToAllGroup("玩家"+e.getPlayer().getName()+"并没有加群,但是他有服务器白名单,将自动移除"+e.getPlayer().getName()+"的白名单!");
						}else {
							GroupUtil.sendMsgToAllTrueConfigGroup("玩家"+e.getPlayer().getName()+"并没有加群,但是他有服务器白名单,将自动移除"+e.getPlayer()+"的白名单!");
						}
					}
				}

				//				}
			}catch(Throwable e2) {
				new BukkitRunnable() {

					@Override
					public void run() {
						if(player.isOnline()&&!this.isCancelled()) {
							player.kickPlayer("出现了内部错误: \r\n"+BotListener.throwableToString(e2));
							e2.printStackTrace();
						}
						else {
							this.cancel();
						}
					}
				}.runTaskTimer(BukkitWhitelistBot.instance, 0, 1);
			}
		}

	}
	@EventHandler
	public void onPlayerLeaveGame(PlayerQuitEvent e) {
		if(BukkitWhitelistBot.instance.config.getConfig().joinServerOrLeaveServerMsgSendToGroup) {
			GroupUtil.sendMsgToGroupAotu(e.getPlayer().getName()+"退出了服务器!");
		}
		//		Vector<ConfirmGUI> ve = new Vector<>();
		//		for(ConfirmGUI gui : ) {
		//			if(gui.p.equals(e.getPlayer())) {
		//				ve.add(gui);
		//				break;
		//			}
		//		}
		//		
		//		BukkitWhitelistBot.instance.confirmGUIs.remove(invaildgui);
		if(BukkitWhitelistBot.instance.config.getConfig().isAddWhitelistConfirm) {
			BukkitWhitelistBot.instance.confirmGUIs.removeIf(i->i.p.equals(e.getPlayer()));
			INoThrowsRunnable.invoke(BukkitWhitelistBot.instance.storage::saveConfig);
		}

	}
	@EventHandler
	public void onPlayerSendMsg(PlayerChatEvent e) {

		if(BukkitWhitelistBot.instance.config.getConfig().serverMsgSendToGroup&&!e.getMessage().startsWith(".")) {
			if(BukkitWhitelistBot.instance.config.getConfig().allGroupUse) {
				GroupUtil.sendMsgToAllGroup("["+e.getPlayer().getWorld().getName()+"]"+e.getPlayer().getName()+": "+e.getMessage());
			}else {
				GroupUtil.sendMsgToAllTrueConfigGroup("["+e.getPlayer().getWorld().getName()+"]"+e.getPlayer().getName()+": "+e.getMessage());
			}
		}
	}

}
