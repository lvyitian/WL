package top.dsbbs2.whitelist.util;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import top.dsbbs2.whitelist.WhiteListPlugin;

public class EventUtil {
    public static boolean checkAndCancel(Cancellable e, Player p, String mess) throws Throwable
    {
        if(p==null){
            MsgUtil.makeDebugMsgAndSend("����NPC ȡ�����");
            e.setCancelled(false);
            return true;
        }
        MsgUtil.makeDebugMsgAndSend("����Ƿ�Ϊ�������û�");
        if(PlayerUtil.isBlackPlayerFromName(p.getName())){
            e.setCancelled(true);
            MsgUtil.makeDebugMsgAndSend("���ֺ������û� ���� ȡ���ж�");
            return true;
        }
        if(PlayerUtil.isBlackUUIDFromName(p.getName())){
            e.setCancelled(true);
            MsgUtil.makeDebugMsgAndSend("���ֺ������û� ���� ȡ���ж�");
            return true;
        }
        MsgUtil.makeDebugMsgAndSend("���û����Ǻ������û�");
        try {
            if(WhiteListPlugin.instance.CNCU.containsValue(p.getName())){
                e.setCancelled(true);
                PlayerUtil.setInv(p, true);
                String messa = WhiteListPlugin.instance.whitelist.con.on_Name_Is_Right_But_UUID;
                if (messa != null && !messa.trim().equals("")){
                    p.sendMessage(messa);
                }else{
                    p.sendMessage("��⵽���İ������е�UUID����,����Ⱥ�� '@������+��֤',���������û�жԽ�Ⱥ,���ҹ���Ա����/wl confirm <����QQ��>");
                }
                return true;
            }else if(WhiteListPlugin.instance.CUCN.containsValue(p.getUniqueId())){
                e.setCancelled(true);
                PlayerUtil.setInv(p, true);
                String messa = WhiteListPlugin.instance.whitelist.con.on_UUID_Is_Right_But_Name;
                if (messa != null && !messa.trim().equals("")){
                    p.sendMessage(messa);
                }else{
                    p.sendMessage("��⵽���İ������е�Name����,����Ⱥ�� '@������+��֤',���������û�жԽ�Ⱥ,���ҹ���Ա����/wl confirm <����QQ��>");
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
            MsgUtil.makeDebugMsgAndSend("����NPC �׳� e2");
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
