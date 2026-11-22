package com.electrifiedded.mocookies;

import com.electrifiedded.archaiclib.legacy.registry.LegacyGameRegistry;
import com.electrifiedded.archaiclib.legacy.registry.LegacyRecipes;
import com.electrifiedded.archaiclib.original.OriginalModAssets;
import com.electrifiedded.mocookies.block.BlockCookie;
import com.electrifiedded.mocookies.block.BlockCookiePortal;
import com.electrifiedded.mocookies.entity.EntityCookieZombie;
import com.electrifiedded.mocookies.item.*;
import com.electrifiedded.mocookies.world.CookieWorldProvider;
import net.minecraft.block.Block;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.DungeonHooks;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = "required-after:archaiclib@[1.26.0,)")
public class MoCookies {
    public static final String ORIGINAL_NAME = "mo_cookies_mod_1.7.10.jar - MoCookies 1.0.0 for 1.7.10 (by Luna)";
    public static final String ORIGINAL_URL = "https://www.minecraftforum.net/ (search: MoCookies mod 1.7.10 by Luna)";

    @SidedProxy(clientSide = "com.electrifiedded.mocookies.client.ClientProxy", serverSide = "com.electrifiedded.mocookies.CommonProxy")
    public static CommonProxy proxy;
    @Mod.Instance(Reference.MOD_ID)
    public static MoCookies instance;

    public static int dimId = 3;
    public static DimensionType dimType;
    public static Block cookieBlock;
    public static BlockCookiePortal portal;
    public static Item coalCookie, ironCookie, redstoneCookie, goldenCookie, lapisCookie, diamondCookie, emeraldCookie, sweetCookie;
    public static Item goldenBucket, bow, pick, sword, shovel, axe, hoe, trigger, helmet, body, legs, boots;

    /** {registry name, texture file name in the original jar} */
    private static final String[][] ITEM_TEX = {
            {"coalcookie", "coal_cookie"}, {"ironcookie", "iron_cookie"}, {"redstonecookie", "redstone_cookie"},
            {"goldencookie", "golden_cookie"}, {"lapiscookie", "lapis_cookie"}, {"diamondcookie", "diamond_cookie"},
            {"emeraldcookie", "emerald_cookie"}, {"sweetcookie", "sweetcookie"}, {"goldenbucket", "goldenbucket"},
            {"bow", "bow1"}, {"pick", "cpick"}, {"sword", "csword"}, {"shovel", "cshovel"}, {"axe", "caxe"}, {"hoe", "choe"},
            {"cd_trigger", "ci"}, {"cookiearmour_head", "cookiearmour_head"}, {"cookiearmour_body", "cookiearmour_body"},
            {"cookiearmour_leggins", "cookiearmour_leggins"}, {"cookiearmour_boots", "cookiearmour_boots"}};

