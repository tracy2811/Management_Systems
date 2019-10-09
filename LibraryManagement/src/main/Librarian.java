
package main;

import java.awt.Component;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class Librarian {
    // Admin can search, add, update, delete librarians
    private final Component parentComponent;
    
    public Librarian(Component parentComponent) {
        this.parentComponent = parentComponent;
    }
    
    DefaultTableModel searchLibrarian(int id, String fname, String lname, String username) {
        try {
            PreparedStatement ps;
            ResultSet rs;
            String query = "SELECT `id`, `first_name`, `last_name`, `username`, `password` FROM `users` WHERE `type`=2 AND (`id`=? OR `username`=? OR `first_name`=? OR `last_name`=?)";
            
            ps = MyConnection.createConnection().prepareStatement(query);
            ps.setInt(1, id);
            ps.setString(2, username);
            ps.setString(3, fname.toLowerCase());
            ps.setString(4, lname.toLowerCase());
            
            rs = ps.executeQuery();
            
            DefaultTableModel table = new DefaultTableModel(new Object[]{"ID", "First name", "Last name", "Username", "Password"}, 0);

            if (rs.next() == false) {
                JOptionPane.showMessageDialog(parentComponent, "No Librarian found!", "No Result", JOptionPane.INFORMATION_MESSAGE);
            } else {
                Object[] row;
                do {
                    row = new Object[5];
                    row[0] = rs.getInt(1);
                    row[1] = rs.getString(2);
                    row[2] = rs.getString(3);
                    row[3] = rs.getString(4);
                    row[4] = rs.getString(5);
                    table.addRow(row);
                } while (rs.next());
            }
            return table;
        } catch (SQLException ex) {
            Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    DefaultTableModel getAllLibrarians() {
        try {
            PreparedStatement ps;
            ResultSet rs;
            String query = "SELECT `id`, `first_name`, `last_name`, `username`, `password` FROM `users` WHERE `type`=2";
            
            ps = MyConnection.createConnection().prepareStatement(query);
            
            rs = ps.executeQuery();
            
            DefaultTableModel table = new DefaultTableModel(new Object[]{"ID", "First name", "Last name", "Username", "Password"}, 0);
            
            Object[] row;
            while(rs.next()) {
                row = new Object[5];
                row[0] = rs.getInt(1);
                row[1] = rs.getString(2);
                row[2] = rs.getString(3);
                row[3] = rs.getString(4);
                row[4] = rs.getString(5);
                table.addRow(row);
            }
            
            return table;
        } catch (SQLException ex) {
            Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    boolean addLibrarian(String fname, String lname, String username, String password) {
        try {
            PreparedStatement ps;
            String query = "INSERT INTO `users`(`first_name`, `last_name`, `username`, `password`, `type`) VALUES (?,?,?,?,2)";
            
            ps = MyConnection.createConnection().prepareStatement(query);
            ps.setString(1, fname.toLowerCase());
            ps.setString(2, lname.toLowerCase());
            ps.setString(3, username);
            ps.setString(4, password);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    boolean deleteLibrarianByID(int id) {
        try {
            PreparedStatement ps;
            String query = "DELETE FROM `users` WHERE `type`=2 AND`id`=?";
            
            ps = MyConnection.createConnection().prepareStatement(query);
            ps.setInt(1, id);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    boolean deleteLibrarianByUsername(String username) {
        try {
            PreparedStatement ps;
            String query = "DELETE FROM `users` WHERE `type`=2 AND `username`=?";
            
            ps = MyConnection.createConnection().prepareStatement(query);
            ps.setString(1, username);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    boolean updateLibrarianByID(int id, String fname, String lname, String username, String password) {
        try {
            PreparedStatement ps;
            String query = "UPDATE `users` SET `first_name`=?,`last_name`=?,`username`=?,`password`=? WHERE `type`=2 AND`id`=?";
            
            ps = MyConnection.createConnection().prepareStatement(query);
            ps.setString(1, fname.toLowerCase());
            ps.setString(2, lname.toLowerCase());
            ps.setString(3, username);
            ps.setString(4, password);
            ps.setInt(5, id);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
