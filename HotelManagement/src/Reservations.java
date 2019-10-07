
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


public class Reservations {
    MyConnection mycon = new MyConnection();
    Room room = new Room();
    // Function to add client
    public boolean addReservation(int clientID, int roomNumber, Date dateIn, Date dateOut) {
        PreparedStatement st;
        String addQuery = "INSERT INTO `reservations`(`client_id`, `room_number`, `date_in`, `date_out`) VALUES (?,?,?,?,?)";
        
        try {
            st = mycon.createConnection().prepareStatement(addQuery);
            
            st.setInt(1, clientID);
            st.setInt(2, roomNumber);
            st.setDate(3, dateIn);
            st.setDate(4, dateOut);
            
            return room.isAvailable(roomNumber) && st.executeUpdate() > 0 && room.setReserved(roomNumber, "Yes");

        } catch (SQLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    // Function to edit client
    public boolean editReservation(int id, int clientID, int roomNumber, Date dateIn, Date dateOut) {
        PreparedStatement st;
        String updateQuery = "UPDATE `reservations` SET `client_id`=?,`room_number`=?,`date_in`=?,`date_out`=? WHERE `id`=?";
        
        int oldRoomNumber = getRoomNumberFromReservation(id);
        if (oldRoomNumber > 0) {
            try {
                st = mycon.createConnection().prepareStatement(updateQuery);

                st.setInt(1, clientID);
                st.setInt(2, roomNumber);
                st.setDate(3, dateIn);
                st.setDate(4, dateOut);
                st.setInt(5, id);

                return room.setReserved(oldRoomNumber, "No") && room.isAvailable(roomNumber) && st.executeUpdate() > 0 && room.setReserved(roomNumber, "Yes");
            } catch (SQLException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }
    // Function to remove client
    public boolean removeReservation(int id) {
        PreparedStatement st;
        String deleteQuery = "DELETE FROM `reservations` WHERE `id`=?";
        int roomNumber = getRoomNumberFromReservation(id);
        
        if (roomNumber > 0) { 
            try {
                st = mycon.createConnection().prepareStatement(deleteQuery);

                st.setInt(1, id);

                return st.executeUpdate() > 0 && room.setReserved(roomNumber, "No");
            } catch (SQLException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }
    // Function to populate jtable with all clients in the database
    public void fillReservationJTable(JTable table) {
        PreparedStatement st;
        ResultSet rs;
        String selectQuery = "SELECT * from `reservations`";
        
        try {
            st = mycon.createConnection().prepareStatement(selectQuery);
            
            rs = st.executeQuery();
            
            DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
            
            Object[] row;
            
            while (rs.next())
            {
                row = new Object[5];
                row[0] = rs.getInt(1);
                row[1] = rs.getInt(2);
                row[2] = rs.getInt(3);
                row[3] = rs.getDate(4);
                row[4] = rs.getDate(5);
                
                tableModel.addRow(row);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // function to get room number from reservation
    public int getRoomNumberFromReservation(int reservationID) {
        PreparedStatement st;
        ResultSet rs;
        String selectQuery = "SELECT `room_number` from `reservations` WHERE `id`=?";
        
        try {
            st = mycon.createConnection().prepareStatement(selectQuery);
            st.setInt(1, reservationID);
            
            rs = st.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return 0;
    }
}
