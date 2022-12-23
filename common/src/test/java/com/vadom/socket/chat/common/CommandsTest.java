package com.vadom.socket.chat.common;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandsTest {

    private final Commands.KEY[] keys = {Commands.KEY.NAME, Commands.KEY.CONFIRM};
    private final String[] args = {"Hello", "world", "!"};
    private Commands command = Commands.LOGIN;
    private String fullCommand = Command.prefix + command +
            " " + Command.prefixKeys + keys[0] +
            " " + Command.prefixKeys + keys[1] +
            " " + args[0] + " " + args[1] + " " + args[2];

    @Test
    void getCommandTest() {
        fullCommand = Command.prefix + Commands.CONNECT;
        command = Commands.CONNECT.getCommand(fullCommand);
        assertSame(command, Commands.CONNECT);

        fullCommand = Command.prefix + Commands.CONNECT.toString().toLowerCase();
        command = Commands.CONNECT.getCommand(fullCommand);
        assertSame(command, Commands.CONNECT);

        String finalFullCommand = Command.prefix + Commands.CONNECT + "d";
        assertThrows(IllegalArgumentException.class,
                () -> Commands.CONNECT.getCommand(finalFullCommand));
    }

    @Test
    void createCommandTest() {
        String createdCommand = Commands.createCommand(command, keys, args);
        assertEquals(fullCommand, createdCommand);
        System.out.println(createdCommand);

        fullCommand = Command.prefix + command;
        createdCommand = Commands.createCommand(command, null);
        assertEquals(fullCommand, createdCommand);
        System.out.println(createdCommand);

        assertThrows(NullPointerException.class,
                () -> Commands.createCommand(null, null));
    }

    @Test
    void containsKeyTest() {
        assertThrows(NullPointerException.class,
                () -> Commands.containsKey(null, Commands.KEY.NAME));
        assertFalse(Commands.containsKey(Command.prefix, (Commands.KEY[]) null));

       fullCommand = Command.prefix + command +
                " " + Command.prefixKeys + keys[0] +
                " " + args[0];

       assertTrue(Commands.containsKey(fullCommand, keys[0]));
       assertFalse(Commands.containsKey(fullCommand, keys[1]));

       fullCommand = Command.prefix + command +
               " " + Command.prefixKeys + keys[0] +
               " " + Command.prefixKeys + keys[1];

       assertTrue(Commands.containsKey(fullCommand, keys));
    }

    @Test
    void checkingArgumentsForErrorMessageTest() {
        assertNull(Commands.checkingArgumentsForErrorMessage(null));
        assertNull(Commands.checkingArgumentsForErrorMessage(
                new ArrayList<>()));

        List<String> argsList = new ArrayList<>();
        argsList.add(args[0]);
        argsList.add(args[1]);
        assertNull(Commands.checkingArgumentsForErrorMessage(argsList));

        argsList.add(0, Commands.ErrorCode.FAILED.name());
        String expected = Commands.ErrorCode.FAILED.name() + " " +
                args[0] + " " + args[1];
        assertEquals(expected,
                Commands.checkingArgumentsForErrorMessage(argsList));
    }

    @Test
    void commandHelpTest() {
        assertNull(Commands.commandHelp((Commands[]) null));
        assertNull(Commands.commandHelp());

        String expected = "Command list:\n  " + Commands.CONNECT + "   \t" +
                Commands.CONNECT.getDescription() + "\n  " +
                Commands.HELP + "      \t" +
                Commands.HELP.getDescription() + "\n";
        assertEquals(expected,
                Commands.commandHelp(Commands.CONNECT, Commands.HELP));
    }
}