package com.vadom.socket.chat.common;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CommandTest {
    private final String command = "command";
    private final String key1 = "key1";
    private final String key2 = "key2";
    private final String arg1 = "arg1";
    private final String arg2 = "arg2";
    private String fullCommand = Command.prefix + command + " " +
            Command.prefixKeys + key1 + " " + Command.prefixKeys + key2 + " " +
            arg1 + " " + arg2;

    @Test
    void isCommandTest() {
        assertFalse(Command.isCommand(""));
        assertFalse(Command.isCommand(Command.prefix
                .substring(0, Command.prefix.length() - 2)));
        assertFalse(Command.isCommand(Command.prefix
                .substring(0, Command.prefix.length() - 1) + "d"));
        assertFalse(Command.isCommand("d " + Command.prefix + "d"));
        assertTrue(Command.isCommand(Command.prefix + "d"));
    }

    @Test
    void getCommandComponentsTest() {
        System.out.println(fullCommand);
        Map<Command.Component, List<String>> components =
                Command.getCommandComponents(fullCommand);
        checkCommandComponents(components, 3, true, true);

        for (Map.Entry<Command.Component, List<String>> map :
                components.entrySet()) {
            switch (map.getKey()) {
                case COMMAND -> {
                    assertTrue(map.getValue().contains(command));
                    assertEquals(1, map.getValue().size());
                }
                case KEYS -> {
                    assertTrue(map.getValue().contains(key1));
                    assertTrue(map.getValue().contains(key2));
                    assertEquals(2, map.getValue().size());
                }
                case ARGS -> {
                    assertTrue(map.getValue().contains(arg1));
                    assertTrue(map.getValue().contains(arg2));
                    assertEquals(2, map.getValue().size());
                }
            }
        }

        System.out.println(components);


        fullCommand = Command.prefix + command + " " + arg1;
        System.out.println(fullCommand);
        components = Command.getCommandComponents(fullCommand);
        checkCommandComponents(components, 2, false, true);

        for (Map.Entry<Command.Component, List<String>> map :
                components.entrySet()) {
            switch (map.getKey()) {
                case COMMAND -> {
                    assertTrue(map.getValue().contains(command));
                    assertEquals(1, map.getValue().size());
                }
                case KEYS -> fail();
                case ARGS -> {
                    assertTrue(map.getValue().contains(arg1));
                    assertEquals(1, map.getValue().size());
                }
            }
        }

        System.out.println(components);


        // the command should always come first
        // in this case, instead of a command, there will be a key
        fullCommand = Command.prefix + key1;
        System.out.println(fullCommand);
        components = Command.getCommandComponents(fullCommand);
        checkCommandComponents(components, 1, false, false);

        for (Map.Entry<Command.Component, List<String>> map :
                components.entrySet()) {
            switch (map.getKey()) {
                case COMMAND -> {
                    assertFalse(map.getValue().contains(command));
                    assertEquals(1, map.getValue().size());
                }
                case KEYS, ARGS -> fail();
            }
        }

        System.out.println(components);


        // command will be ""
        fullCommand = Command.prefix;
        System.out.println(fullCommand);
        components = Command.getCommandComponents(fullCommand);
        checkCommandComponents(components, 1, false, false);

        for (Map.Entry<Command.Component, List<String>> map :
                components.entrySet()) {
            switch (map.getKey()) {
                case COMMAND -> {
                    assertFalse(map.getValue().contains(command));
                    assertEquals(1, map.getValue().size());
                }
                case KEYS, ARGS -> fail();
            }
        }

        System.out.println(components);

        assertThrows(NullPointerException.class,
                () -> Command.getCommandComponents(null));
        assertThrows(IllegalArgumentException.class,
                () -> Command.getCommandComponents("null"));
    }

    @Test
    void getMessageFromArgsTest() {
        assertNull(Command.getMessageFromArgs(null));
        assertNull(Command.getMessageFromArgs(new ArrayList<>()));

        String expected = arg1 + " " + arg2;
        Map<Command.Component, List<String>> components =
                Command.getCommandComponents(fullCommand);
        assertEquals(expected, Command.getMessageFromArgs(
                components.get(Command.Component.ARGS)));
    }

    private void checkCommandComponents(
            Map<Command.Component, List<String>> components,
            int size, boolean keys, boolean args) {
        assertEquals(size, components.size());
        checkComponent(components, Command.Component.COMMAND, true);
        checkComponent(components, Command.Component.KEYS, keys);
        checkComponent(components, Command.Component.ARGS, args);
    }

    private void checkComponent(Map<Command.Component, List<String>> components,
                                Command.Component component, boolean isCommand) {
        if (isCommand) {
            assertTrue(components.containsKey(component));
        } else {
            assertFalse(components.containsKey(component));
        }
    }
}