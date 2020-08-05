package top.whitecola.wlbot.object;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;

public class RequestPlayer {
	public Member sender;
	public Group group;
	public long currentTimeMillis;
	public RequestPlayer(Member sender,Group group,long ctime) {
		this.sender = sender;
		this.group = group;
		this.currentTimeMillis = ctime;
	}
	
}
