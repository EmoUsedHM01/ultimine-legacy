package dev.ftb.mods.ftbultimine;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftblibrary.net.EditConfigChoicePacket;
import dev.ftb.mods.ftbultimine.config.FTBUltimineClientConfig;
import dev.ftb.mods.ftbultimine.config.FTBUltimineServerConfig;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;

public class FTBUltimineCommands {
    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ignoredCtx, Commands.CommandSelection ignoredSel) {
        dispatcher.register(Commands.literal("ftbultimine")
                .then(Commands.literal("serverconfig")
                        .requires(sourceStack -> sourceStack.isPlayer() && sourceStack.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .executes(context -> {
                            NetworkManager.sendToPlayer(context.getSource().getPlayerOrException(), EditConfigChoicePacket.server(FTBUltimineServerConfig.KEY));
                            return 1;
                        })
                )
                .then(Commands.literal("clientconfig")
                        .requires(CommandSourceStack::isPlayer)
                        .executes(context -> {
                            NetworkManager.sendToPlayer(context.getSource().getPlayerOrException(), EditConfigChoicePacket.client(FTBUltimineClientConfig.KEY));
                            return 1;
                        })
                )
                .then(Commands.literal("config")
                        .requires(CommandSourceStack::isPlayer)
                        .executes(context -> {
                            NetworkManager.sendToPlayer(context.getSource().getPlayerOrException(), EditConfigChoicePacket.choose(FTBUltimineClientConfig.KEY, FTBUltimineServerConfig.KEY, Component.translatable("key.ftbultimine")));
                            return 1;
                        })
                )
        );
    }
}
