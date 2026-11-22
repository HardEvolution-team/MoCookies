package com.electrifiedded.mocookies.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCookie extends Block {
    public BlockCookie() {
        super(Material.GROUND);
        setHardness(2.0F);
        setResistance(5.0F);
        setSoundType(SoundType.GROUND);
        setTranslationKey("CB");
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        setHarvestLevel("pickaxe", 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World w, BlockPos pos, Random r) {
        for (int i = 0; i < 2; i++) {
            w.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + r.nextFloat(), pos.getY() + r.nextFloat(), pos.getZ() + r.nextFloat(),
                    (r.nextFloat() - 0.5) * 0.2, (r.nextFloat() - 0.5) * 0.2, (r.nextFloat() - 0.5) * 0.2);
        }
    }
}
