import java.util.Scanner;


// -------- USER CLASS --------
class User 
 {
    String id;
    String password;

    User(String id, String password) {
        this.id = id;
        this.password = password;
    }
}

// -------- BASE CLASS --------
class BudgetItem {
    String date;
    String category;
    double amount;

    BudgetItem(String date, String category, double amount) {
        this.date = date;
        this.category = category;
        this.amount = amount;
    }
}
// -------- CHILD CLASS --------
class Expense extends BudgetItem {
    String note;

    Expense(String date, String category, double amount, String note) {
        super(date, category, amount);
        this.note = note;
    }
}

// -------- MAIN LOGIC CLASS --------
class SmartBudget {

    Expense[] expenses = new Expense[200];
    int count = 0;
    double monthlyBudget;
    User owner;
    
    // Current date fields (set once at startup)
    int currentDay;
    int currentMonth;
    int currentYear;

    // Constructor with current date parameters
    SmartBudget(User owner, double monthlyBudget, int day, int month, int year) {
        this.owner = owner;
        this.monthlyBudget = monthlyBudget;
        this.currentDay = day;
        this.currentMonth = month;
        this.currentYear = year;
    }

    boolean validateDate(String d) {

        // Branch 1: Check format separator
        if (!d.contains("/")) {
            return false;
        }

        String[] p = d.split("/");

        // Branch 2: Must be DD/MM/YYYY
        if (p.length != 3) {
            return false;
        }

        // Branch 3: Only digits allowed
        if (!p[0].matches("\\d+") ||
            !p[1].matches("\\d+") ||
            !p[2].matches("\\d+")) {
            return false;
        }

        int day = Integer.parseInt(p[0]);
        int month = Integer.parseInt(p[1]);
        int year = Integer.parseInt(p[2]);

        // Branch 4: Year must be EXACTLY current year (no past, no future year)
        if (year != currentYear) {
            return false;
        }

        // Branch 5: Month must be 1 to currentMonth (no future months)
        if (month < 1 || month > currentMonth) {
            return false;
        }

        // Branch 6: Day range (1 to 31)
        if (day < 1 || day > 31) {
            return false;
        }

        // Branch 7: 30-day months (April, June, September, November)
        if ((month == 4 || month == 6 || month == 9 || month == 11) && day > 30) {
            return false;
        }

        // Branch 8: LEAP YEAR CHECK for February
        if (month == 2) {
            boolean isLeapYear = false;

            // Leap year logic
            if (year % 400 == 0) {
                isLeapYear = true;
            } else if (year % 100 == 0) {
                isLeapYear = false;
            } else if (year % 4 == 0) {
                isLeapYear = true;
            }

            // February: 29 days in leap year, 28 otherwise
            int maxFebDays = isLeapYear ? 29 : 28;
            
            if (day > maxFebDays) {
                return false;
            }
        }

        // Branch 9: If current month, day cannot be in future
        if (month == currentMonth && day > currentDay) {
            return false;
        }

        // Branch 10: All conditions satisfied
        return true;
    }

    void addExpense(Scanner sc) {
        String date;
        while (true) {
            System.out.print("Enter Date (DD/MM/YYYY): ");
            date = sc.next();
            if (validateDate(date)) break;
            System.out.println("Invalid Date! Only current year, past/current months and days allowed.");
        }

        String category = selectCategory(sc);

        // Amount validation - must be greater than 0
        double amount;
        while (true) {
            System.out.print("Amount: ");
            amount = sc.nextDouble();
            if (amount > 0) break;
            System.out.println("Amount must be greater than 0!");
        }

        sc.nextLine();
        System.out.print("Note: ");
        String note = sc.nextLine();

        expenses[count++] = new Expense(date, category, amount, note);
        System.out.println("Expense Added Successfully");
    }

    void quickExpense(Scanner sc) {
        sc.nextLine();
        System.out.print("Note: ");
        String note = sc.nextLine();

        // Amount validation - must be greater than 0
        double amt;
        while (true) {
            System.out.print("Amount: ");
            amt = sc.nextDouble();
            if (amt > 0) break;
            System.out.println("Amount must be greater than 0!");
        }

        // Use current date instead of hardcoded date
        String todayStr = currentDay + "/" + currentMonth + "/" + currentYear;
        expenses[count++] = new Expense(todayStr, "Other", amt, note);
        System.out.println("Quick Expense Added");
    }
String selectCategory(Scanner sc) {
    while (true) {
        System.out.println("Select Category:");
        System.out.println("1. Food");
        System.out.println("2. Transport");
        System.out.println("3. Rent");
        System.out.println("4. Bills");
        System.out.println("5. Shopping");
        System.out.println("6. Other");

        int ch = sc.nextInt();

        switch (ch) {
            case 1: return "Food";
            case 2: return "Transport";
            case 3: return "Rent";
            case 4: return "Bills";
            case 5: return "Shopping";
            case 6: return "Other";
            default:
                System.out.println("Invalid option selected! Please choose between 1 to 6 only.");s
        }
    }
}

