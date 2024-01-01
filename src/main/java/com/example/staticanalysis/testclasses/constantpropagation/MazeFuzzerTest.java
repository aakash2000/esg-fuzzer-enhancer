package com.example.staticanalysis.testclasses.constantpropagation;

import com.code_intelligence.jazzer.api.Consumer3;
import com.code_intelligence.jazzer.api.Jazzer;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

import java.nio.charset.StandardCharsets;

// A fuzz target that shows how manually informing the fuzzer about important state can make a fuzz
// target much more effective.
// This is a Java version of the famous "maze game" discussed in
// "IJON: Exploring Deep State Spaces via Fuzzing", available at:
// https://wcventure.github.io/FuzzingPaper/Paper/SP20_IJON.pdf
public final class MazeFuzzerTest {
    private static final String[] MAZE_STRING = new String[] {
            "  |||||||||||||||||||",
            "    | | |   | |     |",
            "| | | | ||| | | | |||",
            "| | |   |       |   |",
            "| ||||| ||| ||| | |||",
            "|       |   | | |   |",
            "| ||| ||||||| | ||| |",
            "| |     | |     |   |",
            "||||||| | | ||||| |||",
            "|   |       |     | |",
            "| ||||||| | ||| ||| |",
            "|   |     | | |   | |",
            "||| ||| | ||| | ||| |",
            "|     | | |   |     |",
            "| ||||||| | | | | | |",
            "| |         | | | | |",
            "| | ||||||||| ||| |||",
            "|   |   |   | | |   |",
            "| | | ||| ||||| ||| |",
            "| |         |        ",
            "||||||||||||||||||| #",
    };

    private static final char[][] MAZE = parseMaze();
    private static final char[][] REACHED_FIELDS = parseMaze();
    public static int counter = 0;

    public static void fuzzerTestOneInput(byte[] commands) {
        executeCommands(commands, (x, y, won) -> {
            if (won) {
                throw new TreasureFoundException(commands);
            }
            Jazzer.exploreState((byte) Objects.hash(x, y), 0);
            if (REACHED_FIELDS[y][x] == ' ') {
                // Fuzzer reached a new field in the maze, print its progress.
                REACHED_FIELDS[y][x] = '.';
                System.out.println(renderMaze(REACHED_FIELDS));
                System.out.print("x: " + x + ", y: " + y + ",count: " + counter + ",commands: " + new String(commands, StandardCharsets.UTF_8) + "\n");
                /* Sleep for 1 second */
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //System.exit(0);
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static class TreasureFoundException extends RuntimeException {
        TreasureFoundException(byte[] commands) {
            super(renderPath(commands));
        }
    }

    private static void executeCommands(byte[] commands, Consumer3<Byte, Byte, Boolean> callback) {
        byte x = 0;
        byte y = 0;
        callback.accept(x, y, false);

        for (byte command : commands) {
            byte nextX = x;
            byte nextY = y;
            switch (command) {
                case 'L':
                    nextX--;
                    counter++;
                    break;
                case 'R':
                    nextX++;
                    counter++;
                    break;
                case 'U':
                    nextY--;
                    counter++;
                    break;
                case 'D':
                    nextY++;
                    counter++;
                    break;
                default:
                    counter++;
                    return;
            }
            char nextFieldType;
            try {
                nextFieldType = MAZE[nextY][nextX];
            } catch (IndexOutOfBoundsException e) {
                // Fuzzer tried to walk through the exterior walls of the maze.
                continue;
            }
            if (nextFieldType != ' ' && nextFieldType != '#') {
                // Fuzzer tried to walk through the interior walls of the maze.
                continue;
            }
            // Fuzzer performed a valid move.
            x = nextX;
            y = nextY;
            callback.accept(x, y, nextFieldType == '#');
        }
    }

    private static char[][] parseMaze() {
        return Arrays.stream(MazeFuzzerTest.MAZE_STRING).map(String::toCharArray).toArray(char[][] ::new);
    }

    private static String renderMaze(char[][] maze) {
        return Arrays.stream(maze).map(String::new).collect(Collectors.joining("\n", "\n", "\n"));
    }

    private static String renderPath(byte[] commands) {
        char[][] mutableMaze = parseMaze();
        executeCommands(commands, (x, y, won) -> {
            if (!won) {
                mutableMaze[y][x] = '.';
            } else {
                System.out.print("counter "+counter+" ,commands: " + new String(commands, StandardCharsets.UTF_8));
            }
        });
        return renderMaze(mutableMaze);
    }

    public static void main(String[] args) {
        Object inputs = new Scanner(System.in);
        fuzzerTestOneInput(inputs.toString().getBytes());
    }
}
