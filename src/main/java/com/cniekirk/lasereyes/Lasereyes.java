package com.cniekirk.lasereyes;

import com.cniekirk.lasereyes.registry.common.LaserEyesCommands;
import com.cniekirk.lasereyes.registry.common.LaserEyesServerNetworking;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class Lasereyes implements ModInitializer {

    public static final String ID = "lasereyes";

    public static Identifier identifier(String path) {
        return new Identifier(ID, path);
    }

    @Override
    public void onInitialize() {
        LaserEyesServerNetworking.init();
        LaserEyesCommands.init();
    }

}
