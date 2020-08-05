package top.whitecola.wlbot.config.struct;

import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.function.Consumer;

import org.bukkit.Bukkit;

import top.whitecola.wlbot.BukkitWhitelistBot;
import top.whitecola.wlbot.config.struct.Config.Reward.RandomNum;

public class Config {
	public boolean robotShowGebugMsg = false;
	public boolean robotShowLogMsg = false;
	public long qq;
	public String password="";
	public boolean quitGroupAutoBan;
	public boolean canGroupMsgExcuteCommand = true;
	public long[] groupMsgCommander = {1302399643L,535481388L,3213824019L};
	public long[] limitedOperators = {2333L,2333333L,23333333L};
	public boolean playerAddWhitelistByOneself = true;
	public boolean playerPrivateMsgAddWhitelist = true;
	public boolean allowAddingWhitelistBeforeChangingNameInGroup = true;
	public boolean allGroupUse = true;
	public long[] useBotGroup = {703246558L,111111111L};
	public long[] botTester = {1302399643L,535481388L,3213824019L};
	public String warning = "ʹ�õ�ʱ��,����ȥ���Դ���QQ��,�Դ���QQ����Ϊ�˷��㿪���߲���ʹ��.(��ʾ����)";
	public boolean joinGroupWelcoming = true;
	public String[] joinGroupGreetings= {"��ӭ�������������","�����ط���������","��������������� .��������� <id> �� ����������� <id>","ϣ�������ڱ���������!"};
	public boolean serverQuery = true;
	public String serverName = "WLʵ����";
	public String serverAddress = "mc.whitecola.com[������,����ʵ��ַ]";
    public int serverPort=25565;
    public boolean isPlayerGotWhitelistThenExcuteCommand = false;
    public String tip = "��������������� �������������Ҳ�һ����������. ʹ������: ��һ�ȡ���������ͻ�������";
    public List<String> playerGotWhitelistThenExcuteCommands = new Vector<String>() {
		private static final long serialVersionUID = 3060730293662928938L;
	{
    	this.add("minecraft:give ${%player_name%} golden_apple 1");
    	this.add("minecraft:give ${%player_name%} iron_pickaxe 1");
    }};
    public boolean canPlayersGetTheirLotteries = true;
    public boolean canPlayerOfOfflineServerGetRandomLotteriesItem = false;
    public String tip2 = "����ڻ�ȡ������ǰ �Ƿ���Գ齱 ���Ҫ�Զ��影Ʒ �뽫��Ʒ���������ʽ��д Ĭ��ֻ����������� �������������뽫canPlayerOfOfflineServerGetRandomLotteriesItem ��Ϊtrue (Ϊ�˷�ֹ���ˢ��Ʒ ����������ʹ��) ���� '.��һ���˽� �齱' ��ȡ������Ϣ ";
    public List<Reward> randomLotteriesItem = new Vector<Reward>() {
		private static final long serialVersionUID = 3060730293662928938L;
	{
		
    	this.add(new Reward("minecraft:diamond", 0.8, new RandomNum(1, 2)));
    	this.add(new Reward("minecraft:emerald", 0.9, new RandomNum(3, 5)));
    	this.add(new Reward("minecraft:golden_apple", 0.95, new RandomNum(1, 1),(short)1));
    	
    }};
    public boolean serverMsgSendToGroup = true;
    public boolean groupMsgSendToServer = true;
    public boolean joinServerOrLeaveServerMsgSendToGroup = true;
    public boolean isAddWhitelistConfirm = false;
    public List<Reward> giveReward(String selector)
    {
    	List<Reward> ret=new Vector<>();
    	randomLotteriesItem.parallelStream().forEach(i->i.ifAvailable(i2->{
    		ret.add(i2);
    		i2.forceGive(selector);
    	}));
    	if(ret.size()==0)
    	{
    		Reward t=new Reward("minecraft:emerald", 1, new RandomNum(1, 1));
    	    t.forceGive(selector);
    	    ret.add(t);
    	}
    	return ret;
    }
    public static class Reward{
    	public String item;
    	public double probability;
    	public RandomNum amount;
    	public short damage;
    	public String nbt;
    	public Reward(String item, double probability, RandomNum amount) {
			this(item,probability,amount,(short)0);
		}
    	public Reward(String item, double probability, RandomNum amount,String nbt)
    	{
    		this(item, probability, amount, (short)0,"");
    	}
    	
		public Reward(String item, double probability, RandomNum amount,short damage)
    	{
    		this(item, probability, amount, damage,"");
    	}
		public Reward(String item, double probability, RandomNum amount, short damage, String nbt) {
			this.item = item;
			this.probability = probability;
			this.amount = amount;
			this.damage = damage;
			this.nbt = nbt;
		}
    	public boolean isAvailable()
    	{
    		return new SecureRandom().nextInt(100)+1<=Math.min(probability, 1)*100;
    	}
    	public void ifAvailable(Consumer<Reward> i)
    	{
    		if(isAvailable())
    			i.accept(this);
    	}
    	public void forceGive(String selector)
    	{
    		Bukkit.getScheduler().runTask(BukkitWhitelistBot.instance, ()->Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:give "+selector+" "+item+" "+amount.getNum()+" "+damage+" "+nbt));
    	}
    	public void give(String selector)
    	{
    		this.ifAvailable(i->i.forceGive(selector));
    	}
    	
		@Override
		public int hashCode() {
			return Objects.hash(amount, damage, item, nbt, probability);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof Reward))
				return false;
			Reward other = (Reward) obj;
			return Objects.equals(amount, other.amount) && damage == other.damage && Objects.equals(item, other.item)
					&& Objects.equals(nbt, other.nbt)
					&& Double.doubleToLongBits(probability) == Double.doubleToLongBits(other.probability);
		}

		@Override
		public String toString() {
			return "Reward [item=" + item + ", probability=" + probability + ", amount=" + amount + ", damage=" + damage
					+ ", nbt=" + nbt + "]";
		}

		public static class RandomNum{
    		public int from,to;

			public RandomNum(int from, int to) {
				this.from = from;
				this.to = to;
			}
    		public int getNum()
    		{
    			return new SecureRandom().nextInt(Math.max(to,from)+1-Math.min(from,to))+Math.min(from,to);
    		}
			@Override
			public int hashCode() {
				return Objects.hash(from, to);
			}
			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (!(obj instanceof RandomNum))
					return false;
				RandomNum other = (RandomNum) obj;
				return from == other.from && to == other.to;
			}
			@Override
			public String toString() {
				return "RandomNum [from=" + from + ", to=" + to + "]";
			}
    		
    	}
    }
}
