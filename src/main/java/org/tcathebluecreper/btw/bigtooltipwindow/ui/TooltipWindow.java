package org.tcathebluecreper.btw.bigtooltipwindow.ui;

import com.lowdragmc.lowdraglib2.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Slider;
import com.lowdragmc.lowdraglib2.gui.ui.styletemplate.MCSprites;
import com.lowdragmc.lowdraglib2.gui.ui.window.ModularUIWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TooltipWindow extends ModularUIWindow {
    public TooltipWindow() {
        super(createScreen(), "Tooltip Window");
    }

    private static ModularUI createScreen() {
        UIElement background = new UIElement();
        background.getLayout().widthPercent(100).heightPercent(100);
        background.getStyle().background(MCSprites.RECT);
        UIElement tooltip = new UIElement();
        Slider scale = new Slider.Horizontal();
        scale.setMaxValue(3);
        scale.setMinValue(0.25f);
        background.addChildren(tooltip, scale);
        tooltip.getStyle().background(
            (graphics, mouseX, mouseY, x, y, width, height, partialTicks) -> {
                ItemStack stack = Minecraft.getInstance().player != null ? Minecraft.getInstance().player.getMainHandItem() : ItemStack.EMPTY;
                if(Minecraft.getInstance().screen instanceof AbstractContainerScreen<?> screen) {
                    if(screen.getSlotUnderMouse() != null) stack = screen.getSlotUnderMouse().getItem();
                }
                graphics.pose().pushPose();
                graphics.pose().scale(scale.getValue(),scale.getValue(),scale.getValue());

                graphics.renderItem(stack, (int) (x + 5), (int) (y + 5));
                graphics.renderItemDecorations(Minecraft.getInstance().font, stack, (int) (x + 5), (int) (y + 5));
                graphics.renderTooltip(Minecraft.getInstance().font, stack, (int) x + 16, (int) (y + 30));
                graphics.pose().popPose();
            }
        );

        return ModularUI.of(UI.of(
            background
        ));
    }
}
