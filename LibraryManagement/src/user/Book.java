
package user;

import form.MyConnection;
import java.awt.Component;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class Book {
    // Admin can search, add, update, delete books
    private final Component parentComponent;
    
    public Book(Component parentComponent) {
        this.parentComponent = parentComponent;
    }
    
    public DefaultTableModel searchBook(int id, String title, String fauthor) {
        try {
            PreparedStatement ps;
            ResultSet rs;
            String query = "SELECT * FROM `books` WHERE `id`=? OR `title`=? OR `first_author`=?";
            
            ps = MyConnection.createConnection().prepareStatement(query);
            ps.setInt(1, id);
            ps.setString(2, title.toLowerCase());
            ps.setString(3, fauthor.toLowerCase());
            
            rs = ps.executeQuery();
            
            DefaultTableModel table = new DefaultTableModel(new Object[]{"ID", "Title", "Author", "Quantity", "Available"}, 0);

            if (rs.next() == false) {
                JOptionPane.showMessageDialog(parentComponent, "No book found!", "No Result", JOptionPane.INFORMATION_MESSAGE);
            } else {
                Object[] row;
                do {
                    row = new Object[5];
                    row[0] = rs.getInt(1);
                    row[1] = rs.getString(2);
                    row[2] = rs.getString(3);
                    row[3] = rs.getInt(4);
                    row[4] = rs.getInt(5);
                    table.addRow(row);
                } while (rs.next());
            }
            return table;
        } catch (SQLException ex) {
            Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public DefaultTableModel getAllBooks() {
        try {
            PreparedStatement ps;
            ResultSet rs;
            String query = "SELECT * FROM `books`";
            
            ps = MyConnection.createConnection().prepareStatement(query);
            
            rs = ps.executeQuery();
            
            DefaultTableModel table = new DefaultTableModel(new Object[]{"ID", "Title", "Author", "Quantity", "Available"}, 0);
            
            Object[] row;
            while(rs.next()) {
                row = new Object[5];
                row[0] = rs.getInt(1);
                row[1] = rs.getString(2);
                row[2] = rs.getString(3);
                row[3] = rs.getInt(4);
                row[4] = rs.getInt(5);
                table.addRow(row);
            }
            
            return table;
        } catch (SQLException ex) {
            Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public boolean addBook(String title, String fauthor, int quantity, int available) {
        try {
            PreparedStatement ps;
            String query = "INSERT INTO `books`(`title`, `first_author`, `quantity`, `available`) VALUES (?,?,?,?)";
            
            ps = MyConnection.createConnection().prepareStatement(query);
            ps.setString(1, title.toLowerCase());
            ps.setString(2, fauthor.toLowerCase());
            ps.setInt(3, quantity);
            ps.setInt(4, available);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean deleteBook(int id) {
        try {
            PreparedStatement ps;
            String query = "DELETE FROM `books` WHERE `id`=?";
            
            ps = MyConnection.createConnection().prepareStatement(query);
            ps.setInt(1, id);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean updateBook(int id, String title, String fauthor, int quantity) {
        try {
            PreparedStatement ps;
            String query = "UPDATE `books` SET `title`=?,`first_author`=?,`quantity`=? WHERE `id`=?";
            
            ps = MyConnection.createConnection().prepareStatement(query);
            ps.setString(1, title.toLowerCase());
            ps.setString(2, fauthor.toLowerCase());
            ps.setInt(3, quantity);
            ps.setInt(4, id);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    private int getAvailable(int id) {
        try {
            PreparedStatement ps;
            ResultSet rs;
            String query = "SELECT `available` FROM `books` WHERE `id`=?";
            
            ps = MyConnection.createConnection().prepareStatement(query);
            ps.setInt(1, id);
            
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return 0;
    }
    
    boolean isAvailablel(int id) {
        return getAvailable(id) > 0;
    }
    
    boolean issueBook(int id) {
        int available = getAvailable(id);
        if (available > 0) {
            try {
                --available;
                PreparedStatement ps;
                String query = "UPDATE `books` SET `available`=? WHERE `id`=?";

                ps = MyConnection.createConnection().prepareStatement(query);
                ps.setInt(1, available);
                ps.setInt(2, id);

                return ps.executeUpdate() > 0;

            } catch (SQLException ex) {
                Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }
    
    boolean returnBook(int id) {
        int available = getAvailable(id);
        if (available >= 0) {
            try {
                available++;
                PreparedStatement ps;
                String query = "UPDATE `books` SET `available`=? WHERE `id`=?";

                ps = MyConnection.createConnection().prepareStatement(query);
                ps.setInt(1, available);
                ps.setInt(2, id);

                return ps.executeUpdate() > 0;

            } catch (SQLException ex) {
                Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }
}

