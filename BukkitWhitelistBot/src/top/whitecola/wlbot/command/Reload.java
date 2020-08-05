package top.whitecola.wlbot.command;

import java.util.Optional;
import java.util.Vector;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import net.mamoe.mirai.BotFactoryJvm;
import net.mamoe.mirai.utils.BotConfiguration;
import top.dsbbs2.common.config.SimpleConfig;
import top.dsbbs2.common.lambda.INoThrowsRunnable;
import top.dsbbs2.whitelist.util.VectorUtil;
import top.whitecola.wlbot.BotListener;
import top.whitecola.wlbot.BukkitWhitelistBot;
import top.whitecola.wlbot.EventUtils;
import top.whitecola.wlbot.util.GroupUtil;

public class Reload implements ICommand{

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		GroupUtil.sendMsgToGroupAotu("�����˼�������,���������л���ֲ�����Ϣ��״��!");
		INoThrowsRunnable.invoke(BukkitWhitelistBot.instance.config::loadConfig);
		if(BukkitWhitelistBot.instance.config.getConfig().password==null||BukkitWhitelistBot.instance.config.getConfig().password.isEmpty())
		{
			BukkitWhitelistBot.instance.getLogger().severe("������д"+((SimpleConfig<?>)BukkitWhitelistBot.instance.config).conf.getAbsolutePath().toString()+"�е�qq(�˺�)��password(����)�������ر����");
			
			return true;
		}
		INoThrowsRunnable.invoke(BukkitWhitelistBot.instance.storage::loadConfig);
		
		Optional.ofNullable(BukkitWhitelistBot.instance.bot).ifPresent(i->i.close(new Throwable()));
		BukkitWhitelistBot.instance.bot=BotFactoryJvm.newBot(BukkitWhitelistBot.instance.config.getConfig().qq, BukkitWhitelistBot.instance.config.getConfig().password,new BotConfiguration() {{
			fileBasedDeviceInfo("devicenfo.json");
//			noBotLog();
//			noNetworkLog();
		}});
		EventUtils.registerEvents(new BotListener());
		BukkitWhitelistBot.instance.bot.login();
		GroupUtil.sendMsgToGroupAotu("�������������!");
		return true;
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
        return VectorUtil.toVector();
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
        return "whitelistbot.reload";
    }
    @NotNull
    @Override
    public String getDescription(){
        return "����BukkitWhitelistBot�����ļ�������������";
    }

}