    void viewExpenses() {
        if (count == 0) {
            System.out.println("No expenses found");
            return;
        }

        for (int i = 0; i < count; i++) {
            System.out.println((i + 1) + ". " +
                    expenses[i].date + " | " +
                    expenses[i].category + " | Rs." +
                    expenses[i].amount + " | " +
                    expenses[i].note);
        }
    }

    void searchExpense(Scanner sc) {
        if (count == 0) {
            System.out.println("No expenses available");
            return;
        }

        System.out.println("1. Search by Date");
        System.out.println("2. Search by Category");
        int ch = sc.nextInt();
        boolean found = false;

        if (ch == 1) {
            System.out.print("Enter Date: ");
            String d = sc.next();
            for (int i = 0; i < count; i++) {
                if (expenses[i].date.equals(d)) {
                    printExpense(i);
                    found = true;
                }
            }
        } else if (ch == 2) {
            String c = selectCategory(sc);
            for (int i = 0; i < count; i++) {
                if (expenses[i].category.equalsIgnoreCase(c)) {
                    printExpense(i);
                    found = true;
                }
            }
        } else {
            System.out.println("Invalid option! Enter 1 or 2.");
            return;
        }

        if (!found)
            System.out.println("No matching expense found");
    }

    void printExpense(int i) {
        System.out.println((i + 1) + ". " +
                expenses[i].date + " | " +
                expenses[i].category + " | Rs." +
                expenses[i].amount + " | " +
                expenses[i].note);
    }

    void categorySummary() {
        double f=0,t=0,r=0,b=0,s=0,o=0;

        for (int i=0;i<count;i++) {
            switch (expenses[i].category) {
                case "Food": f+=expenses[i].amount; break;
                case "Transport": t+=expenses[i].amount; break;
                case "Rent": r+=expenses[i].amount; break;
                case "Bills": b+=expenses[i].amount; break;
                case "Shopping": s+=expenses[i].amount; break;
                default: o+=expenses[i].amount;
            }
        }

        System.out.println("Category Summary:");
        System.out.println("Food: Rs." + f);
        System.out.println("Transport: Rs." + t);
        System.out.println("Rent: Rs." + r);
        System.out.println("Bills: Rs." + b);
        System.out.println("Shopping: Rs." + s);
        System.out.println("Other: Rs." + o);
    }

