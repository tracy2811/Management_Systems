
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
            String query = "SELECT `id`, `first_name`, `last_name`, `checkout` FROM `users` WHERE `type`=3 AND (`id`=? OR `first_name`=? OR `last_name`=?)";
            
            ps = MyConnection.createConnection().prepareStatement(query);
            ps.setInt(1, id);
            ps.setString(2, fname.toLowerCase());
            ps.setString(3, lname.toLowerCase());
            
            rs = ps.executeQuery();
            
            DefaultTableModel table = new DefaultTableModel(new Object[]{"ID", "First name", "Last name", "Checkout"}, 0);

            if (rs.next() == false) {
                JOptionPane.showMessageDialog(parentComponent, "No reader found!", "No Result", JOptionPane.INFORMATION_MESSAGE);
            } else {
                Object[] row;
                do {
                    row = new Object[4];
                    row[0] = rs.getInt(1);
                    row[1] = rs.getString(2);
                    row[2] = rs.getString(3);
                    row[3] = rs.getInt(4);
                    table.addRow(row);
                } while (rs.next());
            }
            return table;
        } catch (SQLException ex) {
            Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    DefaultTableModel getAllReaders() {
        try {
            PreparedStatement ps;
            ResultSet rs;
            String query = "SELECT `id`, `first_name`, `last_name`, `checkout` FROM `users` WHERE `type`=3";
            
            ps = MyConnection.createConnection().prepareStatement(query);
            
            rs = ps.executeQuery();
            
            DefaultTableModel table = new DefaultTableModel(new Object[]{"ID", "First name", "Last name", "Checkout"}, 0);
            
            Object[] row;
            while(rs.next()) {
                row = new Object[4];
                row[0] = rs.getInt(1);
                row[1] = rs.getString(2);
                row[2] = rs.getString(3);
                row[3] = rs.getInt(4);
                table.addRow(row);
            }
            
            return table;
        } catch (SQLException ex) {
            Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    boolean updateReader(int id, String fname, String lname) {
        try {
            PreparedStatement ps;
            
            String query = "UPDATE `users` SET `first_name`=?,`last_name`=? WHERE `type`=3 AND`id`=?";
            ps = MyConnection.createConnection().prepareStatement(query);
            ps.setString(1, fname.toLowerCase());
            ps.setString(2, lname.toLowerCase());
            ps.setInt(3, id);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    boolean isReader(int id) {
        try {
            PreparedStatement ps;
            ResultSet rs;
            
            String query = "SELECT `type` FROM `users` WHERE `id`=?";
            ps = MyConnection.createConnection().prepareStatement(query);
            ps.setInt(1, id);
            
            rs = ps.executeQuery();
            
            return (rs.next() && rs.getInt(1) == 3);
            
        } catch (SQLException ex) {
            Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    int getCheckout(int id) {
        try {
            PreparedStatement ps;
            ResultSet rs;
            
            String query = "SELECT `checkout` FROM `users` WHERE `type`=3 AND`id`=?";
            ps = MyConnection.createConnection().prepareStatement(query);
            ps.setInt(1, id);
            
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return -1;
    }
    
    boolean issueBook(int id) {
        int checkout = getCheckout(id);
        
        if (checkout >= 0) {
            checkout++;
            
            try {
                PreparedStatement ps;

                String query = "UPDATE `users` SET `checkout`=? WHERE `type`=3 AND`id`=?";
                ps = MyConnection.createConnection().prepareStatement(query);
                ps.setInt(1, checkout);
                ps.setInt(2, id);

                return ps.executeUpdate() > 0;

            } catch (SQLException ex) {
                Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return false;
    }
    
    boolean returnBook(int id) {
        int checkout = getCheckout(id);
        
        if (checkout > 0) {
            checkout--;
            
            try {
                PreparedStatement ps;

                String query = "UPDATE `users` SET `checkout`=? WHERE `type`=3 AND`id`=?";
                ps = MyConnection.createConnection().prepareStatement(query);
                ps.setInt(1, checkout);
                ps.setInt(2, id);

                return ps.executeUpdate() > 0;

            } catch (SQLException ex) {
                Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return false;
    }
    
    boolean canCheckout(int id) {
        int nCheckout = getCheckout(id);
        return nCheckout >= 0 && nCheckout <5;
    }
}

