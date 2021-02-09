package rolling.stats;

import java.time.Duration;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		Scanner scanner = new Scanner(System.in);
		try {
			while (true) {
				System.out.println("Please select the type of rolling statistics you would like to use. (1 or 2)\n"
						+ "1. Size-capped (there is a size limit to the list of items.)\n"
						+ "2. Time-capped (items are kept in the list for a certain amount of time.");
				String mainInput = scanner.nextLine().trim().toLowerCase();
				if (mainInput.equals("1")) {
					try {
						while (true) {
							System.out.println("Size-capped list was selected!\nPlease enter the maximum amount items: ");
							String sizeInput = scanner.nextLine().toLowerCase();
							/*
							 * validating that the input is a positive integer, with no more than 9 digits
							 * if more digits are entered, the first nine will be taken
							 */
							if (sizeInput.equals("back")) {
								break;
							} else if (!sizeInput.matches("^[1-9]\\d{0,8}")) {
								System.err.println("Invalid command (number is not positive or too big)!");
							} else {
								Integer listSize = Integer.parseInt(sizeInput);
								RollingStatistics statistics = new RollingStatistics(listSize);
								System.out.println("A new list was created with maximum size of " + listSize + ".");
								while (true) {
									System.out.print("Command: ");
									String[] inputArray = scanner.nextLine().toLowerCase().trim().split("\\s+");
									if (inputArray[0].equals("back")) {
										break;
									} else if (inputArray[0].equals("add")) {
										Double stat = Double.parseDouble(inputArray[1]);
										statistics.add(stat);
										System.out.println("Added successfully!");
										System.out.println("Stats: " + statistics.toString());
										System.out.println();

									} else if (inputArray[0].equals("statistics")) {
										System.out.println(statistics.queryAll());
										System.out.println();
										// validating the range inputs are positive integers
									} else if (inputArray[0].equals("range") && inputArray[1].matches("[1-9]{1,9}")
											&& inputArray[2].matches("[1-9]{1,9}")) {
										System.out.println(statistics.queryRange(Integer.parseInt(inputArray[1]),
												Integer.parseInt(inputArray[2])));
										System.out.println();
									} else {
										System.err.println("Invalid command!");
										System.out.println();
									}
								}
							}
						}
					} catch (Exception e) {
						System.err.println("Exception caught " + e.toString());
					}
				} else if (mainInput.equals("2")) {
					try {
						while (true) {
							System.out.println("Time-capped list was selected!\n"
									+ "Please enter the amount of time (in seconds) the items will be in the list\n"
									+ "and the interval (in seconds) you want the list to be checked (seperated by space): ");
							String[] timeInput = scanner.nextLine().toLowerCase().trim().split("\\s+");
							if (timeInput[0].equals("back")) {
								break;

								/*
								 * validating that time inputs are positive integers no longer than 9 digits, if
								 * they are, the first nine digits will be considered
								 */
							} else if (!timeInput[0].matches("[1-9]\\d{0,8}")
									|| !timeInput[1].matches("[1-9]\\d{0,8}")) {
								System.err.println("Invalid command (numbers not positive or too big)!");
							} else {
								// new object with the specified duration in seconds
								RollingStatisticsTimestamped rst = new RollingStatisticsTimestamped(
										Duration.ofSeconds(Integer.parseInt(timeInput[0])));
								System.out.println("A new list was created with a time limit of " + timeInput[0]
										+ " seconds,\nand will be checked every " + timeInput[1] + " seconds.");

								/*
								 * semaphore to make sure there that the list is not accessed by the checker and
								 * when adding simultaneously
								 */
								Semaphore s = new Semaphore(1);
								System.out.println("Will start checking list in 10 seconds!");
								Timer timer = new Timer();
								// timer that checks the list
								timer.scheduleAtFixedRate(new TimerTask() {
									@Override
									public void run() {
										try {
											s.acquire();
											rst.checkList();
											s.release();
											System.out.print("Command: ");

										} catch (InterruptedException e) {
											System.err.println("Exception caught " + e.toString());
										}
									}
								}, 10000, Integer.parseInt(timeInput[1]) * 1000);
								try {
									while (true) {
										System.out.print("Command: ");
										String[] inputArray = scanner.nextLine().toLowerCase().trim().split("\\s+");
										if (inputArray[0].equals("back")) {
											timer.cancel();
											break;
										} else if (inputArray[0].equals("add")) {
											s.acquire();
											rst.add(new DataItem(Double.parseDouble(inputArray[1])));
											System.out.println("Added successfully!");
											System.out.println("Stats: " + rst.toString());
											System.out.println();
											s.release();
										} else if (inputArray[0].equals("statistics")) {
											System.out.println(rst.queryAll());
											System.out.println();

											// validating duration input is a valid integer
										} else if (inputArray[0].equals("range")
												&& inputArray[1].matches("^[1-9]\\d{0,8}")) {
											System.out.println(rst
													.queryRange(Duration.ofSeconds(Integer.parseInt(inputArray[1]))));
											System.out.println();
										} else if (inputArray[0].equals("check")) {
											s.acquire();
											rst.checkList();
											s.release();
										} else {
											System.err.println("Invalid command!\n");
										}
									}
								} catch (Exception e) {
									System.err.println("Exception caught " + e.toString());
									timer.cancel();
								}
							}
						}
					} catch (Exception e) {
						System.err.println("Exception caught " + e.toString());
					}
				} else if (mainInput.equals("exit")) {
					System.out.println("Goodbye!");
					scanner.close();
					break;
				} else {
					System.err.println("Invalid input!");
					System.out.println();
				}
			}
		} catch (Exception e) {
			System.err.println("Exception caught " + e.toString());
		}

	}
}