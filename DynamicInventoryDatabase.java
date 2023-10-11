// **********************************************************************************
// Title: MajorProjectPart1
// Author: Juan Irias-Sanchez
// Course Section: CMIS201-ONL1 (Seidel) Fall 2022
// File: DynamicInventoryDatabase.java
// Description:
// **********************************************************************************

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.text.DecimalFormat;

class InventoryItem{
    private final String name;
    private double price;
    private int quantity;

    public InventoryItem(String name, double price, int quantity){
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
    public String getName(){
        return name;
    }

    public double getPrice(){
        return price;
    }
    public void setPrice(double price){
        this.price = price;
    }
    public int getQuantity(){
        return quantity;
    }
    public void setQuantity(int quantity){
        this.quantity = quantity;
    }
    @Override
    public String toString(){
        return name + "," + price + "," + quantity;
    }
}

public class DynamicInventoryDatabase{
    private static final String FILE_PATH = "inventory.txt";
    private static final List<InventoryItem> inventory = new ArrayList<>();

  public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new DynamicInventoryFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setPreferredSize(new Dimension(500, 300));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
    private static class DynamicInventoryFrame extends JFrame {
        DecimalFormat df = new DecimalFormat("#0.00");
        private final JTextField itemNameField;
        private final JTextField quantityField;
        private final JTextField priceField;
        private final JTextArea inventoryTextArea;

