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


public class CustomerManagement{
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
            String filePath = "customer_files" + File.separator + enteredUsername + ".txt";
            File customerFile = new File(filePath);

            if (!customerFile.exists()) {
                int choice = JOptionPane.showConfirmDialog(frame, "No Account found. Would you like to create a new account?",
                "Create Account", JOptionPane.YES_NO_OPTION);
                if(choice == JOptionPane.YES_OPTION) {
                    try {
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

                }
                else{
                    usernameField.setText("");
                    passwordField.setText("");
                    JOptionPane.showMessageDialog(frame, "Unfortunately without an account you may not enter the site");
                    frame.setVisible(false);
                    System.exit(0);
                }
            }

            boolean isAuthorized = checkAuthorization(enteredUsername, enteredPassword, customerFile);

            if (isAuthorized) {
                JOptionPane.showMessageDialog(frame, "Authorization successful");
                frame.setVisible(false);
                frame.dispose();
                //Code to display order history goes here...
                boolean isAdmin = checkAdminAuthorization(enteredUsername, enteredPassword);
                if(isAdmin){
                    SwingUtilities.invokeLater(CustomerManagement::openDynamicInventoryDatabase);
                }
                else{
                    System.out.println("Goes to store app");
                    //SwingUtilities.invokeLater(() -> openStore());
                }
                System.out.println("frame went away");

            }
            else {

                    JOptionPane.showMessageDialog(null,
                            "Invalid Password",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    usernameField.setText("");
                    passwordField.setText("");
                    createAndShowGUI();

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
    private static boolean checkAuthorization(String username, char[] password, File customerPath ){
        try(BufferedReader br = new BufferedReader(new FileReader(customerPath))){
            String storedUsername = br.readLine();
            String storedPassword = br.readLine();
            String result = charArrayToString(password);
            String enteredUsername = storedUsername.replace("Username: ", "");
            String enteredPassword = storedPassword.replace("Password: ", "");

            return username.equals(enteredUsername) && result.equals(enteredPassword);

        }
        catch(IOException e){
            e.printStackTrace();
        }
        return false;
    }
    public static String charArrayToString(char[] charArray){
        return new String(charArray);
    }
    private static void openDynamicInventoryDatabase(){
        DynamicInventoryDatabase.main(new String[]{});
    }
}
