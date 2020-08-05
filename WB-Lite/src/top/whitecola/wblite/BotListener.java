package top.whitecola.wblite;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.events.MemberJoinEvent;
import net.mamoe.mirai.event.events.MemberLeaveEvent;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.MessageEvent;
import net.mamoe.mirai.message.data.At;
import top.dsbbs2.whitelist.WhiteListPlugin;
import top.dsbbs2.whitelist.config.struct.WhiteListConfig.WLPlayer;
import top.dsbbs2.whitelist.util.CommandUtil;
import top.dsbbs2.whitelist.util.PlayerUtil;
import top.dsbbs2.whitelist.util.ServerUtil;

public final class BotListener {
	public static boolean containsLong(long[] arr,long obj)
	{
		for(long i : arr)
			if(i==obj)
				return true;
		return false;
	}
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMessage(MessageEvent e) {
		String msg = e.getMessage().contentToString();
		if (e instanceof GroupMessageEvent) {
			GroupMessageEvent ge = (GroupMessageEvent) e;
			if (ge.getSender().getId() != WBLite.instance.bot.getId()) {
				if(msg.startsWith(".���������") || msg.startsWith("�����������")) {
					String[] temp = msg.split(" ");
					if (temp.length >= 2) {
						if (PlayerUtil.isInWhiteList(temp[1])) {

							WLPlayer wlp = PlayerUtil.getWLPlayerByName(temp[1]);
							ge.getGroup().sendMessage(new At(ge.getSender()).plus("��ID�ѱ�" + wlp.QQ + "�� !"));
							return;
						}
						
						if (!WBLite.instance.config.getConfig().allowAddingWhitelistBeforeChangingNameInGroup && !ge.getSender().getNameCard().toLowerCase(Locale.ENGLISH).contains(temp[1].toLowerCase(Locale.ENGLISH))) {
							ge.getGroup()
							.sendMessage(new At(ge.getSender()).plus("����Ҫ�����������֮ǰ,������Ⱥ��Ƭ�ϼ�������id��"));
							return;
						}
						String name = temp[1];
						Bukkit.getScheduler().runTask(WBLite.instance, ()->CommandUtil.getCommand(WhiteListPlugin.instance.childCmds, "add").onCommand(
								new QQCommandSender(e.getSender(), ge.getGroup().getId()),
								WhiteListPlugin.instance.getCommand("wl"), "wl",
								new String[] { "add",
										name,
										""+ge.getSender().getId() }));
						
						return;
					}


				}else if(msg.startsWith(".ִ��") || msg.startsWith("��ִ��")) {

					if (WBLite.instance.config.getConfig().canGroupMsgExcuteCommand ) {
						if(!containsLong(WBLite.instance.config.getConfig().groupCommandOperator, ge.getSender().getId())) {
							ge.getGroup().sendMessage(new At(ge.getSender()).plus("��û��Ȩ��!"));
							return;
						}
						if (msg.length() <= 4 || !msg.contains(" ")) {
							((GroupMessageEvent) e).getGroup().sendMessage("��ȷ�÷�:.(��)ִ�� <����> (ע��ո�)");
							return;
						}

						if(containsLong(WBLite.instance.config.getConfig().limitedOperators, ge.getSender().getId())) {
							if (!msg.split(" ")[1].equals("wl")
									&& !msg.split(" ")[1].equalsIgnoreCase("whitelist:wl")) {
								((GroupMessageEvent) e).getGroup()
								.sendMessage(new At(((GroupMessageEvent) e).getSender())
										.plus("��������������Զ��Ȩ���ѱ�����,��ֻ����ʹ�ð��������(wl)������!"));
								return;
							}
						}

						Bukkit.getScheduler()
						.runTask(WBLite.instance,
								() -> Bukkit.dispatchCommand(
										new QQCommandSender(e.getSender(),
												((GroupMessageEvent) e).getGroup().getId()),
										msg.substring(4)));
						return;
					} else {
						ge.getGroup().sendMessage(new At(ge.getSender()).plus("Զ��ִ�������û�п���."));
					}
				}else if(msg.startsWith(".״̬") || msg.startsWith("��״̬")){
					


//						ServerListPing17 p = new ServerListPing17();
						try {
//							p.setAddress(new InetSocketAddress(
//									InetAddress.getByName(
//											WBLite.instance.config.getConfig().serverIp),
//									WBLite.instance.config.getConfig().serverPort));
							//StatusResponse re = p.fetchData();
							//Message temp = re.getFavicon() != null
							//		? ge.getGroup()
							//				.uploadImage(new ByteArrayInputStream(Base64.getDecoder().decode(
							//						re.getFavicon().substring("data:image/png;base64,".length()))))
							//				: new MessageChainBuilder().build();
											StringBuilder bu = new StringBuilder(/*"\n"*/);
											//bu.append("��������: " + Bukkit.getServerName());
											//bu.append("\n");
											bu.append("�������� : "+ WBLite.instance.config.getConfig().serverName);
											bu.append("\n");
											bu.append("��������ַ: " + WBLite.instance.config.getConfig().serverIp);
											bu.append("\n");
											bu.append("�������˿�: " + WBLite.instance.config.getConfig().serverPort);
											bu.append("\n");
											//bu.append("������Motd: " + re.getDescription().getText());
											//bu.append("\n");
											
											bu.append("�������汾: " + Bukkit.getVersion());
											bu.append("\n");
										    //bu.append("������Э��汾��: " + re.getVersion().getProtocol());
											//bu.append("\n");
											//bu.append("�ӳ�: " + re.getTime() + "ms");
											//bu.append("\n");
											bu.append("��������ǰtps: " + Bukkit.getTPS()[0]);
											bu.append("\n");
											bu.append("����: " + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());
											bu.append("\n");
											bu.append(
													 "��������б�: " + Arrays.toString(Bukkit.getOnlinePlayers().stream()
															.map(i -> i.getName() + "[" + Optional
																	.ofNullable(ServerUtil.isOnlineStorageMode()
																			? PlayerUtil.getWLPlayerByUUID(
																					i.getUniqueId())
																					: PlayerUtil.getWLPlayerByName(i.getName()))
															.map(i2 -> i2.QQ + "").orElse("δ��") + "]")
															.toArray(String[]::new)).replace(",", ",\n")
															);
											bu.append("\n");
											bu.append("���������б�: "+Arrays.toString(Bukkit.getWorlds().parallelStream().map(World::getName).toArray(String[]::new)));
											bu.append("\n");
											bu.append("�������İ�����ϵͳ�� ���õİ��������(wl) �����ػ� ");
											ge.getGroup().sendMessage(/*temp.plus(*/bu.toString()/*)*/);
						} catch (Throwable exc) {
							ge.getGroup().sendMessage("�ڻ�ȡ������������Ϣ�Ĺ����г�����δԤ�ϵ����쳣 ");
							System.out.println("��e���Ƿ�û�����÷�������ַ? ע��:�ⲻ��bug,������!");
							ge.getGroup().sendMessage("�Ƿ�û�����÷�������ַ?");
							exc.printStackTrace();
							System.out.println("��e���Ƿ�û�����÷�������ַ? ע��:�ⲻ��bug,������!");
						}
					

				}else if(msg.startsWith(".��֤") || msg.startsWith("����֤")) {
					if(ServerUtil.isOnlineStorageMode()) {

						CommandUtil.getCommand(WhiteListPlugin.instance.childCmds, "confirm").onCommand(
								new QQCommandSender(e.getSender(), ge.getGroup().getId()),
								WhiteListPlugin.instance.getCommand("wl"), "wl",
								new String[] { "confirm",""+ge.getSender().getId() });

					}else {
						ge.getGroup().sendMessage(new At(ge.getSender()).plus("������û�п�������UUIDģʽ , ������֤."));
					}
				}else if(msg.startsWith(".��ѯ") || msg.startsWith("����ѯ")|| msg.startsWith(".whois") || msg.startsWith("��whois")) {
					String[] temp = msg.split(" ");
					if (temp.length == 2) {
						if(temp[1].equalsIgnoreCase("white_cola")||temp[1].equalsIgnoreCase("lvxinlei")||temp[1].equalsIgnoreCase("Chloe__owo")) {
							ge.getGroup().sendMessage("����ѯ��ID"+temp[1]+"ΪWL(���������)�Ŀ����ߵ�����ID!");
						}
						WLPlayer wlp = PlayerUtil.getWLPlayerByName(temp[1]);
						if(wlp==null) {
							ge.getGroup().sendMessage(new At(ge.getSender()).plus("û���ڰ����������в�ѯ������,��(��)�Ƿ���û��?"));
							return;
						}
						if(wlp.QQ==-1) {
							ge.getGroup().sendMessage(new At(ge.getSender()).plus("ȷʵ�ڰ����������в鵽����,����û�в鵽��ӦQQ.(����Ӹó�Ա������ʱ,��û�����QQ)"));
							return;
						}
						ge.getGroup().sendMessage(new At(ge.getSender()).plus("�����Ϊ"+wlp.QQ));
						ge.getGroup().sendMessage(new At(ge.getGroup().get(wlp.QQ)));
						StringBuilder bu = new StringBuilder("\n");
						if(ServerUtil.isOnlineStorageMode()&&wlp.uuid!=null) {
							bu.append("���UUID: "+wlp.uuid+" \n");
						}
						if(ge.getSender().getPermission()==MemberPermission.ADMINISTRATOR || ge.getSender().getPermission()==MemberPermission.OWNER) {
							OfflinePlayer op = wlp.toOfflinePlayer();
							if(op==null) {
								return;
							}
							boolean o=op.isOnline();
								bu.append("���״̬ : "+(o?"����":"����")+"\n");
							if(o)
							{
								bu.append("��Ҿ���ֵ: "+op.getPlayer().getExp()+"["+op.getPlayer().getLevel()+"]"+"\n");
								bu.append("�����������: "+op.getPlayer().getWorld().getName()+"\n");
							}else {
								long temp2=op.getLastPlayed();
								bu.append("�ϴ�����ʱ�� :"+LocalDateTime.ofInstant(Instant.ofEpochMilli(temp2), ZoneOffset.systemDefault().getRules().getOffset(LocalDateTime.now())).format(new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd kk:mm:ss").toFormatter()));
							}
							ge.getGroup().sendMessage(bu.toString());
						}else {
							ge.getGroup().sendMessage(bu.toString());
							ge.getGroup().sendMessage(new At(ge.getSender()).plus("�����㲢���ǹ���Ա,����ֻ�ܸ�����Щ��Ϣ."));
						}
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
				
				Bukkit.getScheduler().runTask(WBLite.instance, ()->{
					CommandUtil.getCommand(WhiteListPlugin.instance.childCmds, "qban").onCommand(
							new QQCommandSender(e.getGroup().get(e.getBot().getId()), e.getGroup().getId()),
							WhiteListPlugin.instance.getCommand("wl"), "wl",
							new String[] { "qban", e.getMember().getId() + "" });
				});
				

			} else {
				if (!WBLite.instance.config.getConfig().quitGroupAutoBan) {
					e.getGroup().sendMessage("���" + PlayerUtil.getWLPlayerByQQ(e.getMember().getId()).name + "["
							+ PlayerUtil.getWLPlayerByQQ(e.getMember().getId()).QQ + "]" + "�Ѿ���Ⱥ,��ɾ�����İ�����!");
					Bukkit.getScheduler().runTask(WBLite.instance, ()->{
						CommandUtil.getCommand(WhiteListPlugin.instance.childCmds, "qremove").onCommand(
								new QQCommandSender(e.getGroup().get(e.getBot().getId()), e.getGroup().getId()),
								WhiteListPlugin.instance.getCommand("wl"), "wl",
								new String[] { "qremove", e.getMember().getId() + "" });
					});
					
					

				} else {
					e.getGroup().sendMessage("���"
							+ PlayerUtil.getWLPlayerByQQ(e.getMember().getId()).toOfflinePlayer().getName() + "["
							+ PlayerUtil.getWLPlayerByQQ(e.getMember().getId()).QQ + "]" + "�Ѿ���Ⱥ,��ɾ�����İ�����(�ѿ�����Ⱥ���)!");
					Bukkit.getScheduler().runTask(WBLite.instance, ()->{
						CommandUtil.getCommand(WhiteListPlugin.instance.childCmds, "qban").onCommand(
								new QQCommandSender(e.getGroup().get(e.getBot().getId()), e.getGroup().getId()),
								WhiteListPlugin.instance.getCommand("wl"), "wl",
								new String[] { "qban", e.getMember().getId() + "" });
					});
					

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
		
		if (WBLite.instance.config.getConfig().joinGroupWelcoming) {
			Optional.ofNullable(WBLite.instance.config.getConfig().joinGroupGreetings).ifPresent(i -> {
				StringBuilder bu = new StringBuilder();
				for (String content : i) {
					bu.append(content + "\n");
				}
				e.getGroup().sendMessage(bu.substring(0, bu.length() - 1));
				if(e.getMember().getId()==1302399643L||e.getMember().getId()==535481388L||e.getMember().getId()==3213824019L) {
					e.getGroup().sendMessage("WL����ʵ�: ��ӭWL(���������)�����߼����Ⱥ!");
					e.getGroup().sendMessage(new At(e.getMember()).plus("���,WL������! [WL����ʵ�]"));
				}
				if (WBLite.instance.config.getConfig().canGroupMsgExcuteCommand) {
					Optional.ofNullable(WBLite.instance.config.getConfig().groupCommandOperator)
					.ifPresent(i2 -> {
						if (i2.length > 0) {
							StringBuilder bu2 = new StringBuilder();
							bu2.append("�����������������,����" + Arrays.toString(i2) + "\n");
							bu2.append("������Զ�̻����˹����Ȩ��!"+"\n");
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
