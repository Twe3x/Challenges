package net.codingarea.challenges.plugin.utils.bukkit.nms.implementations.v1_17;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.utils.bukkit.nms.ReflectionUtil;
import net.codingarea.challenges.plugin.utils.bukkit.nms.type.WorldServer;
import org.bukkit.World;

/**
 * @author sehrschlechtYT | https://github.com/sehrschlechtYT
 * @since 2.2.3
 */
public class WorldServer_1_17 extends WorldServer {

    /**
     * @param object   The instance of the specified bukkit type
     */
    public WorldServer_1_17(World object) throws ClassNotFoundException {
        super(object, ReflectionUtil.getBukkitClass("CraftWorld"));
    }

    /**
     * Creates an NMS object of the specified bukkit type object
     *
     * @param world An instance of the specified bukkit type
     * @return The NMS object
     */
    @Override
    public Object get(World world) {
        Object worldServer;
        try {
            worldServer = ReflectionUtil.invokeMethod(nmsClass, world, "getHandle");
        } catch (Exception exception) {
            Challenges.getInstance().getLogger().error("", exception);
            worldServer = null;
        }
        return worldServer;
    }
}