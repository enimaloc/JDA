/*
 * Copyright 2015 Austin Keener, Michael Ritter, Florian Spieß, and the JDA contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dv8tion.jda.api.interactions.commands.build;

import java.util.EnumSet;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import net.dv8tion.jda.internal.utils.Checks;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * Builder for Application Commands.
 * <br>Use the factory methods provided by {@link Commands} to create instances of this interface.
 *
 * @see Commands
 */
public interface CommandData extends SerializableData
{
    /**
     * Configure the command name.
     *
     * @param  name
     *         The name, 1-32 characters (lowercase and alphanumeric for {@link Command.Type#SLASH})
     *
     * @throws IllegalArgumentException
     *         If the name is not between 1-32 characters long, or not lowercase and alphanumeric for slash commands
     *
     * @return The builder instance, for chaining
     */
    @Nonnull
    CommandData setName(@Nonnull String name);

    @Nonnull
    CommandData setUserPermissionRequired(long rawPermissions);

    @Nonnull
    default CommandData setUserPermissionRequired(Permission... permissions) {
        return setUserPermissionRequired(Permission.getRaw(permissions));
    }

    /**
     * The current command name
     *
     * @return The command name
     */
    @Nonnull
    String getName();

    long getUserPermissionRequiredRaw();

    default EnumSet<Permission> getUserPermissionRequired() {
        return Permission.getPermissions(getUserPermissionRequiredRaw());
    }

    /**
     * The {@link Command.Type}
     *
     * @return The {@link Command.Type}
     */
    @Nonnull
    Command.Type getType();

    /**
     * Converts the provided {@link Command} into a CommandData instance.
     *
     * @param  command
     *         The command to convert
     *
     * @throws IllegalArgumentException
     *         If null is provided or the command has illegal configuration
     *
     * @return An instance of CommandData
     *
     * @see    SlashCommandData#fromCommand(Command)
     */
    @Nonnull
    static CommandData fromCommand(@Nonnull Command command)
    {
        Checks.notNull(command, "Command");
        if (command.getType() != Command.Type.SLASH)
            return new CommandDataImpl(command.getType(), command.getName())
                    .setUserPermissionRequired(command.getUserPermissionRequiredRaw());

        return SlashCommandData.fromCommand(command);
    }

    /**
     * Parses the provided serialization back into an CommandData instance.
     * <br>This is the reverse function for {@link CommandData#toData()}.
     *
     * @param  object
     *         The serialized {@link DataObject} representing the command
     *
     * @throws net.dv8tion.jda.api.exceptions.ParsingException
     *         If the serialized object is missing required fields
     * @throws IllegalArgumentException
     *         If any of the values are failing the respective checks such as length
     *
     * @return The parsed CommandData instance, which can be further configured through setters
     *
     * @see    SlashCommandData#fromData(DataObject)
     * @see    Commands#fromList(Collection)
     */
    @Nonnull
    static CommandData fromData(@Nonnull DataObject object)
    {
        Checks.notNull(object, "DataObject");
        String name = object.getString("name");
        Command.Type commandType = Command.Type.fromId(object.getInt("type", 1));
        if (commandType != Command.Type.SLASH)
            return new CommandDataImpl(commandType, name).setUserPermissionRequired(object.getLong("default_member_permissions"));

        return SlashCommandData.fromData(object);
    }
}
