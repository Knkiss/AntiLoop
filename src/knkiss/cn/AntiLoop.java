package knkiss.cn;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings("deprecation")
public class AntiLoop extends JavaPlugin implements Listener {
    static FileConfiguration config;
    List<Group> itemGroup = new ArrayList<>();
    static List<Integer> itemID = new ArrayList<>();
    static Logger log;

    @Override
    public void onEnable() {
        log = this.getLogger();
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this,this);
        this.getCommand("AntiLoop").setExecutor(this);
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.equalsIgnoreCase("antiloop")){
            if(!sender.hasPermission("AntiLoop.check")){
                sender.sendMessage("你没有[AntiLoop.check]权限");
                return true;
            }
            itemGroup.forEach(group -> {
                sender.sendMessage("----------------------------------------");
                sender.sendMessage("组名："+group.path);
                sender.sendMessage("是否开启（配置无误）: " + group.enable);
                sender.sendMessage("是否替换: " + group.isReplace);
                for(ItemStack item:group.itemOld){
                    sender.sendMessage("oldID:" + item.getTypeId() + "-子ID:" + item.getDurability());
                }
                if(group.isReplace){
                    sender.sendMessage("newID:" + group.itemNew.getTypeId()+"-子ID:"+group.itemNew.getDurability());
                }
                sender.sendMessage("----------------------------------------");
            });
        }
        return true;
    }
}
