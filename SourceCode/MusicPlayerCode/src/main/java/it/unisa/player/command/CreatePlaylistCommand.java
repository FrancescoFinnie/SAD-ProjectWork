package it.unisa.player.command;
import it.unisa.player.model.Library;
import it.unisa.player.model.Playlist;

public class CreatePlaylistCommand implements Command {
    private Library receiver;
    private Playlist playlist;

    public CreatePlaylistCommand(Library receiver, Playlist playlist) {
        this.receiver = receiver;
        this.playlist = playlist;
    }
    @Override public void execute() { receiver.addPlaylist(playlist); }
    @Override public void undo() { receiver.removePlaylist(playlist); }
}