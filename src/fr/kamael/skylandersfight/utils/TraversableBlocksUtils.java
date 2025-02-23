package fr.kamael.skylandersfight.utils;

import java.util.List;

import org.bukkit.Material;

public class TraversableBlocksUtils {
    private static final List<Material> TRAVERSABLE_BLOCKS = List.of(
            Material.AIR,
            Material.CAVE_AIR,
            Material.VOID_AIR,
            Material.WATER,
            Material.LAVA,
            
            // Plantes et fleurs basses
            Material.GRASS,
            Material.TALL_GRASS,
            Material.FERN,
            Material.LARGE_FERN,
            Material.DEAD_BUSH,
            Material.DANDELION,
            Material.POPPY,
            Material.BLUE_ORCHID,
            Material.ALLIUM,
            Material.AZURE_BLUET,
            Material.RED_TULIP,
            Material.ORANGE_TULIP,
            Material.WHITE_TULIP,
            Material.PINK_TULIP,
            Material.OXEYE_DAISY,
            Material.CORNFLOWER,
            Material.LILY_OF_THE_VALLEY,
            Material.WITHER_ROSE,

            // Arbustes et buissons
            Material.SWEET_BERRY_BUSH,
            Material.SNOW, // Couche de neige

            // Grandes plantes et plantes à deux blocs
            Material.SUNFLOWER,
            Material.LILAC,
            Material.ROSE_BUSH,
            Material.PEONY,

            // Cultures et plantes cultivables
            Material.WHEAT,
            Material.CARROTS,
            Material.POTATOES,
            Material.BEETROOTS,
            Material.MELON_STEM,
            Material.PUMPKIN_STEM,
            Material.ATTACHED_MELON_STEM,
            Material.ATTACHED_PUMPKIN_STEM,
            Material.SUGAR_CANE,
            Material.BAMBOO_SAPLING,
            Material.BAMBOO,

            // Vignes et lianes
            Material.VINE,
            Material.TWISTING_VINES,
            Material.TWISTING_VINES_PLANT,
            Material.WEEPING_VINES,
            Material.WEEPING_VINES_PLANT,

            // Coraux
            Material.TUBE_CORAL_FAN,
            Material.BRAIN_CORAL_FAN,
            Material.BUBBLE_CORAL_FAN,
            Material.FIRE_CORAL_FAN,
            Material.HORN_CORAL_FAN,
            Material.DEAD_TUBE_CORAL_FAN,
            Material.DEAD_BRAIN_CORAL_FAN,
            Material.DEAD_BUBBLE_CORAL_FAN,
            Material.DEAD_FIRE_CORAL_FAN,
            Material.DEAD_HORN_CORAL_FAN,

            // Autres éléments décoratifs
            Material.COBWEB,
            Material.SCAFFOLDING,
            Material.LADDER,
            Material.TRIPWIRE,
            Material.LILY_PAD,

            // Champignons
            Material.BROWN_MUSHROOM,
            Material.RED_MUSHROOM,
            Material.CRIMSON_FUNGUS,
            Material.WARPED_FUNGUS,
            Material.CRIMSON_ROOTS,
            Material.WARPED_ROOTS,
            Material.NETHER_SPROUTS,

            // Autres petits éléments
            Material.END_ROD,
            Material.LIGHT,
            Material.TORCH,
            Material.WALL_TORCH,
            Material.REDSTONE_WIRE,
            Material.REDSTONE_TORCH,
            Material.REDSTONE_WALL_TORCH,
            Material.TRIPWIRE_HOOK,

            // Bulles d'eau
            Material.BUBBLE_COLUMN
        );
    
    /// --- Méthodes.
    public static Boolean isTraversableBlock(Material type) {
    	return TRAVERSABLE_BLOCKS.contains(type);
    }
}
