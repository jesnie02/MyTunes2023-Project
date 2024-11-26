package GUI.Utility;

import javafx.scene.Scene;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ThemeManager {
    private static final String THEME_FILE = "resources/Styles/themeSave.txt";
    private static String currentTheme;
    private static List<Scene> openScenes = new ArrayList<>();

    public static void applyTheme(Scene scene, String cssPath) {
        if (currentTheme != null) {
            scene.getStylesheets().remove(currentTheme);
        }
        scene.getStylesheets().add(cssPath);
        currentTheme = cssPath;
        openScenes.add(scene);
    }



    public static void applyThemeToAllWindows(String cssPath) {
        for (Scene scene : openScenes) {
            scene.getStylesheets().remove(currentTheme);
            scene.getStylesheets().add(cssPath);
        }
        currentTheme = cssPath;
    }

    public static void removeScene(Scene scene) {
        openScenes.remove(scene);
    }
}