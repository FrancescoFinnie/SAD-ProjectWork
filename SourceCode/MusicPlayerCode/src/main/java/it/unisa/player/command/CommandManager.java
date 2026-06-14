package it.unisa.player.command;

import java.util.Stack;

// Gestore dei comandi per eseguire e annullare le operazioni
public class CommandManager {
    private Stack<Command> history = new Stack<>();

    public void executeCommand(Command c) {
        c.execute();
        history.push(c);
    }

    public void undo() {
        if (!history.isEmpty()) {
            Command lastCommand = history.pop();
            lastCommand.undo();
        }
    }
}