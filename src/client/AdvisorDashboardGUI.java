package client;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

import javax.swing.*;

import client.ChatClientGUI;
import domain.ComplaintsAndQueries;
import domain.ComplaintsAndQueriesTable;

public class AdvisorDashboardGUI extends JFrame {
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem logoutMenuItem;
    private JTabbedPane tabbedPane;
    private JPanel complaintsPanel;
    private JPanel responsePanel;
	private ClientHandler client;

    public AdvisorDashboardGUI(ClientHandler client) {
    	this.client = client;
        setTitle("Advisor Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Create the menu bar
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        logoutMenuItem = new JMenuItem("Logout");
        logoutMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Display a confirmation dialog
                int confirmed = JOptionPane.showConfirmDialog(null, 
                    "Are you sure you want to logout?", "Logout", 
                    JOptionPane.YES_NO_OPTION);
                if (confirmed == JOptionPane.YES_OPTION) {
                    // Close the dashboard window and return to the login screen
                    dispose();
                    AdLogin login = new AdLogin();
                    login.setVisible(true);
                }
            }
        });
        fileMenu.add(logoutMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // Create the tabbed pane
        tabbedPane = new JTabbedPane();
        complaintsPanel = new JPanel();
        responsePanel = new JPanel();
        tabbedPane.addTab("Complaints", complaintsPanel);
        tabbedPane.addTab("Responses", responsePanel);
        add(tabbedPane, BorderLayout.CENTER);

        // Create the complaints panel
        complaintsPanel.setLayout(new BorderLayout());
//        
        client.sendAction("Get All Complaint_Query For Advisor");
        List<ComplaintsAndQueries> resp = client.receiveObject();

        // Create complaints table
        ComplaintsAndQueriesTable tableCreator = new ComplaintsAndQueriesTable(resp);
        JTable complaintsTable = tableCreator.createTable();
        complaintsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow only one row to be selected at a time
        
     // Add a listener to detect when a row is selected
        complaintsTable.addMouseListener((MouseListener) new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    JTable target = (JTable)e.getSource();
                    int row = target.getSelectedRow();
                    List<ComplaintsAndQueries> complaints = client.receiveObject();
					// Get the ComplaintsAndQueries object for the selected row
                    ComplaintsAndQueries selectedComplaint = complaints.get(row);
                    // Perform your assign action here using the selectedComplaint object
                }
            }
        });
        
        
        complaintsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane complaintsScrollPane = new JScrollPane(complaintsTable);
        complaintsPanel.add(complaintsScrollPane, BorderLayout.CENTER);
        JButton viewComplaintButton = new JButton("View Complaint");
        viewComplaintButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Handle view complaint action
            }
        });
        complaintsPanel.add(viewComplaintButton, BorderLayout.SOUTH);

        // Create the response panel
        responsePanel.setLayout(new BorderLayout());
        JTextArea responseTextArea = new JTextArea(10, 40);
        JScrollPane responseScrollPane = new JScrollPane(responseTextArea);
        responsePanel.add(responseScrollPane, BorderLayout.CENTER);
        JButton sendResponseButton = new JButton("Send Response");
        sendResponseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	client.sendAction("Send Message");
				client.sendPort(9000);
				ChatClientGUI chat = new ChatClientGUI("Student","liveChat", 9000);
				chat.setVisible(true);
            }
        });
        responsePanel.add(sendResponseButton, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        AdvisorDashboardGUI dashboard = new AdvisorDashboardGUI(null);
        dashboard.setVisible(true);
    }
}
