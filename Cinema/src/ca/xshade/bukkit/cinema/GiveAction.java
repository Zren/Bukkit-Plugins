package ca.xshade.bukkit.cinema;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveAction extends Action {
	ItemStack item;
	
	public GiveAction(long t, ItemStack item) {
		this.t = t;
		this.item = item;
	}
	
	public boolean doAction(Player player) {
		player.getInventory().addItem(item);
		return true;
	}
	
	public String toString() {
		return t+";i;"+item.getTypeId()+";"+item.getAmount();
	}
}
