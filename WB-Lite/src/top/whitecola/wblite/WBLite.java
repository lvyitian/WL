package top.whitecola.wblite;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.WatchdogThread;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactoryJvm;
import net.mamoe.mirai.utils.BotConfiguration;
import top.dsbbs2.common.config.IConfig;
import top.dsbbs2.common.config.SimpleConfig;
import top.dsbbs2.common.file.FileUtils;
import top.dsbbs2.common.lambda.INoThrowsRunnable;
import top.dsbbs2.whitelist.commands.IChildCommand;
import top.dsbbs2.whitelist.util.CommandUtil;
import top.dsbbs2.whitelist.util.ListUtil;
import top.dsbbs2.whitelist.util.PlayerUtil;
import top.dsbbs2.whitelist.util.TabUtil;
import top.dsbbs2.whitelist.util.VectorUtil;
import top.whitecola.wblite.commands.Reload;
import top.whitecola.wblite.commands.SendMsg;
import top.whitecola.wblite.listener.PlayerListener;

public class WBLite extends JavaPlugin{
	public volatile Vector<IChildCommand> childCmds=new Vector<>();
	
	{
		initCommands();
		instance = this;
	}
	public IConfig<Config> config = new SimpleConfig<Config>(getDataFolder().getAbsolutePath().toString()+File.separator+"config.json","UTF8",Config.class) {{
		INoThrowsRunnable.invoke(this::loadConfig);
		if (!this.g.toJson(this.getConfig()).trim().equals(FileUtils.readTextFile(new File(getDataFolder().getAbsolutePath().toString()+File.separator+"config.json"), StandardCharsets.UTF_8).trim())) {
			FileUtils.writeTextFile(new File(getDataFolder().getAbsolutePath().toString()+File.separator+"config.json.bak"),FileUtils.readTextFile(new File(getDataFolder().getAbsolutePath().toString()+File.separator+"config.json"), StandardCharsets.UTF_8),StandardCharsets.UTF_8,false);
			INoThrowsRunnable.invoke(this::saveConfig);
		}
	}};
	public static WBLite instance;
	public Bot bot;
	public String awa = "";

	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		
		if(config.getConfig().password==null||config.getConfig().password.isEmpty())
		{
			getLogger().severe("������д"+((SimpleConfig<?>)this.config).conf.getAbsolutePath().toString()+"�е�qq(�˺�)��password(����)�������ر����");
			this.setEnabled(false);
			return;
		}
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		Thread temp=new Thread(()->{
			while(true)
			{
				try {
				WatchdogThread.tick();
				}catch (Throwable e) {
					return;
				}
				try {
					Thread.sleep(1);
				}catch (InterruptedException e) {
					return;
				}
			}
		});
		temp.setDaemon(true);
		try {
		temp.start();
		bot=BotFactoryJvm.newBot(this.config.getConfig().qq, this.config.getConfig().password,new BotConfiguration() {{
			fileBasedDeviceInfo("devicenfo.json");
			if(!config.getConfig().robotShowDebugMsg) {
				noNetworkLog();
			}
			if(!config.getConfig().robotShowLogMsgDebug) {
				noBotLog();
			}
			System.out.println("��b�����������ļ��Ѿ���.");		
		}});
		
		System.out.println("��bWB�����");
		if("���,��������!".equals(awa)) {
			System.out.println("���������awa");
		}
		EventUtils.registerEvents(new BotListener());
		bot.login();
		}finally {
			temp.stop();
		}
		System.out.println("��b�������Ѿ��ɹ�����.");
		
	}
	public void initCommands() {
		this.childCmds.add(new Reload());
		this.childCmds.add(new SendMsg());
	}
	
	@Override
	public void onDisable() {
		Optional.ofNullable(this.bot).ifPresent(i->i.close(new Throwable()));
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length>0)
		{
			IChildCommand c=CommandUtil.getCommand(this.childCmds,args[0]);
			if(c==null)
			{

				sender.sendMessage("���� "+args[0]+" ������");
			}else {
				if(!c.getPermission().trim().equals("") && !sender.hasPermission(c.getPermission()))
				{
					sender.sendMessage("��û��Ȩ����ô��,����Ҫ"+c.getPermission()+"Ȩ��!");
					return true;
				}
				if(!c.onCommand(sender, command, label, args))
				{
					String usage=c.getUsage();
					if(!usage.trim().equals(""))
						sender.sendMessage(usage);
				}
			}
			return true;
		}
		return super.onCommand(sender, command, label, args);
	}
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(args.length>0)
			return TabUtil.betterGetStartsWithList(realOnTabComplete(sender,command,alias,args),args[args.length-1]);
		else
			return realOnTabComplete(sender,command,alias,args);
	}
	
	public List<String> realOnTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(args.length<=1)
			return VectorUtil.toArrayList(CommandUtil.commandListToCommandNameList(childCmds));
		if(args.length>1)
		{
			IChildCommand c=CommandUtil.getCommand(childCmds, args[0]);
			if(c!=null)
			{
				Vector<Class<?>> cats=c.getArgumentsTypes();
				if(cats.size()>args.length-2)
				{
					Class<?> argType=cats.get(args.length-2);
					Vector<String> des=c.getArgumentsDescriptions();
					String desc=null;
					if(des.size()>args.length-2)
						desc=des.get(args.length-2);
					if(desc==null)
					{
						return ListUtil.toList(argType.getSimpleName());
					}else if(desc.equals("player"))
					{
						return PlayerUtil.getOfflinePlayersNameList();
					}else if(desc.contains("/")){
						return ListUtil.toList(desc.split("/"));
					}else{
						return ListUtil.toList(desc);
					}
				}
			}
		}
		return new ArrayList<>();
	}
}
