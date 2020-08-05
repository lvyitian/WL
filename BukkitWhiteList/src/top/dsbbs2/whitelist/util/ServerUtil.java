package top.dsbbs2.whitelist.util;

import org.bukkit.Bukkit;
import top.dsbbs2.whitelist.WhiteListPlugin;

public class ServerUtil {
    public static void updateJson(){
        try {
            WhiteListPlugin.instance.whitelist.saveConfig();
            System.out.println("§awhitelist.json已是最新版本!");
        }catch (Throwable e){
            e.printStackTrace();
            System.out.println("§cwhitelist.json更新失败!");
            System.out.println("§c您的whitelist.json可能已经损坏!");
        }
    }
    public static boolean getOnlineMode(){
        if(Bukkit.getOnlineMode()){
            MsgUtil.makeDebugMsgAndSend("Bukkit.getOnlineMode() is true");
            return true;
        }else{
            MsgUtil.makeDebugMsgAndSend("Bukkit.getOnlineMode() is false");
            return false;
        }
    }
    public static boolean isOnlineStorageMode()
    {
        return (Bukkit.getOnlineMode() || WhiteListPlugin.instance.whitelist.con.forceOnline.equalsIgnoreCase("Online"))&&!WhiteListPlugin.instance.whitelist.con.forceOnline.equalsIgnoreCase("Offline");
    }
}
