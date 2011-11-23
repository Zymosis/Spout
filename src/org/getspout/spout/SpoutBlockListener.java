package org.getspout.spout;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockListener;
import org.getspout.spout.inventory.SimpleMaterialManager;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SpoutBlockListener extends BlockListener {
	
	private final SimpleMaterialManager mm;
	
	public SpoutBlockListener() {
		mm = (SimpleMaterialManager)SpoutManager.getMaterialManager();
	}
	
	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		super.onBlockBreak(event);
		
		if (event.isCancelled()) {
			return;
		}
		
		SpoutBlock block = (SpoutBlock) event.getBlock();
		if (block.getType() != Material.STONE && block.getType() != Material.GLASS) {
			return;
		}
		
		SpoutPlayer player = (SpoutPlayer)event.getPlayer();
		
		if (block.isCustomBlock()) {
			block.getCustomBlock().onBlockDestroyed(block.getWorld(), block.getX(), block.getY(), block.getZ(), player);
			if(mm.hasItemDrop(block.getCustomBlock())) {
				if (player.getGameMode() == GameMode.SURVIVAL) {
					block.getWorld().dropItem(block.getLocation(), mm.getItemDrop(block.getCustomBlock()));
				}
				block.setTypeId(0);
				event.setCancelled(true);
			}
			mm.removeBlockOverride(block);
		}
	}
	
	//This replaces nms functionality that is broken due to 
	//the references in the nms.Block.byId[] no longer matching
	//the static final refernces in Block.
	//Specifically, public boolean a(int i, int j, int k, int l, boolean flag, int i1)
	//in World.java is broken otherwise.
	@Override
	public void onBlockCanBuild(BlockCanBuildEvent event) {
		if (event.isBuildable()) {
			return;
		}
		Block block = event.getBlock();
		Material type = block.getType();
		if (type == Material.WATER || type == Material.STATIONARY_WATER || type == Material.LAVA || type == Material.STATIONARY_LAVA || type == Material.FIRE || type == Material.SNOW) {
			if (net.minecraft.server.Block.byId[event.getMaterialId()].canPlace(((CraftWorld)block.getWorld()).getHandle(), block.getX(), block.getY(), block.getZ())) {
				event.setBuildable(true);
			}
		}
	}

}
