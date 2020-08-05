package top.whitecola.wlbot.config.struct;

import java.util.List;
import java.util.Vector;
import top.dsbbs2.whitelist.config.struct.WhiteListConfig.WLPlayer;

public class Storage {
	public List<WLPlayer> offlinePlayerRecords = new Vector<>();
    public List<WLPlayer> alreadyGetRewardPlayers = new Vector<>();
}
