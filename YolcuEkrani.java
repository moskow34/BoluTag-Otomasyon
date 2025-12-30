import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class YolcuEkrani extends JFrame {
    private Yolcu yolcu;
    private DefaultTableModel model;
    private JTable table;
    private JComboBox<String> cmbKalkis, cmbVaris;
    private JLabel lblTutar;
    private ArrayList<Integer> tripIds = new ArrayList<>();

    String[] mahalleler = {"Aktaş", "Aşağısoku", "Bahçelievler", "Borazanlar", "Dağkent", 
                           "Karaçayır", "İzzet Baysal", "Kültür", "Seyit", "Sümer", "Tabaklar"};

    public YolcuEkrani(Yolcu yolcu) {
        this.yolcu = yolcu;
        setTitle("BoluTag - Yolcu Paneli");
        setSize(1100, 750); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);
        
        try { setIconImage(new ImageIcon("logomain.png").getImage()); } catch (Exception ignored) {}

        int solX = 50; 
        int sagX = 450; 
        int baslangicY = 210;

        try {
            ImageIcon icon = new ImageIcon("logomain.png");
            Image scaled = icon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
            JLabel lblLogo = new JLabel(new ImageIcon(scaled));
            lblLogo.setBounds(460, 5, 180, 180);
            add(lblLogo);
        } catch (Exception e) {
            JLabel lblYedek = new JLabel("BOLUTAG", SwingConstants.CENTER);
            lblYedek.setBounds(475, 50, 150, 50);
            add(lblYedek);
        }

        JButton btnCikis = new JButton("ÇIKIŞ");
        btnCikis.setBounds(950, 20, 100, 30);
        btnCikis.setFocusPainted(false); 
        btnCikis.addActionListener(e -> { new LoginScreen().setVisible(true); dispose(); });
        add(btnCikis);

        JLabel lblAd = new JLabel("Sayın " + yolcu.getName());
        lblAd.setFont(new Font("Arial", Font.BOLD, 16));
        lblAd.setBounds(solX, baslangicY, 300, 30);
        add(lblAd);

        JPanel panelSol = new JPanel(null);
        panelSol.setBounds(solX, baslangicY + 40, 350, 400); 
        panelSol.setBackground(Color.WHITE);
        panelSol.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(panelSol);

        JLabel lKalkis = new JLabel("Kalkış Yeri:"); lKalkis.setBounds(20, 30, 200, 20); panelSol.add(lKalkis);
        cmbKalkis = new JComboBox<>(mahalleler); cmbKalkis.setBounds(20, 55, 300, 35); panelSol.add(cmbKalkis);

        JLabel lVaris = new JLabel("Varış Yeri:"); lVaris.setBounds(20, 110, 200, 20); panelSol.add(lVaris);
        cmbVaris = new JComboBox<>(mahalleler); cmbVaris.setBounds(20, 135, 300, 35); panelSol.add(cmbVaris);

        lblTutar = new JLabel("Tahmini Tutar: 60 TL");
        lblTutar.setFont(new Font("Arial", Font.BOLD, 18));
        lblTutar.setForeground(Color.RED);
        lblTutar.setHorizontalAlignment(SwingConstants.CENTER);
        lblTutar.setBounds(20, 190, 300, 30);
        panelSol.add(lblTutar);

        cmbKalkis.addActionListener(e -> ucretHesapla());
        cmbVaris.addActionListener(e -> ucretHesapla());

        JButton btnTeklif = new JButton("ŞOFÖR ÇAĞIR");
        btnTeklif.setBounds(20, 240, 300, 60);
        btnTeklif.setBackground(new Color(0, 180, 80));
        btnTeklif.setForeground(Color.WHITE);
        btnTeklif.setFont(new Font("Arial", Font.BOLD, 15));
        btnTeklif.setFocusPainted(false);
        btnTeklif.addActionListener(e -> teklifVer());
        panelSol.add(btnTeklif);

        JLabel lblDurum = new JLabel("Yolculuk Durumlarım");
        lblDurum.setFont(new Font("Arial", Font.BOLD, 16));
        lblDurum.setBounds(sagX, baslangicY + 5, 300, 30); 
        add(lblDurum);
        
        JButton btnYenile = new JButton("YENİLE");
        btnYenile.setBounds(sagX + 500, baslangicY + 5, 100, 30);
        btnYenile.setBackground(new Color(245, 245, 245)); 
        btnYenile.setFocusPainted(false);
        btnYenile.addActionListener(e -> listele());
        add(btnYenile);

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{"GÜZERGAH", "SAAT", "DURUM / PLAKA"});

        table = new JTable(model);
        table.setRowHeight(40);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        
        table.getColumnModel().getColumn(0).setPreferredWidth(100); 
        table.getColumnModel().getColumn(1).setPreferredWidth(50);  

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < 3; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(sagX, baslangicY + 40, 600, 350); 
        sp.getViewport().setBackground(Color.WHITE);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(sp);
        
        JButton btnIptal = new JButton("SEÇİLİ TALEBİ İPTAL ET / SİL");
        btnIptal.setBounds(sagX, baslangicY + 400, 600, 40);
        btnIptal.setBackground(Color.RED);
        btnIptal.setForeground(Color.WHITE);
        btnIptal.setFocusPainted(false);
        btnIptal.addActionListener(e -> talepIptal());
        add(btnIptal);
        
        listele();
        ucretHesapla();
    }

    private void ucretHesapla() {
        int index1 = cmbKalkis.getSelectedIndex();
        int index2 = cmbVaris.getSelectedIndex();

        if (index1 == index2) {
            lblTutar.setText("Lütfen farklı yer seçiniz.");
            lblTutar.setForeground(Color.GRAY);
            return;
        }

        int mesafeBirim = Math.abs(index1 - index2);
        int fiyat = 50 + (mesafeBirim * 15);

        lblTutar.setText("Tahmini Tutar: " + fiyat + " TL");
        lblTutar.setForeground(Color.RED);
    }

    private void teklifVer() {
        String k = (String) cmbKalkis.getSelectedItem();
        String v = (String) cmbVaris.getSelectedItem();
        
        if(k.equals(v)) { JOptionPane.showMessageDialog(this, "Aynı yeri seçmeyin!"); return; }
        
        try (Connection con = new DbHelper().getConnection()) {
            
            String kontrolSql = "SELECT COUNT(*) FROM trips WHERE passenger_id = ? AND status IN ('BEKLIYOR', 'ONAYLANDI')";
            PreparedStatement psKontrol = con.prepareStatement(kontrolSql);
            psKontrol.setInt(1, yolcu.getId());
            ResultSet rs = psKontrol.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "Zaten aktif bir yolculuk isteğiniz var! Önce onu tamamlayın veya iptal edin.");
                return;
            }

            String s = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            String sql = "INSERT INTO trips (passenger_id, source, destination, time_slot, status) VALUES (?, ?, ?, ?, 'BEKLIYOR')";
            
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, yolcu.getId()); 
            ps.setString(2, k); 
            ps.setString(3, v); 
            ps.setString(4, s);
            ps.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Çağrı Yapıldı: " + s);
            listele();
            
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void listele() {
        model.setRowCount(0); tripIds.clear();
        try (Connection con = new DbHelper().getConnection()) {
            String sql = "SELECT t.*, u.plate as driver_plate FROM trips t " +
                         "LEFT JOIN users u ON t.driver_id = u.id " +
                         "WHERE t.passenger_id = ? ORDER BY t.id DESC";
            
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, yolcu.getId());
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                tripIds.add(rs.getInt("id")); 
                String durum = rs.getString("status");
                String detay = durum;

                if("ONAYLANDI".equals(durum)) {
                    String p = rs.getString("driver_plate");
                    detay = "GELİYOR: " + (p == null ? "Plaka Yok" : p);
                }
                
                model.addRow(new Object[]{ 
                    rs.getString("source") + " > " + rs.getString("destination"), 
                    rs.getString("time_slot"), 
                    detay 
                });
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void talepIptal() {
        int row = table.getSelectedRow();
        if(row == -1) { JOptionPane.showMessageDialog(this, "Seçim yapınız!"); return; }
        
        try (Connection con = new DbHelper().getConnection()) {
            PreparedStatement ps = con.prepareStatement("DELETE FROM trips WHERE id = ?");
            ps.setInt(1, tripIds.get(row)); 
            ps.executeUpdate();
            
            listele();
            JOptionPane.showMessageDialog(this, "Silindi.");
        } catch (SQLException ex) { ex.printStackTrace(); }
    }
}