    public MoCookies() {
        // textures live in the original's "minecraft" asset domain; served from the player's own jar, never bundled
        for (String[] t : ITEM_TEX)
            OriginalModAssets.aliasFromDomain(Reference.MOD_ID, "textures/items/" + t[0] + ".png", "minecraft", "textures/items/" + t[1] + ".png");
        OriginalModAssets.aliasFromDomain(Reference.MOD_ID, "textures/blocks/cookieblock.png", "minecraft", "textures/blocks/cookieblock.png");
        OriginalModAssets.aliasFromDomain(Reference.MOD_ID, "textures/models/armor/cookiearmour_layer_1.png", "minecraft", "textures/models/armor/cookiearmour_layer_1.png");
        OriginalModAssets.aliasFromDomain(Reference.MOD_ID, "textures/models/armor/cookiearmour_layer_2.png", "minecraft", "textures/models/armor/cookiearmour_layer_2.png");
        OriginalModAssets.hide(Reference.MOD_ID, "lang/en_us.lang");
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        OriginalModAssets.require(Reference.MOD_ID, "MoCookies", ORIGINAL_NAME, ORIGINAL_URL, Reference.MOD_ID);
        Configuration cfg = new Configuration(e.getSuggestedConfigurationFile());
        cfg.load();
        dimId = cfg.getInt("dimensionId", "general", 3, -255, 255, "Dimension id of the cookie dimension");
        if (cfg.hasChanged()) cfg.save();

        Item.ToolMaterial pickM = EnumHelper.addToolMaterial("MOCOOKIES_PICK", 5, 4000, 30.0F, 8.0F, 64);
        Item.ToolMaterial swordM = EnumHelper.addToolMaterial("MOCOOKIES_SWORD", 5, 4000, 0.0F, 50.0F, 64);
        Item.ToolMaterial shovelM = EnumHelper.addToolMaterial("MOCOOKIES_SHOVEL", 5, 4000, 30.0F, 4.0F, 64);
        Item.ToolMaterial axeM = EnumHelper.addToolMaterial("MOCOOKIES_AXE", 5, 4000, 30.0F, 6.0F, 64);
        Item.ToolMaterial hoeM = EnumHelper.addToolMaterial("MOCOOKIES_HOE", 1, 4000, 4.0F, 5.0F, 2);
        ItemArmor.ArmorMaterial armM = EnumHelper.addArmorMaterial("MOCOOKIES_CA", Reference.MOD_ID + ":cookiearmour", 100,
                new int[]{804, 900, 1024, 800}, 100, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.0F);

        cookieBlock = LegacyGameRegistry.registerBlock(new BlockCookie(), "cb");
        reg(Item.getItemFromBlock(cookieBlock), "cb", "block");
        portal = new BlockCookiePortal();
        LegacyGameRegistry.registerBlock(portal, null, "cd_portal");

        coalCookie = food("coalcookie", "CoalCookie", 2, new int[]{9, 2});
        ironCookie = food("ironcookie", "IronCookie", 4, new int[]{11, 2});
        redstoneCookie = food("redstonecookie", "RedstoneCookie", 6, new int[]{21, 4});
        goldenCookie = food("goldencookie", "GoldenCookie", 8, new int[]{3, 2}, new int[]{5, 2});
        lapisCookie = food("lapiscookie", "LapisCookie", 10, new int[]{8, 2}, new int[]{10, 2});
        diamondCookie = food("diamondcookie", "DiamondCookie", 12, new int[]{11, 2}, new int[]{12, 2}, new int[]{5, 2});
        emeraldCookie = food("emeraldcookie", "EmeraldCookie", 14, new int[]{1, 4}, new int[]{3, 3}, new int[]{5, 4}, new int[]{10, 4},
                new int[]{11, 3}, new int[]{12, 3}, new int[]{21, 5});
        sweetCookie = food("sweetcookie", "SweetCookie", 8, new int[]{1, 10}, new int[]{8, 2});
        goldenBucket = reg(new ItemGoldenBucket(), "goldenbucket", "item");
        bow = reg(new ItemCookieBow(), "bow", "item");
        trigger = reg(new ItemPortalTrigger(), "cd_trigger", "item");

        pick = reg(new ItemPickaxe(pickM) { }.setTranslationKey("Pick"), "pick", "handheld");
        sword = reg(new ItemSword(swordM).setTranslationKey("Sword"), "sword", "handheld");
        shovel = reg(new ItemSpade(shovelM).setTranslationKey("Shovel"), "shovel", "handheld");
        axe = reg(new ItemAxe(axeM, 6.0F, -3.0F) { }.setTranslationKey("Axe"), "axe", "handheld");
        hoe = reg(new ItemHoe(hoeM).setTranslationKey("Hoe"), "hoe", "handheld");

        helmet = reg(armor(armM, EntityEquipmentSlot.HEAD, "CookieArmour_head", new int[]{3, 3}, new int[]{13, 3}), "cookiearmour_head", "item");
        body = reg(armor(armM, EntityEquipmentSlot.CHEST, "CookieArmour_body", new int[]{10, 3}, new int[]{11, 3}, new int[]{12, 3}, new int[]{5, 3}), "cookiearmour_body", "item");
        legs = reg(armor(armM, EntityEquipmentSlot.LEGS, "CookieArmour_leggins", new int[]{12, 4}, new int[]{22, 4}), "cookiearmour_leggins", "item");
        boots = reg(armor(armM, EntityEquipmentSlot.FEET, "CookieArmour_boots", new int[]{8, 2}, new int[]{1, 2}), "cookiearmour_boots", "item");

        // recipes (1.7 grids; empty cells are blanks)
        Item cookie = Items.COOKIE;
        for (Item[] r : new Item[][]{{coalCookie, Items.COAL}, {ironCookie, Items.IRON_INGOT}, {redstoneCookie, Items.REDSTONE},
                {goldenCookie, Items.GOLD_INGOT}, {diamondCookie, Items.DIAMOND}, {emeraldCookie, Items.EMERALD}, {sweetCookie, Items.SUGAR}})
            LegacyRecipes.addRecipe(new ItemStack(r[0]), "   ", "WXW", "   ", 'W', Items.WHEAT, 'X', r[1]);
        LegacyRecipes.addRecipe(new ItemStack(lapisCookie), "   ", "WXW", "   ", 'W', Items.WHEAT, 'X', new ItemStack(Items.DYE, 1, 4));
        LegacyRecipes.addRecipe(new ItemStack(goldenBucket), "   ", "G G", " G ", 'G', Items.GOLD_INGOT);
        LegacyRecipes.addRecipe(new ItemStack(bow), " CS", "C S", " CS", 'C', cookie, 'S', Items.STRING);
        LegacyRecipes.addRecipe(new ItemStack(trigger), " C ", " S ", " S ", 'C', cookie, 'S', Items.STICK);
        LegacyRecipes.addRecipe(new ItemStack(pick), "CCC", " S ", " S ", 'C', cookie, 'S', Items.STICK);
        LegacyRecipes.addRecipe(new ItemStack(sword), " C ", " C ", " S ", 'C', cookie, 'S', Items.STICK);
        LegacyRecipes.addRecipe(new ItemStack(shovel), " C ", " S ", " S ", 'C', cookie, 'S', Items.STICK);
        LegacyRecipes.addRecipe(new ItemStack(axe), "CC ", "CS ", " S ", 'C', cookie, 'S', Items.STICK);
        LegacyRecipes.addRecipe(new ItemStack(hoe), "CC ", " S ", " S ", 'C', cookie, 'S', Items.STICK);
        LegacyRecipes.addRecipe(new ItemStack(helmet), "   ", "CCC", "C C", 'C', cookie);
        LegacyRecipes.addRecipe(new ItemStack(body), "C C", "CCC", "CCC", 'C', cookie);
        LegacyRecipes.addRecipe(new ItemStack(legs), "CCC", "C C", "C C", 'C', cookie);
        LegacyRecipes.addRecipe(new ItemStack(boots), "   ", "C C", "C C", 'C', cookie);
        LegacyRecipes.addRecipe(new ItemStack(cookieBlock), "CCC", "CCC", "CCC", 'C', cookie);
        LegacyRecipes.addShapelessRecipe(new ItemStack(cookie, 9), cookieBlock);

        GameRegistry.registerFuelHandler(new IFuelHandler() {
            @Override
            public int getBurnTime(ItemStack f) {
                Item i = f.getItem();
                return i == coalCookie ? 1600 : i == redstoneCookie ? 2000 : i == sweetCookie ? 1000 : 0;
            }
        });

        // dimension
        dimType = DimensionType.register("Cookie", "_cookie", dimId, CookieWorldProvider.class, false);
        DimensionManager.registerDimension(dimId, dimType);

        // cookie zombie
        LegacyGameRegistry.registerModEntity(EntityCookieZombie.class, "cookieZombie", 0, this, 64, 1, true, 16764006, 6749952);
        for (Biome b : ForgeRegistries.BIOMES) {
            if (b == Biomes.HELL || b == Biomes.SKY || b == Biomes.VOID) continue;
            EntityRegistry.addSpawn(EntityCookieZombie.class, 2, 5, 30, EnumCreatureType.MONSTER, b);
        }
        DungeonHooks.addDungeonMob(new ResourceLocation(Reference.MOD_ID, "cookiezombie"), 180);
        proxy.preInit();
    }

