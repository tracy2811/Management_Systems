
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


public class Room {
    MyConnection mycon = new MyConnection();
    // function to display all rooms in jtable
    public void fillRoomJTable(JTable table) {
        PreparedStatement st;
        ResultSet rs;
        String selectQuery = "SELECT * from `rooms`";
        
        try {
            st = mycon.createConnection().prepareStatement(selectQuery);
            
            rs = st.executeQuery();
            
            DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
            
            Object[] row;
            
            while (rs.next())
            {
                row = new Object[4];
                row[0] = rs.getInt(1);
                row[1] = rs.getInt(2);
                row[2] = rs.getString(3);
                row[3] = rs.getString(4);
                
                tableModel.addRow(row);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    // function to complete combobox with room-type id
    public void fillComboBoxJTable(JComboBox combobox) {
        PreparedStatement st;
        ResultSet rs;
        String selectQuery = "SELECT * from `type`";
        
        try {
            st = mycon.createConnection().prepareStatement(selectQuery);
            
            rs = st.executeQuery();

            while (rs.next())
            {
                combobox.addItem(rs.getInt(1));
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // function to add new room
    public boolean addRoom(int number, int type, String phone) {
        PreparedStatement st;
        String addQuery = "INSERT INTO `rooms`(`r_number`, `type`, `phone`, `reserved`) VALUES (?,?,?,?)";
        
        try {
            st = mycon.createConnection().prepareStatement(addQuery);
            
            st.setInt(1, number);
            st.setInt(2, type);
            st.setString(3, phone);
            st.setString(4, "No");
            
            return st.executeUpdate() > 0;

        } catch (SQLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    // display all room types
    // function to display all rooms in jtable
    public void fillRoomTypeJTable(JTable table) {
        PreparedStatement st;
        ResultSet rs;
        String selectQuery = "SELECT * from `type`";
        
        try {
            st = mycon.createConnection().prepareStatement(selectQuery);
            
            rs = st.executeQuery();
            
            DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
            
            Object[] row;
            
            while (rs.next())
            {
                row = new Object[4];
                row[0] = rs.getInt(1);
                row[1] = rs.getString(2);
                row[2] = rs.getString(3);
                
                tableModel.addRow(row);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    // Function to edit client
    public boolean editRoom(int number, int type, String phone, String isReserved) {
        PreparedStatement st;
        String updateQuery = "UPDATE `rooms` SET `type`=?,`phone`=?,`reserved`=? WHERE `r_number`=?";
        
        try {
            st = mycon.createConnection().prepareStatement(updateQuery);
            
            st.setInt(1, type);
            st.setString(2, phone);
            st.setString(3, isReserved);
            st.setInt(4, number);
            
            return st.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    // Function to remove client
    public boolean removeRoom(int number) {
        PreparedStatement st;
        String deleteQuery = "DELETE FROM `rooms` WHERE `r_number`=?";
        
        try {
            st = mycon.createConnection().prepareStatement(deleteQuery);
            
            st.setInt(1, number);
            
            return st.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    // Function set reservation status
    public boolean setReserved(int number, String isReserved) {
        PreparedStatement st;
        String updateQuery = "UPDATE `rooms` SET `reserved`=? WHERE `r_number`=?";
        
        try {
            st = mycon.createConnection().prepareStatement(updateQuery);
            
            st.setString(1, isReserved);
            st.setInt(2, number);
            
            return st.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    // Function to get reservation status
    public boolean isAvailable(int number) {
        PreparedStatement st;
        ResultSet rs;
        String selectQuery = "SELECT `reserved` from `rooms` WHERE `r_number`=?";
        
        try {
            st = mycon.createConnection().prepareStatement(selectQuery);
            st.setInt(1, number);
            
            rs = st.executeQuery();
            
            if (rs.next()) {
                return rs.getString(1).equals("No");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
