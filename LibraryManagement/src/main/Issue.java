
package main;

import java.awt.Component;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
            String query = "SELECT * FROM `issues` WHERE `id`=? OR `book_id`=? OR `user_id`=?";
            
            ps = MyConnection.createConnection().prepareStatement(query);
            ps.setInt(1, id);
            ps.setInt(2, bookID);
            ps.setInt(3, readerID);
            
            rs = ps.executeQuery();
            
            DefaultTableModel table = new DefaultTableModel(new Object[]{"ID", "Book ID", "Reader ID", "Checkout Date", "Return Date", "Fine"}, 0);

            if (rs.next() == false) {
                JOptionPane.showMessageDialog(parentComponent, "No issue found!", "No Result", JOptionPane.INFORMATION_MESSAGE);
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
        // Book not return yet
        if (isCheckout(id) && !(reader.returnBook(getReaderID(id)) && book.returnBook(getBookID(id)))) {
            return false;
        }
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
    
    boolean isCheckout(int id) {
        try {
            PreparedStatement ps;
            ResultSet rs;
            String query = "SELECT `return_date` FROM `issues` WHERE `id`=?";
            
            ps = MyConnection.createConnection().prepareStatement(query);
            ps.setInt(1, id);
            
            rs = ps.executeQuery();
           
            if (rs.next()) {
                return rs.getString(1) == null || rs.getString(1).isBlank();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    boolean updateIssue(int id, int bookID, int readerID, String checkoutDate, String returnDate) throws ParseException {
        if (!book.isAvailablel(bookID)) {
            JOptionPane.showMessageDialog(parentComponent, "Book not available", "Add Error", JOptionPane.ERROR_MESSAGE);
        } else if (!reader.canCheckout(readerID)){
            JOptionPane.showMessageDialog(parentComponent, "Reader not exist or already booked 5 books", "Add Error", JOptionPane.ERROR_MESSAGE);
        } else {
            // If previous one not return, update previous book and reader
            if (isCheckout(id) && !(book.returnBook(getBookID(id)) && reader.returnBook(getReaderID(id)))) {
                return false;
            }
            
            if (returnDate.isBlank()) { // If current one not return, update current book and reader
                try {
                    PreparedStatement ps;

                    String query = "UPDATE `issues` SET `book_id`=?,`user_id`=?,`checkout_date`=?,`return_date`=NULL WHERE `id`=?";
                    ps = MyConnection.createConnection().prepareStatement(query);
                    ps.setInt(1, bookID);
                    ps.setInt(2, readerID);
                    ps.setString(3, checkoutDate);
                    ps.setInt(4, id);

                    return book.issueBook(bookID) && reader.issueBook(readerID) && ps.executeUpdate() > 0;

                } catch (SQLException ex) {
                    Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                
                SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
                
                int diff = (int) ((format.parse(returnDate).getTime() - format.parse(checkoutDate).getTime()) / (1000 * 60 * 60 * 24));

                if (diff < 0) {
                    JOptionPane.showMessageDialog(parentComponent, "Return Date before Checkout Date", "Invalid Date", JOptionPane.WARNING_MESSAGE);
                } else {
                    try {
                        int fine = (diff > 14) ? (diff - 14) : 0;
                        PreparedStatement ps;

                        String query = "UPDATE `issues` SET `book_id`=?,`user_id`=?,`checkout_date`=?,`return_date`=?,`fine`=? WHERE `id`=?";
                        ps = MyConnection.createConnection().prepareStatement(query);
                        ps.setInt(1, bookID);
                        ps.setInt(2, readerID);
                        ps.setString(3, checkoutDate);
                        ps.setString(4, returnDate);
                        ps.setInt(5, fine);
                        ps.setInt(6, id);

                        return ps.executeUpdate() > 0;

                    } catch (SQLException ex) {
                        Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return false;
    }
}
