package org.example.com.folderscanner;

import java.io.File;
import java.util.*;

public class FolderScanner {
    private static String pathFolder = "folder/"; // Папка в которой должны располагаться директории для задания

    public static void main(String[] args) {
        String[] dirs = {"A", "B", "C"};// Массив с директориями для задания
        int N = 3; // Количество массивов, на которые нужно разделить

        Map<Integer, Queue<String>> jsFolders = findJSFolders(dirs);

        List<List<String>> outputList = arrayDistribution(jsFolders, N);

        showResult(outputList, N);
    }

    private static Map<Integer, Queue<String>> findJSFolders(String[] dirs) {
        Map<Integer, Queue<String>> jsFolders = new HashMap<>();
        for (String dir : dirs) {
            findJSFoldersRecursive(pathFolder + dir, jsFolders);
        }
        return jsFolders;
    }


    /**
     * Рекурсивный метод
     * Находит все папки с js-файлами и указывает количество таких файлов в папке
     *
     * @param pathDir   Путь до файла или папка
     * @param jsFolders Map, который хранит информацию о папках с js-файлами
     */
    private static void findJSFoldersRecursive(String pathDir, Map<Integer, Queue<String>> jsFolders) {
        File[] paths = new File(pathDir).listFiles();
        if (paths != null) {
            int countJsFiles = 0;
            for (File path : paths) {
                if (path.isDirectory()) {
                    findJSFoldersRecursive(path.getPath(), jsFolders);
                } else if (path.getName().endsWith(".js")) {
                    countJsFiles++;
                }
            }
            if (countJsFiles != 0) {
                jsFolders.computeIfAbsent(countJsFiles, k -> new ArrayDeque<>()).add(pathDir);
            }
        }
    }




    /**
     * Разделение на N массивов
     * @param jsFolders map с указанными путями и количествавми js-файлов
     * @param N Число массивов (реализация через список)
     * @return Список списков. Подсписок - массив путей
     */
    private static List<List<String>> arrayDistribution(Map<Integer, Queue<String>> jsFolders, int N) {
        Deque<Integer> keys = new ArrayDeque<>();
        int sumKeys = sumKeysCalcAndKeys(jsFolders, keys);
        int measure = sumKeys / N;
        List<List<String>> outputList = new ArrayList<>(N);
        int i;
        for (i = 0; i < N; i++) {
            outputList.add(new ArrayList<>());
            int currKey = keys.removeLast();
            int capacity = currKey;
            outputList.get(i).add(jsFolders.get(currKey).remove() + " (" + currKey + ")");
            deleteEmptyQueueInMap(jsFolders, currKey);
            while (!jsFolders.isEmpty() && capacity < measure) {
                currKey = keys.removeFirst();
                capacity += currKey;
                outputList.get(i).add(jsFolders.get(currKey).remove() + " (" + currKey + ")");
                deleteEmptyQueueInMap(jsFolders, currKey);
            }
        }
        i--;
        while (!jsFolders.isEmpty()) {
            int currKey = keys.removeFirst();
            outputList.get(i).add(jsFolders.get(currKey).remove() + " (" + currKey + ")");
            deleteEmptyQueueInMap(jsFolders, currKey);
        }


        return outputList;
    }


    /**
     * Вычисление суммы ключей и заполнение очереди ключей
     * @param jsFolders
     * @param keys
     * @return количество js-файлов
     */
    private static int sumKeysCalcAndKeys(Map<Integer, Queue<String>> jsFolders, Deque<Integer> keys) {
        int result = 0;
        for (Map.Entry<Integer, Queue<String>> entry : jsFolders.entrySet()) { // спорный момент
            result += entry.getKey() * entry.getValue().size();
            for (int i = 0; i < entry.getValue().size(); i++) {
                keys.add(entry.getKey());
            }
        }
        return result;
    }


    /**
     * Удаляет пустую очередь из map
     * @param map
     * @param key
     */
    private static void deleteEmptyQueueInMap(Map<Integer, Queue<String>> map, int key) {
        if (map.get(key).isEmpty()) {
            map.remove(key);
        }
    }

    /**
     * Находит все папки которые содержат JS-файлы
     * Работает на основе метода findJSFoldersRecursive
     *
     * @param dirs Массив с перечислением путей к корневым папкам
     * @return jsFolders Hashmap, у которого ключ - это количество js-файлов, значение - очередь из путей до папок
     */


    /**
     * Выводит ответ в консоль
     * @param outputArray
     * @param N
     */
    private static void showResult(List<List<String>> outputArray, int N) {
        for (int i = 0; i < N; i++) {
            System.out.println("[" + String.valueOf(i + 1) + "]:");
            for (String j : outputArray.get(i)) {
                System.out.println(j.substring(pathFolder.length()));
            }
        }
    }
}
