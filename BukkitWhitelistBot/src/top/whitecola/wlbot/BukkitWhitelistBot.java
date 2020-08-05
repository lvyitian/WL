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
			getLogger().severe("请先填写"+((SimpleConfig<?>)this.config).conf.getAbsolutePath().toString()+"中的qq(账号)和password(密码)！再重载本插件");
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
				Bukkit.getLogger().warning("您关闭了所有群使用机器人选项,但是插件发现,您并没有给出任何指定群,那么将自动改为所有群使用机器人! 如有疑问,请到插件帖询问!");
				config.getConfig().allGroupUse = true;
			}
			if(!GroupUtil.isBotInAllConfigGroup()) {
				Bukkit.getLogger().warning("您关闭了所有群使用机器人选项,但是插件发现您给出的使用群,并不是全部群,机器人都在里面!那么将会挑出机器人不在的群.您在使用的时候,请不要让机器人退群!");
				if(GroupUtil.getAllTrueBotInConfigGroup()!=null) {
					}else {
					Bukkit.getLogger().warning("插件发现,您给出的所有群,机器人都不在其中,将启动使用所有群选项!");
					this.config.getConfig().allGroupUse = true;
				}
			}
		}
		//检查confirm
		new Thread(()-> {
			while(this.bot!=null&&this.isEnabled()) {
				List<Member> toBeDeleted=new Vector<>();
				for(Entry<Member,Tuple<String,Long>> i : this.confirm.entrySet()) {
					if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()-i.getValue().b)>20) {
						i.getKey().getGroup().sendMessage(new At(i.getKey()).plus("由于您没有在20秒内确认申请白名单,确认已过期,请重新申请!"));
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
//							bot.getGroup(rp.group).sendMessage(new At(rp.sender).plus("由于在3分钟内,您并没有进服进行确认,你的申请请求已经失效.请重新申请!"));
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
