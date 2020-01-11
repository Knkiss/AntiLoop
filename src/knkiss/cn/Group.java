package knkiss.cn;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@SuppressWarnings("deprecation")
public class Group {
    boolean enable = true;
    boolean isReplace = false;
    List<ItemStack> itemOld = new ArrayList<>();
    ItemStack itemNew;
    String message;

    Group(String path){
        try {
            message = AntiLoop.config.getString(path+".message");
            isReplace = AntiLoop.config.getBoolean(path+".settings.isReplace");
            if(isReplace){

                String pattern = "(.*)-(.*)";
                Pattern r = Pattern.compile(pattern);
                Matcher m1 = r.matcher(AntiLoop.config.getString(path+".new"));
                if(m1.find()){
                    int ID = Integer.parseInt(m1.group(1));
                    int Damage = Integer.parseInt(m1.group(2));
                    itemNew = new ItemStack(ID,1,(short) Damage);
                }else{
                    enable = false;
                }
            }
            AntiLoop.config.getStringList(path+".old").forEach(itemStr->{
                String pattern = "(.*)-(.*)";
                Pattern r = Pattern.compile(pattern);
                Matcher m1 = r.matcher(itemStr);
                if(m1.find()){
                    int ID = Integer.parseInt(m1.group(1));
                    int Damage = Integer.parseInt(m1.group(2));
                    itemOld.add(new ItemStack(ID,1,(short) Damage));
                    AntiLoop.itemID.add(ID);
                }
            });
        }catch (Exception e){
            enable = false;
        }
    }

    public boolean check(Block b, Player p){
        if(!inGroup(b))return false;
        if(!isLoop(b))return false;
        p.sendMessage(message);
        return !isReplace;
    }

    public boolean inGroup(Block b){
        int ID=b.getTypeId();
        int damage =b.getData();
        for(ItemStack item:itemOld){
            if(item.getTypeId() == ID && item.getDurability() == damage){
                return true;
            }
        }
        return false;
    }

    public boolean isLoop(Block b){
        ArrayList<Location> list = new ArrayList<>();
        list.add(b.getLocation());
        return isLoop(b.getLocation(), b.getLocation(), b.getLocation(), list);
    }

    private boolean isLoop(Location loc, Location lloc, Location oloc, ArrayList<Location> list) {
        Location[] locate = new Location[]{
                loc.add(0.0D, 1.0D, 0.0D).clone(),
                loc.add(0.0D, -2.0D, 0.0D).clone(),
                loc.add(1.0D, 1.0D, 0.0D).clone(),
                loc.add(-2.0D, 0.0D, 0.0D).clone(),
                loc.add(1.0D, 0.0D, 1.0D).clone(),
                loc.add(0.0D, 0.0D, -2.0D).clone(), null};
        loc.add(0.0D, 0.0D, 1.0D);

        for(int i = 0; i < 6; ++i) {
            if (!locate[i].equals(lloc) && inGroup(locate[i].getBlock())) {
                if (list.contains(locate[i])) {
                    if (isReplace) locate[i].getBlock().setTypeIdAndData(itemNew.getTypeId(), (byte) itemNew.getDurability(),false);
                    return true;
                }
                list.add(locate[i]);
                if (locate[i].equals(oloc)) {
                    return true;
                }
                if (this.isLoop(locate[i], loc, oloc, list)) {
                    return true;
                }
            }
        }
        return false;
    }
}
