//MWP ©2020 Pecacheu. Licensed under GNU GPL 3.0

package net.forestfire.mwp;

import java.util.*;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
static final String MSG="&e[MultiWorldPets] &r", PERM_USE="multiworldpets.use";
static int RADIUS, MAX_LOC_TRIES; static ArrayList<Material> UNSAFE;
static boolean DBG;

//------------------- Initialization -------------------

@Override
public void onEnable() {
	if(Conf.loadConf()) {
		getServer().getPluginManager().registerEvents(this, this);
		msg(null,MSG+"Plugin Loaded!");
	}
}
@Override
public void onDisable() { HandlerList.unregisterAll(); }

@Override
public boolean onCommand(CommandSender s, Command c, String f, String[] a) {
	if(c.getName().equalsIgnoreCase("mwp")) {
		OfflinePlayer p=a.length>0?Bukkit.getOfflinePlayerIfCached(a[0]):(Player)s;
		if(p==null) msg(s,"&cNo such player!"); else {
			UUID u=p.getUniqueId(); msg(s,"Pets of &e"+p.getName()+"&r:");
			for(World w: Bukkit.getWorlds()) for(LivingEntity pet: getPetsOf(u,w)) {
				Location l=pet.getLocation(); msg(s,"&a"+pet.getName()+" &rin &d"+w.getName()
					+" &rat &b("+l.getBlockX()+","+l.getBlockY()+","+l.getBlockZ()+")");
			}
		}
		return true;
	} else if(c.getName().equalsIgnoreCase("tppet")) {
		if(a.length!=1) msg(s,"&cUsage: /tppet <name>"); else {
			String n=a[0]; UUID u=((Player)s).getUniqueId();
			for(World w: Bukkit.getWorlds()) for(LivingEntity pet: getPetsOf(u,w)) {
				if(n.equalsIgnoreCase(pet.getName())) { tpPet(pet, ((Player)s)); return true; }
			}
			msg(s,"&cPet not found.");
		}
		return true;
	}
	return false;
}

//------------------- Plugin Event Handlers -------------------

@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
public void onPlayerChangedWorld(PlayerChangedWorldEvent event) { if(event.getPlayer().hasPermission(PERM_USE)) {
	Player p=event.getPlayer(); World f=event.getFrom();
	for(LivingEntity pet: getPetsOf(p.getUniqueId(),f)) if(!pet.isDead() && !sitting(pet)) tpPet(pet,p);
}}

void tpPet(LivingEntity pet, Player p) {
	String w=pet.getWorld().getName(), nw=p.getWorld().getName(),
	n=p.getName(); Location nl=randomLoc(p.getLocation());
	if(nl != null) { //Teleport
		killOthers(pet); pet.teleport(nl); sitting(pet,false);
		msg(p,MSG+"&bTeleported &a"+n+"&b!"); msg(null,MSG+"&bTeleported "
			+p.getName()+"'s &bpet &a"+n+" &bfrom &d"+w+" &bto &d"+nw);
	} else { //Not safe
		String e=MSG+"&cFailed to Teleport &a"+n+" &cfrom &d"+w
			+" &cto &d"+nw+"&c!"; msg(p,e); msg(null,e);
	}
}

//------------------- Utility Functions -------------------

static ArrayList<LivingEntity> getPetsOf(UUID owner, World w) {
	ArrayList<LivingEntity> pets=new ArrayList<>();
	for(LivingEntity e: w.getEntitiesByClass(LivingEntity.class)) if(e instanceof Tameable
		&& owner.equals(((Tameable)e).getOwnerUniqueId())) pets.add(e);
	return pets;
}

static void killOthers(LivingEntity pet) {
	UUID owner=((Tameable)pet).getOwnerUniqueId(); String n=pet.getName();
	Class<? extends LivingEntity> c=pet.getClass(); for(World w: Bukkit.getWorlds()) { //Iterate worlds
		w.getEntitiesByClass(c).removeIf(e -> owner.equals(((Tameable)e).getOwnerUniqueId())
			&& n.equals(e.getName()) && w.equals(e.getWorld()));
	}
}

static boolean isSafe(Location l) {
	Material t=l.getBlock().getType(), ta=l.clone().add(0,1,0).getBlock().getType();
	boolean u=l.getY()==0 || UNSAFE.contains(t) || UNSAFE.contains(t=ta);
	if(u&&DBG) msg(null,MSG+"&cLocation Unsafe! "+t); return !u;
}
static boolean air(World w, int x, int y, int z) { Block b=w.getBlockAt(x,y,z); return b.isPassable()&&!b.isLiquid(); }

/*If found air (a=1) going down (dir=1), find block, out is y+1
If found block (a=0) going up (dir=0), find air, out is y
If found block (a=0) going down (dir=1), find air, find block, out is y+1
If found air (a=1) going up (dir=0), find block, then air, out is y*/
static int findGnd(World w, int x, int m, int z, boolean dir) {
	int y=dir?m:m+2, e=dir?0:w.getMaxHeight(); if(DBG) msg(null,"Finding ground "+(dir?"below ":"above ")+x+" "+m+" "+z);
	boolean a=air(w,x,m+1,z); for(; y!=e; y+=dir?-1:1) if(a!=air(w,x,y,z)) { if(a==dir) return dir?y+1:y; a=!a; }
	return 0;
}
static int findGnd(World w, int x, int m, int z) {
	int d=findGnd(w,x,m,z,true), u=findGnd(w,x,m,z,false), g=(u==0 || d>0 && m-d < u-m)?d:u;
	if(DBG) msg(null,MSG+"GPos: d="+d+" u="+u+" ("+x+" "+g+" "+z+")"); return g;
}

static Location randomLoc(Location l) {
	World w=l.getWorld(); double x=l.getBlockX(),z=l.getBlockZ(),nx,nz;
	for(int i=0,d=RADIUS*2,m=l.getBlockY(); i<MAX_LOC_TRIES; i++) {
		nx=x+Math.floor(Math.random()*d)-RADIUS+.5; nz=z+Math.floor(Math.random()*d)-RADIUS+.5;
		l=new Location(w,nx,findGnd(w,(int)nx,m,(int)nz),nz); if(isSafe(l)) return l;
	}
	return null;
}

static boolean sitting(LivingEntity entity) { //Getter
	if(entity instanceof Sittable) return ((Sittable)entity).isSitting();
	return false;
}
static void sitting(LivingEntity entity, boolean sit) { //Setter
	if(entity instanceof Sittable) ((Sittable)entity).setSitting(sit);
}

//-------------------  PecacheuLib Functions -------------------

static Component sc(String s) { return LegacyComponentSerializer.legacyAmpersand().deserialize(s); }
static void msg(CommandSender cm, String s) { if(cm==null) cm=Bukkit.getConsoleSender(); cm.sendMessage(sc(s)); }
}