    void mostUsedCategory() {
        if (count == 0) {
            System.out.println("No expenses available");
            return;
        }

        int f=0,t=0,r=0,b=0,s=0,o=0;

        for (int i=0;i<count;i++) {
            switch (expenses[i].category) {
                case "Food": f++; break;
                case "Transport": t++; break;
                case "Rent": r++; break;
                case "Bills": b++; break;
                case "Shopping": s++; break;
                default: o++;
            }
        }

        // Store all counts in array for easier comparison
        int[] counts = {f, t, r, b, s, o};
        String[] categories = {"Food", "Transport", "Rent", "Bills", "Shopping", "Other"};
        
        // Find the maximum count first
        int max = 0;
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] > max) {
                max = counts[i];
            }
        }
        
        // Find ALL categories with max count (handles ties)
        String result = "";
        int tieCount = 0;
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] == max) {
                if (tieCount > 0) {
                    result += ", ";  // Add comma between tied categories
                }
                result += categories[i];
                tieCount++;
            }
        }

        // Show appropriate message for single or multiple winners
        if (tieCount > 1) {
            System.out.println("Most Used Categories (Tie): " + result + " (" + max + " expenses each)");
        } else {
            System.out.println("Most Used Category: " + result + " (" + max + " expenses)");
        }
    }

    void highestSpendingCategory() {
        if (count == 0) {
            System.out.println("No expense is added");
            return;
        }

        double f=0,t=0,r=0,b=0,s=0,o=0;

        for (int i=0;i<count;i++) {
            switch (expenses[i].category) {
                case "Food": f+=expenses[i].amount; break;
                case "Transport": t+=expenses[i].amount; break;
                case "Rent": r+=expenses[i].amount; break;
                case "Bills": b+=expenses[i].amount; break;
                case "Shopping": s+=expenses[i].amount; break;
                default: o+=expenses[i].amount;
            }
        }

        double max=f; String cat="Food";
        if(t>max){max=t;cat="Transport";}
        if(r>max){max=r;cat="Rent";}
        if(b>max){max=b;cat="Bills";}
        if(s>max){max=s;cat="Shopping";}
        if(o>max){max=o;cat="Other";}

        System.out.println("Highest Spending Category: " + cat + " Rs." + max);
    }

    void highestSingleExpense() {
        if (count == 0) {
            System.out.println("No expenses available");
            return;
        }

        int idx=0;
        for(int i=1;i<count;i++)
            if(expenses[i].amount>expenses[idx].amount) idx=i;

        System.out.println("Highest Expense:");
        printExpense(idx);
    }

    void financialPersonality() {
        // Check for division by zero
        if (monthlyBudget == 0) {
            System.out.println("Budget is zero. Cannot calculate financial personality.");
            return;
        }

        double total=0;
        for(int i=0;i<count;i++) total+=expenses[i].amount;

        double p=(total/monthlyBudget)*100;

        if(p<=60) System.out.println("Financial Personality: Saver");
        else if(p<=85) System.out.println("Financial Personality: Balanced");
        else if(p<=100) System.out.println("Financial Personality: Impulsive");
        else System.out.println("Financial Personality: Over Spender");
    }

    void monthlySummary(Scanner sc) {
        System.out.print("Enter Month (1-12): ");
        int searchMonth = sc.nextInt();
        System.out.print("Enter Year: ");
        int searchYear = sc.nextInt();
        double total = 0;

        for(int i = 0; i < count; i++) {
            String[] parts = expenses[i].date.split("/");
            int expMonth = Integer.parseInt(parts[1]);
            int expYear = Integer.parseInt(parts[2]);
            
            if(expMonth == searchMonth && expYear == searchYear) {
                total += expenses[i].amount;
            }
        }
        System.out.println("Monthly Total: Rs." + total);
    }

    void editExpense(Scanner sc) {
        if (count == 0) {
            System.out.println("No expenses to edit");
            return;
        }

        viewExpenses();
        System.out.print("Select expense number to edit: ");
        int i = sc.nextInt() - 1;

        if (i < 0 || i >= count) {
            System.out.println("Invalid selection");
            return;
        }

        expenses[i].category = selectCategory(sc);

        // Amount validation - must be greater than 0
        double newAmount;
        while (true) {
            System.out.print("New Amount: ");
            newAmount = sc.nextDouble();
            if (newAmount > 0) break;
            System.out.println("Amount must be greater than 0!");
        }
        expenses[i].amount = newAmount;
        sc.nextLine();

        System.out.print("New Note: ");
        expenses[i].note = sc.nextLine();

        System.out.println("Expense Updated Successfully");
    }

    void deleteExpense(Scanner sc) {
        if (count == 0) {
            System.out.println("No expenses to delete");
            return;
        }

        viewExpenses();
        System.out.print("Select expense number to delete: ");
        int i = sc.nextInt() - 1;

        if (i < 0 || i >= count) {
            System.out.println("Invalid selection");
            return;
        }

        for (int j = i; j < count - 1; j++) {
            expenses[j] = expenses[j + 1];
        }
        count--;

        System.out.println("Expense Deleted Successfully");
    }

    void displayRemainingBalance() {
        double total=0;
        for(int i=0;i<count;i++) total+=expenses[i].amount;
        System.out.println("Remaining Balance: Rs."+(monthlyBudget-total));
    }
}

// -------- MAIN CLASS --------
public class Main {

