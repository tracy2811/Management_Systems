
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Client {
    MyConnection mycon = new MyConnection();
    // Function to add client
    public boolean addClient(String fname, String lname, String phone, String email) {
        PreparedStatement st;
        String addQuery = "INSERT INTO `clients`(`first_name`, `last_name`, `phone`, `email`) VALUES (?,?,?,?)";
        
        try {
            st = mycon.createConnection().prepareStatement(addQuery);
            
            st.setString(1, fname);
            st.setString(2, lname);
            st.setString(3, phone);
            st.setString(4, email);
            
            return st.executeUpdate() > 0;

        } catch (SQLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    // Function to edit client
    public boolean editClient(int id, String fname, String lname, String phone, String email) {
        PreparedStatement st;
        String updateQuery = "UPDATE `clients` SET `first_name`=?,`last_name`=?,`phone`=?,`email`=? WHERE `id`=?";
        
        try {
            st = mycon.createConnection().prepareStatement(updateQuery);
            
            st.setString(1, fname);
            st.setString(2, lname);
            st.setString(3, phone);
            st.setString(4, email);
            st.setInt(5, id);
            
            return st.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    // Function to remove client
    public boolean removeClient(int id) {
        PreparedStatement st;
        String deleteQuery = "DELETE FROM `clients` WHERE `id`=?";
        
        try {
            st = mycon.createConnection().prepareStatement(deleteQuery);
            
            st.setInt(1, id);
            
            return st.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    // Function to populate jtable with all clients in the database
    public void fillClientJTable(JTable table) {
        PreparedStatement st;
        ResultSet rs;
        String selectQuery = "SELECT * from `clients`";
        
        try {
            st = mycon.createConnection().prepareStatement(selectQuery);
            
            rs = st.executeQuery();
            
            DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
            
            Object[] row;
            
            while (rs.next())
            {
                row = new Object[5];
                row[0] = rs.getInt(1);
                row[1] = rs.getString(2);
                row[2] = rs.getString(3);
                row[3] = rs.getString(4);
                row[4] = rs.getString(5);
                
                tableModel.addRow(row);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
