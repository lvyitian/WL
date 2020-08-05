package top.whitecola.wlbot.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

public interface ICommand extends CommandExecutor,TabCompleter{

	@Override
	default List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		return new ArrayList<>();
	}
	@NotNull
	public default String getUsage() {return "";}
	@NotNull
	public default Vector<Class<?>> getArgumentsTypes(){return new Vector<>();}
	@NotNull
	public default Vector<String> getArgumentsDescriptions(){return new Vector<>();}
	@NotNull
	public default String getPermission() {return "";}
	@NotNull
	public default String getClassName() {
		return this.getClass().getSimpleName();
	}
	@NotNull
	public default String getDescription(){return "";};
}
