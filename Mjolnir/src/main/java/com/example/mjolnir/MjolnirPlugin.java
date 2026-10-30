package com.example.mjolnir;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.RayTraceResult;

public class MjolnirPlugin extends JavaPlugin implements Listener {

    private static final double MAX_DISTANCE = 300.0; // how far the cursor can reach
    private NamespacedKey mjolnirKey;

    @Override
    public void onEnable() {
        mjolnirKey = new NamespacedKey(this, "mjolnir");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        if (!player.hasPermission("mjolnir.give")) {
            player.sendMessage(Component.text("You don't have permission to do that.", NamedTextColor.RED));
            return true;
        }

        player.getInventory().addItem(createMjolnir()).values()
                .forEach(left -> player.getWorld().dropItemNaturally(player.getLocation(), left));
        player.sendMessage(Component.text("You received Mjolnir!", NamedTextColor.RED));
        return true;
    }

    private ItemStack createMjolnir() {
        ItemStack item = new ItemStack(Material.MACE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Mjolnir", NamedTextColor.RED)
                .decoration(TextDecoration.ITALIC, false));
        meta.getPersistentDataContainer().set(mjolnirKey, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    private boolean isMjolnir(ItemStack item) {
        if (item == null || item.getType() != Material.MACE || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(mjolnirKey, PersistentDataType.BYTE);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return; // avoid firing twice (off-hand)

        Player player = event.getPlayer();
        if (!isMjolnir(player.getInventory().getItemInMainHand())) return;

        event.setCancelled(true);

        // Ray trace from the eyes along the crosshair, far beyond normal reach
        RayTraceResult result = player.getWorld().rayTraceBlocks(
                player.getEyeLocation(),
                player.getEyeLocation().getDirection(),
                MAX_DISTANCE,
                FluidCollisionMode.NEVER,
                true // ignore passable blocks like grass
        );

        if (result == null || result.getHitPosition() == null) {
            player.sendActionBar(Component.text("No block in sight.", NamedTextColor.GRAY));
            return;
        }

        Location target = result.getHitPosition().toLocation(player.getWorld());
        player.getWorld().strikeLightning(target);
    }
}
