package com.vadom.socket.chat.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class CommandTest {

    @Test
    void isCommandTest() {
        Assertions.assertFalse(Command.isCommand(""));
        Assertions.assertFalse(Command.isCommand(Command.prefix
                .substring(0, Command.prefix.length() - 2)));
        Assertions.assertFalse(Command.isCommand(Command.prefix
                .substring(0, Command.prefix.length() - 1) + "d"));
        Assertions.assertFalse(Command.isCommand("d " + Command.prefix + "d"));
        Assertions.assertTrue(Command.isCommand(Command.prefix + "d"));
    }

    @Test
    void getLineUpCommandTest() {
        String command = "command";
        String key1 = "key1";
        String key2 = "key2";
        String arg1 = "arg1";
        String arg2 = "arg2";
        String fullCommand = Command.prefix + command + " " +
                Command.prefixKeys + key1 + " " +
                Command.prefixKeys + key2 + " " + arg1 + " " + arg2;

        System.out.println(fullCommand);

        Map<Command.LineUp, List<String>> lineUpCommand =
                Command.getLineUpCommand(fullCommand);

        Assertions.assertEquals(3, lineUpCommand.size());

        for (Map.Entry<Command.LineUp, List<String>> map :
                lineUpCommand.entrySet()) {
            switch (map.getKey()) {
                case COMMAND -> {
                    Assertions.assertTrue(map.getValue().contains(command));
                    Assertions.assertEquals(1, map.getValue().size());
                }
                case KEYS -> {
                    Assertions.assertTrue(map.getValue().contains(key1));
                    Assertions.assertTrue(map.getValue().contains(key2));
                    Assertions.assertEquals(2, map.getValue().size());
                }
                case ARGS -> {
                    Assertions.assertTrue(map.getValue().contains(arg1));
                    Assertions.assertTrue(map.getValue().contains(arg2));
                    Assertions.assertEquals(2, map.getValue().size());
                }
            }
        }

        System.out.println(lineUpCommand);
    }
}