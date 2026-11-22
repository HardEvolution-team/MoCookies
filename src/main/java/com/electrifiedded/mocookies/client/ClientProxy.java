package com.electrifiedded.mocookies.client;

import com.electrifiedded.mocookies.CommonProxy;
import com.electrifiedded.mocookies.entity.EntityCookieZombie;
import net.minecraft.client.renderer.entity.RenderWolf;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit() {
        RenderingRegistry.registerEntityRenderingHandler(EntityCookieZombie.class, RenderWolf::new);
    }
}
