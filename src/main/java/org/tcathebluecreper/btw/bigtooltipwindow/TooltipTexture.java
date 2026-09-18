package org.tcathebluecreper.btw.bigtooltipwindow;

import com.lowdragmc.lowdraglib2.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib2.gui.texture.rendering.RegisteredGuiTextureRenderer;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Slider;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext;
import com.lowdragmc.lowdraglib2.registry.annotation.LDLRegisterClient;
import dev.vfyjxf.taffy.style.TaffyDimension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

import java.util.List;

public class TooltipTexture implements IGuiTexture {
    private final Slider scale;
    private final UIElement self;

    public TooltipTexture(Slider scale, UIElement self) {
        this.scale = scale;
        this.self = self;
    }

    @SuppressWarnings("unused")
    @LDLRegisterClient(name = "tooltip_texture", registry = "ldlib2:gui_texture_renderer")
    public static final class RegisteredTooltipTextureRenderer implements RegisteredGuiTextureRenderer<TooltipTexture, TooltipTexture.RegisteredTooltipTextureRenderer> {
        @Override
        public Class<TooltipTexture> type() {
            return TooltipTexture.class;
        }

        @Override
        public void draw(TooltipTexture texture, GUIContext context, float x, float y, float width, float height) {Player player = Minecraft.getInstance().player;
            GuiGraphicsExtractor graphics = context.graphics;
            UIElement self = texture.self;
            Slider scale = texture.scale;

            ItemStack stack = player != null ? player.getMainHandItem() : ItemStack.EMPTY;
            if(player != null && Minecraft.getInstance().gui.screen() instanceof AbstractContainerScreen<?> screen) {
                if(!player.containerMenu.getCarried().isEmpty()) stack = player.containerMenu.getCarried();
                else if(screen.getHoveredSlot() != null && screen.getHoveredSlot().hasItem()) stack = screen.getHoveredSlot().getItem();
            }
            graphics.pose().pushMatrix();
            graphics.pose().scale(scale.getValue(),scale.getValue());

            Font font = Minecraft.getInstance().font;
            if(!stack.isEmpty()) {
                graphics.item(stack, (int) ((x / scale.getValue()) + 5), (int) (y / scale.getValue()) + 5);
                graphics.itemDecorations(Minecraft.getInstance().font, stack, (int) ((x / scale.getValue()) + 5), (int) (y / scale.getValue()) + 5);

                List<ClientTooltipComponent> components = ClientHooks.gatherTooltipComponents(stack, Screen.getTooltipFromItem(Minecraft.getInstance(), stack), stack.getTooltipImage(), 0, (int) (width / scale.getValue()) - 25, 0, font);
                if(!components.isEmpty()) {
                    RenderTooltipEvent.Pre preEvent = ClientHooks.onRenderTooltipPre(stack, graphics, (int) (x / scale.getValue()), (int) (y / scale.getValue()), graphics.guiWidth(), graphics.guiHeight(), components, font, DefaultTooltipPositioner.INSTANCE);
                    if(preEvent.isCanceled()) {
                        return;
                    }

                    int textWidht = 0;
                    int tempHeight = components.size() == 1 ? -2 : 0;

                    for(ClientTooltipComponent clienttooltipcomponent : components) {
                        int k = clienttooltipcomponent.getWidth(preEvent.getFont());
                        if(k > textWidht) {
                            textWidht = k;
                        }

                        tempHeight += clienttooltipcomponent.getHeight(preEvent.getFont());
                    }

                    int l = (int) (30 + 1 / scale.getValue() * 5);
                    int internalY = (int) (y / scale.getValue()) + 8;
                    graphics.pose().pushMatrix();
                    RenderTooltipEvent.Texture textureEvent = ClientHooks.onRenderTooltipTexture(stack, graphics, (int) ((x / scale.getValue()) + 5), (int) (y / scale.getValue()) + 5, preEvent.getFont(), components, stack.get(DataComponents.TOOLTIP_STYLE));
                    TooltipRenderUtil.extractTooltipBackground(graphics, l, internalY, textWidht, tempHeight, textureEvent.getTexture());
                    self.getLayout().setHeight(TaffyDimension.length(tempHeight * scale.getValue()));
                    graphics.pose().translate(0.0F, 0.0F);
                    int localY = internalY;

                    for(int i = 0; i < components.size(); ++i) {
                        ClientTooltipComponent compartmentalisation1 = components.get(i);
                        compartmentalisation1.extractText(graphics, preEvent.getFont(), l, localY);
                        localY += compartmentalisation1.getHeight(preEvent.getFont()) + (i == 0 ? 2 : 0);
                    }

                    localY = internalY;

                    for(int i = 0; i < components.size(); ++i) {
                        ClientTooltipComponent compartmentalisation2 = components.get(i);
                        compartmentalisation2.extractImage(preEvent.getFont(), l, localY, textWidht, tempHeight, graphics);
                        localY += compartmentalisation2.getHeight(preEvent.getFont()) + (i == 0 ? 2 : 0);
                    }

                    graphics.pose().popMatrix();
                }
            }
            else {
                graphics.text(font, "Nothing to show", (int) x, (int) y, -1);
            }
            graphics.pose().popMatrix();
        }
    }
}
