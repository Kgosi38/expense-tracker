package za.co.kgosi.expensetracker;

import za.co.kgosi.expensetracker.model.Category;

public class App {

    public static void main(String[] args) {
        System.out.println("Expense Tracker is running on Java " + Runtime.version().feature());
        System.out.println(Category.FOOD + " is an " + Category.FOOD.getType());
        System.out.println(Category.BURSARY + " is an " + Category.BURSARY.getType());
    }
}