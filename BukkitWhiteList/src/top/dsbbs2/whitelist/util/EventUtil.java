package top.dsbbs2.whitelist.util;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import top.dsbbs2.whitelist.WhiteListPlugin;

public class EventUtil {
    public static boolean checkAndCancel(Cancellable e, Player p, String mess) throws Throwable
    {
        if(p==null){
            MsgUtil.makeDebugMsgAndSend("发现NPC 取消检查");
            e.setCancelled(false);
            return true;
        }
        MsgUtil.makeDebugMsgAndSend("检查是否为黑名单用户");
        if(PlayerUtil.isBlackPlayerFromName(p.getName())){
            e.setCancelled(true);
            MsgUtil.makeDebugMsgAndSend("发现黑名单用户 返回 取消行动");
            return true;
        }
        if(PlayerUtil.isBlackUUIDFromName(p.getName())){
            e.setCancelled(true);
            MsgUtil.makeDebugMsgAndSend("发现黑名单用户 返回 取消行动");
            return true;
        }
        MsgUtil.makeDebugMsgAndSend("该用户不是黑名单用户");
        try {
            if(WhiteListPlugin.instance.CNCU.containsValue(p.getName())){
                e.setCancelled(true);
                PlayerUtil.setInv(p, true);
                String messa = WhiteListPlugin.instance.whitelist.con.on_Name_Is_Right_But_UUID;
                if (messa != null && !messa.trim().equals("")){
                    p.sendMessage(messa);
                }else{
                    p.sendMessage("检测到您的白名单中的UUID错误,请在群里 '@机器人+验证',如果服务器没有对接群,请找管理员输入/wl confirm <您的QQ号>");
                }
                return true;
            }else if(WhiteListPlugin.instance.CUCN.containsValue(p.getUniqueId())){
                e.setCancelled(true);
                PlayerUtil.setInv(p, true);
                String messa = WhiteListPlugin.instance.whitelist.con.on_UUID_Is_Right_But_Name;
                if (messa != null && !messa.trim().equals("")){
                    p.sendMessage(messa);
                }else{
                    p.sendMessage("检测到您的白名单中的Name错误,请在群里 '@机器人+验证',如果服务器没有对接群,请找管理员输入/wl confirm <您的QQ号>");
                }
                return true;
            }

            if(ServerUtil.isOnlineStorageMode()) {
                if (!PlayerUtil.isInWhiteList(p.getUniqueId())&&!PlayerUtil.isInWhiteList(p.getName())) {
                    e.setCancelled(true);
                    PlayerUtil.setInv(p, true);
                    if (mess != null && !mess.trim().equals(""))
                        p.sendMessage(mess);
                    return true;
                }

            }else{
                if (!PlayerUtil.isInWhiteList(p.getName())) {
                    e.setCancelled(true);
                    PlayerUtil.setInv(p, true);
                    if (mess != null && !mess.trim().equals(""))
                        p.sendMessage(mess);
                    return true;
                }



            }
        }
        catch(Throwable e2)
        {
            MsgUtil.makeDebugMsgAndSend("发现NPC 抛出 e2");
            throw e2;
        }
        return false;


    }

    public static boolean checkAndCancel(Cancellable e, Player p) throws Throwable{
        if(WhiteListPlugin.instance.whitelist.con.antiNPCBug) {
           if(PlayerUtil.isNPC(p)){
               return true;
           }
        }
        return checkAndCancel(e, p, PlayerUtil.informMess);

    }
}
