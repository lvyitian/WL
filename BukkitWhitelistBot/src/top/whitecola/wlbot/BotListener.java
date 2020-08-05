package top.whitecola.wlbot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.justicepro.serverspy.ServerListPing17;
import me.justicepro.serverspy.ServerListPing17.StatusResponse;
import net.mamoe.mirai.event.events.MemberJoinEvent;
import net.mamoe.mirai.event.events.MemberLeaveEvent;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import top.dsbbs2.common.lambda.INoThrowsRunnable;
import top.dsbbs2.whitelist.WhiteListPlugin;
import top.dsbbs2.whitelist.config.struct.WhiteListConfig.WLPlayer;
import top.dsbbs2.whitelist.util.CommandUtil;
import top.dsbbs2.whitelist.util.PlayerUtil;
import top.dsbbs2.whitelist.util.ServerUtil;
import top.whitecola.wlbot.gui.ConfirmGUI;
import top.whitecola.wlbot.listener.PlayerListener;
import top.whitecola.wlbot.object.RequestPlayer;

public final class BotListener {
	public static String throwableToString(Throwable t) {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			try (PrintStream ps = new PrintStream(baos, true, "UTF8")) {
				t.printStackTrace(ps);
				return new String(baos.toByteArray(), "UTF8");
			}
		} catch (Throwable e) {
			if (e instanceof Error)
				throw (Error) e;
			else if (e instanceof RuntimeException)
				throw (RuntimeException) e;
			else
				throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMessage(MessageEvent e) {
		if (BukkitWhitelistBot.instance.config.getConfig().playerAddWhitelistByOneself) {
			String msg = e.getMessage().contentToString();
			// String senderName = e.getSenderName();
			if (e instanceof GroupMessageEvent) {
				GroupMessageEvent ge = (GroupMessageEvent) e;
				if (!BukkitWhitelistBot.instance.config.getConfig().allGroupUse
						&& !PlayerListener.containsLong(BukkitWhitelistBot.instance.config.getConfig().useBotGroup,ge.getGroup().getId())) {
					return;
				}
				if (ge.getSender().getId() != BukkitWhitelistBot.instance.bot.getId()) {
					if (msg.startsWith(".申请白名单") || msg.startsWith("。申请白名单")) {
						String[] temp = msg.split(" ");
						if (temp.length >= 2) {
							if (PlayerUtil.isInWhiteList(temp[1])) {
								WLPlayer wlp = PlayerUtil.getWLPlayerByName(temp[1]);
								ge.getGroup().sendMessage(new At(ge.getSender()).plus("该ID已被" + wlp.QQ + "绑定 !"));
								return;
							}
							if (!BukkitWhitelistBot.instance.config
									.getConfig().allowAddingWhitelistBeforeChangingNameInGroup) {
								if (!ge.getSender().getNameCard().contains(temp[1])) {
									ge.getGroup()
									.sendMessage(new At(ge.getSender()).plus("您需要在申请白名单之前,在您的群名片上加上您的id！"));
									return;
								}
							}
							// if(!BukkitWhitelistBot.instance.confirm.containsKey(ge.getSender().getId()))
							// {
							BukkitWhitelistBot.instance.confirm.put(ge.getSender(),
									new Tuple<>(temp[1], System.currentTimeMillis()));
							ge.getGroup().sendMessage(new At(ge.getSender())
									.plus("你确定你的minecraft用户名为 " + temp[1] + " 吗? 如果是,请在20秒内输入.确认 若不是请重新"));
							// }else {

							// }

							/*
							 * try { CommandUtil.getCommand(WhiteListPlugin.instance.childCmds,
							 * "add").onCommand(new QQCommandSender(e.getSender(),ge.getGroup().getId()),
							 * WhiteListPlugin.instance.getCommand("wl"), "wl", new String[]
							 * {"add",temp[1],""+e.getSender().getId()}); }catch(Throwable exc) {
							 * e.getSender().sendMessage(String.format("执行命令期间发生了内部错误: %n%s"
							 * ,throwableToString(exc))); } }
							 */
							return;
						} else {
							ge.getGroup().sendMessage(new At(ge.getSender()).plus("请以正确的格式申请白名单 将为您示范(示范内容无效):"));
							ge.getGroup().sendMessage("正确格式  '.申请白名单 <id>' ");
							return;
						}
					} else if (msg.startsWith(".执行") || msg.startsWith("。执行")) {
						if (msg.length() <= 4 || !msg.contains(" ")) {
							((GroupMessageEvent) e).getGroup().sendMessage("正确用法:.(。)执行 <命令> (注意空格)");
							return;
						}
						if (BukkitWhitelistBot.instance.config.getConfig().canGroupMsgExcuteCommand && PlayerListener
								.containsLong(BukkitWhitelistBot.instance.config.getConfig().groupMsgCommander,
										e.getSender().getId())) {
							if (PlayerListener.containsLong(
									BukkitWhitelistBot.instance.config.getConfig().limitedOperators,
									e.getSender().getId())) {
								if (!msg.split(" ")[1].equals("wl")
										&& !msg.split(" ")[1].equalsIgnoreCase("whitelist:wl")) {
									((GroupMessageEvent) e).getGroup()
									.sendMessage(new At(((GroupMessageEvent) e).getSender())
											.plus("不允许的命令，您的远程权限已被限制,您只可以使用白名单插件(wl)的命令!"));
									return;
								}
							}
							Bukkit.getScheduler()
							.runTask(BukkitWhitelistBot.instance,
									() -> Bukkit.dispatchCommand(
											new QQCommandSender(e.getSender(),
													((GroupMessageEvent) e).getGroup().getId()),
											msg.substring(4)));
							return;
						} else {
							if (BukkitWhitelistBot.instance.config.getConfig().canGroupMsgExcuteCommand) {
								((GroupMessageEvent) e).getGroup()
								.sendMessage(new At(((GroupMessageEvent) e).getSender()).plus("你不是远程命令操作员!"));
								return;
							} else {
								((GroupMessageEvent) e).getGroup()
								.sendMessage(new At(((GroupMessageEvent) e).getSender()).plus("远程命令功能未启用!"));
								return;

							}
						}
					} else if (msg.startsWith(".验证") || msg.startsWith("。验证")) {

						if (!ServerUtil.isOnlineStorageMode()) {
							ge.getGroup().sendMessage("在Offline模式储存下不能使用验证");
							ge.getGroup().sendMessage("输入 \".进一步了解 储存模式\" (.和。通用)来了解白名单(wl)的储存模式!");
							return;
						}
						CommandUtil.getCommand(WhiteListPlugin.instance.childCmds, "confrim").onCommand(
								new QQCommandSender(e.getSender(), ge.getGroup().getId()),
								WhiteListPlugin.instance.getCommand("wl"), "wl",
								new String[] { "confrim", ge.getSender().getId() + "" });
						return;
					} else if (msg.startsWith(".确认") || msg.startsWith("。确认") || msg.startsWith("。确定")
							|| msg.startsWith(".确定")) {

						if (BukkitWhitelistBot.instance.config.getConfig().isAddWhitelistConfirm) {
							if (!BukkitWhitelistBot.instance.confirm.containsKey(ge.getSender())) {
								ge.getGroup().sendMessage(new At(ge.getSender()).plus("没有待处理的确认"));
								return;
							}

							if(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()
									- BukkitWhitelistBot.instance.confirm.get(ge.getSender()).b) <= 20) {
								ge.getGroup().sendMessage(new At(ge.getSender()).plus("确认完成,请在5分钟内加入服务器进行最后的确认!"));
								if(BukkitWhitelistBot.instance.confirm.get(ge.getSender())!=null) {
									Player p = Bukkit.getPlayer(BukkitWhitelistBot.instance.confirm.get(ge.getSender()).a);
									if(p!=null&&p.isOnline()) {
										ConfirmGUI confirmgui = new ConfirmGUI(p,new RequestPlayer(ge.getSender(), ge.getGroup(), System.currentTimeMillis()));
										confirmgui.open();
										BukkitWhitelistBot.instance.confirmGUIs.add(confirmgui);
										p.sendMessage("请选择");
									}
								}
							}else {
								ge.getGroup().sendMessage(new At(ge.getSender()).plus("确认已过期,请重新申请! .申请白名单 <id>"));
							}
							//			
							
							Bukkit.getScheduler().runTask(BukkitWhitelistBot.instance,
									() -> BukkitWhitelistBot.instance.confirm.remove(e.getSender()));
							return;
							
						} else {

							if (!BukkitWhitelistBot.instance.confirm.containsKey(ge.getSender())) {
								ge.getGroup().sendMessage(new At(ge.getSender()).plus("没有待处理的确认"));
								return;
							} else {
								if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()
										- BukkitWhitelistBot.instance.confirm.get(ge.getSender()).b) <= 20) {

									try {
										CommandUtil.getCommand(WhiteListPlugin.instance.childCmds, "add").onCommand(
												new QQCommandSender(e.getSender(), ge.getGroup().getId()),
												WhiteListPlugin.instance.getCommand("wl"), "wl",
												new String[] { "add",
														BukkitWhitelistBot.instance.confirm.get(e.getSender()).a,
														new StringBuilder().append(e.getSender().getId()).toString() });
										if (!Bukkit.getOfflinePlayer(
												BukkitWhitelistBot.instance.confirm.get(e.getSender()).a)
												.isOnline()) {
											BukkitWhitelistBot.instance.storage.getConfig().offlinePlayerRecords
											.add(PlayerUtil.getWLPlayerByQQ(e.getSender().getId()));
											INoThrowsRunnable.invoke(BukkitWhitelistBot.instance.storage::saveConfig);
										} else {
											BukkitWhitelistBot.instance.config
											.getConfig().playerGotWhitelistThenExcuteCommands
											.stream()
											.forEach(i -> Bukkit.getScheduler().runTask(BukkitWhitelistBot.instance, ()->{
												Bukkit.dispatchCommand(
														Bukkit.getConsoleSender(),
														i.replace("${%player_name%}",
																(CharSequence) BukkitWhitelistBot.instance.confirm
																.get(e.getSender()).a));
											}));
											
										}
									} catch (Throwable exc) {
										ge.getGroup().sendMessage((Message) new At(ge.getSender()).plus(String.format(
												"执行命令期间发生了内部错误: %n%s",
												exc.getMessage())));
										exc.printStackTrace();
									}

								} else {
									ge.getGroup().sendMessage(new At(ge.getSender()).plus("确认已过期,请重新申请! .申请白名单 <id>"));
								}
								Bukkit.getScheduler().runTask(BukkitWhitelistBot.instance,
										() -> BukkitWhitelistBot.instance.confirm.remove(e.getSender()));
								return;
							}

						}
					} else if (msg.equals("申请白名单") || (e.getMessage().get(0) instanceof At
							&& e.getMessage().get(0)
							.equals(new At(((GroupMessageEvent) e).getGroup().get(e.getBot().getId())))
							&& msg.contains("申请白名单"))) {
						ge.getGroup().sendMessage(new At(ge.getSender()));
						ge.getGroup().sendMessage("正确格式: .申请白名单 <id>(.或。)");
						return;
					} else if (msg.startsWith(".进一步了解") || msg.startsWith("。进一步了解")) {
						return;
					} else if (msg.startsWith(".帮助") || msg.startsWith("。帮助")) {
						return;
					} else if (msg.startsWith(".状态") || msg.startsWith("。状态")) {
						// 服务器状态
						if (BukkitWhitelistBot.instance.config.getConfig().serverQuery) {
							ServerListPing17 p = new ServerListPing17();
							try {
								p.setAddress(new InetSocketAddress(
										InetAddress.getByName(
												BukkitWhitelistBot.instance.config.getConfig().serverAddress),
										BukkitWhitelistBot.instance.config.getConfig().serverPort));
								StatusResponse re = p.fetchData();
								Message temp = re.getFavicon() != null
										? ge.getGroup()
												.uploadImage(new ByteArrayInputStream(Base64.getDecoder().decode(
														re.getFavicon().substring("data:image/png;base64,".length()))))
												: new MessageChainBuilder().build();
												StringBuilder bu = new StringBuilder("\n");
												bu.append("服务器名: " + Bukkit.getServerName());
												bu.append("\n");
												bu.append("服务器地址: " + BukkitWhitelistBot.instance.config.getConfig().serverAddress);
												bu.append("\n");
												bu.append("服务器端口: " + BukkitWhitelistBot.instance.config.getConfig().serverPort);
												bu.append("\n");
												bu.append("服务器Motd: " + re.getDescription().getText());
												bu.append("\n");
												bu.append("服务器版本: " + re.getVersion().getName());
												bu.append("\n");
												bu.append("服务器协议版本号: " + re.getVersion().getProtocol());
												bu.append("\n");
												bu.append("延迟: " + re.getTime() + "ms");
												bu.append("\n");
												bu.append("服务器当前tps: " + Bukkit.getTPS()[0]);
												bu.append("\n");
												bu.append("人数: " + re.getPlayers().getOnline() + "/" + re.getPlayers().getMax());
												bu.append("\n");
												bu.append(
														re.getPlayers() != null && re.getPlayers().getSample() != null
														? "在线玩家列表: " + Arrays.toString(re.getPlayers().getSample().stream()
																.map(i -> i.getName() + "[" + Optional
																		.ofNullable(ServerUtil.isOnlineStorageMode()
																				? PlayerUtil.getWLPlayerByUUID(
																						UUID.fromString(i.getId()))
																						: PlayerUtil.getWLPlayerByName(i.getName()))
																.map(i2 -> i2.QQ + "").orElse("未绑定") + "]")
																.toArray(String[]::new)).replace(",", ",\n")
																: "");
												bu.append("\n");
												bu.append("输入 .进一步了解 状态功能 了解此功能");
												bu.append("\n");
												bu.append("服务器的白名单系统由(wl)插件进行守护, 输入 .进一步了解 wl 了解更多");
												ge.getGroup().sendMessage(temp.plus(bu.toString()));
							} catch (Throwable exc) {
								ge.getGroup().sendMessage("在获取服务器基本信息的过程中出现了未预料到的异常 输入 .进一步了解 常见异常 了解更多");
								ge.getGroup().sendMessage(throwableToString(exc));
							}
						} else {
							ge.getGroup().sendMessage("未开启服务器状态查询,输入 .进一步了解 状态功能 了解此功能");
						}
						return;
						
						
					}
					if (BukkitWhitelistBot.instance.config.getConfig().groupMsgSendToServer
							&& !ge.getMessage().toString().startsWith(".")
							&& !ge.getMessage().toString().startsWith("。")) {
						Bukkit.getServer().broadcastMessage("[&][§b§l" + ge.getGroup().getName() + "§r.§l"
								+ ge.getSenderName() + "§r]: " + ge.getMessage().contentToString());
						return;
					}
				}
			}
		}
	}

	@EventHandler
	public void onMemberLeaveGroup(MemberLeaveEvent e) {
		if (PlayerUtil.getWLPlayerByQQ(e.getMember().getId()) != null) {
			if (e instanceof MemberLeaveEvent.Kick) {
				e.getGroup().sendMessage("玩家" + PlayerUtil.getWLPlayerByQQ(e.getMember().getId()).name + "["
						+ PlayerUtil.getWLPlayerByQQ(e.getMember().getId()).QQ + "]" + "已经退群,将删除他的白名单,并封禁!");

				CommandUtil.getCommand(WhiteListPlugin.instance.childCmds, "qban").onCommand(
						new QQCommandSender(e.getGroup().get(e.getBot().getId()), e.getGroup().getId()),
						WhiteListPlugin.instance.getCommand("wl"), "wl",
						new String[] { "qban", e.getMember().getId() + "" });

			} else {
				if (!BukkitWhitelistBot.instance.config.getConfig().quitGroupAutoBan) {
					e.getGroup().sendMessage("玩家" + PlayerUtil.getWLPlayerByQQ(e.getMember().getId()).name + "["
							+ PlayerUtil.getWLPlayerByQQ(e.getMember().getId()).QQ + "]" + "已经退群,将删除他的白名单!");

					CommandUtil.getCommand(WhiteListPlugin.instance.childCmds, "qremove").onCommand(
							new QQCommandSender(e.getGroup().get(e.getBot().getId()), e.getGroup().getId()),
							WhiteListPlugin.instance.getCommand("wl"), "wl",
							new String[] { "qremove", e.getMember().getId() + "" });

				} else {
					e.getGroup().sendMessage("玩家"
							+ PlayerUtil.getWLPlayerByQQ(e.getMember().getId()).toOfflinePlayer().getName() + "["
							+ PlayerUtil.getWLPlayerByQQ(e.getMember().getId()).QQ + "]" + "已经退群,将删除他的白名单(已开启退群封禁)!");

					CommandUtil.getCommand(WhiteListPlugin.instance.childCmds, "qban").onCommand(
							new QQCommandSender(e.getGroup().get(e.getBot().getId()), e.getGroup().getId()),
							WhiteListPlugin.instance.getCommand("wl"), "wl",
							new String[] { "qban", e.getMember().getId() + "" });

				}
			}
			
		} else {
			if (e instanceof MemberLeaveEvent.Kick) {
				e.getGroup().sendMessage(e.getMember().getId() + "已经踢出群,由于他并没有申请白名单,所以不进行任何操作!");
				return;
			}
			e.getGroup().sendMessage(e.getMember().getId() + "已经退群,由于他并没有申请白名单,所以不进行任何操作!");
			return;
		}

	}

	@EventHandler
	public void onMemberJoinGroup(MemberJoinEvent e) {
		if (BukkitWhitelistBot.instance.config.getConfig().joinGroupWelcoming) {
			Optional.ofNullable(BukkitWhitelistBot.instance.config.getConfig().joinGroupGreetings).ifPresent(i -> {
				StringBuilder bu = new StringBuilder();
				for (String content : i) {
					bu.append(content + "\n");
				}
				e.getGroup().sendMessage(bu.substring(0, bu.length() - 1));

				if (BukkitWhitelistBot.instance.config.getConfig().canGroupMsgExcuteCommand) {
					Optional.ofNullable(BukkitWhitelistBot.instance.config.getConfig().groupMsgCommander)
					.ifPresent(i2 -> {
						if (i2.length > 0) {
							StringBuilder bu2 = new StringBuilder();
							bu2.append("如果遇到白名单问题,请找" + Arrays.toString(i2) + "\n");
							bu2.append("他们有远程机器人管理的权限!");
							if (BukkitWhitelistBot.instance.config.getConfig().groupMsgSendToServer) {
								bu2.append("!群内的聊天消息将会发到服务器里(已开启此功能),如果您想说悄悄话,请在开头加个.(。)!");
							}
							e.getGroup().sendMessage(bu2.toString());
						}
					});
				} else {
					e.getGroup().sendMessage("如果遇到白名单问题,请找管理员!");
				}

			});

		}
	}

}
