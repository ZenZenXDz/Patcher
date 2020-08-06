package club.sk1er.patcher.hooks;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class ChatStyleHook {

    public static HoverEvent getChatHoverEvent(ChatStyle chatComponent) {
        final HoverEvent hoverEvent = chatComponent.chatHoverEvent == null ? chatComponent.getParent().getChatHoverEvent() : chatComponent.chatHoverEvent;
        if (!PatcherConfig.safeChatClicks) {
            return hoverEvent;
        }
        final ClickEvent chatClickEvent = chatComponent.chatClickEvent;
        if (chatClickEvent == null || !(chatClickEvent.getAction().equals(ClickEvent.Action.OPEN_FILE)
            || chatClickEvent.getAction().equals(ClickEvent.Action.OPEN_URL)
            || chatClickEvent.getAction().equals(ClickEvent.Action.RUN_COMMAND))) {
            return hoverEvent;
        }
        final String msg = EnumChatFormatting.YELLOW.toString() + (chatClickEvent.getAction() == ClickEvent.Action.RUN_COMMAND ? "Runs " : "Opens ") + EnumChatFormatting.AQUA + chatClickEvent.getValue() + EnumChatFormatting.YELLOW.toString() + " on click";
        if (hoverEvent == null) {
            return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(msg));
        }
        if (hoverEvent.getAction().equals(HoverEvent.Action.SHOW_TEXT)) {
            final ChatComponentText append = new ChatComponentText(msg);
            final IChatComponent value = hoverEvent.getValue();
            if (value.getSiblings().contains(append) || value.getFormattedText().contains(msg)) {
                return hoverEvent;
            }
            final IChatComponent copy = value.createCopy();
            copy.appendText("\n");
            copy.appendText(msg);
            return new HoverEvent(HoverEvent.Action.SHOW_TEXT, copy);
        }
        return hoverEvent;
    }
}
