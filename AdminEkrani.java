import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AdminEkrani extends JFrame {
    
    private DefaultTableModel modelUser, modelTrip;
    private JTable tableUser, tableTrip;

    public AdminEkrani() {
        setTitle("BoluTag - Yönetim Paneli (CEO)");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);
        
        try { setIconImage(new ImageIcon("logomain.png").getImage()); } catch (Exception ignored) {}

        try {
            ImageIcon icon = new ImageIcon("logomain.png");
            Image scaled = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            JLabel lblLogo = new JLabel(new ImageIcon(scaled));
            lblLogo.setBounds(550, 10, 100, 100);
            add(lblLogo);
        } catch (Exception e) {
            JLabel lblYedek = new JLabel("CEO PANEL", SwingConstants.CENTER);
            lblYedek.setBounds(550, 30, 100, 30);
            add(lblYedek);
        }

        JLabel lblBaslik = new JLabel("ŞİRKET YÖNETİM EKRANI", SwingConstants.CENTER);
        lblBaslik.setFont(new Font("Arial", Font.BOLD, 24));
        lblBaslik.setBounds(300, 120, 600, 30);
        add(lblBaslik);

        JButton btnCikis = new JButton("ÇIKIŞ");
        btnCikis.setBounds(1050, 30, 100, 30);
        btnCikis.setFocusPainted(false);
        btnCikis.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginScreen().setVisible(true);
                dispose();
            }
        });
        add(btnCikis);

        JLabel lblUser = new JLabel("Kullanıcı Listesi");
        lblUser.setFont(new Font("Arial", Font.BOLD, 16));
        lblUser.setBounds(50, 170, 300, 20);
        add(lblUser);

        modelUser = new DefaultTableModel();
        modelUser.setColumnIdentifiers(new Object[]{"ID", "TC", "İSİM", "TÜR", "PLAKA"});
        tableUser = new JTable(modelUser);
        tableUser.setRowHeight(30);
        JScrollPane spUser = new JScrollPane(tableUser);
        spUser.setBounds(50, 200, 500, 400);
        add(spUser);

        JButton btnEkle = new JButton("YENİ KULLANICI EKLE");
        btnEkle.setBounds(50, 610, 240, 40);
        btnEkle.setBackground(new Color(0, 100, 200)); 
        btnEkle.setForeground(Color.WHITE);
        btnEkle.setFont(new Font("Arial", Font.BOLD, 14));
        
        btnEkle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AdminKullaniciEkle().setVisible(true);
            }
        });
        add(btnEkle);


        JButton btnSil = new JButton("SEÇİLİ KULLANICIYI SİL");
        btnSil.setBounds(310, 610, 240, 40);
        btnSil.setBackground(Color.RED);
        btnSil.setForeground(Color.WHITE);
        btnSil.setFont(new Font("Arial", Font.BOLD, 14));
        btnSil.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                kullaniciSil();
            }
        });
        add(btnSil);


        JLabel lblTrip = new JLabel("Tüm Yolculuk Hareketleri (DATA)");
        lblTrip.setFont(new Font("Arial", Font.BOLD, 16));
        lblTrip.setBounds(600, 170, 300, 20);
        add(lblTrip);

        modelTrip = new DefaultTableModel();
        modelTrip.setColumnIdentifiers(new Object[]{"YOLCU", "ŞOFÖR", "GÜZERGAH", "DURUM"});
        tableTrip = new JTable(modelTrip);
        tableTrip.setRowHeight(30);

        tableTrip.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                String durum = (String) modelTrip.getValueAt(row, 3);
                
                if (col == 3) { 
                    c.setFont(new Font("Arial", Font.BOLD, 12));
                    if ("BEKLIYOR".equals(durum)) c.setForeground(Color.RED);
                    else if ("ONAYLANDI".equals(durum)) c.setForeground(new Color(0, 150, 0));
                    else if ("TAMAMLANDI".equals(durum)) c.setForeground(Color.BLUE);
                    else c.setForeground(Color.BLACK);
                } else {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        JScrollPane spTrip = new JScrollPane(tableTrip);
        spTrip.setBounds(600, 200, 550, 400);
        add(spTrip);

        
        JButton btnYenile = new JButton("TABLOYU YENİLE / GÜNCELLE");
        btnYenile.setBounds(600, 610, 550, 40); 
        btnYenile.setBackground(Color.BLACK);
        btnYenile.setForeground(Color.WHITE);
        btnYenile.setFont(new Font("Arial", Font.BOLD, 14));
        btnYenile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verileriGetir(); 
            }
        });
        add(btnYenile);

        verileriGetir();
    }

    private void verileriGetir() {
        modelUser.setRowCount(0);
        try (Connection con = new DbHelper().getConnection()) {
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM users");
            while(rs.next()) {
                modelUser.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("tc"), rs.getString("name"),
                    rs.getString("type"), rs.getString("plate")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }

        modelTrip.setRowCount(0);
        try (Connection con = new DbHelper().getConnection()) {
            String sql = "SELECT t.*, u1.name as p_name, u2.name as d_name FROM trips t " +
                         "LEFT JOIN users u1 ON t.passenger_id = u1.id " +
                         "LEFT JOIN users u2 ON t.driver_id = u2.id ORDER BY t.id DESC";
            ResultSet rs = con.createStatement().executeQuery(sql);
            while(rs.next()) {
                String yolcu = rs.getString("p_name");
                String sofor = rs.getString("d_name");
                String rota = rs.getString("source") + " > " + rs.getString("destination");
                String durum = rs.getString("status");
                modelTrip.addRow(new Object[]{ yolcu, (sofor==null ? "-" : sofor), rota, durum });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void kullaniciSil() {
        int row = tableUser.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Seçim yapınız!"); return; }
        
        int id = (int) modelUser.getValueAt(row, 0);
        int onay = JOptionPane.showConfirmDialog(this, "Bu kullanıcıyı silmek istediğinize emin misiniz?", "Onay", JOptionPane.YES_NO_OPTION);
        
        if (onay == JOptionPane.YES_OPTION) {
            try (Connection con = new DbHelper().getConnection()) {
                con.createStatement().executeUpdate("DELETE FROM trips WHERE passenger_id=" + id + " OR driver_id=" + id);
                con.createStatement().executeUpdate("DELETE FROM users WHERE id=" + id);
                verileriGetir();
                JOptionPane.showMessageDialog(this, "Silindi.");
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}