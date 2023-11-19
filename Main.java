// **********************************************************************************
// Title: MajorProjectPart1
// Author: Juan Irias-Sanchez
// Course Section: CMIS202-ONL1 (Seidel) Fall 2023
// File: Main.java
// Description: The file is used as the central point to run the program.Making it easier for further expansion.
// ******
import javax.swing.*;

public class Main {
    public static void main(String[] args){
        SwingUtilities.invokeLater(CustomerManagement::createAndShowGUI);
    }
}
