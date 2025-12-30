import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminKullaniciEkle extends JFrame {
    
    private JTextField tTc, tAd, tPlaka, tPass; 
    private JComboBox<String> cmbTip, cmbMahalle;
    
    String[] mahalleler = {"Aktaş", "Aşağısoku", "Bahçelievler", "Borazanlar", "Dağkent", 
                           "Karaçayır", "İzzet Baysal", "Kültür", "Seyit", "Sümer", "Tabaklar"};

    public AdminKullaniciEkle() {
        setTitle("Hızlı Kullanıcı Ekleme Paneli");
        setSize(400, 550); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        JLabel l1 = new JLabel("TC Kimlik No:"); l1.setBounds(30, 20, 150, 20); add(l1);
        tTc = new JTextField(); tTc.setBounds(30, 45, 320, 30); add(tTc);

        JLabel l2 = new JLabel("Ad Soyad:"); l2.setBounds(30, 85, 150, 20); add(l2);
        tAd = new JTextField(); tAd.setBounds(30, 110, 320, 30); add(tAd);

        JLabel l3 = new JLabel("Şifre:"); l3.setBounds(30, 150, 150, 20); add(l3);
        tPass = new JTextField(); tPass.setBounds(30, 175, 320, 30); add(tPass);

        JLabel l4 = new JLabel("Kullanıcı Tipi:"); l4.setBounds(30, 215, 150, 20); add(l4);
        cmbTip = new JComboBox<>(new String[]{"yolcu", "sofor", "admin"});
        cmbTip.setBounds(30, 240, 320, 30);
        add(cmbTip);

        JLabel l5 = new JLabel("Mahalle (Sadece Şoför):"); l5.setBounds(30, 280, 200, 20); add(l5);
        cmbMahalle = new JComboBox<>(mahalleler); cmbMahalle.setBounds(30, 305, 320, 30); add(cmbMahalle);
        cmbMahalle.setEnabled(false); 

        JLabel l6 = new JLabel("Plaka (Sadece Şoför):"); l6.setBounds(30, 345, 200, 20); add(l6);
        tPlaka = new JTextField(); tPlaka.setBounds(30, 370, 320, 30); add(tPlaka);
        tPlaka.setEnabled(false); 

        cmbTip.addActionListener(e -> {
            String secilen = (String) cmbTip.getSelectedItem();
            boolean soforMu = secilen.equals("sofor");
            cmbMahalle.setEnabled(soforMu);
            tPlaka.setEnabled(soforMu);
        });

        JButton btnKaydet = new JButton("KULLANICIYI SİSTEME EKLE");
        btnKaydet.setBounds(30, 430, 320, 50);
        btnKaydet.setBackground(new Color(0, 180, 80)); 
        btnKaydet.setForeground(Color.WHITE);
        btnKaydet.setFont(new Font("Arial", Font.BOLD, 14));
        btnKaydet.addActionListener(e -> kaydet());
        add(btnKaydet);
    }

    private void kaydet() {
        String tc = tTc.getText();
        String ad = tAd.getText();
        String pass = tPass.getText();
        String tip = (String) cmbTip.getSelectedItem();
        
        String mahalle = null;
        String plaka = null;
        
        if (tip.equals("sofor")) {
            mahalle = (String) cmbMahalle.getSelectedItem();
            plaka = tPlaka.getText();
        }

        if(tc.isEmpty() || ad.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen temel alanları doldurun!");
            return;
        }

        try (Connection con = new DbHelper().getConnection()) {
            String sql = "INSERT INTO users (tc, password, name, type, location, plate) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, tc); 
            ps.setString(2, pass); 
            ps.setString(3, ad);
            ps.setString(4, tip); 
            ps.setString(5, mahalle); 
            ps.setString(6, plaka);
            
            ps.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Kullanıcı Başarıyla Eklendi! ✅");
            dispose(); 
            
        } catch (SQLException ex) { 
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage()); 
        }
    }
}