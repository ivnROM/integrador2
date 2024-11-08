package app;

import app.view.MainFrame;
import app.view.ResultFrame;

public class MainController {
    private MainFrame mainFrame;
    private ResultFrame resultFrame;

    public MainController() {
        mainFrame = new MainFrame(this);
    }

    public void startApp() {
        mainFrame.setVisible(true);
    }
}
