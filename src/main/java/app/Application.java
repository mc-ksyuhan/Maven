package app;

import io.github.humbleui.jwm.*;

import java.util.function.Consumer;

public class Application implements Consumer<Event> {
    /**
     * Одно приложение
     */
    private final Window window;
    public Application() {
        // создаём окно
        window = App.makeWindow();
        window.setEventListener(this);
        window.setVisible(true);
    }

    /**
     * обработчик событий
     * @param e the input argument
     */
    @Override
    public void accept(Event e) {
        if (e instanceof EventWindowClose) {
            App.terminate();
        }else if (e instanceof EventWindowCloseRequest) {
            window.close();

        }
    }
}