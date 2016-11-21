package me.contrapost.gameApi;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.BufferedInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;

/**
 * Created by alexandershipunov on 21/11/2016.
 *
 */
public class GameApplicationIT extends GameApplicationTestBase{

    private static Process process;

    @BeforeClass
    public static void startJar() throws Exception {

        String version = "1.0-SNAPSHOT"; //NOTE: those could be system properties
        String jar = "gameApi-" + version + ".jar";
        String jarLocation = "target" + File.separator + jar;

        if (!Files.exists(Paths.get(jarLocation))) {
            throw new AssertionError("Jar file was not created at: " + jarLocation);
        }

        String[] command = new String[]{"java", "-jar", jarLocation, "server"};

        process = new ProcessBuilder().command(command).start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                stopProcess();
            }
        });

        assertTrue(process.isAlive());

        Scanner in = new Scanner(new BufferedInputStream(process.getInputStream()));
        while (in.hasNext()) {
            String line = in.nextLine();
            System.out.println(line);
            if (line.contains("Server: Started")) {
                break;
            }
        }
    }

    @AfterClass
    public static void stopJar() {
        stopProcess();
    }

    private static void stopProcess() {
        if (process != null && process.isAlive()) {
            process.destroy();
            process = null;
        }
    }
}
