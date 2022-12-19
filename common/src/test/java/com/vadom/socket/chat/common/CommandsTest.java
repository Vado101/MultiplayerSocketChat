package com.vadom.socket.chat.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandsTest {

    @Test
    void getCommandTest() {
        String fullCommand = Command.prefix + Commands.CONNECT;
        Commands command = Commands.CONNECT.getCommand(fullCommand);
        assertSame(command, Commands.CONNECT);

        fullCommand = Command.prefix + Commands.CONNECT.toString().toLowerCase();
        command = Commands.CONNECT.getCommand(fullCommand);
        assertSame(command, Commands.CONNECT);

        String finalFullCommand = Command.prefix + Commands.CONNECT + "d";
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Commands.CONNECT.getCommand(finalFullCommand));
    }
}