package com.electrifiedded.mocookies.item;

import com.electrifiedded.mocookies.MoCookies;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Lights a cookie-block frame, like flint and steel for a nether portal. */
public class ItemPortalTrigger extends Item {
    public ItemPortalTrigger() {
        setMaxStackSize(1);
        setMaxDamage(64);
        setTranslationKey("cD_trigger");
        setCreativeTab(CreativeTabs.TOOLS);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float x, float y, float z) {
        BlockPos at = pos.offset(facing);
        ItemStack stack = player.getHeldItem(hand);
        if (!player.canPlayerEdit(at, facing, stack)) return EnumActionResult.FAIL;
        if (world.isAirBlock(at)) {
            world.playSound(player, at, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
            MoCookies.portal.tryToCreatePortal(world, at);
        }
        stack.damageItem(1, player);
        return EnumActionResult.SUCCESS;
    }
}
