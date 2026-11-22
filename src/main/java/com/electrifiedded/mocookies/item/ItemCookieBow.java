package com.electrifiedded.mocookies.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

/** Fires a heavy arrow instantly on right click, no ammo. */
public class ItemCookieBow extends Item {
    public ItemCookieBow() {
        setMaxStackSize(1);
        setTranslationKey("Bow");
        setCreativeTab(CreativeTabs.COMBAT);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 2.0F);
        if (!world.isRemote) {
            EntityTippedArrow arrow = new EntityTippedArrow(world, player);
            arrow.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 8.0F, 1.0F);
            arrow.setDamage(20.0D);
            arrow.setKnockbackStrength(1);
            arrow.pickupStatus = EntityArrow.PickupStatus.DISALLOWED;
            world.spawnEntity(arrow);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }
}
