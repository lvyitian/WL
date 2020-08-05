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
		GroupUtil.sendMsgToGroupAotu("机器人即将重启,重启过程中会出现不回消息等状况!");
		INoThrowsRunnable.invoke(BukkitWhitelistBot.instance.config::loadConfig);
		if(BukkitWhitelistBot.instance.config.getConfig().password==null||BukkitWhitelistBot.instance.config.getConfig().password.isEmpty())
		{
			BukkitWhitelistBot.instance.getLogger().severe("请先填写"+((SimpleConfig<?>)BukkitWhitelistBot.instance.config).conf.getAbsolutePath().toString()+"中的qq(账号)和password(密码)！再重载本插件");
			
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
		GroupUtil.sendMsgToGroupAotu("机器人重启完毕!");
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
        return "重载BukkitWhitelistBot配置文件并重启机器人";
    }

}