    private static Item food(String name, String key, int amount, int[]... fx) {
        return reg(new ItemCookieFood(key, amount, fx), name, "item");
    }

    private static Item armor(ItemArmor.ArmorMaterial m, EntityEquipmentSlot slot, String key, int[]... fx) {
        return new ItemArmor(m, 0, slot) {
            @Override
            public void onArmorTick(World world, EntityPlayer p, ItemStack s) {
                for (int[] e : fx) {
                    Potion pot = Potion.getPotionById(e[0]);
                    if (pot != null) p.addPotionEffect(new PotionEffect(pot, 100000, e[1], false, false));
                }
            }

            @Override
            public String getArmorTexture(ItemStack stack, net.minecraft.entity.Entity entity, EntityEquipmentSlot s, String type) {
                return Reference.MOD_ID + ":textures/models/armor/cookiearmour_layer_" + (s == EntityEquipmentSlot.LEGS ? 2 : 1) + ".png";
            }
        }.setTranslationKey(key).setCreativeTab(net.minecraft.creativetab.CreativeTabs.COMBAT);
    }

    /** register item + its model (resources/assets/mocookies/models/item/&lt;name&gt;.json) */
    private static Item reg(Item item, String name, String kind) {
        if (item.getRegistryName() == null) LegacyGameRegistry.registerItem(item, name);
        LegacyGameRegistry.registerItemModel(item, 0, Reference.MOD_ID + ":" + name, "inventory");
        return item;
    }
}
