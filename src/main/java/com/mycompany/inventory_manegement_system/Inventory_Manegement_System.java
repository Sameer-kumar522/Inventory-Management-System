package com.mycompany.inventory_manegement_system;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
 
class Product {
    String name;
    int quantity;
    double price;

    public Product(String name, int quantity, double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }
}

class DBHelper {
    private static final String DB_URL = "jdbc:sqlite:inventory.db";    

    public static Connection connect() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS products (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                     "name TEXT NOT NULL" +
                     "quantity INTEGER NOT NULL," +
                     "price REAL NOT NULL)";
        try (Connection conn = connect(); Statement stmt= conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}  
public class Inventory_Manegement_System {
    private final ArrayList<Product> productList = new ArrayList<>();
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final String role;

    public Inventory_Manegement_System(String role) {
        this.role = role;
        DBHelper.createTable();

        JFrame frame = new JFrame("Inventory Management System");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"Name", "Quantity", "Price"}, 0);
        table = new JTable(tableModel);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        JButton addButton = new JButton("Add Product");
        JButton  updateButton = new JButton("Update Product");
        JButton deleteButton = new JButton("Delete Product");

        addButton.setBackground(new Color(100, 180, 100));
        addButton.setForeground(Color.WHITE);
        updateButton.setBackground(new Color(255, 165, 0));
        updateButton.setForeground(Color.WHITE);
        deleteButton.setBackground(new Color(220, 50, 50));
        deleteButton.setForeground(Color.WHITE);

       
        if (role.equals("manager")) {
            deleteButton.setEnabled(false);
        } else if (role.equals("supervisor")) {
            addButton.setEnabled(false);
            updateButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }

        panel.add(addButton);
        panel.add(updateButton);
        panel.add(deleteButton);
        frame.add(panel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addProduct());
        updateButton.addActionListener(e -> updateProduct());
        deleteButton.addActionListener(e -> deleteProduct());

        loadProductsFromDB();
        frame.setVisible(true);
    }

    private void addProduct() {
        JTextField nameField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField priceField = new JTextField();
        Object[] fields = {"Name:", nameField, "Quantity:", quantityField, "Price:", priceField};

        int option = JOptionPane.showConfirmDialog(null, fields, "Add Product", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                int quantity = Integer.parseInt(quantityField.getText());
                double price = Double.parseDouble(priceField.getText());
                
                
               if (quantity < 0 || price < 0) {
                   JOptionPane.showMessageDialog(null, "Quantity and Price must be non-negative."); 
                   return;
   }     

                String sql = "INSERT INTO products(name, quantity, price) VALUES (?, ?, ?)";
                try (Connection conn = DBHelper.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, name);
                    pstmt.setInt(2, quantity);
                    pstmt.setDouble(3, price);
                    pstmt.executeUpdate();
                }

                productList.add(new Product(name, quantity, price));
                tableModel.addRow(new Object[]{name, quantity, price});
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Invalid input!");
            }
        }
    }

    private void updateProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a product to update.");
            return;
        }

        JTextField nameField = new JTextField((String) tableModel.getValueAt(selectedRow, 0));
        JTextField quantityField = new JTextField(tableModel.getValueAt(selectedRow, 1).toString());
        JTextField priceField = new JTextField(tableModel.getValueAt(selectedRow, 2).toString());
        Object[] fields = {"Name:", nameField, "Quantity:", quantityField, "Price:", priceField};
           if(role=="manager"){
            quantityField.setEnabled(false);
            priceField.setEnabled(false);
            
        }

        int option = JOptionPane.showConfirmDialog(null, fields, "Update Product", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                int quantity = Integer.parseInt(quantityField.getText());
                double price = Double.parseDouble(priceField.getText());

                String oldName = (String) tableModel.getValueAt(selectedRow, 0);
                String sql = "UPDATE products SET name = ?, quantity = ?, price = ? WHERE name = ? ";
                try (Connection conn = DBHelper.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, name);
                    pstmt.setInt(2, quantity);
                    pstmt.setDouble(3, price);
                    pstmt.setString(4, oldName);
                    pstmt.executeUpdate();
                    
                }
             

                productList.get(selectedRow).name = name;
                productList.get(selectedRow).quantity = quantity;
                productList.get(selectedRow).price = price;

                tableModel.setValueAt(name, selectedRow, 0);
                tableModel.setValueAt(quantity, selectedRow, 1);
                tableModel.setValueAt(price, selectedRow, 2);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Invalid input!");
            }
        }
    }

    private void deleteProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a product to delete.");
            return;
        }

        int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this product?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            String name = (String) tableModel.getValueAt(selectedRow, 0);
            String sql = "DELETE FROM products WHERE name = ?";
            try (Connection conn = DBHelper.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            productList.remove(selectedRow);
            tableModel.removeRow(selectedRow);
        } 
    }

    private void loadProductsFromDB() {
        try (Connection conn = DBHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM products")) {

            while (rs.next()) {
                String name = rs.getString("name");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");

                productList.add(new Product(name, quantity, price));
                tableModel.addRow(new Object[]{name, quantity, price});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Inventory_Manegement_System::showLogin);
    }

    private static void showLogin() { 
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        Object[] fields = {"Username:", usernameField, "Password:", passwordField};

        int option = JOptionPane.showConfirmDialog(null, fields, "Login", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.equals("admin") && password.equals("admin123")) {
                JOptionPane.showMessageDialog(null, "Admin Login Successful!");
                new Inventory_Manegement_System("admin");
            } else if (username.equals("manager") && password.equals("mng123")) {
                JOptionPane.showMessageDialog(null, "Manager Login Successful!");
                new Inventory_Manegement_System("manager");
            } else if (username.equals("supervisor") && password.equals("sv123")) {
                JOptionPane.showMessageDialog(null, "Supervisor Login Successful!");
                new Inventory_Manegement_System("supervisor");
            } else {
                JOptionPane.showMessageDialog(null, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
