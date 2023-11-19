// **********************************************************************************
// Title: MajorProjectPart1
// Author: Juan Irias-Sanchez
// Course Section: CMIS202-ONL1 (Seidel) Fall 2023
// File: DynamicInventoryDatabase.java
// Description:
// **********************************************************************************

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.text.DecimalFormat;
import java.util.List;

class InventoryItem{

    private final String brand;
    private final String name;
    private double price;
    private int quantity;

    public InventoryItem(String brand, String name, double price, int quantity){
        this.brand = brand;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
    public String getBrand(){
        return brand;
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
        return brand + ", " + name + "," + price + "," + quantity;
    }
}

public class DynamicInventoryDatabase extends InventoryItem{
    private static final String filePath = "inventory.txt";
    public static final List<InventoryItem> inventory = new ArrayList<>();
    public static final Map<String, InventoryItem> inventoryMap = new HashMap<>();

    public DynamicInventoryDatabase(String brand, String name, double price, int quantity) {
        super(brand, name, price, quantity);
    }



    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new DynamicInventoryFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setPreferredSize(new Dimension(550, 500));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
    private static class DynamicInventoryFrame extends JFrame {
        DecimalFormat df = new DecimalFormat("#0.00");
        private final JTextField brandNameField;
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

            brandNameField = new JTextField(15);
            itemNameField = new JTextField(15);
            quantityField = new JTextField(15);
            priceField = new JTextField(15);


            inputPanel.add(new JLabel("Brand: "), gbc);
            gbc.gridx++;
            inputPanel.add(brandNameField, gbc);
            gbc.gridx = 0;
            gbc.gridy++;
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
            JButton searchButton = new JButton("Search For");

            addButton.addActionListener(e -> {
                String brandName = brandNameField.getText().trim();
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
                    InventoryItem newItem = new InventoryItem(brandName, itemName, price, quantity);
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
            searchButton.addActionListener(e -> {
                switchToSearchPanel();
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
            gbc.gridy++;
            inputPanel.add(searchButton, gbc);
            gbc.gridx++;


            add(inputPanel, BorderLayout.SOUTH);
            loadInventoryFromTextFile();
            updateInventoryTextArea();
        }

        private void loadInventoryFromTextFile() {
            try {
                Scanner scanner = new Scanner(new File(filePath));
                inventory.clear();
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");
                    if (parts.length == 4) {
                        String brand = parts[0].trim();
                        String itemName = parts[1].trim();
                        double price = Double.parseDouble(parts[2].trim());
                        int quantity = Integer.parseInt(parts[3].trim());
                        InventoryItem item = new InventoryItem(brand, itemName, price, quantity);
                        inventory.add(item);
                        inventoryMap.put(brand + itemName, item);
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
                PrintWriter writer = new PrintWriter(new FileWriter(filePath));
                for (InventoryItem item : inventory) {
                    writer.println(item.getBrand() + "," + item.getName() + "," + df.format(item.getPrice()) + "," + item.getQuantity());
                }
                writer.close();
            } catch (IOException a) {
                JOptionPane.showMessageDialog(this, "Error saving the inventory to the file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void updateInventoryTextArea() {
            StringBuilder sb = new StringBuilder();
            for (InventoryItem item : DynamicInventoryDatabase.inventory) {
                sb.append("  /// Brand: ").append(item.getBrand()).append("  /// Item: ").append(item.getName()).append("  /// Quantity: ").append(item.getQuantity()).append("  /// Price: $").append(df.format(item.getPrice())).append("\n");
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
                if (left.get(i).getBrand().compareToIgnoreCase(right.get(j).getBrand()) < 0) {
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

        private void switchToSearchPanel(){
            getContentPane().removeAll();
            getContentPane().add(new SearchPanel(), BorderLayout.CENTER);
            revalidate();
            repaint();
        }
    }
    private static class SearchPanel extends JPanel{
        private final JTextField searchBrandField;
        private final JTextField searchPartField;
        private final JTextArea searchResultsTextArea;


        public SearchPanel(){

            setLayout(new BorderLayout());

            searchBrandField = new JTextField(15);
            searchPartField = new JTextField(15);
            searchResultsTextArea = new JTextArea(10, 30);
            searchResultsTextArea.setEditable(false);

            JButton backButton = new JButton("Back to Inventory");
            backButton.addActionListener(e -> {
                switchToInventoryPanel();
            });

            JButton searchByNameButton = new JButton("Search");
            searchByNameButton.addActionListener(e -> {
                searchInventory();
            });

            JPanel searchInputPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(5,5,5,5);

            searchInputPanel.add(new JLabel("Search by Brand Name: "), gbc);
            gbc.gridx++;
            searchInputPanel.add(searchBrandField, gbc);
            gbc.gridy++;
            gbc.gridx--;
            searchInputPanel.add(new JLabel("Search by Part Name: "), gbc);
            gbc.gridx--;
            searchInputPanel.add(searchPartField, gbc);
            gbc.gridy++;
            gbc.gridx = gbc.gridx + 2;
            searchInputPanel.add(searchByNameButton, gbc);

            add(searchInputPanel, BorderLayout.NORTH);
            add(new JScrollPane(searchResultsTextArea), BorderLayout.CENTER);
            add(backButton, BorderLayout.SOUTH);
        }




        private void searchInventory(){
            String searchBrandName = searchBrandField.getText().trim();
            String searchPartName = searchPartField.getText().trim();
            if(!searchBrandName.isEmpty() || !searchPartName.isEmpty()){
                StringBuilder searchResults = new StringBuilder("Search Results: \n");

                for(InventoryItem item : inventoryMap.values()){
                    boolean brandNameMatches = item.getBrand().equalsIgnoreCase(searchBrandName);
                    boolean partNameMatches = item.getName().equalsIgnoreCase(searchPartName);

                    if((brandNameMatches || searchBrandName.isEmpty()) && (partNameMatches||searchPartName.isEmpty())){
                        searchResults.append(item.toString()).append("\n");
                    }
                }
                if(searchResults.length() > "Search Results: \n".length()){
                    searchResultsTextArea.setText(searchResults.toString());
                }else{
                    searchResultsTextArea.setText("No items found");
                }
            }else{
                searchResultsTextArea.setText("Please Enter a Valid Brand name or Part Name to Begin Search");
            }
        }
        private void switchToInventoryPanel() {
            DynamicInventoryFrame inventoryFrame = new DynamicInventoryFrame();
            inventoryFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            inventoryFrame.setPreferredSize(new Dimension(550, 500));
            inventoryFrame.pack();
            inventoryFrame.setLocationRelativeTo(null);
            inventoryFrame.setVisible(true);
            SwingUtilities.getWindowAncestor(this).dispose();
        }
    }


}
