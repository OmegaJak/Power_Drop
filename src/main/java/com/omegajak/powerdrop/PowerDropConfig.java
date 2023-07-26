package com.omegajak.powerdrop;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class PowerDropConfig {
    public static final PowerDropConfig INSTANCE;
    public static final ForgeConfigSpec SPEC;

    public final ForgeConfigSpec.BooleanValue adjustFOV;

    PowerDropConfig(ForgeConfigSpec.Builder builder) {
        adjustFOV = builder.comment("Set to false to disable FOV adjustment when charging for a power drop").define("adjust_fov", true);
    }

    static {
        Pair<PowerDropConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(PowerDropConfig::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }
}
