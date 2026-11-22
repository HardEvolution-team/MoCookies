package com.electrifiedded.mocookies.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemGoldenBucket extends Item {
    public ItemGoldenBucket() {
        setMaxStackSize(1);
        setTranslationKey("GoldenBucket");
        setCreativeTab(CreativeTabs.MISC);
    }

    /** Right click on a block teleports the player to it. */
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float x, float y, float z) {
        if (!world.isRemote && player instanceof EntityPlayerMP) {
            ((EntityPlayerMP) player).connection.setPlayerLocation(pos.getX(), pos.getY(), pos.getZ(), player.rotationYaw, player.rotationPitch);
        }
        return EnumActionResult.SUCCESS;
    }

    /** Crafting it summons lightning, as in the original. */
    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) world.addWeatherEffect(new EntityLightningBolt(world, player.posX, player.posY, player.posZ, false));
    }
}
