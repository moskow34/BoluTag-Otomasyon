import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginScreen extends JFrame {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }

    public LoginScreen() {
        setTitle("BoluTag - Giriş");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE); 
        setLayout(null);

        try {
            ImageIcon icon = new ImageIcon("logomain.png");
            Image scaled = icon.getImage().getScaledInstance(220, 220, Image.SCALE_SMOOTH);
            JLabel lblLogo = new JLabel(new ImageIcon(scaled));
            lblLogo.setBounds(150, 10, 150, 150);
            add(lblLogo);
        } catch (Exception e) {
            JLabel lblYedek = new JLabel("LOGO", SwingConstants.CENTER);
            lblYedek.setBounds(150, 50, 150, 50);
            add(lblYedek);
        }

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBounds(20, 170, 400, 280);
        tabbedPane.setBackground(Color.WHITE);

        tabbedPane.addTab("Yolcu Girişi", createLoginPanel("yolcu"));
        tabbedPane.addTab("Şoför Girişi", createLoginPanel("sofor"));

        add(tabbedPane);
    }

    private JPanel createLoginPanel(String type) {
        JPanel p = new JPanel(null);
        p.setBackground(Color.WHITE);

        JLabel l1 = new JLabel("T.C. Numaranız:");
        l1.setBounds(30, 30, 150, 20);
        p.add(l1);

        JTextField t1 = new JTextField();
        t1.setBounds(30, 55, 330, 30);
        p.add(t1);

        JLabel l2 = new JLabel("Şifreniz:");
        l2.setBounds(30, 100, 150, 20);
        p.add(l2);

        JPasswordField t2 = new JPasswordField();
        t2.setBounds(30, 125, 330, 30);
        p.add(t2);

        JButton btnGiris = new JButton("GİRİŞ YAP");
        btnGiris.setBounds(30, 180, 150, 40);
        btnGiris.setBackground(Color.BLACK);
        btnGiris.setForeground(Color.WHITE);
        p.add(btnGiris);

        JButton btnKayit = new JButton("KAYIT OL");
        btnKayit.setBounds(210, 180, 150, 40);
        btnKayit.setBackground(Color.BLACK);
        btnKayit.setForeground(Color.WHITE);
        p.add(btnKayit);

        btnGiris.addActionListener(e -> loginIslemi(t1.getText(), new String(t2.getPassword()), type));
        btnKayit.addActionListener(e -> new KayitEkrani().setVisible(true));

        return p;
    }

    private void loginIslemi(String tc, String pass, String loginType) {
        try {
            Connection con = new DbHelper().getConnection();
            String sql = "SELECT * FROM users WHERE tc=? AND password=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, tc);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String dbType = rs.getString("type");
                int id = rs.getInt("id");
                String name = rs.getString("name");
                
                String loc = rs.getString("location");
                if (loc == null) loc = ""; 
                String plate = rs.getString("plate");
                if (plate == null) plate = "";

                if (loginType.equals("sofor")) {
                    
                    if (dbType.equals("admin")) {
                        new AdminEkrani().setVisible(true);
                        dispose();
                    } 
                    else if (dbType.equals("sofor")) {
                        new SoforEkrani(new Sofor(id, tc, name, pass, dbType, loc)).setVisible(true);
                        dispose();
                    } 
                    else {
                        JOptionPane.showMessageDialog(this, "Yetkisiz Giriş! Sadece Şoförler ve Yöneticiler girebilir.");
                    }
                } 
                else if (loginType.equals("yolcu")) {
                    if (dbType.equals("yolcu")) {
                        new YolcuEkrani(new Yolcu(id, tc, name, pass, dbType)).setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Bu kısımdan sadece Yolcular girebilir!");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Hatalı TC veya Şifre!");
            }
            con.close();
        } catch (Exception ex) { ex.printStackTrace(); }
    }}