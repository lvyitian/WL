package top.whitecola.wlbot.command;

import java.util.NoSuchElementException;
import java.util.Vector;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import top.dsbbs2.whitelist.util.CommandUtil;
import top.dsbbs2.whitelist.util.VectorUtil;
import top.whitecola.wlbot.BukkitWhitelistBot;
import top.whitecola.wlbot.util.GroupUtil;

public class SendMsg implements ICommand{

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		if(arg3.length<2) {
			arg0.sendMessage("����дҪ���͵�����");
			return false;
		}
		if(arg3.length==2) {
			GroupUtil.sendMsgToAllGroup(arg3[1]);
		}else if(arg3.length==3) {
			if(!CommandUtil.ArgumentUtil.isLong(arg3[2])) {
				arg0.sendMessage("Ⱥ�ű���������");
				return false;
			}
			try {
			BukkitWhitelistBot.instance.bot.getGroup(Long.parseLong(arg3[2])).sendMessage(arg3[1]);
			}catch (NoSuchElementException e) {
				arg0.sendMessage("������û�м�Ⱥ"+arg3[2]+",�����û����˼�Ⱥ");
				return true;
			}
		}
		return false;
	}
	@NotNull
    @Override
    public String getUsage() {
        return "/wb sendmsg <msg> [groupid]";
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
        return "whitelistbot.sendmsg";
    }
    @NotNull
    @Override
    public String getDescription(){
        return "�û����˷���Ϣ��ָ��Ⱥ(��ָ����������Ⱥ)";
    }
}
