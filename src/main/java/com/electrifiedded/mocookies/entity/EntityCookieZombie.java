package com.electrifiedded.mocookies.entity;

import com.electrifiedded.mocookies.MoCookies;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

/** A cookie-wielding wolf that drops Emerald Cookies. */
public class EntityCookieZombie extends EntityWolf {
    public EntityCookieZombie(World w) {
        super(w);
        this.experienceValue = 64;
        setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.COOKIE));
    }

    @Override protected void setEquipmentBasedOnDifficulty(DifficultyInstance d) { setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.COOKIE)); }
    @Override public boolean processInteract(EntityPlayer p, EnumHand h) { return true; }
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.ENTITY_GENERIC_EAT; }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return SoundEvents.ENTITY_GENERIC_BIG_FALL; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_ENDERMEN_DEATH; }

    @Override
    protected void dropLoot(boolean recentlyHit, int looting, DamageSource src) {
        entityDropItem(new ItemStack(MoCookies.emeraldCookie), 0.0F);
        if (recentlyHit && rand.nextInt(200) - looting < 5) entityDropItem(new ItemStack(Items.COAL), 0.0F);
    }
}
