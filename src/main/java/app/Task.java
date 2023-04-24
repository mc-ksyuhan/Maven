package app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.humbleui.jwm.MouseButton;
import io.github.humbleui.skija.*;
import lombok.Getter;
import misc.CoordinateSystem2d;
import misc.CoordinateSystem2i;
import misc.Vector2d;
import misc.Vector2i;
import panels.PanelLog;
import java.util.ArrayList;
import java.util.Collections;
import static app.Colors.*;

/**
 * Класс задачи
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public class Task {
    /**
     * Текст задачи
     */
    public static final String TASK_TEXT = """
            ПОСТАНОВКА ЗАДАЧИ:
            Дано множество точек на плоскости. Выберем из этого
            множества две точки и проведем через них прямую.
            Назовем дистанцией такую величину, что на расстоянии
            от прямой не меньше, чем дистанция лежит хотя бы
            половина оставшихся точек множества (кроме этих двух).
            Найти такую пару точек, у которой дистанция будет
            минимальна. В качестве ответа: выделить эти две точки,
            нарисовать проходящую через них прямую, выделить
            точки, лежащие на дистанции, нарисовать дистанцию
            (отрезок от одной из самых удаленных точек до прямой),
            а также "коридор" (две прямые, параллельные найденной
            прямой, находящиеся на найденной дистанции).""";

    /**
     * Вещественная система координат задачи
     */
    @Getter
    private final CoordinateSystem2d ownCS;
    /**
     * Список точек (начальный)
     */
    @Getter
    private final ArrayList<Point> points;
    /**
     * Список линий
     */
    @Getter
    @JsonIgnore
    private final ArrayList<Line> lines;
    /**
     * Список точек выделенных
     */
    @Getter
    //@JsonIgnore
    private final ArrayList<Point> selected;
    /**
     * Список точек выживших
     */
    @Getter
    @JsonIgnore
    private final ArrayList<Point> survived;
    /**
     * Список дистанций выживших точек
     */
    @Getter
    @JsonIgnore
    private ArrayList<Double> distSurv;
    /**
     * Список двух точек на минимальной дистанции
     */
    @Getter
    @JsonIgnore
    private final ArrayList<Point> minDist;
    /**
     * Список точек на дистанции(которые не в minDist и не на макс. дистанции)
     */
    @Getter
    @JsonIgnore
    private final ArrayList<Point> dist;
    /**
     * Размер точки
     */
    private static final int POINT_SIZE = 3;
    /**
     * последняя СК окна
     */
    protected CoordinateSystem2i lastWindowCS;
    /**
     * Флаг, решена ли задача
     */
    private boolean solved;
    private int selectedPointsCounter=0;
    /**
     * Порядок разделителя сетки, т.е. раз во сколько отсечек будет нарисована увеличенная
     */
    private static final int DELIMITER_ORDER = 10;
    /**
     * коэффициент колёсика мыши
     */
    private static final float WHEEL_SENSITIVE = 0.001f;
    /**
     * Задача
     *
     * @param ownCS  СК задачи
     * @param points массив точек
     *
     */
    @JsonCreator
    public Task(
            @JsonProperty("ownCS") CoordinateSystem2d ownCS,
            @JsonProperty("points") ArrayList<Point> points,
            @JsonProperty("selected") ArrayList<Point> selected
    ) {
        this.ownCS = ownCS;
        this.points = points;
        this.selected = selected;
        this.survived = new ArrayList<>();
        this.lines = new ArrayList<>();
        this.distSurv = new ArrayList<>();
        this.minDist = new ArrayList<>();
        this.dist = new ArrayList<>();
    }

    /**
     * Получить положение курсора мыши в СК задачи
     *
     * @param x        координата X курсора
     * @param y        координата Y курсора
     * @param windowCS СК окна
     * @return вещественный вектор положения в СК задачи
     */
    @JsonIgnore
    public Vector2d getRealPos(int x, int y, CoordinateSystem2i windowCS) {
        return ownCS.getCoords(x, y, windowCS);
    }

    /**
     * Рисование
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    public void paint(Canvas canvas, CoordinateSystem2i windowCS) {
        // Сохраняем последнюю СК
        lastWindowCS = windowCS;
        // рисуем координатную сетку
        renderGrid(canvas, lastWindowCS);
        // рисуем задачу
        renderTask(canvas, windowCS);
    }
    /**
     * Добавить точку в массив
     *
     * @param pos положение
     */
    public void addPoint(Vector2d pos) {
        solved = false;
        Point newPoint = new Point(pos);
        points.add(newPoint);
        // Добавляем в лог запись информации
        PanelLog.info("точка " + newPoint + " добавлена");
    }

    /**
     * Решить
     */
    public void solve() {
        this.lines.add(new Line(
                selected.get(0),
                selected.get(1)
        ));

        //создаём массив выживших точек
        for (Point s: points) {
            if (!selected.contains(s)) survived.add(s);
        }
        for (Point t: survived) {
            Line l = lines.get(0);
            distSurv.add(l.getDistance(t));

        }
        //сортировка и вывод дистанций выживших точек
        Collections.sort(distSurv);
        for (double ds: distSurv) {
            System.out.println(ds);
        }
        for (Point u:survived){
            Line l = lines.get(0);
            if (l.getDistance(u)==distSurv.get(0) || l.getDistance(u)==distSurv.get(1))    {
                minDist.add(u);
            }
        }
        //точки на дистанции
        for (Point u:survived){
            Line l = lines.get(0);
            if (l.getDistance(u)>=distSurv.get(2) && l.getDistance(u)<distSurv.get(distSurv.size()/2))    {
                dist.add(u);
            }
        }
        //нарисуем вторую линию по точкам с мин. дистанциями
        this.lines.add(new Line(
                minDist.get(0),
                minDist.get(1)
        ));
        // w2 проекция w1 на вторую прямую
        Point w1 = findPoint(); //зелёная точка
        Line l2 = lines.get(1);
        double d = l2.getDistance(w1);
        double x11 = l2.pointA.pos.x;
        double y11 = l2.pointA.pos.y;
        double x12 = l2.pointB.pos.x;
        double y12 = l2.pointB.pos.y;
        double x2 = w1.pos.x;
        double y2 = w1.pos.y;
        double ex1 = -1;
        double ey1 = -1;
        double a = y12-y11;
        if (a==0) a=0.000001;
        double b = x11-x12;
        if (b==0) b=0.000001;
        double c = y11*x12-y12*x11;
        Point w2 = new Point(new Vector2d(0,0));

        for (int i = 0; i < 2; i++) {
            ex1=-ex1;
            for (int j = 0; j < 2; j++) {
                ey1=-ey1;
                w2 = new Point(new Vector2d(
                        ex1*d*(y11-y12)/Math.sqrt((y11-y12)*(y11-y12)+(x11-x12)*(x11-x12))+x2,
                        ey1*d*(x12-x11)/Math.sqrt((y11-y12)*(y11-y12)+(x11-x12)*(x11-x12))+y2
                ));
                if (Math.abs(a*w2.pos.x+b*w2.pos.y+c)<=0.5){
                    //нарисуем максимальную дистанцию
                    this.lines.add(new Line(w1, w2));
                    i=j=2; //break
                }
            }
        }
        //w3 точка симметричная отн-но найденной прямой
        Point w3 = new Point(new Vector2d(
                w1.pos.x+2*(w2.pos.x-w1.pos.x),
                w1.pos.y+2*(w2.pos.y-w1.pos.y)

        ));
        //this.lines.add(new Line(w2, w3));
        double c2 = -x2*a-y2*b;
        double xw12=0;
        double yw12=0;
        if (a==0.000001) {
            xw12=w1.pos.x+1;
            yw12=w1.pos.y;
        } else if (b==0.000001) {
            xw12=w1.pos.x;
            yw12=w1.pos.y+1;
        } else {
            xw12=w1.pos.x+1;
            yw12=-c2/b-a*xw12/b;
        }

        Point w12 = new Point(new Vector2d(
                xw12,
                yw12
        ));
        this.lines.add(new Line(w1, w12));

        double c3 = -w3.pos.x*a-w3.pos.y*b;
        double xw32=0;
        double yw32=0;
        if (a==0.000001) {
            xw32=w3.pos.x+1;
            yw32=w3.pos.y;
        } else if (b==0.000001) {
            xw32=w3.pos.x;
            yw32=w3.pos.y+1;
        } else {
            xw32=w3.pos.x+1;
            yw32=-c3/b-a*xw32/b;
        }

        Point w32 = new Point(new Vector2d(
                xw32,
                yw32
        ));
        this.lines.add(new Line(w3, w32));
    }

    /**
     * Выбрать точку из массива
     *
     * @param pos положение
     */
    public void selectPoint(Vector2d pos) {
        solved = false;
        Point newPoint = new Point(pos);
        for (Point p: points) {
            if (Math.abs(newPoint.pos.x-p.pos.x)<=0.2 && Math.abs(newPoint.pos.y-p.pos.y)<=0.2||(selected.size()>=2)) {
                if (!selected.contains(p) && selectedPointsCounter<=2) {
                    if (selected.size()<2) {
                        selectedPointsCounter++;
                        selected.add(p);
                        // Добавляем в лог запись информации
                        PanelLog.info(selectedPointsCounter + "ая точка " + p + " выбрана");
                    }
                    if (selectedPointsCounter==2||(selected.size()==2)) {
                        this.solve();
                        break;
                    }
                }
            }
        }
    }

    /**
     * Найти точку из массива выживших точек
     * с максимальной дистанцией
     */
    public Point findPoint() {
        for (Point u:survived){
            Line l = lines.get(0);
            if (l.getDistance(u)==distSurv.get(distSurv.size()/2))    {
                Point w1 = u;
                return w1;
            }
        }
        return new Point(new Vector2d(0,0));
    }

    /**
     * Клик мыши по пространству задачи
     *
     * @param pos         положение мыши
     * @param mouseButton кнопка мыши
     */
    public void click(Vector2i pos, MouseButton mouseButton) {
        if (lastWindowCS == null) return;
        // получаем положение на экране
        Vector2d taskPos = ownCS.getCoords(pos, lastWindowCS);
        if (mouseButton.equals(MouseButton.PRIMARY)) {
            addPoint(taskPos);
            // если правая, то во второе
        } else if (mouseButton.equals(MouseButton.SECONDARY)) {
            selectPoint(taskPos);
        }
    }

    /**
     * Добавить случайные точки
     *
     * @param cnt кол-во случайных точек
     */
    public void addRandomPoints(int cnt) {
        // если создавать точки с полностью случайными координатами,
        // то вероятность того, что они совпадут крайне мала
        // поэтому нужно создать вспомогательную малую целочисленную ОСК
        // для получения случайной точки мы будем запрашивать случайную
        // координату этой решётки (их всего 30х30=900).
        // после нам останется только перевести координаты на решётке
        // в координаты СК задачи
        CoordinateSystem2i addGrid = new CoordinateSystem2i(30, 30);
        // повторяем заданное количество раз
        for (int i = 0; i < cnt; i++) {
            // получаем случайные координаты на решётке
            Vector2i gridPos = addGrid.getRandomCoords();
            // получаем координаты в СК задачи
            Vector2d pos = ownCS.getCoords(gridPos, addGrid);
            addPoint(pos);
        }
    }

    /**
     * Очистить задачу
     */
    public void clear() {
        points.clear();
        solved = false;
    }

    /**
     * Отмена решения задачи
     */
    public void cancel() {
        solved = false;
    }

    /**
     * Рисование сетки
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    public void renderGrid(Canvas canvas, CoordinateSystem2i windowCS) {
        // сохраняем область рисования
        canvas.save();
        // получаем ширину штриха(т.е. по факту толщину линии)
        float strokeWidth = 0.03f / (float) ownCS.getSimilarity(windowCS).y + 0.5f;
        // создаём перо соответствующей толщины
        try (var paint = new Paint().setMode(PaintMode.STROKE).setStrokeWidth(strokeWidth).setColor(TASK_GRID_COLOR)) {
            // перебираем все целочисленные отсчёты нашей СК по оси X
            for (int i = (int) (ownCS.getMin().x); i <= (int) (ownCS.getMax().x); i++) {
                // находим положение этих штрихов на экране
                Vector2i windowPos = windowCS.getCoords(i, 0, ownCS);
                // каждый 10 штрих увеличенного размера
                float strokeHeight = i % DELIMITER_ORDER == 0 ? 5 : 2;
                // рисуем вертикальный штрих
                canvas.drawLine(windowPos.x, windowPos.y, windowPos.x, windowPos.y + strokeHeight, paint);
                canvas.drawLine(windowPos.x, windowPos.y, windowPos.x, windowPos.y - strokeHeight, paint);
            }
            // перебираем все целочисленные отсчёты нашей СК по оси Y
            for (int i = (int) (ownCS.getMin().y); i <= (int) (ownCS.getMax().y); i++) {
                // находим положение этих штрихов на экране
                Vector2i windowPos = windowCS.getCoords(0, i, ownCS);
                // каждый 10 штрих увеличенного размера
                float strokeHeight = i % 10 == 0 ? 5 : 2;
                // рисуем горизонтальный штрих
                canvas.drawLine(windowPos.x, windowPos.y, windowPos.x + strokeHeight, windowPos.y, paint);
                canvas.drawLine(windowPos.x, windowPos.y, windowPos.x - strokeHeight, windowPos.y, paint);
            }
        }
        // восстанавливаем область рисования
        canvas.restore();
    }
    /**
     * Рисование задачи
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    private void renderTask(Canvas canvas, CoordinateSystem2i windowCS) {
        canvas.save();
        // создаём перо
        try (var paint = new Paint()) {
            for (Point p : points) {
                if (minDist.contains(p)) {
                    paint.setColor(MINDIST_COLOR);
                } else if (p == findPoint()) {
                    paint.setColor(MAXDIST_COLOR);
                } else if (dist.contains(p)) {
                    paint.setColor(DIST_COLOR);
                } else {paint.setColor(p.getColor());}
                // y-координату разворачиваем, потому что у СК окна ось y направлена вниз, а в классическом представлении - вверх
                Vector2i windowPos = windowCS.getCoords(p.pos.x, p.pos.y, ownCS);
                // рисуем точку
                canvas.drawRect(Rect.makeXYWH(windowPos.x - POINT_SIZE, windowPos.y - POINT_SIZE, POINT_SIZE * 2, POINT_SIZE * 2), paint);
            }
            paint.setColor(SELECTL_COLOR);
            for (Line l : lines) {
                if (l==lines.get(0)) {
                    paint.setColor(SELECTL_COLOR);
                } else if (l==lines.get(1)){
                    paint.setColor(MINDIST_COLOR);
                } else if (l==lines.get(2)) {
                    paint.setColor(MAXDIST_COLOR);
                } else if (l==lines.get(3)||l==lines.get(4)) {
                    paint.setColor(CORR_COLOR);
                } else {paint.setColor(POINT_COLOR);}
                // опорные точки линии
                Vector2i pointA = windowCS.getCoords(l.pointA.pos, ownCS);
                Vector2i pointB = windowCS.getCoords(l.pointB.pos, ownCS);
                // вектор, ведущий из точки A в точку B
                Vector2i delta = Vector2i.subtract(pointA, pointB);
                // получаем максимальную длину отрезка на экране, как длину диагонали экрана
                int maxDistance = (int) windowCS.getSize().length();
                // получаем новые точки для рисования, которые гарантируют, что линия
                // будет нарисована до границ экрана
                Vector2i renderPointA = Vector2i.sum(pointA, Vector2i.mult(delta, maxDistance));
                Vector2i renderPointB = Vector2i.sum(pointA, Vector2i.mult(delta, -maxDistance));
                // рисуем линию
                if (l==lines.get(2)) canvas.drawLine(pointA.x, pointA.y, pointB.x, pointB.y, paint);
                else canvas.drawLine(renderPointA.x, renderPointA.y, renderPointB.x, renderPointB.y, paint);
            }
        }
        canvas.restore();
    }
    /**
     * Масштабирование области просмотра задачи
     *
     * @param delta  прокрутка колеса
     * @param center центр масштабирования
     */
    public void scale(float delta, Vector2i center) {
        if (lastWindowCS == null) return;
        // получаем координаты центра масштабирования в СК задачи
        Vector2d realCenter = ownCS.getCoords(center, lastWindowCS);
        // выполняем масштабирование
        ownCS.scale(1 + delta * WHEEL_SENSITIVE, realCenter);
    }
    /**
     * Рисование курсора мыши
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     * @param font     шрифт
     * @param pos      положение курсора мыши
     */
    public void paintMouse(Canvas canvas, CoordinateSystem2i windowCS, Font font, Vector2i pos) {
        // создаём перо
        try (var paint = new Paint().setColor(TASK_GRID_COLOR)) {
            // сохраняем область рисования
            canvas.save();
            // рисуем перекрестие
            canvas.drawRect(Rect.makeXYWH(0, pos.y - 1, windowCS.getSize().x, 2), paint);
            canvas.drawRect(Rect.makeXYWH(pos.x - 1, 0, 2, windowCS.getSize().y), paint);
            // смещаемся немного для красивого вывода текста
            canvas.translate(pos.x + 3, pos.y - 5);
            // положение курсора в пространстве задачи
            Vector2d realPos = getRealPos(pos.x, pos.y, lastWindowCS);
            // выводим координаты
            canvas.drawString(realPos.toString(), 0, 0, font, paint);
            // восстанавливаем область рисования
            canvas.restore();
        }
    }
}