package ca.xshade.bukkit.cinema;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class MoveAction extends Action {
	Location l;
	
	public MoveAction(long t, Location l) {
		this.t = t;
		this.l = l;
	}
	
	public boolean doAction(Player player) {
		player.teleportTo(l);
		return true;
	}
	
	public String toString() {
		return t+";m;"+l.getWorld().getName()+";"+l.getX()+";"+l.getY()+";"+l.getZ()+";"+l.getPitch()+";"+l.getYaw();
	}
}
