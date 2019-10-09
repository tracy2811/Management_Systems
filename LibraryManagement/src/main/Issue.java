
package main;

import java.awt.Component;
import java.nio.channels.NetworkChannel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Issue {
    private final Component parentComponent;
    private final Reader reader;
    private final Book book;
    
    public Issue(Component parentComponent) {
        this.parentComponent = parentComponent;
        reader = new Reader(parentComponent);
        book = new Book(parentComponent);
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
                    row = new Object[6];
                    row[0] = rs.getInt(1);
                    row[1] = rs.getInt(2);
                    row[2] = rs.getInt(3);
                    row[3] = rs.getString(4);
                    row[4] = rs.getString(5);
                    row[5] = rs.getInt(6);
                    table.addRow(row);
                } while (rs.next());
            }
            return table;
        } catch (SQLException ex) {
            Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
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
                row[1] = rs.getInt(2);
                row[2] = rs.getInt(3);
                row[3] = rs.getString(4);
                row[4] = rs.getString(5);
                row[5] = rs.getInt(6);
                table.addRow(row);
            }
            
            return table;
        } catch (SQLException ex) {
            Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    boolean addIssue(int bookID, int readerID, String checkoutDate) {
        if (!book.isAvailablel(bookID)) {
            JOptionPane.showMessageDialog(parentComponent, "Book is not available", "Add Error", JOptionPane.ERROR_MESSAGE);
        } else {
            int nCheckout = reader.getCheckout(readerID);
            if (nCheckout >= 0 && nCheckout < 5) {
                try {
                    PreparedStatement ps;
                    String query = "INSERT INTO `issues`(`book_id`, `user_id`, `checkout_date`) VALUES (?,?,?)";

                    ps = MyConnection.createConnection().prepareStatement(query);
                    ps.setInt(1, bookID);
                    ps.setInt(2, readerID);
                    ps.setString(3, checkoutDate);

                    return book.issueBook(bookID) && reader.issueBook(readerID) && ps.executeUpdate()>0;

                } catch (SQLException ex) {
                    Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JOptionPane.showMessageDialog(parentComponent, "Reader ID is incorrect or Reader has aldready borrowed 5 books!", "Add Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        return false;

    }
    
    boolean deleteIssue(int id) {
        try {
            PreparedStatement ps;
            String query = "DELETE FROM `issues` WHERE `id`=?";
            
            ps = MyConnection.createConnection().prepareStatement(query);
            ps.setInt(1, id);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    int getBookID(int id) {
        try {
            PreparedStatement ps;
            ResultSet rs;
            String query = "SELECT `book_id` FROM `issues` WHERE `id`=?";
            
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
    
    int getReaderID(int id) {
        try {
            PreparedStatement ps;
            ResultSet rs;
            String query = "SELECT `user_id` FROM `issues` WHERE `id`=?";
            
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
    
    boolean updateIssue(int id, int bookID, int readerID, String checkoutDate, String returnDate) {
        if (!book.isAvailablel(bookID)) {
            JOptionPane.showMessageDialog(parentComponent, "Book is not available", "Add Error", JOptionPane.ERROR_MESSAGE);
        } else {
            int nCheckout = reader.getCheckout(readerID);
            if (nCheckout >= 0 && nCheckout < 5) {
                int prevBook = getBookID(id);
                int prevReader = getReaderID(id);
                
                if (prevBook > 0 && prevReader > 0) {
                    try {
                        PreparedStatement ps;

                        String query = "UPDATE `issues` SET `book_id`=?,`user_id`=?,`checkout_date`=?,`return_date`=? WHERE `id`=?";
                        ps = MyConnection.createConnection().prepareStatement(query);
                        ps.setInt(1, bookID);
                        ps.setInt(2, readerID);
                        ps.setString(3, checkoutDate);
                        ps.setString(4, returnDate);
                        ps.setInt(5, id);

                        return book.returnBook(prevBook) && reader.returnBook(prevReader) && ps.executeUpdate() > 0;

                    } catch (SQLException ex) {
                        Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return false;
    }
}
