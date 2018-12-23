package net.peepocloud.node.api.logging;
/*
 * Created by Mc_Ruben on 22.12.2018
 */

import jline.console.ConsoleReader;

import java.util.function.Function;

public interface ConsoleLogger {

    /**
     * Reads a line out of the console and blocks the {@link Thread}, should be only used once for each logger at the same moment
     * @return the line
     */
    public String readLine();

    /**
     * Reads the lines until the {@link Function#apply(String)} returns {@code true}
     * @param function the {@link Function} which must return {@code true} to break the loop and let it return the input of the user
     * @param invalidInputMessage the message which is printed when the {@link Function#apply(String)} returns {@code false}
     * @param nullOn if the input of the user is equal to nullOn, {@code null} is returned
     * @return the user input line when the {@link Function#apply(String)} returns {@code true}
     */
    public String readLineUntil(Function<String, Boolean> function, String invalidInputMessage, String nullOn);

    /**
     * Reads the lines until the {@link Function#apply(String)} returns {@code true}
     * @param function the {@link Function} which must return {@code true} to break the loop and let it return the input of the user
     * @param invalidInputMessage the message which is printed when the {@link Function#apply(String)} returns {@code false}
     * @return the user input line when the {@link Function#apply(String)} returns {@code true}
     */
    public String readLineUntil(Function<String, Boolean> function, String invalidInputMessage);

    /**
     * Gets the {@link ConsoleReader} of this {@link ColoredLogger}
     * @return the {@link ConsoleReader} of this {@link ColoredLogger}
     */
    public ConsoleReader getConsoleReader();

    /**
     * Clears the console screen
     */
    public void clearScreen();

    /**
     * Prints the specified line to this {@link ColoredLogger}
     * @param line the line to print
     */
    public void print(String line);

    /**
     * Prints the specified line without removing the command prompt, updating to console animation if exists and reset line to this {@link ColoredLogger}
     * @param line the line to print
     */
    public void printRaw(String line);

    /**
     * Prints the given {@code line} to this {@link ColoredLogger} if {@code debugging} is enabled
     * @param line the line to print
     */
    public void debug(String line);

    /**
     * Checks if there is an {@link AbstractConsoleAnimation} running in this {@link ColoredLogger}
     * @return {@code true} if the running {@link AbstractConsoleAnimation} is not null or {@code false} if it is null
     */
    public boolean isAnimationRunning();

    /**
     * Starts an {@link AbstractConsoleAnimation} to this {@link ColoredLogger} if there is no other animation running
     * @param animation the animation to start
     * @throws IllegalArgumentException if there is already an {@link AbstractConsoleAnimation} running in this {@link ColoredLogger}
     */
    public void startAnimation(AbstractConsoleAnimation animation);

}
