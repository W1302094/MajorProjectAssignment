// **********************************************************************************
// Title: MajorProjectPart1
// Author: Juan Irias-Sanchez
// Course Section: CMIS201-ONL1 (Seidel) Fall 2022
// File: Main.java
// Description: This file is specifically dedicated to the arrangement of items kept within a text file that an
// administrative user can utilize to add, delete, or update inventories that will be saved to the text file.
//Additionally, it has the capability of sorting the inventory items by highest price or by brand name, making it easier to locate certain items.
// ******
import javax.swing.*;

public class Main {
    public static void main(String[] args){
        SwingUtilities.invokeLater(CustomerManagement::createAndShowGUI);
    }
}
