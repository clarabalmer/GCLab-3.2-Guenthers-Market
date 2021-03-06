import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Arrays;

public class Market {
	
	private static Scanner scnr;
	private static Map<String, Double> items = new TreeMap<>();
	private static Map<Integer, Map.Entry<String,Double>> menuSystem = new TreeMap<>();
	private static ArrayList<String> itemsOrdered = new ArrayList<>();
	private static ArrayList<Double> itemPrices = new ArrayList<>();
	private static ArrayList<Integer> itemQuantities = new ArrayList<>();
	
	public static void main(String[] args) {
		scnr = new Scanner(System.in);
		fillItemsMap();
		fillMenuSystemMap();
		System.out.println("Welcome to Guenther's Market!");
		printMenu();
		printMultiOrderInstructions();
		
		boolean repeatOrder = true;
		while (repeatOrder) {
			processUserOrder(userOrderChoice());
			repeatOrder = optionToContinueOrdering();
		}
		
		displayOrder();
		displayMinMaxOrdered();
		
		scnr.close();
	}
	//fill first map, item names and prices
	private static void fillItemsMap() {
		items.put("Club Sandwich", 4.99);
		items.put("Meatloaf", 5.99);
		items.put("Taco Dinner", 6.99);
		items.put("Salad", 2.99);
		items.put("Fries", 3.99);
		items.put("Onion Rings", 4.49);
		items.put("Apple Pie", 2.99);
		items.put("Ice Cream", 1.99);
		items.put("Brownie", 2.99);
		items.put("Banana", 10.00);
	}
	//fill second map, item codes and item names
	private static void fillMenuSystemMap() {
		int i = 1;
		for (Map.Entry<String,Double> entry : items.entrySet()) {
			menuSystem.put(i, entry);
			i++;
		}
	}
	//print menu to console
	private static void printMenu() {
		System.out.println("Menu");
		System.out.printf("%-10s", "code");
		System.out.printf("%-20s", "item");
		System.out.printf("%-20s", "price");
		System.out.print("\n===================================");
		for (Map.Entry<Integer, Map.Entry<String,Double>> entry : menuSystem.entrySet()) {
			System.out.println();
			System.out.printf("%-10s", entry.getKey());
			System.out.printf("%-20s", keyAndValueSplitter(entry, 1));
			System.out.printf("%5.2f", Double.parseDouble(keyAndValueSplitter(entry, 2)));
		}
		System.out.print("\n===================================");
		System.out.print("\n(To order, enter item code or name)");
		System.out.print("\n");
	}
	//takes entry from a map and int 1 or 2, returns key or value as strings (i=1 for key, 2 for value)
	private static String keyAndValueSplitter(Map.Entry<Integer, Map.Entry<String, Double>> entry, int i) {
		String itemAndPrice = entry.getValue().toString();
		String[] menuStringSplit = new String[2];
		menuStringSplit = itemAndPrice.split("=");
		if (i == 1) {
			return menuStringSplit[0];
		} else if (i == 2) {
			return menuStringSplit[1];
		} else {
			return "";
		}
	}
	//prints instructions for ordering multiples
	private static void printMultiOrderInstructions() {
		System.out.println("To order multiples of the same item, type 'x' first.");
		System.out.println("E.g. 'x5 apples' or 'x12 3'.");
	}
	//turns user input into string array for processing
	private static String[] userOrderChoice() {
		System.out.print("\nWhat item would you like to order? ");
		String[] pieces = scnr.nextLine().split("\\s+");
		return pieces;
	}
	//changes/adds to item ArrayLists based on user input string array
	private static void processUserOrder(String[] orderSentence) {
		boolean validChoice = false;
		int quantity = 1;
		String itemNameOrNumber = "";
		//if ordering multiple items at a time, parse quantity and put item name in correct format
		if (orderSentence[0].startsWith("x")) {
			try {
				quantity = Integer.parseInt(orderSentence[0].substring(1));
				for (int i = 1; i < orderSentence.length; i++) {
					itemNameOrNumber += orderSentence[i];
					if (i < orderSentence.length - 1) {
						itemNameOrNumber += " ";
					}
				}
			} catch (NumberFormatException e) {
			}
		} else {
		//if ordering one item, put item name in correct format
			for (int i = 0; i < orderSentence.length; i++) {
				itemNameOrNumber += orderSentence[i];
				if (i < orderSentence.length - 1) {
					itemNameOrNumber += " ";
				}
			}
		}
		
		//if ordered by item code
		if (isInteger(itemNameOrNumber)) {
			if (addToOrderByCode(itemNameOrNumber, quantity)) {
				validChoice = true;
			}
		} else {
		//if ordered by item name
			if (addToOrderByName(itemNameOrNumber, quantity)) {
				validChoice = true;
			}
		}
		//if order is not recognized
		if (!validChoice) {
			System.out.println("I don't recognize that order. Please try again.");
		}
	}
	//check if code is valid, print confirmation, add order to ArrayLists
	private static boolean addToOrderByCode(String str, int quantity) {
		int itemCode = Integer.parseInt(str);
		String itemName = "";
		//find matching item code in map
		for (Map.Entry<Integer, Map.Entry<String, Double>> entry : menuSystem.entrySet()) {
			if (entry.getKey() == itemCode) {
				itemName = keyAndValueSplitter(entry, 1);
				//find matching item name in map
				for (Map.Entry<String, Double> entry2 : items.entrySet()) {
					if (itemName.equals(entry2.getKey())) {
						System.out.println("Adding x" + quantity + " " + entry2.getKey() + " to cart, " + formatDoublePrice(entry2.getValue()) + " each.");
						//add order correct number of times
						for (int i = 0; i < quantity; i++) {
							orderAdd(entry2);
						}
						return true;
					}
				}
			}
		}
		return false;
	}
	//check if string can be parsed into integer
	private static boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	//format price string into double with two decimals
	private static String formatDoublePrice(double price) {
		return String.format("%,.2f", price);
	}
	//if first time ordering, add to all ArrayLists. Otherwise, add only to quantities list.
	private static void orderAdd(Entry<String, Double> entry) {
		if (itemsOrdered.indexOf(entry.getKey()) == -1) {
			itemsOrdered.add(entry.getKey());
			itemPrices.add(entry.getValue());
			itemQuantities.add(1);
		} else {
			int previousAmt = itemQuantities.get(itemsOrdered.indexOf(entry.getKey())); 
			itemQuantities.set(itemsOrdered.indexOf(entry.getKey()), previousAmt + 1);
		}
	}
	//check if name is valid, print confirmation, add order to ArrayLists
	private static boolean addToOrderByName(String str, int quantity) {
		//find matching item name in map
		for (Map.Entry<String, Double> entry : items.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(str)) {
				System.out.println("Adding x" + quantity + " "+ entry.getKey() + " to cart, " + formatDoublePrice(entry.getValue()) + " each.");
				//add order correct number of times
				for (int i = 0; i < quantity; i++) {
					orderAdd(entry);
				}
				return true;
			}
		}
		return false;
	}
	//offer to take more orders, reprint menu, or reprint multiple order instructions
	private static boolean optionToContinueOrdering() {
		System.out.print("\nWould you like to order anything else? y/n/menu/help ");
		String choice = scnr.next();//only takes the first word, repeats for second word if there is one
		scnr.nextLine();
		if (choice.equalsIgnoreCase("y" )) {
			return true;
		} else if (choice.equalsIgnoreCase("n")) {
			return false;
		} else if (choice.equalsIgnoreCase("menu")) {
			printMenu();
			return optionToContinueOrdering();
		} else if (choice.equalsIgnoreCase("help")) {
			System.out.println("You can do this!");
			printMultiOrderInstructions();
			return (optionToContinueOrdering());
		} else {
			System.out.println("I don't understand.");
			return (optionToContinueOrdering());
		}
	}
	//display final order: item quantities and subtotals, total bill, average price per item
	private static void displayOrder() {
		if (itemQuantities.size() == 0) {
			System.out.println("\nYou didn't order anything! Come back when you are ready.");
		} else {
			System.out.println("\nThanks for your order!");
			System.out.println("Here's what you got:");
			System.out.println("===================================");
			String itemString = "";
			double itemPrice = 0.0;
			double total = 0.0;
			for (int i = 0; i < itemsOrdered.size(); i++) {
				itemString = itemsOrdered.get(i);
				itemPrice = itemPrices.get(i) * itemQuantities.get(i);
				total += itemPrice;
				System.out.printf("%-10s", itemQuantities.get(i));
				System.out.printf("%-15s", itemString);
				System.out.printf("%10s", "$" + formatDoublePrice(itemPrice));
				System.out.println();
			}
			System.out.println("===================================");
			System.out.print("Your total is:");
			System.out.printf("%21s", "$" + formatDoublePrice(total));
			System.out.println("\nAverage price per item in order was " + formatDoublePrice(averageItemPrice()) + ".");
		}
	}
	//calculates average price per item
	private static double averageItemPrice() {
		double itemsTotal = 0.0;
		for (int i = 0; i < itemPrices.size(); i++) {
			itemsTotal += itemPrices.get(i);
		}
		return itemsTotal / itemPrices.size();
	}
	//prints most and least expensive items ordered
	private static void displayMinMaxOrdered() {
		if (itemQuantities.size() != 0) {
			System.out.println("Most expensive item ordered: " + itemsOrdered.get(highestPriceIndex()) + " at " + formatDoublePrice(itemPrices.get(highestPriceIndex())) + ".");
			System.out.println("Least expensive item ordered: " + itemsOrdered.get(lowestPriceIndex()) + " at " + formatDoublePrice(itemPrices.get(lowestPriceIndex())) + ".");
		}
	}
	//returns ArrayList index of highest price item
	private static int highestPriceIndex() {
		int index = -1;
		double max = 0;
		for (int i = 0; i < itemPrices.size(); i++) {
			if (max < itemPrices.get(i)) {
				max = itemPrices.get(i);
				index = i;
			}
		}
		return index;
	}
	//returns ArrayList index of lowest price item
	private static int lowestPriceIndex() {
		int index = -1;
		double min = 1000000000000.00;
		for (int i = 0; i < itemPrices.size(); i++) {
			if (min > itemPrices.get(i)) {
				min = itemPrices.get(i);
				index = i;
			}
		}
		return index;
	}
}