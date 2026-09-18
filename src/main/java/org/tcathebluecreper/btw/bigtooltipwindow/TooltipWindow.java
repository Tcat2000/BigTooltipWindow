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

        tooltip.getStyle().background(new TooltipTexture(scale, tooltip));

        return ModularUI.of(UI.of(
            background,
            StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.MODERN))
        );
    }
}