    // 🔐 LOGIN CHECK METHOD
    static boolean loginCheck(Scanner sc, User owner) {
        System.out.print("Enter ID: ");
        String uid = sc.next();
        System.out.print("Enter Password: ");
        String upass = sc.next();
        return owner.id.equals(uid) && owner.password.equals(upass);
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Create ID: ");
        String id = sc.next();
        System.out.print("Create Password: ");
        String pass = sc.next();

        User owner = new User(id, pass);

        // Budget validation - must be greater than 0
        double budget;
        while (true) {
            System.out.print("Enter Monthly Budget: ");
            budget = sc.nextDouble();
            if (budget > 0) break;
            System.out.println("Budget must be greater than 0!");
        }

        // Enter today's date ONCE at startup (DD/MM/YYYY format)
        String todayDate;
        String[] dateParts;
        int currentDay, currentMonth, currentYear;
        while (true) {
            System.out.print("Enter Today's Date (DD/MM/YYYY): ");
            todayDate = sc.next();
            dateParts = todayDate.split("/");
            
            // Validate format: must have 3 parts and all digits
            if (dateParts.length != 3 || 
                !dateParts[0].matches("\\d+") || 
                !dateParts[1].matches("\\d+") || 
                !dateParts[2].matches("\\d+")) {
                System.out.println("Invalid format! Use DD/MM/YYYY (e.g., 06/02/2026)");
                continue;
            }
            
            currentDay = Integer.parseInt(dateParts[0]);
            currentMonth = Integer.parseInt(dateParts[1]);
            currentYear = Integer.parseInt(dateParts[2]);
            
            // Validate date ranges
            if (currentMonth < 1 || currentMonth > 12) {
                System.out.println("Invalid month! Must be 1-12.");
                continue;
            }
            if (currentDay < 1 || currentDay > 31) {
                System.out.println("Invalid day! Must be 1-31.");
                continue;
            }
            if (currentYear < 2000 || currentYear > 2100) {
                System.out.println("Invalid year! Must be 2000-2100.");
                continue;
            }
            
            // Check 30-day months
            if ((currentMonth == 4 || currentMonth == 6 || currentMonth == 9 || currentMonth == 11) && currentDay > 30) {
                System.out.println("Invalid day! This month has only 30 days.");
                continue;
            }
            
            // Check February with leap year
            if (currentMonth == 2) {
                boolean isLeap = (currentYear % 400 == 0) || (currentYear % 100 != 0 && currentYear % 4 == 0);
                int maxDays = isLeap ? 29 : 28;
                if (currentDay > maxDays) {
                    System.out.println("Invalid day! February " + currentYear + " has only " + maxDays + " days.");
                    continue;
                }
            }
            
            break;  // All validations passed
        }

        SmartBudget sb = new SmartBudget(owner, budget, currentDay, currentMonth, currentYear);

        boolean run = true;

        while (run) {
            System.out.println("\nLogin As:");
            System.out.println("1. User");
            System.out.println("2. Family");
            System.out.println("3. Exit");

            int login = sc.nextInt();

            // EXIT
            if (login == 3) {
                System.out.println("\nThank you for using Smart Budget Tracker");
                break;
            }

            // Invalid option check
            if (login != 1 && login != 2) {
                System.out.println("Invalid option! Please enter 1, 2, or 3.");
                continue;
            }

            // ASK ID & PASSWORD AGAIN
            if (!loginCheck(sc, owner)) {
                System.out.println("Invalid Login");
                continue;
            }

            // -------- USER LOGIN --------
            if (login == 1) {
                int choice;
                do {
                    sb.displayRemainingBalance();

                    System.out.println("\n1.Add Expense");
                    System.out.println("2.Quick Expense");
                    System.out.println("3.View Expenses");
                    System.out.println("4.Monthly Summary");
                    System.out.println("5.Category Summary");
                    System.out.println("6.Most Used Category");
                    System.out.println("7.Highest Spending Category");
                    System.out.println("8.Highest Single Expense");
                    System.out.println("9.Financial Personality");
                    System.out.println("10.Search Expense");
                    System.out.println("11.Edit Expense");
                    System.out.println("12.Delete Expense");
                    System.out.println("13.Logout");

                    choice = sc.nextInt();

                    switch (choice) {
                        case 1: sb.addExpense(sc); break;
                        case 2: sb.quickExpense(sc); break;
                        case 3: sb.viewExpenses(); break;
                        case 4: sb.monthlySummary(sc); break;
                        case 5: sb.categorySummary(); break;
                        case 6: sb.mostUsedCategory(); break;
                        case 7: sb.highestSpendingCategory(); break;
                        case 8: sb.highestSingleExpense(); break;
                        case 9: sb.financialPersonality(); break;
                        case 10: sb.searchExpense(sc); break;
                        case 11: sb.editExpense(sc); break;
                        case 12: sb.deleteExpense(sc); break;
                        case 13: break;
                        default: System.out.println("Invalid option! Enter 1-13.");
                    }

                } while (choice != 13);
            }

            // -------- FAMILY LOGIN --------
            else if (login == 2) {
                int choice;
                do {
                    System.out.println("\n1.Add Expense");
                    System.out.println("2.Quick Expense");
                    System.out.println("3.Logout");

                    choice = sc.nextInt();

                    if (choice == 1) sb.addExpense(sc);
                    else if (choice == 2) sb.quickExpense(sc);
                    else if (choice != 3) System.out.println("Invalid option! Enter 1-3.");

                } while (choice != 3);
            }
        }
    }
                               }
