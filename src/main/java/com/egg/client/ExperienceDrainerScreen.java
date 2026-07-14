package com.egg.client;

import com.egg.screen.ExperienceDrainerScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ExperienceDrainerScreen extends HandledScreen<ExperienceDrainerScreenHandler> {

    private static final Identifier TEXTURE = Identifier.of("egg", "textures/gui/experience_drainer.png");

    public ExperienceDrainerScreen(ExperienceDrainerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;  // à ajuster selon votre texture
        this.backgroundHeight = 240; // idem, plus haut vu le nombre de slots
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }
}