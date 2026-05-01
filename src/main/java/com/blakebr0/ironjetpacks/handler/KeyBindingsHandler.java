package com.blakebr0.ironjetpacks.handler;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.lib.ModTooltips;
import com.blakebr0.ironjetpacks.network.NetworkHandler;
import com.blakebr0.ironjetpacks.network.message.DecrementThrottleMessage;
import com.blakebr0.ironjetpacks.network.message.IncrementThrottleMessage;
import com.blakebr0.ironjetpacks.network.message.ToggleEngineMessage;
import com.blakebr0.ironjetpacks.network.message.ToggleHoverMessage;
import com.blakebr0.ironjetpacks.network.message.ToggleHUDMessage;
import com.blakebr0.ironjetpacks.network.message.UpdateInputMessage;
import com.blakebr0.ironjetpacks.util.JetpackUtils;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class KeyBindingsHandler {
    private static KeyMapping keyEngine;
    private static KeyMapping keyHover;
    private static KeyMapping keyHUD;
    private static KeyMapping keyDescend;
    private static KeyMapping keyIncrementThrottle;
    private static KeyMapping keyDecrementThrottle;
    
    private static boolean up = false;
    private static boolean down = false;
    private static boolean forwards = false;
    private static boolean backwards = false;
    private static boolean left = false;
    private static boolean right = false;
    private static boolean sprint = false;
    
    public static void onClientSetup() {
        keyEngine = create("engine", GLFW.GLFW_KEY_V, IronJetpacks.NAME);
        keyHover = create("hover", GLFW.GLFW_KEY_H, IronJetpacks.NAME);
        keyHUD = create("hud", InputConstants.UNKNOWN.getValue(), IronJetpacks.NAME);
        keyDescend = create("descend", InputConstants.UNKNOWN.getValue(), IronJetpacks.NAME);
        keyIncrementThrottle = create("increment_throttle", GLFW.GLFW_KEY_PERIOD, IronJetpacks.NAME);
        keyDecrementThrottle = create("decrement_throttle", GLFW.GLFW_KEY_COMMA, IronJetpacks.NAME);
    }
    
    private static KeyMapping create(String id, int key, String category) {
        return KeyBindingHelper.registerKeyBinding(new KeyMapping("key." + IronJetpacks.MOD_ID + "." + id, InputConstants.Type.KEYSYM, key, category));
    }
    
    public static void onClientTick(Minecraft client) {
        handleInputs(client);
        updateInputs(client);
    }
    
    private static void handleInputs(Minecraft client) {
        Player player = client.player;
        if (player == null)
            return;

        ItemStack stack = JetpackUtils.getJetpackStack(player);
        if (!(stack.getItem() instanceof JetpackItem jetpack))
            return;

        while (keyEngine.consumeClick()) {
            NetworkHandler.sendToServer(new ToggleEngineMessage());
            boolean on = !jetpack.isEngineOn(stack);
            Component state = on ? ModTooltips.ON.color(ChatFormatting.GREEN) : ModTooltips.OFF.color(ChatFormatting.RED);
            player.displayClientMessage(ModTooltips.TOGGLE_ENGINE.args(state), true);
        }

        while (keyHover.consumeClick()) {
            NetworkHandler.sendToServer(new ToggleHoverMessage());
            boolean on = !jetpack.isHovering(stack);
            Component state = on ? ModTooltips.ON.color(ChatFormatting.GREEN) : ModTooltips.OFF.color(ChatFormatting.RED);
            player.displayClientMessage(ModTooltips.TOGGLE_HOVER.args(state), true);
        }

        while (keyHUD.consumeClick()) {
            NetworkHandler.sendToServer(new ToggleHUDMessage());
            boolean on = !jetpack.isHUDEnabled(stack);
            Component state = on ? ModTooltips.ON.color(ChatFormatting.GREEN) : ModTooltips.OFF.color(ChatFormatting.RED);
            player.displayClientMessage(ModTooltips.TOGGLE_HUD.args(state), true);
        }

        while (keyIncrementThrottle.consumeClick()) {
            double throttle = jetpack.incrementThrottle(stack);
            Component throttleText = Component.literal((int) (throttle * 100) + "%").withStyle(ChatFormatting.GREEN);
            NetworkHandler.sendToServer(new IncrementThrottleMessage());
            player.displayClientMessage(ModTooltips.CHANGE_THROTTLE.args(throttleText), true);
        }

        while (keyDecrementThrottle.consumeClick()) {
            double throttle = jetpack.decrementThrottle(stack);
            Component throttleText = Component.literal((int) (throttle * 100) + "%").withStyle(ChatFormatting.RED);
            NetworkHandler.sendToServer(new DecrementThrottleMessage());
            player.displayClientMessage(ModTooltips.CHANGE_THROTTLE.args(throttleText), true);
        }
    }
    
    /*
     * Keyboard handling borrowed from Simply Jetpacks
     * https://github.com/Tomson124/SimplyJetpacks-2/blob/1.12/src/main/java/tonius/simplyjetpacks/client/handler/KeyTracker.java
     */
    public static void updateInputs(Minecraft client) {
        Options settings = client.options;
        
        if (client.getConnection() == null)
            return;
        
        boolean upNow = settings.keyJump.isDown();
        boolean downNow = keyDescend.isUnbound() ? settings.keyShift.isDown() : keyDescend.isDown();
        boolean forwardsNow = settings.keyUp.isDown();
        boolean backwardsNow = settings.keyDown.isDown();
        boolean leftNow = settings.keyLeft.isDown();
        boolean rightNow = settings.keyRight.isDown();
        boolean sprintNow = settings.keySprint.isDown();

        if (upNow != up || downNow != down || forwardsNow != forwards || backwardsNow != backwards || leftNow != left || rightNow != right || sprintNow != sprint) {
            up = upNow;
            down = downNow;
            forwards = forwardsNow;
            backwards = backwardsNow;
            left = leftNow;
            right = rightNow;
            sprint = sprintNow;

            NetworkHandler.sendToServer(new UpdateInputMessage(upNow, downNow, forwardsNow, backwardsNow, leftNow, rightNow, sprintNow));
            InputHandler.update(client.player, upNow, downNow, forwardsNow, backwardsNow, leftNow, rightNow, sprintNow);
        }
    }
}
