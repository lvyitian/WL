package top.whitecola.wlbot.gui;

import java.util.Vector;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;

import top.dsbbs2.whitelist.gui.ItemBuilder;
import top.whitecola.wlbot.object.RequestPlayer;

public class ConfirmGUI {
	public Player p;
	public Inventory inv;
	//items ��0���Ǿܾ� ��1����ȷ��
	public RequestPlayer rp;
	public Vector<ItemStack> items = new Vector<ItemStack>();
	public ConfirmGUI(Player p,RequestPlayer rp) {
		this.p = p;
		this.rp = rp;
		this.items.add(getColorfulWoolButton("red"));
		this.items.add(getColorfulWoolButton("green"));
		this.inv = Bukkit.createInventory(null, 18, "��r��lQQ��r[��b"+rp.sender.getId()+"��r]"+"��f��l�����");
		inv.setItem(0, new ItemBuilder().setType(Material.BOOK).setAmount(1).setDisplayName(rp.sender.getNick()+"��r[��e"+rp.sender.getId()+"��r]"+"����Ⱥ: "+rp.group).create());
		inv.setItem(1, new ItemBuilder().setType(Material.BOOK).setAmount(1).setDisplayName("�����ͬ��"+rp.sender.getNick()+"��Ϊ���İ󶨰�����").setLore("��ô����ͬ��,�������ܾ�!").create());
		inv.setItem(2, new ItemBuilder().setType(Material.BOOK).setAmount(1).setDisplayName("��ȷ���������˲���!").create());
		inv.setItem(3, new ItemBuilder().setType(Material.BOOK).setAmount(1).setDisplayName("�󶨺�,������ð�����!").create());
		inv.setItem(4, new ItemBuilder().setType(Material.BOOK).setAmount(1).setDisplayName("�������С�ĵ㵽�ܾ�,�Կ��Դ�Ⱥ���������������!").create());
		inv.setItem(5, new ItemBuilder().setType(Material.BOOK).setAmount(1).setDisplayName("������˶�������,��Ѱ��������!").create());
		inv.setItem(11,getColorfulWoolButton("red"));
		inv.setItem(15,getColorfulWoolButton("green"));
		
		
	}
	public void open() {
		p.openInventory(inv);
	}
	public void close() {
		if(this.p.getOpenInventory().getTopInventory().equals(this.inv)) {
			p.closeInventory();
		}
	}

	public ItemStack getColorfulWoolButton(String color) {
		ItemStack wool = null;
		if (color.equalsIgnoreCase("red")) {
			wool = new ItemBuilder().setType(Material.WOOL).setAmount(1).setDisplayName("�ܾ�").addLore("��c��l����˰�ť,��ζ���ܾ����QQ�󶨰�����!").create();
			Wool w=(Wool)wool.getData();
			w.setColor(DyeColor.RED);
			wool.setData(w);
		}else if(color.equalsIgnoreCase("green")) {
			wool = new ItemBuilder().setType(Material.WOOL).setAmount(1).setDisplayName("����").addLore("��a��l����˰�ť,��ζ���������QQ�󶨰�����!").create();
			Wool w=(Wool)wool.getData();
			w.setColor(DyeColor.GREEN);
			wool.setData(w);
		}
		if(wool==null) {
			throw new RuntimeException("�޷���ȡGUI�еİ�ť !");
		}
		return wool;
	}
}
