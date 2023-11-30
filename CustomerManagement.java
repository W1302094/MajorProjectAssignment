// **********************************************************************************
// Title: MajorProjectPart1
// Author: Juan Irias-Sanchez
// Course Section: CMIS202-ONL1 (Seidel) Fall 2023
// File: CustomerManagement.java
// Description: The point of this file is to prompt user and password and check from a folder of existing customers.
// If the user exists they will be able to log in and then proceed to shop. If user does not exist it will ask if they would like to register.
//If you enter with the admin credentials (user: admin , pass: abc) it will allow you to the inventory database.
// ******

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;

class Customer {
    private String username;
    private String password;

    public Customer(String username, String password){
        this.username = username;
        this.password = password;
    }

    public String getUsername(){
        return username;
    }
    public String getPassword(){
        return password;
    }
}

class CustomerNode {
    Customer customer;
    CustomerNode left, right;

    public CustomerNode(Customer customer){
        this.customer = customer;
        this.left = this.right = null;
    }
}

class CustomerBST{
    private CustomerNode root;

    public CustomerBST(){
        this.root = null;
    }

    public void insert(Customer customer){
        root = insertRec(root, customer);
    }

    private CustomerNode insertRec(CustomerNode root, Customer customer){
        if(root == null){
            root = new CustomerNode(customer);
            return root;
        }
        if(customer.getUsername().compareTo(root.customer.getUsername()) < 0){
            root.left = insertRec(root.left, customer);
        }
        else if(customer.getUsername().compareTo(root.customer.getUsername()) > 0){
            root.right = insertRec(root.right, customer);
        }
        return root;

    }
    public boolean search(String username, char[] password){
        return searchRec(root, username, password);
    }

    private boolean searchRec(CustomerNode root, String username, char[] password){
        if(root == null){
            return false;
        }
        if(username.equals(root.customer.getUsername()) && charArrayToString(password).equals(root.customer.getPassword())){
            return true;
        }
        if(username.compareTo(root.customer.getUsername()) < 0){
            return searchRec(root.left, username, password);
        }
        else{
            return searchRec(root.right, username, password);
        }
    }

    private static String charArrayToString(char[] charArray) {
        return new String(charArray);
    }


}


public class CustomerManagement{
    private static CustomerBST customerBST = new CustomerBST();

    public static void main(String[] args){
        loadCustomersIntoBST();
        SwingUtilities.invokeLater(CustomerManagement::createAndShowGUI);
    }

    private static void loadCustomersIntoBST(){
        File customerFilesDirectory = new File("customer_files");

        if(customerFilesDirectory.exists() && customerFilesDirectory.isDirectory()){
            File[] customerFiles = customerFilesDirectory.listFiles();

            if(customerFiles != null){
                for(File customerFile : customerFiles){
                    if(customerFile.isFile() && customerFile.getName().endsWith(".txt")){
                        try(BufferedReader br = new BufferedReader(new FileReader(customerFile))){
                            String storedUsername = br.readLine();
                            String storedPassword = br.readLine();

                            String username = storedUsername.replace("Username: ", "");
                            String password = storedPassword.replace("Password: ", "");

                            Customer customer = new Customer(username, password);
                            customerBST.insert(customer);

                        } catch(IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            }



        }
        Customer admin = new Customer("admin" , "abc");
        customerBST.insert(admin);
    }

    public static void createAndShowGUI(){

        JFrame frame = new JFrame("Sign In Account");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 150);
        frame.setLayout(new FlowLayout());

        JLabel usernameLabel = new JLabel("Username: ");
        JTextField usernameField = new JTextField(15);

        JLabel passwordLabel = new JLabel("Password: ");
        JPasswordField passwordField = new JPasswordField(15);

        JButton loginButton = new JButton("Login");

        ActionListener loginActionListener = e -> {
            String enteredUsername = usernameField.getText();
            char[] enteredPassword = passwordField.getPassword();

            boolean isAuthorized = customerBST.search(enteredUsername, enteredPassword);

            if(isAuthorized){
                JOptionPane.showMessageDialog(frame, "Authorization successful");
                frame.setVisible(false);
                frame.dispose();

                boolean isAdmin = checkAdminAuthorization(enteredUsername, enteredPassword);
                if(isAdmin){
                    SwingUtilities.invokeLater(CustomerManagement::openDynamicInventoryDatabase);
                }else{
                    displayCustomerItems(enteredUsername);
                }
            }else {
                int choice = JOptionPane.showConfirmDialog(frame, "No Account found. Would you like to create a new account?",
                        "Create Account", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    try {
                        String filePath = "customer_files" + File.separator + enteredUsername + ".txt";
                        File customerFile = new File(filePath);

                        FileWriter writer = new FileWriter(customerFile);
                        writer.write("Username: " + enteredUsername + "\n");
                        String result = charArrayToString(enteredPassword);
                        writer.write("Password: " + result + "\n");
                        writer.close();
                        System.out.println("Customer registered successfully!");
                    } catch (IOException f) {
                        System.out.println("An error occurred while creating the customer file.");
                        f.printStackTrace();
                    }

                } else {
                    usernameField.setText("");
                    passwordField.setText("");
                    JOptionPane.showMessageDialog(frame, "Unfortunately without an account you may not enter the site");
                    frame.setVisible(false);
                    System.exit(0);
                }
            }
        };

        loginButton.addActionListener(loginActionListener);

        frame.add(usernameLabel);
        frame.add(usernameField);
        frame.add(passwordLabel);
        frame.add(passwordField);
        frame.add(loginButton);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);

    }

    private static boolean checkAdminAuthorization(String Username, char[] password){

        return Username.equals("admin") && charArrayToString(password).equals("abc");
    }
    private static boolean checkAuthorization(String username, char[] password){
        return customerBST.search(username, password);
    }
    public static String charArrayToString(char[] charArray){
        return new String(charArray);
    }
    private static void openDynamicInventoryDatabase(){
        DynamicInventoryDatabase.main(new String[]{});
    }

    private static void displayCustomerItems(String username){
        JFrame itemFrame = new JFrame("Customer Items: ");
        itemFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        itemFrame.setSize(400, 300);

        JTextArea itemTextArea = new JTextArea();
        itemTextArea.setEditable(false);

        String filePath = "customer_files" + File.separator + username + ".txt";
        try(BufferedReader br = new BufferedReader(new FileReader(filePath))){
            String line;
            int lineCount = 0;
            while((line = br.readLine()) != null){
                lineCount++;
                if(lineCount > 2){
                    itemTextArea.append(line + "\n");
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JScrollPane scrollPane = new JScrollPane(itemTextArea);
        itemFrame.add(scrollPane);
        itemFrame.setVisible(true);
        itemFrame.setLocationRelativeTo(null);
    }
}
