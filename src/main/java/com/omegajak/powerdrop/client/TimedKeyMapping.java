package com.omegajak.powerdrop.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

@OnlyIn(Dist.CLIENT)
public class TimedKeyMapping extends KeyBinding {
    private boolean wasDown = false;
    private long holdStartTime = 0;
    private long lastReleaseHoldTime = 0;
    private int holdStartCount = 0;
    private int holdEndCount = 0;

    public TimedKeyMapping(String description, IKeyConflictContext keyConflictContext, KeyModifier keyModifier, InputMappings.Type inputType, int keyCode, String category) {
        super(description, keyConflictContext, keyModifier, inputType, keyCode, category);
    }

    @Override
    public void setDown(boolean down) {
        super.setDown(down);
        trackEdges();
    }

    public boolean consumeHoldStart() {
        if (this.holdStartCount == 0) {
            return false;
        } else {
            this.holdStartCount--;
            return true;
        }
    }

    public boolean consumeHoldEnd() {
        if (this.holdEndCount == 0) {
            return false;
        } else {
            this.holdEndCount--;
            return true;
        }
    }

    public long getLastReleaseHoldTimeMs() {
        return lastReleaseHoldTime;
    }

    public long getTimeSinceKeyDown() {
        return System.currentTimeMillis() - holdStartTime;
    }

    private void trackEdges() {
        boolean isDown = this.isDown(); // Important to call this instead of using down directly since this factors in the modifier and conflict context
        if (!wasDown && isDown) {
            holdStartCount++;
            holdStartTime = System.currentTimeMillis();
        } else if (wasDown && !isDown) {
            holdEndCount++;
            lastReleaseHoldTime = getTimeSinceKeyDown();
        }

        wasDown = isDown;
    }
}
