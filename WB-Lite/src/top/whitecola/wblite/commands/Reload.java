package top.whitecola.wblite.commands;

import java.util.Optional;
import java.util.Vector;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.WatchdogThread;

import net.mamoe.mirai.BotFactoryJvm;
import net.mamoe.mirai.utils.BotConfiguration;
import top.dsbbs2.common.config.SimpleConfig;
import top.dsbbs2.common.lambda.INoThrowsRunnable;
import top.dsbbs2.whitelist.commands.IChildCommand;
import top.dsbbs2.whitelist.util.VectorUtil;
import top.whitecola.wblite.BotListener;
import top.whitecola.wblite.EventUtils;
import top.whitecola.wblite.WBLite;
import top.whitecola.wlbot.util.GroupUtil;

public class Reload implements IChildCommand{

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length==1) {
			GroupUtil.sendMsgToGroupAuto("�����˼�������,���������л���ֲ�����Ϣ��״��!");
			INoThrowsRunnable.invoke(WBLite.instance.config::loadConfig);
			if(WBLite.instance.config.getConfig().password==null||WBLite.instance.config.getConfig().password.isEmpty())
			{
				WBLite.instance.getLogger().severe("������д"+((SimpleConfig<?>)WBLite.instance.config).conf.getAbsolutePath().toString()+"�е�qq(�˺�)��password(����)�������ر����");
				
				return true;
			}
			
			Optional.ofNullable(WBLite.instance.bot).ifPresent(i->i.close(new Throwable()));
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
			WBLite.instance.bot=BotFactoryJvm.newBot(WBLite.instance.config.getConfig().qq, WBLite.instance.config.getConfig().password,new BotConfiguration() {{
				fileBasedDeviceInfo("devicenfo.json");
				if(!WBLite.instance.config.getConfig().robotShowDebugMsg) {
					noNetworkLog();
				}
				if(!WBLite.instance.config.getConfig().robotShowLogMsgDebug) {
					noBotLog();
				}
			}});
			EventUtils.registerEvents(new BotListener());
			WBLite.instance.bot.login();
			}finally {
				temp.stop();
			}
			System.out.println("�������������!");
			GroupUtil.sendMsgToGroupAuto("�������������!");
			return true;
		}
		return false;
	}
	@NotNull
    @Override
    public String getUsage() {
        return "/wb reload";
    }

    @NotNull
    @Override
    public Vector<Class<?>> getArgumentsTypes()
    {
        return VectorUtil.toVector(String.class);
    }

    @NotNull
    @Override
    public Vector<String> getArgumentsDescriptions()
    {
        return VectorUtil.toVector();
    }

    @NotNull
    @Override
    public String getPermission()
    {
        return "wb.reload";
    }
    @NotNull
    @Override
    public String getDescription(){
        return "���¶�ȡ�����ļ�,����������.";
    }
}
