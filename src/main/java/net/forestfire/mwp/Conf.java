//MWP ©2020 Pecacheu. Licensed under GNU GPL 3.0

package net.forestfire.mwp;

import org.bukkit.Material;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Conf {
static final String CONFIG_PATH="plugins/MultiWorldPets/config.yml";
static MemoryConfiguration defaults=new MemoryConfiguration();

static void initDefaults() {
	defaults.set("debug", false); defaults.set("spawnRadius", 10); defaults.set("maxTries", 25);
	defaults.set("unsafeBlocks", new String[]{"LAVA", "FIRE", "CACTUS"});
}

static boolean loadConf() { try {
	File f=new File(CONFIG_PATH); YamlConfiguration conf;
	boolean pf=f.exists(); if(pf) conf=YamlConfiguration.loadConfiguration(f);
	else { conf=new YamlConfiguration(); Main.msg(null,Main.MSG+"Creating new config"); }
	initDefaults(); conf.setDefaults(defaults);

	//Load Global Settings:
	Main.DBG=conf.getBoolean("debug"); Main.RADIUS=conf.getInt("spawnRadius");
	Main.MAX_LOC_TRIES=conf.getInt("maxTries"); List<String> bList=conf.getStringList("unsafeBlocks");
	Main.UNSAFE=new ArrayList<>(bList.size()); for(String m: bList) Main.UNSAFE.add(getMat(m));
	conf.options().copyDefaults(true); conf.save(f); return true;
} catch(Exception e) { Main.msg(null,Main.MSG+"&cError loading config: "+e.getMessage()); return false; }}


//-------------------  Useful Functions -------------------

private static Material getMat(String m) throws Exception {
	try { return Material.valueOf(m); }
	catch(IllegalArgumentException e) { throw new Exception("No such material "+m); }
}
}