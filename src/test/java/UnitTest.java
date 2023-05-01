import app.Line;
import app.Point;
import app.Task;
import misc.CoordinateSystem2d;
import misc.Vector2d;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс тестирования
 */
public class UnitTest {

    /**
     * Тест
     *
     * @param points        список точек
*/
    private static void test(ArrayList<Point> points, ArrayList<Point> selected, ArrayList<Line> lines) {
        Task task = new Task(new CoordinateSystem2d(10, 10, 20, 20), points, selected);
        task.solve();
        // проверяем, что выбраны точки из общего списка точек
        for (Point p: task.getSelected()) {
            assert points.contains(p);
        }
        // проверяем, что выбранные точки не лежат в списке выживших точек
        for (Point p: selected) {
            assert !task.getSurvived().contains(p);
        }
        // проверяем, что первая линия проходит через выбранные точки
        assert task.getLines().get(0).equals(new Line(selected.get(0),selected.get(1)));
        // проверяем, что обе красные точки выбраны правильно
        assert lines.get(0).getDistance(task.getMinDist().get(0))==task.getDistSurv().get(0) || lines.get(0).getDistance(task.getMinDist().get(0))==task.getDistSurv().get(1);
        assert lines.get(0).getDistance(task.getMinDist().get(1))==task.getDistSurv().get(1) || lines.get(0).getDistance(task.getMinDist().get(1))==task.getDistSurv().get(0);
        // проверяем, что через них проходит красная прямая
        //assert lines.get(1).equals(new Line(task.getMinDist().get(0),task.getMinDist().get(1)));
        // проверяем, что зелёный отрезок правильно проведен
        assert lines.get(2).equals(new Line(task.getWlist().get(0),task.getWlist().get(1)));


    }


    /**
     * Первый тест
     */
    @Test
    public void test1() {
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(new Vector2d(1, 1)));
        points.add(new Point(new Vector2d(-1, 1)));
        points.add(new Point(new Vector2d(-5, 1)));
        points.add(new Point(new Vector2d(2, 1)));
        points.add(new Point(new Vector2d(1, 2)));
        points.add(new Point(new Vector2d(2, 2)));

        ArrayList<Point> selected = new ArrayList<>();
        selected.add(new Point(new Vector2d(1, 2)));
        selected.add(new Point(new Vector2d(-1, 1)));

        ArrayList<Line> lines = new ArrayList<>();
        lines.add(new Line(selected.get(0),selected.get(1))); //добавляем первую прямую
        lines.add(new Line(new Point(new Vector2d(1, 1)), //крА
                           new Point(new Vector2d(2, 2)))); //крВ
        lines.add(new Line(new Point(new Vector2d(2, 1)), //зел
                new Point(new Vector2d(1.5, 1.5)))); //проекция зел

        /*1.0 1.0 //крА
        2.0 2.0 //крВ
        2.0 1.0 //зел
        1.5 1.5 //проекция зел
        2.0 1.0 //зел
        3.0 2.0 //коридорная от зел
        1.0 2.0 //симм зел
        2.0 3.0 //коридоная от симм зел*/
        test(points, selected, lines);
    }

    /**
     * Второй тест
     */
    @Test
    public void test2() {
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(new Vector2d(1, 1)));
        points.add(new Point(new Vector2d(-1, 1)));
        points.add(new Point(new Vector2d(-5, 1)));
        points.add(new Point(new Vector2d(2, 1)));
        points.add(new Point(new Vector2d(1, 2)));
        points.add(new Point(new Vector2d(2, 2)));

        ArrayList<Point> selected = new ArrayList<>();
        selected.add(new Point(new Vector2d(1, 2)));
        selected.add(new Point(new Vector2d(-1, 1)));

        ArrayList<Line> lines = new ArrayList<>();
        lines.add(new Line(selected.get(0),selected.get(1))); //добавляем первую прямую

        test(points, selected, lines);
    }

    /**
     * Третий тест
     */
    @Test
    public void test3() {
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(new Vector2d(1, 1)));
        points.add(new Point(new Vector2d(-1, 1)));
        points.add(new Point(new Vector2d(-5, 1)));
        points.add(new Point(new Vector2d(2, 1)));
        points.add(new Point(new Vector2d(1, 2)));
        points.add(new Point(new Vector2d(2, 2)));

        ArrayList<Point> selected = new ArrayList<>();
        selected.add(new Point(new Vector2d(1, 2)));
        selected.add(new Point(new Vector2d(-1, 1)));

        ArrayList<Line> lines = new ArrayList<>();
        lines.add(new Line(selected.get(0),selected.get(1))); //добавляем первую прямую

        test(points, selected, lines);
    }
}