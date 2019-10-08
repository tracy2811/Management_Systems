
package main;

import java.awt.Component;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Reader {
    private final Component parentComponent;
    
    public Reader(Component parentComponent) {
        this.parentComponent = parentComponent;
    }
    
    DefaultTableModel searchReader(int id, String fname, String lname) {
        try {
            PreparedStatement ps;
            ResultSet rs;
            String query = "SELECT `id`, `first_name`, `last_name` FROM `users` WHERE `type`=3 AND (`id`=? OR `first_name`=? OR `last_name`=?)";
            
            ps = MyConnection.createConnection().prepareStatement(query);
            ps.setInt(1, id);
            ps.setString(2, fname.toLowerCase());
            ps.setString(3, lname.toLowerCase());
            
            rs = ps.executeQuery();
            
            DefaultTableModel table = new DefaultTableModel(new Object[]{"ID", "First name", "Last name"}, 0);

            if (rs.next() == false) {
                JOptionPane.showMessageDialog(parentComponent, "No book found!", "No Result", JOptionPane.INFORMATION_MESSAGE);
            } else {
                Object[] row;
                do {
                    row = new Object[3];
                    row[0] = rs.getInt(1);
                    row[1] = rs.getString(2);
                    row[1] = rs.getString(3);
                    table.addRow(row);
                } while (rs.next());
            }
            return table;
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    DefaultTableModel getAllReaders() {
        try {
            PreparedStatement ps;
            ResultSet rs;
            String query = "SELECT `id`, `first_name`, `last_name` FROM `users` WHERE `type`=3";
            
            ps = MyConnection.createConnection().prepareStatement(query);
            
            rs = ps.executeQuery();
            
            DefaultTableModel table = new DefaultTableModel(new Object[]{"ID", "First name", "Last name"}, 0);
            
            Object[] row;
            while(rs.next()) {
                row = new Object[3];
                row[0] = rs.getInt(1);
                row[1] = rs.getString(2);
                row[2] = rs.getString(3);
                table.addRow(row);
            }
            
            return table;
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    boolean addReader(String fname, String lname) {
        try {
            PreparedStatement ps;
            String query = "INSERT INTO `users`(`first_name`, `last_name`, `type`) VALUES (?,?,3)";
            
            ps = MyConnection.createConnection().prepareStatement(query);
            ps.setString(1, fname.toLowerCase());
            ps.setString(2, lname.toLowerCase());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    boolean deleteReader(int id) {
        try {
            PreparedStatement ps;
            String query = "DELETE FROM `users` WHERE `type`=3 AND `id`=?";
            
            ps = MyConnection.createConnection().prepareStatement(query);
            ps.setInt(1, id);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    boolean updateReader(int id, String fname, String lname) {
        try {
            PreparedStatement ps;
            
            String query = "UPDATE `users` SET `first_name`=?,`last_name`=? WHERE `type`=3 AND`id`=?";
            ps = MyConnection.createConnection().prepareStatement(query);
            ps.setString(id, fname.toLowerCase());
            ps.setString(id, lname.toLowerCase());
            ps.setInt(5, id);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}

