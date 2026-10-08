import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

  static ArrayList<String> expenseNames = new ArrayList<>();
  static ArrayList<Double> expenseAmounts = new ArrayList<>();
  static double budget = 0;

  static final String FILE_NAME = "budget_data.txt";

  static Scanner input = new Scanner(System.in);

  public static void main(String[] args) {

    System.out.println("==== TIPID SULIT: BUDGET TRACKER ====");

    loadData();

    if (budget <= 0) {
      budget = readDouble("Enter your starting budget: PHP ");
      saveData();
    } else {
      System.out.println("Welcome back! Your saved data is now loaded.");
    }

    int choice = 0;

    while (choice != 5) {
      System.out.println("\n==== MENU ====");
      System.out.println("1. Add Expense");
      System.out.println("2. View Expenses");
      System.out.println("3. View Remaining Budget");
      System.out.println("4. Reset / Set New Budget");
      System.out.println("5. Exit");

      choice = readInt("Choose an option please: ");

      if (choice == 1) {
        addExpense();
      } else if (choice == 2) {
        viewExpenses();
      } else if (choice == 3) {
        viewSummary();
      } else if (choice == 4) {
        resetBudget();
      } else if (choice == 5) {
        System.out.println("Thank you for using Tipid Sulit!");
      } else {
        System.out.println("Invalid choice.");
      }
    }

    input.close();
  }

  static void addExpense() {
    System.out.print("Expense name (e.g. Pang-Cafe): ");
    String name = input.nextLine().trim();
    if (name.isEmpty()) {
      System.out.println("OOps! Expense name cannot be empty.");
      return;
    }
    name = name.replace("|", "-"); 

    double amount = readDouble("Amount: PHP ");

    if (amount <= 0) {
      System.out.println("Amount must be greater than 0.");
      return;
    }

    expenseNames.add(name);
    expenseAmounts.add(amount);
    saveData();

    System.out.println("Expense added!");
    checkWarning();
  }

  static void viewExpenses() {
    if (expenseNames.isEmpty()) {
      System.out.println("No expenses recorded yet.");
      return;
    }

    System.out.println("\n---- EXPENSES ----");
    for (int i = 0; i < expenseNames.size(); i++) {
      System.out.printf("%d. %-20s PHP %.2f%n", i + 1, expenseNames.get(i), expenseAmounts.get(i));
    }
    System.out.println("------------------");
    System.out.printf("Total spent: PHP %.2f%n", getTotalSpent());
  }

  static void viewSummary() {
    double spent = getTotalSpent();
    double remaining = budget - spent;

    System.out.println("\n---- SUMMARY ----");
    System.out.printf("Starting budget : PHP %.2f%n", budget);
    System.out.printf("Total spent     : PHP %.2f%n", spent);
    System.out.printf("Remaining       : PHP %.2f%n", remaining);
    checkWarning();
  }

  static void resetBudget() {
    budget = readDouble("Enter new budget: PHP ");
    expenseNames.clear();
    expenseAmounts.clear();
    saveData();
    System.out.println("Budget set and old expenses cleared.");
  }

  static double getTotalSpent() {
    double total = 0;
    for (int i = 0; i < expenseAmounts.size(); i++) {
      total += expenseAmounts.get(i);
    }
    return total;
  }

  static void checkWarning() {
    double remaining = budget - getTotalSpent();
    if (budget <= 0) {
      return; 
    }
    if (remaining < 0) {
      System.out.println("WARNING: You are over budget!");
    } else if (remaining <= budget * 0.2) {
      System.out.println("WARNING: You are almost out of budget (20% or less left)!");
    }
  }

  static double readDouble(String message) {
    while (true) {
      System.out.print(message);
      String text = input.nextLine().trim();
      try {
        return Double.parseDouble(text);
      } catch (NumberFormatException e) {
        System.out.println("Please enter a valid number.");
      }
    }
  }

  static int readInt(String message) {
    while (true) {
      System.out.print(message);
      String text = input.nextLine().trim();
      try {
        return Integer.parseInt(text);
      } catch (NumberFormatException e) {
        System.out.println("Please enter a valid integer choice.");
      }
    }
  }

  static void saveData() {
    try (PrintWriter out = new PrintWriter(FILE_NAME)) {
      out.println(budget);
      for (int i = 0; i < expenseNames.size(); i++) {
        out.println(expenseNames.get(i) + "|" + expenseAmounts.get(i));
      }
    } catch (Exception e) {
      System.out.println("Could not save data: " + e.getMessage());
    }
  }

  static void loadData() {
    File file = new File(FILE_NAME);
    if (!file.exists()) {
      return;
    }

    try (Scanner fileIn = new Scanner(file)) {
      if (!fileIn.hasNextLine()) {
        return;
      }
      String first = fileIn.nextLine().trim();
      if (!first.isEmpty()) {
        try {
          budget = Double.parseDouble(first);
        } catch (NumberFormatException nf) {
          System.out.println("Invalid budget value in saved data. Using 0.");
          budget = 0;
        }
      }

      while (fileIn.hasNextLine()) {
        String line = fileIn.nextLine();
        String[] parts = line.split("\\|", 2);
        if (parts.length == 2) {
          expenseNames.add(parts[0]);
          try {
            expenseAmounts.add(Double.parseDouble(parts[1]));
          } catch (NumberFormatException nf) {
            
          }
        }
      }
    } catch (Exception e) {
      System.out.println("Could not load saved data, starting fresh.");
      budget = 0;
      expenseNames.clear();
      expenseAmounts.clear();
    }
  }
}