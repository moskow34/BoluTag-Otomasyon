import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class KayitEkrani extends JFrame {
    
    private JTextField tTc, tAd, tPlaka;
    private JPasswordField tPass;
    private JComboBox<String> cmbTip, cmbMahalle;
    
    String[] mahalleler = {"Aktaş", "Aşağısoku", "Bahçelievler", "Borazanlar", "Dağkent", 
                           "Karaçayır", "İzzet Baysal", "Kültür", "Seyit", "Sümer", "Tabaklar"};

    public KayitEkrani() {
        setTitle("BoluTag - Yeni Üye Kaydı");
        setSize(400, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        try { setIconImage(new ImageIcon("logomain.png").getImage()); } catch (Exception ignored) {}

        JLabel l1 = new JLabel("TC Kimlik No:"); l1.setBounds(30, 20, 150, 20); add(l1);
        tTc = new JTextField(); tTc.setBounds(30, 45, 320, 30); add(tTc);

        JLabel l2 = new JLabel("Ad Soyad:"); l2.setBounds(30, 85, 150, 20); add(l2);
        tAd = new JTextField(); tAd.setBounds(30, 110, 320, 30); add(tAd);

        JLabel l3 = new JLabel("Şifre:"); l3.setBounds(30, 150, 150, 20); add(l3);
        tPass = new JPasswordField(); tPass.setBounds(30, 175, 320, 30); add(tPass);

        JLabel l4 = new JLabel("Kullanıcı Tipi:"); l4.setBounds(30, 215, 150, 20); add(l4);
        cmbTip = new JComboBox<>(new String[]{"yolcu", "sofor"});
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

        JButton btnKayit = new JButton("KAYDI TAMAMLA");
        btnKayit.setBounds(30, 430, 320, 50);
        btnKayit.setBackground(Color.BLACK);
        btnKayit.setForeground(Color.WHITE);
        btnKayit.setFont(new Font("Arial", Font.BOLD, 14));
        btnKayit.addActionListener(e -> kayitOl());
        add(btnKayit);
    }

    private void kayitOl() {
        String tc = tTc.getText();
        String pass = new String(tPass.getPassword());
        
        if (tc.isEmpty()) { 
            JOptionPane.showMessageDialog(this, "TC boş olamaz!"); return; 
        }
        if (!tc.matches("[0-9]+")) { 
            JOptionPane.showMessageDialog(this, "TC sadece rakamlardan oluşmalıdır!"); return; 
        }
        if (tc.length() > 3) { 
            JOptionPane.showMessageDialog(this, "TC en fazla 3 haneli olabilir! (Örn: 123)"); return; 
        }

        if (pass.isEmpty()) { 
            JOptionPane.showMessageDialog(this, "Şifre boş olamaz!"); return; 
        }
        
        String ad = tAd.getText();
        String tip = (String) cmbTip.getSelectedItem();
        String mahalle = (String) cmbMahalle.getSelectedItem();
        String plaka = tPlaka.getText();
        
        if(tip.equals("yolcu")) { 
            mahalle = null; 
            plaka = null; 
        } else {
            if(plaka == null || plaka.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Şoförler için plaka zorunludur!");
                return;
            }
        }

        try (Connection con = new DbHelper().getConnection()) {
            PreparedStatement psKontrol = con.prepareStatement("SELECT COUNT(*) FROM users WHERE tc = ?");
            psKontrol.setString(1, tc);
            ResultSet rs = psKontrol.executeQuery();
            if(rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "Bu TC numarası zaten kayıtlı!");
                return;
            }

            String sql = "INSERT INTO users (tc, password, name, type, location, plate) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, tc); 
            ps.setString(2, pass); 
            ps.setString(3, ad);
            ps.setString(4, tip); 
            ps.setString(5, mahalle); 
            ps.setString(6, plaka);
            ps.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Kayıt Başarılı! Şimdi giriş yapabilirsiniz.");
            
            dispose();
            
        } catch (SQLException ex) { 
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanı Hatası: " + ex.getMessage()); 
        }
    }
}