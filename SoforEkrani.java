import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class SoforEkrani extends JFrame {
    private Sofor sofor;
    private DefaultTableModel model;
    private JTable table;
    private ArrayList<Integer> tripIds = new ArrayList<>();

    public SoforEkrani(Sofor sofor) {
        this.sofor = sofor;
        setTitle("BoluTag - Şoför Paneli (" + sofor.getName() + ")");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);
        
        try { setIconImage(new ImageIcon("logomain.png").getImage()); } catch (Exception ignored) {}

        try {
            ImageIcon icon = new ImageIcon("logomain.png");
            Image scaled = icon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
            JLabel lblLogo = new JLabel(new ImageIcon(scaled));
            lblLogo.setBounds(410, 10, 180, 180);
            add(lblLogo);
        } catch (Exception e) {
            JLabel lblYedek = new JLabel("BOLUTAG", SwingConstants.CENTER);
            lblYedek.setBounds(410, 50, 180, 50);
            add(lblYedek);
        }

        JButton btnCikis = new JButton("ÇIKIŞ");
        btnCikis.setBounds(850, 20, 100, 30);
        btnCikis.setFocusPainted(false);
        btnCikis.addActionListener(e -> { new LoginScreen().setVisible(true); dispose(); });
        add(btnCikis);

        JLabel lblBaslik = new JLabel("Gelen Yolcu Talepleri / Aktif Görevler", SwingConstants.CENTER);
        lblBaslik.setFont(new Font("Arial", Font.BOLD, 22));
        lblBaslik.setBounds(250, 200, 500, 30);
        add(lblBaslik);

        JButton btnYenile = new JButton("YENİLE");
        btnYenile.setBounds(850, 200, 100, 30);
        btnYenile.setBackground(new Color(245, 245, 245));
        btnYenile.setFocusPainted(false);
        btnYenile.addActionListener(e -> listele());
        add(btnYenile);

        model = new DefaultTableModel();
        model.addColumn("YOLCU ADI");
        model.addColumn("GÜZERGAH");
        model.addColumn("SAAT");
        model.addColumn("DURUM");

        table = new JTable(model);
        table.setRowHeight(40);
        table.setFillsViewportHeight(true);
        
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);

                String durum = (String) model.getValueAt(row, 3);
                
                if (column == 3) {
                    if (durum.contains("GİDİLİYOR")) {
                        c.setForeground(new Color(0, 150, 0)); 
                        c.setFont(new Font("Arial", Font.BOLD, 12));
                    } else if ("BEKLIYOR".equals(durum)) {
                        c.setForeground(Color.RED); 
                        c.setFont(new Font("Arial", Font.BOLD, 12));
                    } else {
                        c.setForeground(Color.BLACK);
                    }
                } else {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(40, 240, 910, 300);
        add(sp);

        JButton btnIslem = new JButton("İŞLEM YAP (KABUL ET / TAMAMLA)");
        btnIslem.setBounds(40, 560, 910, 50);
        btnIslem.setBackground(Color.BLACK); 
        btnIslem.setForeground(Color.WHITE);
        btnIslem.setFont(new Font("Arial", Font.BOLD, 16));
        btnIslem.setFocusPainted(false);
        btnIslem.addActionListener(e -> islemYap());
        add(btnIslem);

        listele();
    }

    private void listele() {
        model.setRowCount(0);
        tripIds.clear();
        try (Connection con = new DbHelper().getConnection()) {
            String sql = "SELECT t.*, u.name as passenger_name FROM trips t " +
                         "JOIN users u ON t.passenger_id = u.id " +
                         "WHERE t.status = 'BEKLIYOR' " +
                         "OR (t.status = 'ONAYLANDI' AND t.driver_id = ?) " +
                         "ORDER BY t.id DESC";
            
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, sofor.getId()); 
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                tripIds.add(rs.getInt("id"));
                
                String dbDurum = rs.getString("status");
                String gorunenDurum = dbDurum;

                if ("ONAYLANDI".equals(dbDurum)) {
                    gorunenDurum = "YOLCUYU ALMAYA GİDİLİYOR";
                }

                String rota = rs.getString("source") + " > " + rs.getString("destination");

                model.addRow(new Object[]{
                    rs.getString("passenger_name"),
                    rota,
                    rs.getString("time_slot"),
                    gorunenDurum
                });
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void islemYap() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen listeden bir iş seçiniz!");
            return;
        }

        int tripId = tripIds.get(row);
        String durum = (String) model.getValueAt(row, 3); 

        try (Connection con = new DbHelper().getConnection()) {
            
            if ("BEKLIYOR".equals(durum)) {
                
                String kontrolSql = "SELECT COUNT(*) FROM trips WHERE driver_id = ? AND status = 'ONAYLANDI'";
                PreparedStatement psKontrol = con.prepareStatement(kontrolSql);
                psKontrol.setInt(1, sofor.getId());
                ResultSet rs = psKontrol.executeQuery();
                
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "Zaten aktif bir göreviniz var! Önce onu bitirin.");
                    return;
                }

                String sql = "UPDATE trips SET status = 'ONAYLANDI', driver_id = ? WHERE id = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, sofor.getId());
                ps.setInt(2, tripId);
                ps.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Çağrı Kabul Edildi! İyi yolculuklar.");
            } 
            
            else if (durum.contains("GİDİLİYOR")) {
                
                int cevap = JOptionPane.showConfirmDialog(this, "Yolculuk tamamlandı mı?", "Onay", JOptionPane.YES_NO_OPTION);
                
                if (cevap == JOptionPane.YES_OPTION) {
                    String sql = "UPDATE trips SET status = 'TAMAMLANDI' WHERE id = ?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setInt(1, tripId);
                    ps.executeUpdate();
                    
                    JOptionPane.showMessageDialog(this, "Görevi tamamladınız. Eline sağlık!");
                }
            }
            
            listele(); 
            
        } catch (SQLException ex) { ex.printStackTrace(); }
    }
}