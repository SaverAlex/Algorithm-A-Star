package com.company;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RouteBuilding implements Navigator {

    private Point startPoint; // Координаты старта
    private Point endPoint; // Координаты конца
    private final ArrayList<Point> closed = new ArrayList<>(); // Посещённые вершины
    private final ArrayList<Point> open = new ArrayList<>(); // Вершины готовые к посещению
    private final Map<Point,Point> from = new HashMap<>(); // Откуда мы посещаем эту вершину, с минимальным расстоянием
    private final Map<Point,Integer> distanceToThePoint = new HashMap<>(); // Расстояние от старта до точки
    private final Map<Point,Double> distanceToTheEnd = new HashMap<>(); // Растояние до конца

    @Override
    public char[][] searchRoute(char[][] map) {
        findStartEnd(map); // Находим начало и конец
        open.add(startPoint);
        distanceToThePoint.put(startPoint,0); // Расстояние до старта принимаем равное 0
        distanceToTheEnd.put(startPoint,distanceToThePoint.get(startPoint) + heuristic(startPoint, endPoint));
        Point currentPoint;
        while(!open.isEmpty()){ // Будем искать конец, пока open не опустеет
            currentPoint = minDistToTheEnd(); // Ищем из всех доступных вершин, ближайшую к концу
            if (currentPoint.equals(endPoint)){ // Если текущая вершина равна конечной, обновляем map и заканчиваем
                currentPoint = from.get(currentPoint);
                while(from.containsKey(currentPoint)){
                    map[currentPoint.x][currentPoint.y] = '+';
                    currentPoint = from.get(currentPoint);
                }
                return map;
            }
            // Переводим вершину из доступных в посещенные
            open.remove(currentPoint);
            closed.add(currentPoint);
            for(Point neighbour: findingNeighbors(currentPoint,map)){ // Обходим всех доступных соседей текущей вершины
                int distToTheCurrentPoint = distanceToThePoint.get(currentPoint) + 1;
                if (!open.contains(neighbour) || distToTheCurrentPoint < distanceToThePoint.get(neighbour)){
                    // Переназначаем путь до вершины
                    from.put(neighbour,currentPoint);
                    distanceToThePoint.put(neighbour,distToTheCurrentPoint);
                    distanceToTheEnd.put(neighbour, distanceToThePoint.get(neighbour) + heuristic(neighbour, endPoint));
                }
                if (!open.contains(neighbour)){
                    open.add(neighbour);
                }
            }
        }
        return null;
    }

    private void findStartEnd(char [][] map) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                switch (map[i][j]) {
                    case '@':
                        startPoint = new Point(i, j);
                        break;
                    case 'X':
                        endPoint = new Point(i, j);
                        break;
                }
            }
        }
    }

    private Double heuristic(Point start, Point end){
        // Ищем растояние между двумя точками по теореме Пифагора
        return Math.sqrt((start.x - end.x) * (start.x - end.x) + (start.y - end.y) * (start.y - end.y));
    }

    private Point minDistToTheEnd(){
        // Среди всех доступных вершин ищем, вершину с минимальных расстоянием до конечной точки
        Integer min = Integer.MAX_VALUE;
        Point minPoint = null;
        for (Point point: open){
            if (distanceToTheEnd.get(point) < min){
                min = distanceToThePoint.get(point);
                minPoint = point;
            }
        }
        return minPoint;
    }

    private ArrayList<Point> findingNeighbors(Point currentPoint, char[][] map){
        // Собираем массив соседних вершин для currentPoint
        int startPosX = (currentPoint.x - 1 < 0) ? currentPoint.x : currentPoint.x - 1;
        int startPosY = (currentPoint.y - 1 < 0) ? currentPoint.y : currentPoint.y - 1;
        int endPosX = (currentPoint.x + 1 > map.length - 1) ? currentPoint.x : currentPoint.x + 1;
        int endPosY = (currentPoint.y + 1 > map[0].length - 1) ? currentPoint.y : currentPoint.y + 1;
        ArrayList<Point> neighbors = new ArrayList<>();
        // Добавляем вершину, если это не стена и эта вершина должна быть ещё не использована
        for (int row = startPosX; row <= endPosX; row++){
            if (map[row][currentPoint.y] != '#' && !closed.contains(new Point(row,currentPoint.y)))
                neighbors.add(new Point(row,currentPoint.y));
        }
        for (int col = startPosY; col <= endPosY; col++){
            if (map[currentPoint.x][col] != '#' && !closed.contains(new Point(currentPoint.x,col)))
                neighbors.add(new Point(currentPoint.x,col));
        }
        return neighbors;
    }
}
