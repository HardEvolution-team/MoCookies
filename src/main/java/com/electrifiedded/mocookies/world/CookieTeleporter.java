package com.electrifiedded.mocookies.world;

import com.electrifiedded.mocookies.MoCookies;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/** Finds the nearest cookie portal at the destination or builds a fresh frame. */
public class CookieTeleporter extends Teleporter {
    private final WorldServer ws;

    public CookieTeleporter(WorldServer ws) {
        super(ws);
        this.ws = ws;
    }

    @Override
    public void placeEntity(World world, Entity e, float yaw) {
        BlockPos at = find(e);
        if (at == null) at = build(e);
        e.motionX = e.motionY = e.motionZ = 0;
        e.setLocationAndAngles(at.getX() + 0.5, at.getY(), at.getZ() + 0.5, yaw, e.rotationPitch);
        e.timeUntilPortal = 10;
    }

    private BlockPos find(Entity e) {
        int ex = (int) e.posX, ey = (int) e.posY, ez = (int) e.posZ;
        BlockPos best = null;
        double bd = Double.MAX_VALUE;
        for (int x = ex - 24; x <= ex + 24; x++)
            for (int z = ez - 24; z <= ez + 24; z++)
                for (int y = Math.max(1, ey - 32); y <= Math.min(250, ey + 32); y++) {
                    BlockPos p = new BlockPos(x, y, z);
                    if (ws.getBlockState(p).getBlock() == MoCookies.portal) {
                        double d = p.distanceSq(ex, ey, ez);
                        if (d < bd) { bd = d; best = p; }
                    }
                }
        if (best == null) return null;
        while (ws.getBlockState(best.down()).getBlock() == MoCookies.portal) best = best.down();
        return best;
    }

    private BlockPos build(Entity e) {
        int x = (int) Math.floor(e.posX), z = (int) Math.floor(e.posZ);
        ws.getChunk(x >> 4, z >> 4);
        int y = Math.max(4, Math.min(240, ws.getHeight(x, z)));
        IBlockState cookie = MoCookies.cookieBlock.getDefaultState();
        for (int l = -1; l <= 2; l++)
            for (int dz = -1; dz <= 1; dz++)
                for (int h = 0; h <= 3; h++) ws.setBlockState(new BlockPos(x + l, y + h, z + dz), Blocks.AIR.getDefaultState(), 2);
        for (int l = -1; l <= 2; l++)
            for (int dz = -1; dz <= 1; dz++) ws.setBlockState(new BlockPos(x + l, y - 1, z + dz), cookie, 2);
        for (int i = 0; i <= 3; i++) {
            ws.setBlockState(new BlockPos(x - 1, y + i, z), cookie, 2);
            ws.setBlockState(new BlockPos(x + 2, y + i, z), cookie, 2);
        }
        for (int l = 0; l <= 1; l++) ws.setBlockState(new BlockPos(x + l, y + 3, z), cookie, 2);
        MoCookies.portal.tryToCreatePortal(ws, new BlockPos(x, y, z));
        return new BlockPos(x, y, z);
    }
}