        public DynamicInventoryFrame() {
            setTitle("Inventory Management System");
            setLayout(new BorderLayout());

            inventoryTextArea = new JTextArea(10, 30);
            inventoryTextArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(inventoryTextArea);
            add(scrollPane, BorderLayout.CENTER);

            JPanel inputPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(5, 5, 5, 5);

            itemNameField = new JTextField(15);
            quantityField = new JTextField(15);
            priceField = new JTextField(15);

            inputPanel.add(new JLabel("Item Name: "), gbc);
            gbc.gridx++;
            inputPanel.add(itemNameField, gbc);
            gbc.gridx = 0;
            gbc.gridy++;
            inputPanel.add(new JLabel("Price: "), gbc);
            gbc.gridx++;
            inputPanel.add(priceField, gbc);
            gbc.gridx = 0;
            gbc.gridy++;
            inputPanel.add(new JLabel("Quantity: "), gbc);
            gbc.gridx++;
            inputPanel.add(quantityField, gbc);
            gbc.gridx = 0;
            gbc.gridy++;

            JButton addButton = new JButton("Add Item");
            JButton updateButton = new JButton("Update Quantity and Price");
            JButton deleteButton = new JButton("Delete Item");
            JButton sortByNameButton = new JButton("Sort By Brand");
            JButton sortByPriceButton = new JButton("Sort by price(Highest)");

            addButton.addActionListener(e -> {
                String itemName = itemNameField.getText().trim();
                String quantityStr = quantityField.getText().trim();
                String priceStr = priceField.getText().trim();
                int quantity;
                double price;
                if (inventoryContainsItemWithName(itemName)) {
                    JOptionPane.showMessageDialog(DynamicInventoryFrame.this,
                            "Item already Exists. Please edit Price or Quantity.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    quantity = Integer.parseInt(quantityStr);
                    price = Double.parseDouble(priceStr);
                    InventoryItem newItem = new InventoryItem(itemName, price, quantity);
                    inventory.add(newItem);
                    updateInventoryTextArea();
                    saveInventoryToTextFile();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(DynamicInventoryFrame.this,
                            "Invalid quantity or price. Please enter valid numbers.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            updateButton.addActionListener(e -> {
                String itemName = itemNameField.getText().trim();
                String priceStr = priceField.getText().trim();
                String quantityStr = quantityField.getText().trim();
                int quantity;
                double price;
                try {
                    quantity = Integer.parseInt(quantityStr);
                    price = Double.parseDouble(priceStr);

                    for (InventoryItem item : inventory) {
                        if (item.getName().equalsIgnoreCase(itemName)) {
                            item.setQuantity(quantity);
                            item.setPrice(price);
                            updateInventoryTextArea();
                            saveInventoryToTextFile();
                            return;
                        }
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(DynamicInventoryFrame.this,
                            "Invalid quantity or price. Please enter valid numbers.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

            });

            deleteButton.addActionListener(e -> {
                String itemName = itemNameField.getText().trim();
                for (InventoryItem item : inventory) {
                    if (item.getName().equalsIgnoreCase(itemName)) {
                        inventory.remove(item);
                        updateInventoryTextArea();
                        saveInventoryToTextFile();
                        return;
                    }
                }
                JOptionPane.showMessageDialog(DynamicInventoryFrame.this,
                        "Item not found in the inventory.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            });

            sortByNameButton.addActionListener(e -> {
                mergeSortByName(inventory);
                updateInventoryTextArea();
            });
            sortByPriceButton.addActionListener(e -> {
                mergeSortByPrice(inventory);
                updateInventoryTextArea();
            });


            inputPanel.add(addButton, gbc);
            gbc.gridy++;
            inputPanel.add(sortByNameButton, gbc);
            gbc.gridx++;
            inputPanel.add(sortByPriceButton, gbc);
            gbc.gridy--;
            inputPanel.add(updateButton, gbc);
            gbc.gridx++;
            inputPanel.add(deleteButton, gbc);
            gbc.gridx++;


            add(inputPanel, BorderLayout.SOUTH);
            loadInventoryFromTextFile();
            updateInventoryTextArea();
        }

        private void loadInventoryFromTextFile() {
            try {
                Scanner scanner = new Scanner(new File(FILE_PATH));
                inventory.clear();
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        String itemName = parts[0].trim();
                        double price = Double.parseDouble(parts[1].trim());
                        int quantity = Integer.parseInt(parts[2].trim());
                        InventoryItem item = new InventoryItem(itemName, price, quantity);
                        inventory.add(item);
                    }
                }
                scanner.close();
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(DynamicInventoryFrame.this,
                        "File Not Found.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void saveInventoryToTextFile() {
            try {
                PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH));
                for (InventoryItem item : inventory) {
                    writer.println(item.getName() + "," + df.format(item.getPrice()) + "," + item.getQuantity());
                }
                writer.close();
            } catch (IOException a) {
                JOptionPane.showMessageDialog(this, "Error saving the inventory to the file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void updateInventoryTextArea() {
            StringBuilder sb = new StringBuilder();
            for (InventoryItem item : DynamicInventoryDatabase.inventory) {
                sb.append(item.getName()).append("  /// Quantity: ")
                        .append(item.getQuantity()).append("  /// Price: $")
                        .append(df.format(item.getPrice())).append("\n");
            }
            inventoryTextArea.setText(sb.toString());
        }

        private boolean inventoryContainsItemWithName(String name) {
            for (InventoryItem item : inventory) {
                if (item.getName().equals(name)) {
                    return true;
                }
            }
            return false;
        }
        private void mergeSortByName(List<InventoryItem> items) {
            if (items.size() <= 1) {
                return;
            }
            List<InventoryItem> left = new ArrayList<>();
            List<InventoryItem> right = new ArrayList<>();
            int middle = items.size() / 2;

            for (int i = 0; i < middle; i++) {
                left.add(items.get(i));
            }
            for (int i = middle; i < items.size(); i++) {
                right.add(items.get(i));
            }

            mergeSortByName(left);
            mergeSortByName(right);

            mergeByName(items, left, right);
        }
        private void mergeSortByPrice(List<InventoryItem> items) {
            if (items.size() <= 1) {
                return;
            }
            List<InventoryItem> left = new ArrayList<>();
            List<InventoryItem> right = new ArrayList<>();
            int middle = items.size() / 2;

            for (int i = 0; i < middle; i++) {
                left.add(items.get(i));
            }
            for (int i = middle; i < items.size(); i++) {
                right.add(items.get(i));
            }

            mergeSortByPrice(left);
            mergeSortByPrice(right);

            mergeByPrice(items, left, right);
        }
        //The time complexity: O(n log n) for both sorting by name and sorting by price.
        private void mergeByName(List<InventoryItem> items, List<InventoryItem> left, List<InventoryItem> right) {
            int i = 0, j = 0, k = 0;
            while (i < left.size() && j < right.size()) {
                if (left.get(i).getName().compareToIgnoreCase(right.get(j).getName()) < 0) {
                    items.set(k, left.get(i));
                    i++;
                } else {
                    items.set(k, right.get(j));
                    j++;
                }
                k++;
            }
            while (i < left.size()) {
                items.set(k, left.get(i));
                i++;
                k++;
            }
            while (j < right.size()) {
                items.set(k, right.get(j));
                j++;
                k++;
            }
        }
        private void mergeByPrice(List<InventoryItem> items, List<InventoryItem> left, List<InventoryItem> right) {
            int i = 0, j = 0, k = 0;
            while (i < left.size() && j < right.size()) {
                if (left.get(i).getPrice() > right.get(j).getPrice()) {
                    items.set(k, left.get(i));
                    i++;
                } else {
                    items.set(k, right.get(j));
                    j++;
                }
                k++;
            }
            while (i < left.size()) {
                items.set(k, left.get(i));
                i++;
                k++;
            }
            while (j < right.size()) {
                items.set(k, right.get(j));
                j++;
                k++;
            }
        }

    }

}

