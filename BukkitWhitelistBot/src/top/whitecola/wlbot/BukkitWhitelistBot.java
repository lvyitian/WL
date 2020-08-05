package top.whitecola.wlbot;

import java.io.File;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactoryJvm;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.utils.BotConfiguration;
import top.dsbbs2.common.config.IConfig;
import top.dsbbs2.common.config.SimpleConfig;
import top.dsbbs2.common.lambda.INoThrowsRunnable;
import top.whitecola.wlbot.command.CommandRegistry;
import top.whitecola.wlbot.config.struct.Config;
import top.whitecola.wlbot.config.struct.Storage;
import top.whitecola.wlbot.gui.ConfirmGUI;
import top.whitecola.wlbot.listener.PlayerListener;
import top.whitecola.wlbot.util.GroupUtil;

public class BukkitWhitelistBot extends JavaPlugin{
	{
		instance=this;
		CommandRegistry.regComWithCompleter(getName(), CommandRegistry.newPluginCommand("wlbot", this));
	}
	public Vector<ConfirmGUI> confirmGUIs = new Vector<ConfirmGUI>(); 
	public Vector<String> waitForPlayer = new Vector<String>();
	
	public Bot bot;
	public IConfig<Config> config=new SimpleConfig<Config>(getDataFolder().getAbsolutePath().toString()+File.separator+"config.json","UTF8",Config.class) {{
		
		INoThrowsRunnable.invoke(this::loadConfig);
		
	}};
	public IConfig<Storage> storage=new SimpleConfig<Storage>(getDataFolder().getAbsolutePath().toString()+File.separator+"storage.json","UTF8",Storage.class) {{
		INoThrowsRunnable.invoke(this::loadConfig);
	}};
	public static BukkitWhitelistBot instance;
	public ConcurrentMap<Member,Tuple<String,Long>> confirm=new ConcurrentHashMap<>();
	public Vector<Group> getGroupFromUseBotGroupIds(){
		Vector<Group> gr = new Vector<Group>();
		if(!config.getConfig().allGroupUse) {
			for(long id : config.getConfig().useBotGroup) {
				try {
				gr.add(this.bot.getGroup(id));
				}catch (Throwable e) {
					
				}
			}
			return gr;
		}

		return null;
	}
	@Override
	public void onEnable() {
		if(config.getConfig().password==null||config.getConfig().password.isEmpty())
		{
			getLogger().severe("������д"+((SimpleConfig<?>)this.config).conf.getAbsolutePath().toString()+"�е�qq(�˺�)��password(����)�������ر����");
			this.setEnabled(false);
			return;
		}
		bot=BotFactoryJvm.newBot(this.config.getConfig().qq, this.config.getConfig().password,new BotConfiguration() {{
			fileBasedDeviceInfo("devicenfo.json");
			if(!config.getConfig().robotShowGebugMsg) {
				noNetworkLog();
			}
			if(!config.getConfig().robotShowLogMsg) {
				noBotLog();
			}
			//			
			//			
		}});
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		EventUtils.registerEvents(new BotListener());
		bot.login();

		if(!this.config.getConfig().allGroupUse) {
			
			if(this.config.getConfig().useBotGroup.length==0) {
				Bukkit.getLogger().warning("���ر�������Ⱥʹ�û�����ѡ��,���ǲ������,����û�и����κ�ָ��Ⱥ,��ô���Զ���Ϊ����Ⱥʹ�û�����! ��������,�뵽�����ѯ��!");
				config.getConfig().allGroupUse = true;
			}
			if(!GroupUtil.isBotInAllConfigGroup()) {
				Bukkit.getLogger().warning("���ر�������Ⱥʹ�û�����ѡ��,���ǲ��������������ʹ��Ⱥ,������ȫ��Ⱥ,�����˶�������!��ô�������������˲��ڵ�Ⱥ.����ʹ�õ�ʱ��,�벻Ҫ�û�������Ⱥ!");
				if(GroupUtil.getAllTrueBotInConfigGroup()!=null) {
					}else {
					Bukkit.getLogger().warning("�������,������������Ⱥ,�����˶���������,������ʹ������Ⱥѡ��!");
					this.config.getConfig().allGroupUse = true;
				}
			}
		}
		//���confirm
		new Thread(()-> {
			while(this.bot!=null&&this.isEnabled()) {
				List<Member> toBeDeleted=new Vector<>();
				for(Entry<Member,Tuple<String,Long>> i : this.confirm.entrySet()) {
					if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()-i.getValue().b)>20) {
						i.getKey().getGroup().sendMessage(new At(i.getKey()).plus("������û����20����ȷ�����������,ȷ���ѹ���,����������!"));
						toBeDeleted.add(i.getKey());
						// i.getKey().getGroup().sendMessage(i.getKey().getGroup().uploadImage(image));
					}
				}
				for(Member i : toBeDeleted)
					this.confirm.remove(i);
				toBeDeleted.clear();

				if(BukkitWhitelistBot.instance.config.getConfig().isAddWhitelistConfirm) {
//					for(RequestPlayer rp : BukkitWhitelistBot.instance.storage.getConfig().confrimAddWhitelistRequest) {
//						if(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()-
//								rp.currentTimeMillis)>=90) {
//
//							bot.getGroup(rp.group).sendMessage(new At(rp.sender).plus("������3������,����û�н�������ȷ��,������������Ѿ�ʧЧ.����������!"));
//						}
//					}
//					BukkitWhitelistBot.instance.storage.getConfig().confrimAddWhitelistRequest.removeIf(i->TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()-i.currentTimeMillis)>=90);
//					try {
//						BukkitWhitelistBot.instance.storage.saveConfig();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
					
					
					
					
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}


			}

		}) {{
			this.setDaemon(true);
			this.start();
		}};
	}

	@Override
	public void onDisable() {
		Optional.ofNullable(this.bot).ifPresent(i->i.close(new Throwable()));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		return super.onCommand(sender, command, label, args);
	}

	//	@Override
	//	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
	//		if(args.length>0)
	//			return TabUtil.betterGetStartsWithList(realOnTabComplete(sender,command,alias,args),args[args.length-1]);
	//		else
	//			return realOnTabComplete(sender,command,alias,args);
	//	}
	//	public List<String> realOnTabComplete(CommandSender sender, Command command, String alias, String[] args) {
	//		if(args.length<=1)
	//			return VectorUtil.toArrayList(CommandUtil.commandListToCommandNameList(childCmds));
	//		if(args.length>1)
	//		{
	//			IChildCommand c=CommandUtil.getCommand(childCmds, args[0]);
	//			if(c!=null)
	//			{
	//				Vector<Class<?>> cats=c.getArgumentsTypes();
	//				if(cats.size()>args.length-2)
	//				{
	//					Class<?> argType=cats.get(args.length-2);
	//					Vector<String> des=c.getArgumentsDescriptions();
	//					String desc=null;
	//					if(des.size()>args.length-2)
	//						desc=des.get(args.length-2);
	//					if(desc==null)
	//					{
	//						return ListUtil.toList(argType.getSimpleName());
	//					}else if(desc.equals("player"))
	//					{
	//						return PlayerUtil.getOfflinePlayersNameList();
	//					}else if(desc.equals("unwhitelisted_player"))
	//					{
	//						return PlayerUtil.getUnwhitelistedOfflinePlayersNameList();
	//					}else if(desc.equals("whitelisted_player")){
	//						return PlayerUtil.whiteListPlayerListToNameList(this.whitelist.con.players);
	//					}else if(desc.equals("qq")){
	//						return VectorUtil.toArrayList(VectorUtil.toStringVector(PlayerUtil.getQQList()));
	//					}else if(desc.equals("noname_player")){
	//						return VectorUtil.toArrayList(PlayerUtil.getNoNameWhiteListPlayerUUIDString());
	//					}else if(desc.contains("/")){
	//						return ListUtil.toList(desc.split("/"));
	//					}else{
	//						return ListUtil.toList(desc);
	//					}
	//				}
	//			}
	//		}
	//		return new ArrayList<>();
	//	}
}
