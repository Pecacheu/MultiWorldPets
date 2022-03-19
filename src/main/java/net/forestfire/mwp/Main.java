package com.pecacheu.mwp;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.AnimalTamer;

public class Main extends JavaPlugin implements Listener {
	static final String MSG_BADGE = "&e[MultiWorldPets] ", PERM_USE = "multiworldpets.use";
	
	//------------------- Initialization -------------------
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	@Override
	public void onDisable() {
		HandlerList.unregisterAll();
	}
	
	/*@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("> CMDNAME <")) {
			return true;
		}
		return false;
	}*/
	
	//------------------- Plugin Event Handlers -------------------
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) { if(event.getPlayer().hasPermission(PERM_USE)) {
		Player player = event.getPlayer(); Location pLoc = player.getLocation();
		World from = event.getFrom(); ChuList<LivingEntity> pets = getPetsOf(player, from);
		
		for(int i=0,l=pets.length; i<l; i++) { LivingEntity pet = pets.get(i); if(pet.isValid() && !pet.isDead() && !sitting(pet)) {
			
			Location loc = pet.getLocation(); String name = pet.getCustomName(); if(name == null) continue;
			Location nLoc = randomLoc(pLoc, 5, loc.getYaw(), loc.getPitch());
			
			if(nLoc != null) { //Teleport to location:
				removeOthers(pet); pet.teleport(nLoc); sitting(pet, false);
				player.sendMessage(c(MSG_BADGE+"&bTeleported &a"+name+"&b!"));
				Bukkit.getConsoleSender().sendMessage(c(MSG_BADGE+"&bTeleported "+player.getName()+"'s &bpet &a"
				+name+" &bfrom &d"+from.getName()+" &bto &d"+pLoc.getWorld().getName()+"&b!"));
			} else { //No safe blocks found!
				String err = c(MSG_BADGE+"&cFailed to Teleport &a"+name
				+" &cfrom &d"+from.getName()+" &cto &d"+pLoc.getWorld().getName()+"&c!");
				player.sendMessage(err); Bukkit.getConsoleSender().sendMessage(err);
			}
		}}
	}}
	
	//------------------- Utility Functions -------------------
	
	private static ChuList<LivingEntity> getPetsOf(Player owner, World inWorld) {
		Object[] eList = inWorld.getEntitiesByClass(LivingEntity.class).toArray(); ChuList<LivingEntity> pets = new ChuList<LivingEntity>();
		Predicate<LivingEntity> isPet = (e) -> { return e instanceof Tameable && owner.equals(((Tameable)e).getOwner()); };
		for(int i=0,l=eList.length; i<l; i++) { LivingEntity e = (LivingEntity)eList[i]; if(isPet.test(e)) pets.push(e); }
		return pets;
	}
	
	private static void removeOthers(LivingEntity pet) {
		AnimalTamer owner = ((Tameable)pet).getOwner(); String name = pet.getCustomName(); Location loc = pet.getLocation();
		
		BiPredicate<LivingEntity, World> match = (e, world) -> { return owner.equals(((Tameable)e).getOwner()) &&
		name.equals(e.getCustomName()) && world.equals(e.getLocation().getWorld()) && !locEquals(e.getLocation(), loc); };
		
		List<World> worlds = Bukkit.getWorlds(); for(int w=0,k=worlds.size(); w<k; w++) { //Iterate through worlds:
			Object[] eList = worlds.get(w).getEntitiesByClass(pet.getClass()).toArray(); //Get animals of same type in world.
			for(int i=0,l=eList.length; i<l; i++) { LivingEntity e = (LivingEntity)eList[i]; if(match.test(e, worlds.get(w))) e.remove(); }
		}
	}
	
	private static ChuList<Material>
	unsafeBlocks = new ChuList<Material>(
		Material.AIR,
		Material.LAVA,
		Material.STATIONARY_LAVA,
		Material.FIRE,
		Material.CACTUS
	);
	
	private static boolean isSafe(Location loc) {
		World world = loc.getWorld();
		if(world.getBlockAt(loc).getType() != Material.AIR) return false;
		Block below = world.getBlockAt((int)Math.floor(loc.getX()), (int)Math.floor(loc.getY()-1), (int)Math.floor(loc.getZ()));
		if(unsafeBlocks.indexOf(below.getType()) != -1) return false;
		return true;
	}
	
	private static Location randomLoc(Location loc, int maxDif, float yaw, float pitch, int DEPTH) {
		if(DEPTH > 20) return null;
		Location nLoc; if(DEPTH == 20) nLoc = loc; else {
			int dDif = maxDif*2; World world = loc.getWorld();
			int nX = loc.getBlockX()+(int)Math.floor(Math.random()*dDif)-maxDif;
			int nZ = loc.getBlockZ()+(int)Math.floor(Math.random()*dDif)-maxDif;
			nLoc = new Location(world, nX+0.5, loc.getY(), nZ+0.5, yaw, pitch);
		}
		if(!isSafe(nLoc)) return randomLoc(loc, maxDif, yaw, pitch, DEPTH+1);
		return nLoc;
	} private static Location randomLoc(Location loc, int maxDif, float yaw, float pitch) { return randomLoc(loc, maxDif, yaw, pitch, 0); }
	
	private static boolean sitting(LivingEntity entity) { //Getter
		if(entity instanceof Wolf) return ((Wolf)entity).isSitting();
		if(entity instanceof Ocelot) return ((Ocelot)entity).isSitting();
		return false;
	}
	private static void sitting(LivingEntity entity, boolean sitting) { //Setter
		if(entity instanceof Wolf) ((Wolf)entity).setSitting(sitting);
		else if(entity instanceof Ocelot) ((Ocelot)entity).setSitting(sitting);
	}
	
	private static boolean locEquals(Location locA, Location locB) {
		return locA.getWorld().equals(locB.getWorld()) &&
		locA.getBlockX() == locB.getBlockX() &&
		locA.getBlockY() == locB.getBlockY() &&
		locA.getBlockZ() == locB.getBlockZ();
	}
	
	//-------------------  PecacheuLib Functions -------------------
	
	private static String c(String str) {
		String clr[] = str.split("&"), cStr = clr[0];
		for(int i=1,l=clr.length; i<l; i++) cStr += org.bukkit.ChatColor
		.getByChar(clr[i].charAt(0)).toString()+clr[i].substring(1);
		return cStr;
	}
}