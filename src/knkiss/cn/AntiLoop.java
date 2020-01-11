package knkiss.cn;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class AntiLoop extends JavaPlugin implements Listener {
    static FileConfiguration config;
    List<Group> itemGroup = new ArrayList<>();
    static List<Integer> itemID = new ArrayList<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this,this);
        config = this.getConfig();
        config.getKeys(false).forEach(path-> itemGroup.add(new Group(path)));
        this.getLogger().info("Author：Knkiss");
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent e){
        if(e.getPlayer().hasPermission("AntiLoop.bypass"))return;
        if(!itemID.contains(e.getBlock().getTypeId()))return;
        itemGroup.forEach(group -> {
            if(group.check(e.getBlock(),e.getPlayer())){
                e.setCancelled(true);
            }
        });
    }
}
