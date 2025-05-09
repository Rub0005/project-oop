package gui;

import model.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Main GUI for the Hospital Management System, providing tabs for managing staff, patients, and appointments.
 * In guest mode, only the staff tab is accessible in read-only mode.
 */
public class HospitalManagementGUI extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(HospitalManagementGUI.class.getName());
    private final HospitalSystem hospitalSystem;
    private final boolean isAdmin;
    private JTabbedPane tabbedPanel;
    private JPanel staffPanel, patientPanel, appointmentPanel;
    private List<Nurse> nurses; // Local list to manage nurses since HospitalSystem doesn't have add/update/remove methods for nurses

    // Staff components (for both Doctors and Nurses)
    private JTable staffTable;
    private DefaultTableModel staffTableModel;
    private JTextField staffNameField, staffAgeField, staffSpecializationField, staffAvailableDatesField;
    private TableRowSorter<DefaultTableModel> staffSorter;

    // Patient components
    private JTable patientTable;
    private DefaultTableModel patientTableModel;
    private JTextField patientNameField, patientAgeField, patientDiseaseField;
    private TableRowSorter<DefaultTableModel> patientSorter;

    // Appointment components
    private JTable appointmentTable;
    private DefaultTableModel appointmentTableModel;
    private JComboBox<String> staffDropdown;
    private JComboBox<String> patientDropdown;
    private JTextField appointmentDateField;
    private TableRowSorter<DefaultTableModel> appointmentSorter;

    // Input validation patterns
    private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z\\s]{1,50}");
    private static final Pattern SPECIALIZATION_PATTERN = Pattern.compile("[a-zA-Z\\s]{1,100}");
    private static final Pattern DISEASE_PATTERN = Pattern.compile("[a-zA-Z0-9\\s,.]{1,200}");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("[a-zA-Z0-9]{1,20}");
    private GridBagConstraints gbc;

    public HospitalManagementGUI(HospitalSystem hospitalSystem, boolean isAdmin) {
        this.hospitalSystem = hospitalSystem;
        this.isAdmin = isAdmin;
        this.nurses = new ArrayList<>(hospitalSystem.getAllNurses()); // Initialize nurses list
        initializeUI();
    }

    public HospitalManagementGUI() {
        this.hospitalSystem = new HospitalSystem();
        this.isAdmin = authenticateAdmin();
        this.nurses = new ArrayList<>(hospitalSystem.getAllNurses());
        initializeUI();
    }

    private void initializeUI() {
        HospitalUIUtils.applyHospitalTheme();
        setTitle(isAdmin ? "Hospital Management System - Administrator Mode" : "Hospital Management System - Guest Mode");
        setMinimumSize(new Dimension(900, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        initComponents();
        loadData();
    }

    private boolean authenticateAdmin() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
        panel.setBackground(HospitalUIUtils.BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel usernameLabel = HospitalUIUtils.createStyledLabel("Username:");
        JTextField usernameField = HospitalUIUtils.createStyledTextField(20);
        usernameField.getAccessibleContext().setAccessibleDescription("Enter admin username");
        JLabel passwordLabel = HospitalUIUtils.createStyledLabel("Password:");
        JPasswordField passwordField = HospitalUIUtils.createStyledPasswordField(20);
        passwordField.getAccessibleContext().setAccessibleDescription("Enter admin password");

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);

        usernameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { validateUsername(); }
            @Override
            public void removeUpdate(DocumentEvent e) { validateUsername(); }
            @Override
            public void changedUpdate(DocumentEvent e) { validateUsername(); }

            private void validateUsername() {
                String text = usernameField.getText().trim();
                Border border = USERNAME_PATTERN.matcher(text).matches() || text.isEmpty()
                        ? BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(HospitalUIUtils.PRIMARY_COLOR, 1),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5))
                        : BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(HospitalUIUtils.ERROR_COLOR, 1),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5));
                usernameField.setBorder(border);
            }
        });

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Administrator Authentication", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            char[] password = passwordField.getPassword();
            try {
                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Username cannot be empty!",
                            "Authentication Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                if (!USERNAME_PATTERN.matcher(username).matches()) {
                    JOptionPane.showMessageDialog(this,
                            "Username must be 1-20 alphanumeric characters!",
                            "Authentication Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                if (password.length == 0) {
                    JOptionPane.showMessageDialog(this,
                            "Password cannot be empty!",
                            "Authentication Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                if (hospitalSystem.verifyAdminPassword(username, password)) {
                    return true;
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Invalid username or password!",
                            "Authentication Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } finally {
                Arrays.fill(password, ' ');
            }
        }

        JOptionPane.showMessageDialog(this,
                "Operating in guest mode. Only staff information is accessible.",
                "Authentication Notice", JOptionPane.INFORMATION_MESSAGE);
        return false;
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(HospitalUIUtils.BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HospitalUIUtils.PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        JLabel titleLabel = new JLabel("Hospital Management System");
        titleLabel.setFont(HospitalUIUtils.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        JLabel modeLabel = new JLabel(isAdmin ? "Administrator Mode" : "Guest Mode");
        modeLabel.setFont(HospitalUIUtils.BOLD_FONT);
        modeLabel.setForeground(Color.WHITE);
        headerPanel.add(modeLabel, BorderLayout.EAST);

        // Tabbed panel
        tabbedPanel = new JTabbedPane();
        tabbedPanel.setFont(HospitalUIUtils.BOLD_FONT);
        tabbedPanel.setBackground(HospitalUIUtils.BACKGROUND_COLOR);
        tabbedPanel.setForeground(HospitalUIUtils.TEXT_COLOR);

        createStaffPanel();
        tabbedPanel.addTab("Staff", staffPanel);

        if (isAdmin) {
            createPatientPanel();
            createAppointmentPanel();
            tabbedPanel.addTab("Patients", patientPanel);
            tabbedPanel.addTab("Appointments", appointmentPanel);
        }

        // Footer panel
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(HospitalUIUtils.BACKGROUND_COLOR);
        JLabel statusLabel = HospitalUIUtils.createStyledLabel("© 2025 Hospital Management System");
        footerPanel.add(statusLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void createStaffPanel() {
        staffPanel = HospitalUIUtils.createStyledPanel("Staff Management", new BorderLayout(10, 10));

        String[] staffColumns = {"Type", "ID", "Name", "Age", "Specializations", "Available Dates"};
        staffTableModel = new DefaultTableModel(staffColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        staffTable = HospitalUIUtils.createStyledTable(staffTableModel);
        staffTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        staffSorter = new TableRowSorter<>(staffTableModel);
        staffTable.setRowSorter(staffSorter);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(HospitalUIUtils.BACKGROUND_COLOR);
        JLabel filterLabel = HospitalUIUtils.createStyledLabel("Search: ");
        JTextField filterField = HospitalUIUtils.createStyledTextField(20);
        filterField.getAccessibleContext().setAccessibleDescription("Search staff");
        filterPanel.add(filterLabel);
        filterPanel.add(filterField);

        filterField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateFilter(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateFilter(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateFilter(); }

            private void updateFilter() {
                String text = filterField.getText().trim();
                staffSorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
            }
        });

        JScrollPane staffScrollPane = new JScrollPane(staffTable);
        staffScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(HospitalUIUtils.BACKGROUND_COLOR);
        topPanel.add(filterPanel, BorderLayout.NORTH);
        topPanel.add(staffScrollPane, BorderLayout.CENTER);
        staffPanel.add(topPanel, BorderLayout.CENTER);

        String[] labels = {"Name:", "Age:", "Specializations (comma-separated, for doctors only):", "Available Dates (YYYY-MM-DD, comma-separated):"};
        JComponent[] fields = {
                staffNameField = HospitalUIUtils.createStyledTextField(20),
                staffAgeField = HospitalUIUtils.createStyledTextField(20),
                staffSpecializationField = HospitalUIUtils.createStyledTextField(20),
                staffAvailableDatesField = HospitalUIUtils.createStyledTextField(20)
        };
        JPanel staffFormPanel = HospitalUIUtils.createFormPanel("Staff Details", labels, fields);

        addValidationListener(staffNameField, NAME_PATTERN);
        addValidationListener(staffSpecializationField, SPECIALIZATION_PATTERN);
        staffAgeField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { validateAge(); }
            @Override
            public void removeUpdate(DocumentEvent e) { validateAge(); }
            @Override
            public void changedUpdate(DocumentEvent e) { validateAge(); }

            private void validateAge() {
                String text = staffAgeField.getText().trim();
                try {
                    if (!text.isEmpty()) Integer.parseInt(text);
                    staffAgeField.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(HospitalUIUtils.PRIMARY_COLOR, 1),
                            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
                } catch (NumberFormatException e) {
                    staffAgeField.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(HospitalUIUtils.ERROR_COLOR, 1),
                            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
                }
            }
        });

        JButton addStaffBtn = HospitalUIUtils.createStyledButton("Add Staff", KeyEvent.VK_A);
        JButton updateStaffBtn = HospitalUIUtils.createStyledButton("Update Staff", KeyEvent.VK_U);
        JButton deleteStaffBtn = HospitalUIUtils.createStyledButton("Delete Staff", KeyEvent.VK_D);
        JButton clearStaffBtn = HospitalUIUtils.createStyledButton("Clear Fields", KeyEvent.VK_C);
        JPanel staffButtonPanel = HospitalUIUtils.createButtonPanel(
                addStaffBtn, updateStaffBtn, deleteStaffBtn, clearStaffBtn);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = labels.length;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        staffFormPanel.add(staffButtonPanel, gbc);

        staffPanel.add(staffFormPanel, BorderLayout.SOUTH);

        staffTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && staffTable.getSelectedRow() != -1) {
                int row = staffTable.convertRowIndexToModel(staffTable.getSelectedRow());
                staffNameField.setText(staffTableModel.getValueAt(row, 2).toString());
                staffAgeField.setText(staffTableModel.getValueAt(row, 3).toString());
                staffSpecializationField.setText(staffTableModel.getValueAt(row, 4).toString());
                staffAvailableDatesField.setText(staffTableModel.getValueAt(row, 5).toString());
            }
        });

        addStaffBtn.addActionListener(e -> addStaff());
        updateStaffBtn.addActionListener(e -> updateStaff());
        deleteStaffBtn.addActionListener(e -> deleteStaff());
        clearStaffBtn.addActionListener(e -> clearStaffFields());

        if (!isAdmin) {
            addStaffBtn.setEnabled(false);
            updateStaffBtn.setEnabled(false);
            deleteStaffBtn.setEnabled(false);
            for (JComponent field : fields) field.setEnabled(false);
        }
    }

    private void createPatientPanel() {
        patientPanel = HospitalUIUtils.createStyledPanel("Patients Management", new BorderLayout(10, 10));

        String[] patientColumns = {"ID", "Name", "Age", "Disease Description"};
        patientTableModel = new DefaultTableModel(patientColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        patientTable = HospitalUIUtils.createStyledTable(patientTableModel);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientSorter = new TableRowSorter<>(patientTableModel);
        patientTable.setRowSorter(patientSorter);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(HospitalUIUtils.BACKGROUND_COLOR);
        JLabel filterLabel = HospitalUIUtils.createStyledLabel("Search: ");
        JTextField filterField = HospitalUIUtils.createStyledTextField(20);
        filterField.getAccessibleContext().setAccessibleDescription("Search patients");
        filterPanel.add(filterLabel);
        filterPanel.add(filterField);

        filterField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateFilter(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateFilter(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateFilter(); }

            private void updateFilter() {
                String text = filterField.getText().trim();
                patientSorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
            }
        });

        JScrollPane patientScrollPane = new JScrollPane(patientTable);
        patientScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(HospitalUIUtils.BACKGROUND_COLOR);
        topPanel.add(filterPanel, BorderLayout.NORTH);
        topPanel.add(patientScrollPane, BorderLayout.CENTER);
        patientPanel.add(topPanel, BorderLayout.CENTER);

        String[] labels = {"Name:", "Age:", "Disease Description:"};
        JComponent[] fields = {
                patientNameField = HospitalUIUtils.createStyledTextField(20),
                patientAgeField = HospitalUIUtils.createStyledTextField(20),
                patientDiseaseField = HospitalUIUtils.createStyledTextField(20)
        };
        JPanel patientFormPanel = HospitalUIUtils.createFormPanel("Patient Details", labels, fields);

        addValidationListener(patientNameField, NAME_PATTERN);
        addValidationListener(patientDiseaseField, DISEASE_PATTERN);
        patientAgeField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { validateAge(); }
            @Override
            public void removeUpdate(DocumentEvent e) { validateAge(); }
            @Override
            public void changedUpdate(DocumentEvent e) { validateAge(); }

            private void validateAge() {
                String text = patientAgeField.getText().trim();
                try {
                    if (!text.isEmpty()) Integer.parseInt(text);
                    patientAgeField.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(HospitalUIUtils.PRIMARY_COLOR, 1),
                            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
                } catch (NumberFormatException e) {
                    patientAgeField.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(HospitalUIUtils.ERROR_COLOR, 1),
                            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
                }
            }
        });

        JButton addPatientBtn = HospitalUIUtils.createStyledButton("Add Patient", KeyEvent.VK_A);
        JButton updatePatientBtn = HospitalUIUtils.createStyledButton("Update Patient", KeyEvent.VK_U);
        JButton deletePatientBtn = HospitalUIUtils.createStyledButton("Delete Patient", KeyEvent.VK_D);
        JButton clearPatientBtn = HospitalUIUtils.createStyledButton("Clear Fields", KeyEvent.VK_C);
        JPanel patientButtonPanel = HospitalUIUtils.createButtonPanel(
                addPatientBtn, updatePatientBtn, deletePatientBtn, clearPatientBtn);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = labels.length;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        patientFormPanel.add(patientButtonPanel, gbc);

        patientPanel.add(patientFormPanel, BorderLayout.SOUTH);

        patientTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && patientTable.getSelectedRow() != -1) {
                int row = patientTable.convertRowIndexToModel(patientTable.getSelectedRow());
                patientNameField.setText(patientTableModel.getValueAt(row, 1).toString());
                patientAgeField.setText(patientTableModel.getValueAt(row, 2).toString());
                patientDiseaseField.setText(patientTableModel.getValueAt(row, 3).toString());
                updateStaffDropdown();
            }
        });

        addPatientBtn.addActionListener(e -> addPatient());
        updatePatientBtn.addActionListener(e -> updatePatient());
        deletePatientBtn.addActionListener(e -> deletePatient());
        clearPatientBtn.addActionListener(e -> clearPatientFields());
    }

    private void createAppointmentPanel() {
        appointmentPanel = HospitalUIUtils.createStyledPanel("Appointments Management", new BorderLayout(10, 10));

        String[] appointmentColumns = {"Appointment ID", "Staff", "Staff Type", "Patient", "Date"};
        appointmentTableModel = new DefaultTableModel(appointmentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        appointmentTable = HospitalUIUtils.createStyledTable(appointmentTableModel);
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentSorter = new TableRowSorter<>(appointmentTableModel);
        appointmentTable.setRowSorter(appointmentSorter);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(HospitalUIUtils.BACKGROUND_COLOR);
        JLabel filterLabel = HospitalUIUtils.createStyledLabel("Search: ");
        JTextField filterField = HospitalUIUtils.createStyledTextField(20);
        filterField.getAccessibleContext().setAccessibleDescription("Search appointments");
        filterPanel.add(filterLabel);
        filterPanel.add(filterField);

        filterField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateFilter(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateFilter(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateFilter(); }

            private void updateFilter() {
                String text = filterField.getText().trim();
                appointmentSorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
            }
        });

        JScrollPane appointmentScrollPane = new JScrollPane(appointmentTable);
        appointmentScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(HospitalUIUtils.BACKGROUND_COLOR);
        topPanel.add(filterPanel, BorderLayout.NORTH);
        topPanel.add(appointmentScrollPane, BorderLayout.CENTER);
        appointmentPanel.add(topPanel, BorderLayout.CENTER);

        String[] labels = {"Medical Staff:", "Patient:", "Date (YYYY-MM-DD):"};
        JComponent[] fields = {
                staffDropdown = HospitalUIUtils.createStyledComboBox(),
                patientDropdown = HospitalUIUtils.createStyledComboBox(),
                appointmentDateField = HospitalUIUtils.createStyledTextField(20)
        };
        JPanel appointmentFormPanel = HospitalUIUtils.createFormPanel("Appointment Details", labels, fields);

        appointmentDateField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { validateDate(); }
            @Override
            public void removeUpdate(DocumentEvent e) { validateDate(); }
            @Override
            public void changedUpdate(DocumentEvent e) { validateDate(); }

            private void validateDate() {
                String text = appointmentDateField.getText().trim();
                try {
                    if (!text.isEmpty()) LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE);
                    appointmentDateField.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(HospitalUIUtils.PRIMARY_COLOR, 1),
                            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
                } catch (DateTimeParseException e) {
                    appointmentDateField.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(HospitalUIUtils.ERROR_COLOR, 1),
                            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
                }
            }
        });

        patientDropdown.addActionListener(e -> updateStaffDropdown());

        JButton scheduleBtn = HospitalUIUtils.createStyledButton("Schedule Appointment", KeyEvent.VK_S);
        JButton updateBtn = HospitalUIUtils.createStyledButton("Update Appointment", KeyEvent.VK_U);
        JButton deleteBtn = HospitalUIUtils.createStyledButton("Delete Appointment", KeyEvent.VK_D);
        JButton clearBtn = HospitalUIUtils.createStyledButton("Clear Fields", KeyEvent.VK_C);
        JPanel appointmentButtonPanel = HospitalUIUtils.createButtonPanel(scheduleBtn, updateBtn, deleteBtn, clearBtn);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = labels.length;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        appointmentFormPanel.add(appointmentButtonPanel, gbc);

        appointmentPanel.add(appointmentFormPanel, BorderLayout.SOUTH);

        appointmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && appointmentTable.getSelectedRow() != -1) {
                int row = appointmentTable.convertRowIndexToModel(appointmentTable.getSelectedRow());
                staffDropdown.setSelectedItem(appointmentTableModel.getValueAt(row, 1));
                patientDropdown.setSelectedItem(appointmentTableModel.getValueAt(row, 3));
                appointmentDateField.setText(appointmentTableModel.getValueAt(row, 4).toString());
            }
        });

        scheduleBtn.addActionListener(e -> scheduleAppointment());
        updateBtn.addActionListener(e -> updateAppointment());
        deleteBtn.addActionListener(e -> deleteAppointment());
        clearBtn.addActionListener(e -> clearAppointmentFields());
    }

    private void loadData() {
        loadStaff();
        if (isAdmin) {
            loadPatients();
            loadAppointments();
            updatePatientDropdown();
            updateStaffDropdown();
        }
    }

    private void loadStaff() {
        staffTableModel.setRowCount(0);
        // Load doctors
        for (Doctor doctor : hospitalSystem.getAllDoctors()) {
            StringBuilder dates = new StringBuilder();
            for (LocalDate date : doctor.getAvailableDates()) {
                if (dates.length() > 0) dates.append(", ");
                dates.append(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
            }
            staffTableModel.addRow(new Object[]{
                    "Doctor",
                    doctor.getDoctorID(),
                    doctor.getName(),
                    doctor.getAge(),
                    String.join(", ", doctor.getSpecializations()),
                    dates.toString()
            });
        }
        // Load nurses
        for (Nurse nurse : nurses) {
            StringBuilder dates = new StringBuilder();
            for (LocalDate date : nurse.getAvailableDates()) {
                if (dates.length() > 0) dates.append(", ");
                dates.append(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
            }
            staffTableModel.addRow(new Object[]{
                    "Nurse", nurse.getNurseID(), nurse.getName(), nurse.getAge(), "",dates.toString()});
        }
    }

    private void loadPatients() {
        patientTableModel.setRowCount(0);
        for (Patient patient : hospitalSystem.getAllPatients()) {
            patientTableModel.addRow(new Object[]{
                    patient.getPatientID(),
                    patient.getName(),
                    patient.getAge(),
                    patient.getDiseaseDescription()
            });
        }
    }

    private void loadAppointments() {
        appointmentTableModel.setRowCount(0);
        for (Appointment appointment : hospitalSystem.getAllAppointments()) {
            appointmentTableModel.addRow(new Object[]{
                    appointment.getAppointmentID(),
                    appointment.getMedicalStaffName(),
                    appointment.getMedicalStaffType(),
                    appointment.getPatient().getName() + " (" + appointment.getPatient().getPatientID() + ")",
                    appointment.getAppointmentDate().format(DateTimeFormatter.ISO_LOCAL_DATE)
            });
        }
    }

    private void updateStaffDropdown() {
        staffDropdown.removeAllItems();
        String selectedPatient = patientDropdown.getSelectedItem() != null ? patientDropdown.getSelectedItem().toString() : null;
        Patient patient = null;

        if (selectedPatient != null) {
            String patientId = selectedPatient.substring(selectedPatient.lastIndexOf("(") + 1, selectedPatient.lastIndexOf(")"));
            patient = hospitalSystem.getAllPatients().stream()
                    .filter(p -> p.getPatientID().equals(patientId))
                    .findFirst()
                    .orElse(null);
        }

        if (patient != null) {
            if (patient.requiresBandaging()) {
                // Only nurses for bandaging cases
                for (Nurse nurse : nurses) {
                    staffDropdown.addItem(nurse.getName() + " (" + nurse.getNurseID() + ")");
                }
            } else if (patient.getAge() < 18) {
                // Only pediatricians for patients under 18
                for (Doctor doctor : hospitalSystem.getAllDoctors()) {
                    if (doctor.hasSpecialization("Pediatrics")) {
                        staffDropdown.addItem(doctor.getName() + " (" + doctor.getDoctorID() + ")");
                    }
                }
            } else {
                // All doctors for other cases
                for (Doctor doctor : hospitalSystem.getAllDoctors()) {
                    staffDropdown.addItem(doctor.getName() + " (" + doctor.getDoctorID() + ")");
                }
            }
        } else {
            // If no patient is selected, show all staff
            for (Doctor doctor : hospitalSystem.getAllDoctors()) {
                staffDropdown.addItem(doctor.getName() + " (" + doctor.getDoctorID() + ")");
            }
            for (Nurse nurse : nurses) {
                staffDropdown.addItem(nurse.getName() + " (" + nurse.getNurseID() + ")");
            }
        }
    }

    private void updatePatientDropdown() {
        patientDropdown.removeAllItems();
        for (Patient patient : hospitalSystem.getAllPatients()) {
            patientDropdown.addItem(patient.getName() + " (" + patient.getPatientID() + ")");
        }
    }

    private void addStaff() {
        if (!isAdmin) return;
        try {
            String name = staffNameField.getText().trim();
            String ageText = staffAgeField.getText().trim();
            String specializationsStr = staffSpecializationField.getText().trim();
            String datesStr = staffAvailableDatesField.getText().trim();

            if (!validateInputs(name, ageText, specializationsStr, datesStr)) return;

            int age = Integer.parseInt(ageText);
            List<LocalDate> dates = parseDates(datesStr);
            if (dates.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "At least one valid date is required!", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Object[] options = {"Doctor", "Nurse"};
            int choice = JOptionPane.showOptionDialog(this, "Select staff type:", "Staff Type",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (choice == -1) return; // User cancelled

            boolean isDoctor = choice == 0;
            if (isDoctor) {
                List<String> specializations = List.of(specializationsStr.split("\\s*,\\s*"));
                if (specializationsStr.isEmpty() || specializations.stream().anyMatch(String::isEmpty)) {
                    JOptionPane.showMessageDialog(this,
                            "Specializations are required for doctors!", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Doctor newDoctor = new Doctor(name, age, specializations, dates);
                if (hospitalSystem.addDoctor(newDoctor)) {
                    loadStaff();
                    updateStaffDropdown();
                    clearStaffFields();
                    JOptionPane.showMessageDialog(this, "Doctor added successfully with ID: " + newDoctor.getDoctorID(),
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add doctor due to ID conflict!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                String nurseID = generateNurseID();
                Nurse newNurse = new Nurse(name, age, nurseID, dates);
                nurses.add(newNurse);
                loadStaff();
                updateStaffDropdown();
                clearStaffFields();
                JOptionPane.showMessageDialog(this, "Nurse added successfully with ID: " + nurseID,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Age must be a valid integer!",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.severe("Error adding staff: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error adding staff: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStaff() {
        if (!isAdmin) return;
        try {
            int viewRow = staffTable.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a staff member to update!",
                        "Selection Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = staffTable.convertRowIndexToModel(viewRow);
            String type = staffTableModel.getValueAt(modelRow, 0).toString();
            String id = staffTableModel.getValueAt(modelRow, 1).toString();

            String name = staffNameField.getText().trim();
            String ageText = staffAgeField.getText().trim();
            String specializationsStr = staffSpecializationField.getText().trim();
            String datesStr = staffAvailableDatesField.getText().trim();

            if (!validateInputs(name, ageText, specializationsStr, datesStr)) return;

            int age = Integer.parseInt(ageText);
            List<LocalDate> dates = parseDates(datesStr);
            if (dates.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "At least one valid date is required!", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (type.equals("Doctor")) {
                List<String> specializations = List.of(specializationsStr.split("\\s*,\\s*"));
                if (specializationsStr.isEmpty() || specializations.stream().anyMatch(String::isEmpty)) {
                    JOptionPane.showMessageDialog(this,
                            "Specializations are required for doctors!", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Doctor updatedDoctor = new Doctor(name, age, specializations, dates);
                if (hospitalSystem.updateDoctor(id, updatedDoctor)) {
                    loadStaff();
                    updateStaffDropdown();
                    clearStaffFields();
                    JOptionPane.showMessageDialog(this, "Doctor updated successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update doctor!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                Nurse updatedNurse = new Nurse(name, age, id, dates);
                for (int i = 0; i < nurses.size(); i++) {
                    if (nurses.get(i).getNurseID().equals(id)) {
                        nurses.set(i, updatedNurse);
                        loadStaff();
                        updateStaffDropdown();
                        clearStaffFields();
                        JOptionPane.showMessageDialog(this, "Nurse updated successfully!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }
                JOptionPane.showMessageDialog(this, "Failed to update nurse!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Age must be a valid integer!",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.severe("Error updating staff: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error updating staff: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStaff() {
        if (!isAdmin) return;
        int viewRow = staffTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a staff member to delete!",
                    "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = staffTable.convertRowIndexToModel(viewRow);
        String type = staffTableModel.getValueAt(modelRow, 0).toString();
        String id = staffTableModel.getValueAt(modelRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this staff member?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success;
                if (type.equals("Doctor")) {
                    success = hospitalSystem.removeDoctor(id);
                } else {
                    success = nurses.removeIf(nurse -> nurse.getNurseID().equals(id));
                }
                if (success) {
                    loadStaff();
                    updateStaffDropdown();
                    clearStaffFields();
                    loadAppointments();
                    JOptionPane.showMessageDialog(this, type + " deleted successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete " + type.toLowerCase() + "!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                LOGGER.severe("Error deleting staff: " + e.getMessage());
                JOptionPane.showMessageDialog(this, "Error deleting staff: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearStaffFields() {
        staffNameField.setText("");
        staffAgeField.setText("");
        staffSpecializationField.setText("");
        staffAvailableDatesField.setText("");
        staffTable.clearSelection();
        resetFieldBorders(staffNameField, staffAgeField, staffSpecializationField, staffAvailableDatesField);
    }

    private void addPatient() {
        if (!isAdmin) return;
        try {
            String name = patientNameField.getText().trim();
            String ageText = patientAgeField.getText().trim();
            String disease = patientDiseaseField.getText().trim();

            if (!validatePatientInputs(name, ageText, disease)) return;

            int age = Integer.parseInt(ageText);
            Patient tempPatient = new Patient("", name, age, disease);
            String patientId = hospitalSystem.addPatient(tempPatient);

            loadPatients();
            updatePatientDropdown();
            clearPatientFields();
            JOptionPane.showMessageDialog(this, "Patient added successfully with ID: " + patientId,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Age must be a valid integer!",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, "Cannot add patient: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.severe("Error adding patient: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error adding patient: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePatient() {
        if (!isAdmin) return;
        try {
            int viewRow = patientTable.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a patient to update!",
                        "Selection Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = patientTable.convertRowIndexToModel(viewRow);
            String id = patientTableModel.getValueAt(modelRow, 0).toString();

            String name = patientNameField.getText().trim();
            String ageText = patientAgeField.getText().trim();
            String disease = patientDiseaseField.getText().trim();

            if (!validatePatientInputs(name, ageText, disease)) return;

            int age = Integer.parseInt(ageText);
            Patient updatedPatient = new Patient(id, name, age, disease);
            if (hospitalSystem.updatePatient(id, updatedPatient)) {
                loadPatients();
                updatePatientDropdown();
                clearPatientFields();
                JOptionPane.showMessageDialog(this, "Patient updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update patient!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Age must be a valid integer!",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.severe("Error updating patient: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error updating patient: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePatient() {
        if (!isAdmin) return;
        int viewRow = patientTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient to delete!",
                    "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = patientTable.convertRowIndexToModel(viewRow);
        String id = patientTableModel.getValueAt(modelRow, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this patient?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (hospitalSystem.removePatient(id)) {
                    loadPatients();
                    updatePatientDropdown();
                    clearPatientFields();
                    loadAppointments();
                    JOptionPane.showMessageDialog(this, "Patient deleted successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete patient!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                LOGGER.severe("Error deleting patient: " + e.getMessage());
                JOptionPane.showMessageDialog(this, "Error deleting patient: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearPatientFields() {
        patientNameField.setText("");
        patientAgeField.setText("");
        patientDiseaseField.setText("");
        patientTable.clearSelection();
        resetFieldBorders(patientNameField, patientAgeField, patientDiseaseField);
        updateStaffDropdown();
    }

    private void scheduleAppointment() {
        if (!isAdmin) return;
        try {
            if (staffDropdown.getSelectedIndex() == -1 || patientDropdown.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(this, "Please select both medical staff and patient!",
                        "Selection Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String staffText = staffDropdown.getSelectedItem().toString();
            String patientText = patientDropdown.getSelectedItem().toString();
            String dateStr = appointmentDateField.getText().trim();

            String staffId = staffText.substring(staffText.lastIndexOf("(") + 1, staffText.lastIndexOf(")"));
            String patientId = patientText.substring(patientText.lastIndexOf("(") + 1, patientText.lastIndexOf(")"));
            boolean isNurse = staffId.startsWith("NUR");

            if (dateStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a date!",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);

            if (hospitalSystem.scheduleAppointment(staffId, patientId, date, isNurse)) {
                loadAppointments();
                clearAppointmentFields();
                JOptionPane.showMessageDialog(this, "Appointment scheduled successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                String errorMsg = isNurse ? "Failed to schedule appointment! Nurse may not be available."
                        : "Failed to schedule appointment! Doctor may not be available, or patient requires a nurse or pediatrician.";
                JOptionPane.showMessageDialog(this, errorMsg,
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format! Use " + HospitalUIUtils.DATE_FORMAT + ".",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.severe("Error scheduling appointment: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error scheduling appointment: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateAppointment() {
        if (!isAdmin) return;
        try {
            int viewRow = appointmentTable.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an appointment to update!",
                        "Selection Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = appointmentTable.convertRowIndexToModel(viewRow);
            String appointmentId = appointmentTableModel.getValueAt(modelRow, 0).toString();

            if (staffDropdown.getSelectedIndex() == -1 || patientDropdown.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(this, "Please select both medical staff and patient!",
                        "Selection Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String staffText = staffDropdown.getSelectedItem().toString();
            String patientText = patientDropdown.getSelectedItem().toString();
            String dateStr = appointmentDateField.getText().trim();

            String staffId = staffText.substring(staffText.lastIndexOf("(") + 1, staffText.lastIndexOf(")"));
            String patientId = patientText.substring(patientText.lastIndexOf("(") + 1, patientText.lastIndexOf(")"));
            boolean isNurse = staffId.startsWith("NUR");

            if (dateStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a date!",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);

            if (hospitalSystem.updateAppointment(appointmentId, staffId, patientId, date, isNurse)) {
                loadAppointments();
                clearAppointmentFields();
                JOptionPane.showMessageDialog(this, "Appointment updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                String errorMsg = isNurse ? "Failed to update appointment! Nurse may not be available."
                        : "Failed to update appointment! Doctor may not be available, or patient requires a nurse or pediatrician.";
                JOptionPane.showMessageDialog(this, errorMsg,
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format! Use " + HospitalUIUtils.DATE_FORMAT + ".",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.severe("Error updating appointment: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error updating appointment: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAppointment() {
        if (!isAdmin) return;
        int viewRow = appointmentTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to delete!",
                    "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = appointmentTable.convertRowIndexToModel(viewRow);
        String appointmentId = appointmentTableModel.getValueAt(modelRow, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this appointment?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (hospitalSystem.removeAppointment(appointmentId)) {
                    loadAppointments();
                    clearAppointmentFields();
                    JOptionPane.showMessageDialog(this, "Appointment deleted successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete appointment!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                LOGGER.severe("Error deleting appointment: " + e.getMessage());
                JOptionPane.showMessageDialog(this, "Error deleting appointment: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearAppointmentFields() {
        staffDropdown.setSelectedIndex(-1);
        patientDropdown.setSelectedIndex(-1);
        appointmentDateField.setText("");
        appointmentTable.clearSelection();
        resetFieldBorders(appointmentDateField);
    }

    private boolean validateInputs(String name, String ageText, String specializationsStr, String datesStr) {
        if (!NAME_PATTERN.matcher(name).matches()) {
            JOptionPane.showMessageDialog(this, "Name must be 1-50 letters and spaces!",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            if (!ageText.isEmpty()) Integer.parseInt(ageText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Age must be a valid integer!",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!specializationsStr.isEmpty() && !SPECIALIZATION_PATTERN.matcher(specializationsStr).matches()) {
            JOptionPane.showMessageDialog(this, "Specializations must be 1-100 letters and spaces!",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean validatePatientInputs(String name, String ageText, String disease) {
        if (!NAME_PATTERN.matcher(name).matches()) {
            JOptionPane.showMessageDialog(this, "Name must be 1-50 letters and spaces!",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            if (!ageText.isEmpty()) Integer.parseInt(ageText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Age must be a valid integer!",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!DISEASE_PATTERN.matcher(disease).matches()) {
            JOptionPane.showMessageDialog(this, "Disease description must be 1-200 alphanumeric characters, spaces, or punctuation!",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private List<LocalDate> parseDates(String datesStr) {
        List<LocalDate> dates = new ArrayList<>();
        if (datesStr.isEmpty()) return dates;
        String[] dateArray = datesStr.split("\\s*,\\s*");
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        for (String dateStr : dateArray) {
            try {
                dates.add(LocalDate.parse(dateStr.trim(), formatter));
            } catch (DateTimeParseException e) {
                // Ignore invalid dates
            }
        }
        return dates;
    }

    private void addValidationListener(JTextField field, Pattern pattern) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { validate(); }
            @Override
            public void removeUpdate(DocumentEvent e) { validate(); }
            @Override
            public void changedUpdate(DocumentEvent e) { validate(); }

            private void validate() {
                String text = field.getText().trim();
                Border border = pattern.matcher(text).matches() || text.isEmpty()
                        ? BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(HospitalUIUtils.PRIMARY_COLOR, 1),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5))
                        : BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(HospitalUIUtils.ERROR_COLOR, 1),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5));
                field.setBorder(border);
            }
        });
    }

    private void resetFieldBorders(JComponent... fields) {
        for (JComponent field : fields) {
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(HospitalUIUtils.PRIMARY_COLOR, 1),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        }
    }

    private String generateNurseID() {
        int maxId = nurses.stream().map(n -> {String id = n.getNurseID();
                    return Integer.parseInt(id.substring(id.lastIndexOf("-") + 1));
        }).max(Integer::compare).orElse(0);
        return "NUR-" + String.format("%03d", maxId + 1);
    }
}