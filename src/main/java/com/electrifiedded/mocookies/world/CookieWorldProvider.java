package com.electrifiedded.mocookies.world;

import com.electrifiedded.mocookies.MoCookies;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Overworld-like terrain made of a single beach biome with a cyan fog. */
public class CookieWorldProvider extends WorldProvider {
    @Override
    protected void init() {
        this.biomeProvider = new BiomeProviderSingle(Biomes.BEACH);
        this.hasSkyLight = true;
    }

    @Override public DimensionType getDimensionType() { return MoCookies.dimType; }
    @Override public IChunkGenerator createChunkGenerator() { return new ChunkGeneratorOverworld(world, world.getSeed() - 22272L, false, ""); }
    @Override public boolean isSurfaceWorld() { return false; }
    @Override public boolean canRespawnHere() { return false; }

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3d getFogColor(float celestialAngle, float partialTicks) {
        float f = MathHelper.clamp(MathHelper.cos(celestialAngle * ((float) Math.PI * 2F)) * 2.0F + 0.5F, 0.0F, 1.0F) * 0.94F + 0.06F;
        return new Vec3d(0.6 * f, 1.0 * f, 1.0 * f);
    }
}
