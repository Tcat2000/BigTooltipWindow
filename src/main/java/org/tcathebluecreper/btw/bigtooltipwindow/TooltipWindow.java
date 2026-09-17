package org.tcathebluecreper.btw.bigtooltipwindow;

import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ScrollerView;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Slider;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lowdragmc.lowdraglib2.gui.ui.style.StylesheetManager;
import com.lowdragmc.lowdraglib2.gui.ui.window.ModularUIWindow;
import dev.vfyjxf.taffy.style.TaffyDimension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

import java.util.List;

public class TooltipWindow extends ModularUIWindow {
    public TooltipWindow() {
        super(createScreen(), "Tooltip Window");
    }

    private static ModularUI createScreen() {
        UIElement background = new UIElement();
        background.getLayout().widthPercent(100).heightPercent(100);
        background.addClass("panel_bg");
        ScrollerView scroller = new ScrollerView();
        background.addEventListener(UIEvents.LAYOUT_CHANGED, event -> scroller.getLayout().setHeight(TaffyDimension.length(event.currentElement.getSizeHeight() - 22)));
        scroller.getLayout().bottom(0);
        UIElement tooltip = new UIElement();
        tooltip.getLayout().setHeight(TaffyDimension.length(1));
        Slider scale = new Slider.Horizontal();
        scale.setValue(1f);
        scale.setMaxValue(3);
        scale.setMinValue(0.25f);
        background.addChildren(scale, scroller);
        scroller.addScrollViewChild(tooltip);

        tooltip.getStyle().background(
            (graphics, mouseX, mouseY, x, y, width, height, partialTicks) -> {
                Player player = Minecraft.getInstance().player;
                ItemStack stack = player != null ? player.getMainHandItem() : ItemStack.EMPTY;
                if(player != null && Minecraft.getInstance().screen instanceof AbstractContainerScreen<?> screen) {
                    if(!player.containerMenu.getCarried().isEmpty()) stack = player.containerMenu.getCarried();
                    else if(screen.getSlotUnderMouse() != null && screen.getSlotUnderMouse().hasItem()) stack = screen.getSlotUnderMouse().getItem();
                }
                graphics.pose().pushPose();
                graphics.pose().scale(scale.getValue(),scale.getValue(),scale.getValue());

                Font font = Minecraft.getInstance().font;
                if(!stack.isEmpty()) {
                    graphics.renderItem(stack, (int) ((x / scale.getValue()) + 5), (int) (y / scale.getValue()) + 5);
                    graphics.renderItemDecorations(Minecraft.getInstance().font, stack, (int) ((x / scale.getValue()) + 5), (int) (y / scale.getValue()) + 5);

                    List<ClientTooltipComponent> components = ClientHooks.gatherTooltipComponents(stack, Screen.getTooltipFromItem(Minecraft.getInstance(), stack), stack.getTooltipImage(), 0, (int) (width / scale.getValue()) - 25, 0, font);
                    if(!components.isEmpty()) {
                        RenderTooltipEvent.Pre preEvent = ClientHooks.onRenderTooltipPre(stack, graphics, (int) (x / scale.getValue()), (int) (y / scale.getValue()), graphics.guiWidth(), graphics.guiHeight(), components, font, DefaultTooltipPositioner.INSTANCE);
                        if(preEvent.isCanceled()) {
                            return;
                        }

                        int i = 0;
                        int j = components.size() == 1 ? -2 : 0;

                        for(ClientTooltipComponent clienttooltipcomponent : components) {
                            int k = clienttooltipcomponent.getWidth(preEvent.getFont());
                            if(k > i) {
                                i = k;
                            }

                            j += clienttooltipcomponent.getHeight();
                        }

                        int l = (int) (30 + 1 / scale.getValue() * 5);
                        int i1 = (int) (y / scale.getValue()) + 8;
                        graphics.pose().pushPose();
                        RenderTooltipEvent.Color colorEvent = ClientHooks.onRenderTooltipColor(stack, graphics, l, i1, preEvent.getFont(), components);
                        TooltipRenderUtil.renderTooltipBackground(graphics, l, i1, i, j, 400, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd(), colorEvent.getBorderStart(), colorEvent.getBorderEnd());
                        tooltip.getLayout().setHeight(TaffyDimension.length(j * scale.getValue()));
                        graphics.pose().translate(0.0F, 0.0F, 400.0F);
                        int k1 = i1;

                        for(int l1 = 0; l1 < components.size(); ++l1) {
                            ClientTooltipComponent compartmentalisation1 = components.get(l1);
                            compartmentalisation1.renderText(preEvent.getFont(), l, k1, graphics.pose().last().pose(), graphics.bufferSource());
                            k1 += compartmentalisation1.getHeight() + (l1 == 0 ? 2 : 0);
                        }

                        k1 = i1;

                        for(int k2 = 0; k2 < components.size(); ++k2) {
                            ClientTooltipComponent compartmentalisation2 = components.get(k2);
                            compartmentalisation2.renderImage(preEvent.getFont(), l, k1, graphics);
                            k1 += compartmentalisation2.getHeight() + (k2 == 0 ? 2 : 0);
                        }

                        graphics.pose().popPose();
                    }
                }
                else {
                    graphics.drawString(font, "Nothing to show", (int) x, (int) y, -1);
                }
                graphics.pose().popPose();
            }
        );

        return ModularUI.of(UI.of(
            background,
            StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.MODERN))
        );
    }
}
