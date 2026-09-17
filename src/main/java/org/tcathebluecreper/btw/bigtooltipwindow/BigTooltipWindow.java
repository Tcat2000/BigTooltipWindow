package org.tcathebluecreper.btw.bigtooltipwindow;

import net.minecraft.commands.Commands;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(BigTooltipWindow.MODID)
public class BigTooltipWindow {
    public static final String MODID = "bigtooltipwindow";
    public static boolean needOpenWindow = false;

    public BigTooltipWindow() {
        // hi
    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onPostFrame(RenderFrameEvent.Post event) {
            if(needOpenWindow) {
                TooltipWindow window = new TooltipWindow();
                window.open(5,50,500,500, true);
                window.window().setAlwaysOnTop(true);
                needOpenWindow = false;
            }
        }

        @SubscribeEvent
        public static void registerClientCommand(RegisterCommandsEvent event) {
            event.getDispatcher().register(Commands.literal("tooltip_window").executes(ctx -> {
                BigTooltipWindow.needOpenWindow = true;
                return 1;
            }));
        }
    }
}
