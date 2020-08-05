package top.whitecola.wlbot;

import java.lang.reflect.Method;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.internal.EventInternalJvmKt;
import top.dsbbs2.common.lambda.INoThrowsRunnable;

public final class EventUtils {
private EventUtils() {}
@SuppressWarnings("unchecked")
public static <T extends Event> void registerEvents(Object o)
{
	INoThrowsRunnable.invoke(()->{
		Method[] methods=o.getClass().getDeclaredMethods();
		for(Method i:methods)
		{
			i.setAccessible(true);
			EventHandler e=i.getAnnotation(EventHandler.class);
			if(e!=null)
			{
				if(i.getReturnType().equals(void.class) || i.getReturnType().equals(Void.class))
				{
					if(i.getParameters().length==1)
					{
						if(Event.class.isAssignableFrom(i.getParameterTypes()[0]))
						{
							EventInternalJvmKt._subscribeEventForJaptOnly((Class<T>)i.getParameterTypes()[0], BukkitWhitelistBot.instance.bot, new IEventHandler<T>() {

								@Override
								public void accept(T t) {
									INoThrowsRunnable.invoke(()->i.invoke(o, t));
								}
								
							});
						}
					}
				}
			}
		}
	});
}
}
