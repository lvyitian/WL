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
	//items 第0个是拒绝 第1个是确认
	public RequestPlayer rp;
	public Vector<ItemStack> items = new Vector<ItemStack>();
	public ConfirmGUI(Player p,RequestPlayer rp) {
		this.p = p;
		this.rp = rp;
		this.items.add(getColorfulWoolButton("red"));
		this.items.add(getColorfulWoolButton("green"));
		this.inv = Bukkit.createInventory(null, 18, "§r§lQQ§r[§b"+rp.sender.getId()+"§r]"+"§f§l请求绑定");
		inv.setItem(0, new ItemBuilder().setType(Material.BOOK).setAmount(1).setDisplayName(rp.sender.getNick()+"§r[§e"+rp.sender.getId()+"§r]"+"来自群: "+rp.group).create());
		inv.setItem(1, new ItemBuilder().setType(Material.BOOK).setAmount(1).setDisplayName("如果您同意"+rp.sender.getNick()+"作为您的绑定白名单").setLore("那么请点击同意,否则点击拒绝!").create());
		inv.setItem(2, new ItemBuilder().setType(Material.BOOK).setAmount(1).setDisplayName("请确认是您本人操作!").create());
		inv.setItem(3, new ItemBuilder().setType(Material.BOOK).setAmount(1).setDisplayName("绑定后,您将获得白名单!").create());
		inv.setItem(4, new ItemBuilder().setType(Material.BOOK).setAmount(1).setDisplayName("如果您不小心点到拒绝,仍可以从群里重新申请白名单!").create());
		inv.setItem(5, new ItemBuilder().setType(Material.BOOK).setAmount(1).setDisplayName("如果有人恶意申请,请寻求管理帮助!").create());
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
			wool = new ItemBuilder().setType(Material.WOOL).setAmount(1).setDisplayName("拒绝").addLore("§c§l点击此按钮,意味您拒绝与该QQ绑定白名单!").create();
			Wool w=(Wool)wool.getData();
			w.setColor(DyeColor.RED);
			wool.setData(w);
		}else if(color.equalsIgnoreCase("green")) {
			wool = new ItemBuilder().setType(Material.WOOL).setAmount(1).setDisplayName("接受").addLore("§a§l点击此按钮,意味您接受与该QQ绑定白名单!").create();
			Wool w=(Wool)wool.getData();
			w.setColor(DyeColor.GREEN);
			wool.setData(w);
		}
		if(wool==null) {
			throw new RuntimeException("无法获取GUI中的按钮 !");
		}
		return wool;
	}
}
