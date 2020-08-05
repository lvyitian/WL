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
					if (msg.startsWith(".���������") || msg.startsWith("�����������")) {
						String[] temp = msg.split(" ");
						if (temp.length >= 2) {
							if (PlayerUtil.isInWhiteList(temp[1])) {
								WLPlayer wlp = PlayerUtil.getWLPlayerByName(temp[1]);
								ge.getGroup().sendMessage(new At(ge.getSender()).plus("��ID�ѱ�" + wlp.QQ + "�� !"));
								return;
							}
							if (!BukkitWhitelistBot.instance.config
									.getConfig().allowAddingWhitelistBeforeChangingNameInGroup) {
								if (!ge.getSender().getNameCard().contains(temp[1])) {
									ge.getGroup()
									.sendMessage(new At(ge.getSender()).plus("����Ҫ�����������֮ǰ,������Ⱥ��Ƭ�ϼ�������id��"));
									return;
								}
							}
							// if(!BukkitWhitelistBot.instance.confirm.containsKey(ge.getSender().getId()))
							// {
							BukkitWhitelistBot.instance.confirm.put(ge.getSender(),
									new Tuple<>(temp[1], System.currentTimeMillis()));
							ge.getGroup().sendMessage(new At(ge.getSender())
									.plus("��ȷ�����minecraft�û���Ϊ " + temp[1] + " ��? �����,����20��������.ȷ�� ������������"));
							// }else {

							// }

							/*
							 * try { CommandUtil.getCommand(WhiteListPlugin.instance.childCmds,
							 * "add").onCommand(new QQCommandSender(e.getSender(),ge.getGroup().getId()),
							 * WhiteListPlugin.instance.getCommand("wl"), "wl", new String[]
							 * {"add",temp[1],""+e.getSender().getId()}); }catch(Throwable exc) {
							 * e.getSender().sendMessage(String.format("ִ�������ڼ䷢�����ڲ�����: %n%s"
							 * ,throwableToString(exc))); } }
							 */
							return;
						} else {
							ge.getGroup().sendMessage(new At(ge.getSender()).plus("������ȷ�ĸ�ʽ��������� ��Ϊ��ʾ��(ʾ��������Ч):"));
							ge.getGroup().sendMessage("��ȷ��ʽ  '.��������� <id>' ");
							return;
						}
					} else if (msg.startsWith(".ִ��") || msg.startsWith("��ִ��")) {
						if (msg.length() <= 4 || !msg.contains(" ")) {
							((GroupMessageEvent) e).getGroup().sendMessage("��ȷ�÷�:.(��)ִ�� <����> (ע��ո�)");
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
											.plus("��������������Զ��Ȩ���ѱ�����,��ֻ����ʹ�ð��������(wl)������!"));
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
								.sendMessage(new At(((GroupMessageEvent) e).getSender()).plus("�㲻��Զ���������Ա!"));
								return;
							} else {
								((GroupMessageEvent) e).getGroup()
								.sendMessage(new At(((GroupMessageEvent) e).getSender()).plus("Զ�������δ����!"));
								return;

							}
						}
					} else if (msg.startsWith(".��֤") || msg.startsWith("����֤")) {

						if (!ServerUtil.isOnlineStorageMode()) {
							ge.getGroup().sendMessage("��Offlineģʽ�����²���ʹ����֤");
							ge.getGroup().sendMessage("���� \".��һ���˽� ����ģʽ\" (.�͡�ͨ��)���˽������(wl)�Ĵ���ģʽ!");
							return;
						}
						CommandUtil.getCommand(WhiteListPlugin.instance.childCmds, "confrim").onCommand(
								new QQCommandSender(e.getSender(), ge.getGroup().getId()),
								WhiteListPlugin.instance.getCommand("wl"), "wl",
								new String[] { "confrim", ge.getSender().getId() + "" });
						return;
					} else if (msg.startsWith(".ȷ��") || msg.startsWith("��ȷ��") || msg.startsWith("��ȷ��")
							|| msg.startsWith(".ȷ��")) {

						if (BukkitWhitelistBot.instance.config.getConfig().isAddWhitelistConfirm) {
							if (!BukkitWhitelistBot.instance.confirm.containsKey(ge.getSender())) {
								ge.getGroup().sendMessage(new At(ge.getSender()).plus("û�д������ȷ��"));
								return;
							}

							if(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()
									- BukkitWhitelistBot.instance.confirm.get(ge.getSender()).b) <= 20) {
								ge.getGroup().sendMessage(new At(ge.getSender()).plus("ȷ�����,����5�����ڼ����������������ȷ��!"));
								if(BukkitWhitelistBot.instance.confirm.get(ge.getSender())!=null) {
									Player p = Bukkit.getPlayer(BukkitWhitelistBot.instance.confirm.get(ge.getSender()).a);
									if(p!=null&&p.isOnline()) {
										ConfirmGUI confirmgui = new ConfirmGUI(p,new RequestPlayer(ge.getSender(), ge.getGroup(), System.currentTimeMillis()));
										confirmgui.open();
										BukkitWhitelistBot.instance.confirmGUIs.add(confirmgui);
										p.sendMessage("��ѡ��");
									}
								}
							}else {
								ge.getGroup().sendMessage(new At(ge.getSender()).plus("ȷ���ѹ���,����������! .��������� <id>"));
							}
							//			
							
							Bukkit.getScheduler().runTask(BukkitWhitelistBot.instance,
									() -> BukkitWhitelistBot.instance.confirm.remove(e.getSender()));
							return;
							
						} else {

							if (!BukkitWhitelistBot.instance.confirm.containsKey(ge.getSender())) {
								ge.getGroup().sendMessage(new At(ge.getSender()).plus("û�д������ȷ��"));
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
												"ִ�������ڼ䷢�����ڲ�����: %n%s",
												exc.getMessage())));
										exc.printStackTrace();
									}

								} else {
									ge.getGroup().sendMessage(new At(ge.getSender()).plus("ȷ���ѹ���,����������! .��������� <id>"));
								}
								Bukkit.getScheduler().runTask(BukkitWhitelistBot.instance,
										() -> BukkitWhitelistBot.instance.confirm.remove(e.getSender()));
								return;
							}

						}
					} else if (msg.equals("���������") || (e.getMessage().get(0) instanceof At
							&& e.getMessage().get(0)
							.equals(new At(((GroupMessageEvent) e).getGroup().get(e.getBot().getId())))
							&& msg.contains("���������"))) {
						ge.getGroup().sendMessage(new At(ge.getSender()));
						ge.getGroup().sendMessage("��ȷ��ʽ: .��������� <id>(.��)");
						return;
					} else if (msg.startsWith(".��һ���˽�") || msg.startsWith("����һ���˽�")) {
						return;
					} else if (msg.startsWith(".����") || msg.startsWith("������")) {
						return;
					} else if (msg.startsWith(".״̬") || msg.startsWith("��״̬")) {
						// ������״̬
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
												bu.append("��������: " + Bukkit.getServerName());
												bu.append("\n");
												bu.append("��������ַ: " + BukkitWhitelistBot.instance.config.getConfig().serverAddress);
												bu.append("\n");
												bu.append("�������˿�: " + BukkitWhitelistBot.instance.config.getConfig().serverPort);
												bu.append("\n");
												bu.append("������Motd: " + re.getDescription().getText());
												bu.append("\n");
												bu.append("�������汾: " + re.getVersion().getName());
												bu.append("\n");
												bu.append("������Э��汾��: " + re.getVersion().getProtocol());
												bu.append("\n");
												bu.append("�ӳ�: " + re.getTime() + "ms");
												bu.append("\n");
												bu.append("��������ǰtps: " + Bukkit.getTPS()[0]);
												bu.append("\n");
												bu.append("����: " + re.getPlayers().getOnline() + "/" + re.getPlayers().getMax());
												bu.append("\n");
												bu.append(
														re.getPlayers() != null && re.getPlayers().getSample() != null
														? "��������б�: " + Arrays.toString(re.getPlayers().getSample().stream()
																.map(i -> i.getName() + "[" + Optional
																		.ofNullable(ServerUtil.isOnlineStorageMode()
																				? PlayerUtil.getWLPlayerByUUID(
																						UUID.fromString(i.getId()))
																						: PlayerUtil.getWLPlayerByName(i.getName()))
																.map(i2 -> i2.QQ + "").orElse("δ��") + "]")
																.toArray(String[]::new)).replace(",", ",\n")
																: "");
												bu.append("\n");
												bu.append("���� .��һ���˽� ״̬���� �˽�˹���");
												bu.append("\n");
												bu.append("�������İ�����ϵͳ��(wl)��������ػ�, ���� .��һ���˽� wl �˽����");
												ge.getGroup().sendMessage(temp.plus(bu.toString()));
							} catch (Throwable exc) {
								ge.getGroup().sendMessage("�ڻ�ȡ������������Ϣ�Ĺ����г�����δԤ�ϵ����쳣 ���� .��һ���˽� �����쳣 �˽����");
								ge.getGroup().sendMessage(throwableToString(exc));
							}
						} else {
							ge.getGroup().sendMessage("δ����������״̬��ѯ,���� .��һ���˽� ״̬���� �˽�˹���");
						}
						return;
						
						
					}
					if (BukkitWhitelistBot.instance.config.getConfig().groupMsgSendToServer
							&& !ge.getMessage().toString().startsWith(".")
							&& !ge.getMessage().toString().startsWith("��")) {
						Bukkit.getServer().broadcastMessage("[&][��b��l" + ge.getGroup().getName() + "��r.��l"
								+ ge.getSenderName() + "��r]: " + ge.getMessage().contentToString());
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
				e.getGroup().sendMessage("���" + PlayerUtil.getWLPlayerByQQ(e.getMember().getId()).name + "["
						+ PlayerUtil.getWLPlayerByQQ(e.getMember().getId()).QQ + "]" + "�Ѿ���Ⱥ,��ɾ�����İ�����,�����!");

				CommandUtil.getCommand(WhiteListPlugin.instance.childCmds, "qban").onCommand(
						new QQCommandSender(e.getGroup().get(e.getBot().getId()), e.getGroup().getId()),
						WhiteListPlugin.instance.getCommand("wl"), "wl",
						new String[] { "qban", e.getMember().getId() + "" });

			} else {
				if (!BukkitWhitelistBot.instance.config.getConfig().quitGroupAutoBan) {
					e.getGroup().sendMessage("���" + PlayerUtil.getWLPlayerByQQ(e.getMember().getId()).name + "["
							+ PlayerUtil.getWLPlayerByQQ(e.getMember().getId()).QQ + "]" + "�Ѿ���Ⱥ,��ɾ�����İ�����!");

					CommandUtil.getCommand(WhiteListPlugin.instance.childCmds, "qremove").onCommand(
							new QQCommandSender(e.getGroup().get(e.getBot().getId()), e.getGroup().getId()),
							WhiteListPlugin.instance.getCommand("wl"), "wl",
							new String[] { "qremove", e.getMember().getId() + "" });

				} else {
					e.getGroup().sendMessage("���"
							+ PlayerUtil.getWLPlayerByQQ(e.getMember().getId()).toOfflinePlayer().getName() + "["
							+ PlayerUtil.getWLPlayerByQQ(e.getMember().getId()).QQ + "]" + "�Ѿ���Ⱥ,��ɾ�����İ�����(�ѿ�����Ⱥ���)!");

					CommandUtil.getCommand(WhiteListPlugin.instance.childCmds, "qban").onCommand(
							new QQCommandSender(e.getGroup().get(e.getBot().getId()), e.getGroup().getId()),
							WhiteListPlugin.instance.getCommand("wl"), "wl",
							new String[] { "qban", e.getMember().getId() + "" });

				}
			}
			
		} else {
			if (e instanceof MemberLeaveEvent.Kick) {
				e.getGroup().sendMessage(e.getMember().getId() + "�Ѿ��߳�Ⱥ,��������û�����������,���Բ������κβ���!");
				return;
			}
			e.getGroup().sendMessage(e.getMember().getId() + "�Ѿ���Ⱥ,��������û�����������,���Բ������κβ���!");
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
							bu2.append("�����������������,����" + Arrays.toString(i2) + "\n");
							bu2.append("������Զ�̻����˹����Ȩ��!");
							if (BukkitWhitelistBot.instance.config.getConfig().groupMsgSendToServer) {
								bu2.append("!Ⱥ�ڵ�������Ϣ���ᷢ����������(�ѿ����˹���),�������˵���Ļ�,���ڿ�ͷ�Ӹ�.(��)!");
							}
							e.getGroup().sendMessage(bu2.toString());
						}
					});
				} else {
					e.getGroup().sendMessage("�����������������,���ҹ���Ա!");
				}

			});

		}
	}

}
