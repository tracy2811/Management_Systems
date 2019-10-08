
package main;

import java.awt.Component;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Issue {
    private final Component parentComponent;
    
    public Issue(Component parentComponent) {
        this.parentComponent = parentComponent;
    }
    
    DefaultTableModel searchIssue(int id, int bookID, int readerID) {
        try {
            PreparedStatement ps;
            ResultSet rs;
            String query = "SELECT * FROM `issues` WHERE `id`=? OR `book_id`=? OR `reader_id`=?";
            
            ps = MyConnection.createConnection().prepareStatement(query);
            ps.setInt(1, id);
            ps.setInt(2, bookID);
            ps.setInt(3, readerID);
            
            rs = ps.executeQuery();
            
            DefaultTableModel table = new DefaultTableModel(new Object[]{"ID", "Book ID", "Reader ID", "Checkout Date", "Return Date", "Fine"}, 0);

            if (rs.next() == false) {
                JOptionPane.showMessageDialog(parentComponent, "No book found!", "No Result", JOptionPane.INFORMATION_MESSAGE);
            } else {
                Object[] row;
                do {
                    row = new Object[8];
                    row[0] = rs.getInt(1);
                    row[1] = rs.getString(2);
                    row[2] = rs.getString(3);
                    row[3] = rs.getInt(4);
                    row[4] = rs.getInt(5);
                    row[5] = rs.getString(6);
                    row[6] = rs.getString(7);
                    row[7] = rs.getInt(8);
                    table.addRow(row);
                } while (rs.next());
            }
            return table;
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    DefaultTableModel getAllIssues() {
        try {
            PreparedStatement ps;
            ResultSet rs;
            String query = "SELECT * FROM `issues`";
            
            ps = MyConnection.createConnection().prepareStatement(query);
            
            rs = ps.executeQuery();
            
            DefaultTableModel table = new DefaultTableModel(new Object[]{"ID", "Book ID", "Reader ID", "Checkout Date", "Return Date", "Fine"}, 0);
            
            Object[] row;
            while(rs.next()) {
                row = new Object[8];
                row[0] = rs.getInt(1);
                row[1] = rs.getString(2);
                row[2] = rs.getString(3);
                row[3] = rs.getInt(4);
                row[4] = rs.getInt(5);
                row[5] = rs.getString(6);
                row[6] = rs.getString(7);
                row[7] = rs.getInt(8);
                table.addRow(row);
            }
            
            return table;
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    boolean addIssue(int bookID, int readerID, String checkoutDate) {
        try {
            PreparedStatement ps;
            String query = "INSERT INTO `issues`(`book_id`, `reader_id`, `checkout_date`) VALUES (?,?,?)";
            
            ps = MyConnection.createConnection().prepareStatement(query);
            ps.setInt(1, bookID);
            ps.setInt(2, readerID);
            ps.setString(3, checkoutDate);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    boolean deleteIssue(int id) {
        try {
            PreparedStatement ps;
            String query = "DELETE FROM `issues` WHERE `id`=?";
            
            ps = MyConnection.createConnection().prepareStatement(query);
            ps.setInt(1, id);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    boolean updateIssue(int id, int bookID, int readerID, String checkoutDate, String returnDate) {
        try {
            PreparedStatement ps;
            
            String query = "UPDATE `issues` SET `book_id`=?,`user_id`=?,`checkout_date`=?,`return_date`=? WHERE `id`=?";
            ps = MyConnection.createConnection().prepareStatement(query);
            ps.setInt(1, bookID);
            ps.setInt(2, readerID);
            ps.setString(3, checkoutDate);
            ps.setString(4, returnDate);
            ps.setInt(5, id);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
