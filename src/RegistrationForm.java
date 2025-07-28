import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class RegistrationForm extends JDialog {
    private JTextField tfName;
    private JTextField tfEmail;
    private JTextField tfPhone;
    private JTextField tfAddress;
    private JPasswordField pfPassword;
    private JPasswordField pfConfirmPassword;
    private JButton btnRegister;
    private JButton btnCancel;
    private JPanel registerPanel;
    private JComboBox comboBox1;
    private JComboBox<String> cbCountryCode; // JComboBox for country codes

    public User user;

    public RegistrationForm(JFrame parent) {
        super(parent);
        setTitle("Create a new account");

        // Initialize components
        registerPanel = new JPanel();
        registerPanel.setLayout(new GridLayout(0, 2));

        tfName = new JTextField();
        tfEmail = new JTextField();
        tfPhone = new JTextField();
        tfAddress = new JTextField();
        pfPassword = new JPasswordField();
        pfConfirmPassword = new JPasswordField();
        btnRegister = new JButton("Register");
        btnCancel = new JButton("Cancel");

        // Initialize JComboBox for country codes
        String[] countryCodes = { "+1 (USA)", "+91 (India)", "+44 (UK)", "+61 (Australia)","+65(Singapore)","+94(Sri Lanka)","+93(Afghanistan)" };
        cbCountryCode = new JComboBox<>(countryCodes);

        // Add components to the panel
        registerPanel.add(new JLabel("Name:"));
        registerPanel.add(tfName);
        registerPanel.add(new JLabel("Email:"));
        registerPanel.add(tfEmail);
        registerPanel.add(new JLabel("Phone:"));
        registerPanel.add(tfPhone);
        registerPanel.add(new JLabel("Country Code:"));
        registerPanel.add(cbCountryCode); // Add JComboBox to the panel
        registerPanel.add(new JLabel("Address:"));
        registerPanel.add(tfAddress);
        registerPanel.add(new JLabel("Password:"));
        registerPanel.add(pfPassword);
        registerPanel.add(new JLabel("Confirm Password:"));
        registerPanel.add(pfConfirmPassword);
        registerPanel.add(btnRegister);
        registerPanel.add(btnCancel);

        setContentPane(registerPanel);
        setMinimumSize(new Dimension(450, 474));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    private void registerUser() {
        String name = tfName.getText();
        String email = tfEmail.getText();
        String phone = tfPhone.getText().trim();
        String address = tfAddress.getText();
        String password = String.valueOf(pfPassword.getPassword());
        String confirmPassword = String.valueOf(pfConfirmPassword.getPassword());

        // Get the selected country code
        String selectedCountryCode = (String) cbCountryCode.getSelectedItem();
        String fullPhone = selectedCountryCode + phone; // Combine country code with phone number

        // Validate input fields
        if (name.isEmpty() || email.isEmpty() || address.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all fields", "Try again", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if email ends with @gmail.com
        if (!email.endsWith("@gmail.com")) {
            JOptionPane.showMessageDialog(this, "Email must end with @gmail.com", "Invalid Email", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate phone number
        if (phone.length() != 10 || !phone.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Phone Number must contain 10 digits only", "Invalid Phone Number", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Confirm Password does not match", "Try again", JOptionPane.ERROR_MESSAGE);
            return;
        }

        user = addUserToDatabase(name, email, fullPhone, address, password);
        if (user != null) {
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to register new user", "Try again", JOptionPane.ERROR_MESSAGE);
        }
    }

    private User addUserToDatabase(String name, String email, String phone, String address, String password) {
        User user = null;
        final String DB_URL = "jdbc:mysql://localhost/MyStore?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "INSERT INTO users (name, email, phone, address, password) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, phone);
            preparedStatement.setString(4, address);
            preparedStatement.setString(5, password); // Hash this for security in a real application

            int addedRows = preparedStatement.executeUpdate();
            if (addedRows > 0) {
                user = new User();
                user.name = name;
                user.email = email;
                user.phone = phone;
                user.address = address;
                user.password = password; // Store hashed password
            }

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static void main(String[] args) {
        RegistrationForm myForm = new RegistrationForm(null);
        User user = myForm.user;
        if (user != null) {
            System.out.println("Successful registration of: " + user.name);
        } else {
            System.out.println("Registration Canceled");
        }
    }
}
