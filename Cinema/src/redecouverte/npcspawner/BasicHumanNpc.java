package redecouverte.npcspawner;

import java.lang.reflect.Field;
import java.util.logging.Logger;
import net.minecraft.server.EntityLiving;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;

public class BasicHumanNpc extends BasicNpc {

    private CHumanNpc mcEntity;
    private static final Logger logger = Logger.getLogger("Minecraft");

    public BasicHumanNpc(CHumanNpc entity, String uniqueId, String name) {
        super(uniqueId, name);

        this.mcEntity = entity;
    }

    public HumanEntity getBukkitEntity() {
        return (HumanEntity) this.mcEntity.getBukkitEntity();
    }

    protected CHumanNpc getMCEntity() {
        return this.mcEntity;
    }

    public void moveTo(double x, double y, double z, float yaw, float pitch) {
        this.mcEntity.c(x, y, z, yaw, pitch);
    }

    public void attackLivingEntity(LivingEntity ent) {
        try {
            this.mcEntity.animateArmSwing();
            Field f = CraftLivingEntity.class.getDeclaredField("entity");
            f.setAccessible(true);
            EntityLiving lEntity = (EntityLiving) f.get(ent);
            this.mcEntity.h(lEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void animateArmSwing()
    {
        this.mcEntity.animateArmSwing();
    }


}
