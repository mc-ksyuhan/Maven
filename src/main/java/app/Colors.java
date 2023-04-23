package app;

import misc.Misc;

/**
 * Класс цветов
 */
public class Colors {
    /**
     * Цвет текста заголовка
     */
    public static final int LABEL_TEXT_COLOR = Misc.getColor(64, 255, 255, 255);
    /**
     * Цвет подложки панелей
     */
    public static final int PANEL_BACKGROUND_COLOR = Misc.getColor(32, 0, 0, 0);
    /**
     * Цвет фона
     */
    public static final int APP_BACKGROUND_COLOR = Misc.getColor(255, 38, 70, 83);
    /**
     * Цвет подложки поля ввода
     */
    public static final int FIELD_BACKGROUND_COLOR = Misc.getColor(255, 255, 255, 255);
    /**
     * Цвет текста
     */
    public static final int FIELD_TEXT_COLOR = Misc.getColor(255, 0, 0, 0);
    public static final int MULTILINE_TEXT_COLOR = Misc.getColor(64, 255, 255, 255);
    /**
     * Цвет кнопки
     */
    public static final int BUTTON_COLOR = Misc.getColor(80, 0, 0, 0);
    /**
     * Цвет точек
     */
    public static final int POINT_COLOR = Misc.getColor(200, 255, 255, 0);
    /**
     * Цвет первой линии
     */
    public static final int SELECTL_COLOR = Misc.getColor(200, 0, 255, 255);
    /**
     * Цвет мин.дист. точек и второй линии
     */
    public static final int MINDIST_COLOR = Misc.getColor(200, 255, 0, 0);
    /**
     * Цвет точек, расположенных на дистанции
     */
    public static final int DIST_COLOR = Misc.getColor(200, 255, 0, 255);
    /**
     * Цвет макс.дист. точки и макс.дист
     */
    public static final int MAXDIST_COLOR = Misc.getColor(200, 0, 200, 0);


    /*private Colors() {
        throw new AssertionError("Вызов этого конструктора запрещён"); (запрещённый конструктор)
    }*/
    /**
     * Цвет заливки панели
     */
    public static final int DIALOG_BACKGROUND_COLOR = Misc.getColor(230, 70, 38, 83);
    /**
     * Цвет сетки
     */
    public static final int TASK_GRID_COLOR = Misc.getColor(64, 255, 255, 255);
    /**
     * Цвет подложки
     */
    public static final int STATS_BACKGROUND_COLOR = Misc.getColor(64, 51, 200, 51);
    //public static final int STATS_COLOR = Misc.getColor(255, 51, 200, 51); (цвет подложки)
    /**
     * Цвет текста
     */
    public static final int STATS_TEXT_COLOR = Misc.getColor(255, 255, 255, 255);
    /**
     * Цвет текста
     */
    public static final int HELP_TEXT = Misc.getColor(255, 255, 255, 255);
    /**
     * Цвет фона
     */
    public static final int HELP_TEXT_BACKGROUND = Misc.getColor(50, 0, 0, 0);
    /**
     * Цвет заливки панели
     */
    public static final int SCROLLER_BACKGROUND_COLOR = Misc.getColor(150, 83, 38, 70);
    /**
     * Цвет заливки панели
     */
    public static final int SCROLLER_COLOR = Misc.getColor(255, 83, 38, 70);
}