package it.unisa.player.command;

// Command interface per il Command design pattern
public interface Command {
    void execute();
    void undo();
}