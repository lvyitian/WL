package top.whitecola.wlbot;

import java.util.function.Consumer;

import net.mamoe.mirai.event.Event;

public interface IEventHandler<T extends Event> extends Consumer<T>{
}
