package com.electrifiedded.mocookies.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

/** Cookie that grants potion effects (ids are the 1.7 potion ids; amplifier as in the original). */
public class ItemCookieFood extends ItemFood {
    private final int[][] effects; // {potionId, amplifier}

    public ItemCookieFood(String key, int food, int[]... effects) {
        super(food, 0.3F, false);
        this.effects = effects;
        setTranslationKey(key);
        setCreativeTab(CreativeTabs.FOOD);
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);
        if (world.isRemote) return;
        for (int[] e : effects) {
            Potion p = Potion.getPotionById(e[0]);
            if (p != null) player.addPotionEffect(new PotionEffect(p, 1000, e[1]));
        }
    }
}
