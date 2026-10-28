import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class Main extends javax.swing.JFrame {

    private DefaultTableModel modelTabeli;

    private static final String URL = "jdbc:mysql://localhost:3306/baza_egzaminacyjna?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public Main() {
        initComponents();
        konfigurujTabele();
        wczytajDaneZTabeli();
        btnDodaj.addActionListener(e -> dodajPracownika());
    }

    private void konfigurujTabele() {
        String[] kolumny = {"ID", "Imię", "Nazwisko", "Stanowisko"};
        modelTabeli = new DefaultTableModel(kolumny, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaPracownikow.setModel(modelTabeli);
    }

    private void wczytajDaneZTabeli() {
        modelTabeli.setRowCount(0);
        String query = "SELECT id, imie, nazwisko, stanowisko FROM pracownicy";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String imie = rs.getString("imie");
                String nazwisko = rs.getString("nazwisko");
                String stanowisko = rs.getString("stanowisko");

                modelTabeli.addRow(new Object[]{id, imie, nazwisko, stanowisko});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Błąd bazy danych: " + ex.getMessage(),
                    "Błąd Połączenia",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void dodajPracownika() {
        String imie = txtImie.getText().trim();
        String nazwisko = txtNazwisko.getText().trim();
        String stanowisko = txtStanowisko.getText().trim();
        if (imie.isEmpty() || nazwisko.isEmpty() || stanowisko.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Wszystkie pola formularza muszą zostać uzupełnione!",
                    "Błąd walidacji",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query = "INSERT INTO pracownicy (imie, nazwisko, stanowisko) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, imie);
            stmt.setString(2, nazwisko);
            stmt.setString(3, stanowisko);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this,
                    "Pracownik został pomyślnie dodany.",
                    "Sukces",
                    JOptionPane.INFORMATION_MESSAGE);
            txtImie.setText("");
            txtNazwisko.setText("");
            txtStanowisko.setText("");
            wczytajDaneZTabeli();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Błąd zapisu do bazy: " + ex.getMessage(),
                    "Błąd SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        tabelaPracownikow = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane(tabelaPracownikow);

        txtImie = new javax.swing.JTextField(10);
        txtNazwisko = new javax.swing.JTextField(10);
        txtStanowisko = new javax.swing.JTextField(10);
        btnDodaj = new javax.swing.JButton("Dodaj pracownika");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("INF.04 - System Obsługi Pracowników");
        setSize(new java.awt.Dimension(750, 500));
        setLocationRelativeTo(null);

        setLayout(new java.awt.BorderLayout());

        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        javax.swing.JPanel panelFormularza = new javax.swing.JPanel();
        panelFormularza.add(new javax.swing.JLabel("Imię:"));
        panelFormularza.add(txtImie);
        panelFormularza.add(new javax.swing.JLabel("Nazwisko:"));
        panelFormularza.add(txtNazwisko);
        panelFormularza.add(new javax.swing.JLabel("Stanowisko:"));
        panelFormularza.add(txtStanowisko);
        panelFormularza.add(btnDodaj);

        add(panelFormularza, java.awt.BorderLayout.SOUTH);
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }

    private javax.swing.JButton btnDodaj;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabelaPracownikow;
    private javax.swing.JTextField txtImie;
    private javax.swing.JTextField txtNazwisko;
    private javax.swing.JTextField txtStanowisko;
}