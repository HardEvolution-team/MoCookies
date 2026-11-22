package com.electrifiedded.mocookies.block;

import com.electrifiedded.mocookies.MoCookies;
import com.electrifiedded.mocookies.world.CookieTeleporter;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Nether-portal style gateway framed with cookie blocks (2 wide, 3 tall inside a 4x5 frame). */
public class BlockCookiePortal extends Block {
    public static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class, EnumFacing.Axis.X, EnumFacing.Axis.Z);
    protected static final AxisAlignedBB X_AABB = new AxisAlignedBB(0.0, 0.0, 0.375, 1.0, 1.0, 0.625);
    protected static final AxisAlignedBB Z_AABB = new AxisAlignedBB(0.375, 0.0, 0.0, 0.625, 1.0, 1.0);

    public BlockCookiePortal() {
        super(Material.PORTAL);
        setDefaultState(blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.X));
        setTickRandomly(true);
        setBlockUnbreakable();
        setResistance(6000000.0F);
        setLightLevel(0.75F);
        setTranslationKey("cD_portal");
    }

    @Override protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, AXIS); }
    @Override public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(AXIS, (meta & 3) == 2 ? EnumFacing.Axis.Z : EnumFacing.Axis.X); }
    @Override public int getMetaFromState(IBlockState s) { return s.getValue(AXIS) == EnumFacing.Axis.Z ? 2 : 1; }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState s, IBlockAccess w, BlockPos p) {
        return s.getValue(AXIS) == EnumFacing.Axis.Z ? Z_AABB : X_AABB;
    }
    @Override public AxisAlignedBB getCollisionBoundingBox(IBlockState s, IBlockAccess w, BlockPos p) { return NULL_AABB; }
    @Override public boolean isOpaqueCube(IBlockState s) { return false; }
    @Override public boolean isFullCube(IBlockState s) { return false; }
    @Override public int quantityDropped(Random r) { return 0; }
    @Override public ItemStack getItem(World w, BlockPos p, IBlockState s) { return ItemStack.EMPTY; }
    @Override public BlockFaceShape getBlockFaceShape(IBlockAccess w, IBlockState s, BlockPos p, EnumFacing f) { return BlockFaceShape.UNDEFINED; }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(world, pos, state, rand);
        if (world.provider.isSurfaceWorld() && world.getGameRules().getBoolean("doMobSpawning") && rand.nextInt(2000) < world.getDifficulty().getId()) {
            BlockPos p = pos;
            while (p.getY() > 0 && !world.getBlockState(p).isTopSolid()) p = p.down();
            if (p.getY() > 0 && !world.getBlockState(p.up()).isNormalCube()) {
                EntityPigZombie e = new EntityPigZombie(world);
                e.setLocationAndAngles(p.getX() + 0.5, p.getY() + 1.1, p.getZ() + 0.5, rand.nextFloat() * 360F, 0F);
                e.timeUntilPortal = e.getPortalCooldown();
                world.spawnEntity(e);
            }
        }
    }

    private Block frame() { return MoCookies.cookieBlock; }

    /** @return true when a portal was created in the frame next to {@code pos}. */
    public boolean tryToCreatePortal(World world, BlockPos pos) {
        int b0 = 0, b1 = 0;
        if (world.getBlockState(pos.west()).getBlock() == frame() || world.getBlockState(pos.east()).getBlock() == frame()) b0 = 1;
        if (world.getBlockState(pos.north()).getBlock() == frame() || world.getBlockState(pos.south()).getBlock() == frame()) b1 = 1;
        if (b0 == b1) return false;
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        if (world.isAirBlock(new BlockPos(x - b0, y, z - b1))) { x -= b0; z -= b1; }
        for (int l = -1; l <= 2; l++) {
            for (int i = -1; i <= 3; i++) {
                boolean edge = l == -1 || l == 2 || i == -1 || i == 3;
                if ((l != -1 && l != 2) || (i != -1 && i != 3)) {
                    if (edge && world.getBlockState(new BlockPos(x + b0 * l, y + i, z + b1 * l)).getBlock() != frame()) return false;
                }
            }
        }
        IBlockState st = getDefaultState().withProperty(AXIS, b0 == 1 ? EnumFacing.Axis.X : EnumFacing.Axis.Z);
        for (int l = 0; l < 2; l++)
            for (int i = 0; i < 3; i++)
                world.setBlockState(new BlockPos(x + b0 * l, y + i, z + b1 * l), st, 2);
        return true;
    }

    private boolean isMe(IBlockAccess w, int x, int y, int z) { return w.getBlockState(new BlockPos(x, y, z)).getBlock() == this; }
    private boolean isFrame(IBlockAccess w, int x, int y, int z) { return w.getBlockState(new BlockPos(x, y, z)).getBlock() == frame(); }

    @Override
    public void neighborChanged(IBlockState state, World w, BlockPos pos, Block blockIn, BlockPos fromPos) {
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        int b0 = 0, b1 = 1;
        if (isMe(w, x - 1, y, z) || isMe(w, x + 1, y, z)) { b0 = 1; b1 = 0; }
        int i1 = y;
        while (isMe(w, x, i1 - 1, z)) i1--;
        if (!isFrame(w, x, i1 - 1, z)) { w.setBlockToAir(pos); return; }
        int j1 = 1;
        while (j1 < 4 && isMe(w, x, i1 + j1, z)) j1++;
        if (j1 == 3 && isFrame(w, x, i1 + j1, z)) {
            boolean f = isMe(w, x - 1, y, z) || isMe(w, x + 1, y, z);
            boolean f1 = isMe(w, x, y, z - 1) || isMe(w, x, y, z + 1);
            if (f && f1) w.setBlockToAir(pos);
            else if (!((isFrame(w, x + b0, y, z + b1) && isMe(w, x - b0, y, z - b1)) || (isFrame(w, x - b0, y, z - b1) && isMe(w, x + b0, y, z + b1))))
                w.setBlockToAir(pos);
        } else w.setBlockToAir(pos);
    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (world.isRemote || entity.isRiding() || entity.isBeingRidden() || !entity.isNonBoss()) return;
        if (entity.timeUntilPortal > 0) { entity.timeUntilPortal = 10; return; }
        entity.timeUntilPortal = 10;
        int target = world.provider.getDimension() == MoCookies.dimId ? 0 : MoCookies.dimId;
        WorldServer dest = world.getMinecraftServer().getWorld(target);
        entity.changeDimension(target, new CookieTeleporter(dest));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World w, BlockPos pos, Random r) {
        if (r.nextInt(100) == 0)
            w.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.5F, r.nextFloat() * 0.4F + 0.8F, false);
        boolean xAxis = state.getValue(AXIS) == EnumFacing.Axis.X;
        for (int l = 0; l < 4; l++) {
            int dir = r.nextInt(2) * 2 - 1;
            double px = pos.getX() + r.nextFloat(), py = pos.getY() + r.nextFloat(), pz = pos.getZ() + r.nextFloat();
            double vx = (r.nextFloat() - 0.5) * 0.5, vy = (r.nextFloat() - 0.5) * 0.5, vz = (r.nextFloat() - 0.5) * 0.5;
            if (xAxis) { pz = pos.getZ() + 0.5 + 0.25 * dir; vz = r.nextFloat() * 2.0F * dir; }
            else { px = pos.getX() + 0.5 + 0.25 * dir; vx = r.nextFloat() * 2.0F * dir; }
            w.spawnParticle(EnumParticleTypes.PORTAL, px, py, pz, vx, vy, vz);
        }
    }
}
