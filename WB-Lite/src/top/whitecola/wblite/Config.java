package top.whitecola.wblite;

import java.util.Vector;

public class Config {
	public long qq = 0;
	public String password = "";
	public String tip = "���Ϸ���������.";
	public boolean robotShowDebugMsg = false;
	public boolean robotShowLogMsgDebug = false;
	public boolean canGroupMsgExcuteCommand = true;
	public long[] groupCommandOperator = {3213824019L,123456789L};
	public String tip2 = "�Ϸ������Զ�̻�����ִ�������ߵ�QQ��.";
	public long[] limitedOperators = {1234567,7654321};
	public String tip3 = "�Ϸ�����Ҫ����Ȩ�޵Ŀ�Զ�̻�����ִ�������ߵ�QQ��. ����Ȩ�޺�,��������ֻ��ʹ��wl������.";
	public String serverName = "WLʵ����";
	public String serverIp = "mc.whitecola.com[������,����ʵ��ַ.]";
	public int serverPort = 25565;
	public boolean quitGroupAutoBan = false;
	public boolean joinGroupWelcoming = true;
	public String[] joinGroupGreetings= {"��ӭ�������������","�����ط���������","��������������� .��������� <id> �� ����������� <id>","ϣ�������ڱ���������!"};
	public boolean isAllGroupUseBot = true;
	public Vector<Long> useBotGroup = new Vector<>();
	{
		useBotGroup.add(703246558L);
		useBotGroup.add(123456L);
	}
	public boolean playerJoinServerGroupMsg = true;
	public boolean playerLeaveServerGroupMsg = true;
	public boolean allowAddingWhitelistBeforeChangingNameInGroup = false;
}
