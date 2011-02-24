package ca.xshade.bukkit.debugger;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;

public class DebuggerBlockListener extends BlockListener {
	
	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		BukkitDebugger.log(event.getType(), new String[]{
			"Block",
			"Player"
		}, new Object[]{
			event.getBlock(),
			event.getPlayer()
		});
	}
	
	@Override
	public void onBlockBurn(BlockBurnEvent event) {
		BukkitDebugger.log(event.getType(), new String[]{
			"Block"
		}, new Object[]{
			event.getBlock()
		});
	}
	
	@Override
	public void onBlockCanBuild(BlockCanBuildEvent event) {
		BukkitDebugger.log(event.getType(), new String[]{
			"Block",
			"Material"
		}, new Object[]{
			event.getBlock(),
			event.getMaterial()
		});
	}
	
	@Override
	public void onBlockDamage(BlockDamageEvent event) {
		BukkitDebugger.log(event.getType(), new String[]{
			"Block",
			"Damage Level"
		}, new Object[]{
			event.getBlock(),
			event.getDamageLevel()
		});
	}
	
	
}
