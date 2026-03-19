package com.steganography.view;

import com.steganography.controller.AuthController;
import com.steganography.controller.LsbController;
import com.steganography.controller.PvdController;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

/**
 * The main application window.
 *
 * In the original Home.java this was the Home class itself — a single JFrame
 * subclass that contained EVERYTHING (all panels, all buttons, all logic).
 *
 * Now it only does what a frame should do:
 *   - Sets up the window chrome (undecorated, drag-to-move, close/minimise)
 *   - Creates the sidebar navigation
 *   - Holds a CardLayout content area and swaps panels into it
 *   - Delegates all logic to the controller layer
 *
 * Original window size: 1120 × 660  (preserved exactly)
 */
public class MainFrame extends JFrame {

    // -----------------------------------------------------------------------
    // Panels managed by this frame
    // -----------------------------------------------------------------------
    private final LoginPanel    loginPanel;
    private final RegisterPanel registerPanel;
    private final LsbPanel      lsbPanel;
    private final PvdPanel      pvdPanel;
    private final InfoPanel     infoPanel;

    // -----------------------------------------------------------------------
    // Sidebar navigation items
    // -----------------------------------------------------------------------
    private final JPanel lsbNavItem  = new JPanel();
    private final JPanel pvdNavItem  = new JPanel();
    private final JPanel infoNavItem = new JPanel();
    private final JLabel logoutLabel = new JLabel("Logout");
    private final JLabel loginLabel  = new JLabel("Login");

    // -----------------------------------------------------------------------
    // Content switcher
    // -----------------------------------------------------------------------
    private final JPanel     contentArea = new JPanel(new CardLayout());

    // Drag-to-move support (original mousepX / mousepY fields)
    private int dragStartX, dragStartY;

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    public MainFrame() {
        // --- Window settings (mirrors original Home initComponents) ---
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);          // no OS title bar
        setSize(1120, 660);
        setLocationRelativeTo(null);   // centre on screen
        setBackground(new Color(0, 0, 0));

        // --- Build controllers (they wire services to panels) ---
        loginPanel    = new LoginPanel();
        registerPanel = new RegisterPanel();
        lsbPanel      = new LsbPanel();
        pvdPanel      = new PvdPanel();
        infoPanel     = new InfoPanel();

        AuthController authCtrl = new AuthController(loginPanel, registerPanel, this);
        LsbController  lsbCtrl  = new LsbController(lsbPanel);
        PvdController  pvdCtrl  = new PvdController(pvdPanel);

        // --- Assemble UI ---
        JPanel background = buildBackground();
        add(background);

        // Start on the login screen
        showContent("login");
        hideSidebarMenus();
    }

    // -----------------------------------------------------------------------
    // Layout builders
    // -----------------------------------------------------------------------

    private JPanel buildBackground() {
        JPanel bg = new JPanel(null); // AbsoluteLayout equivalent
        bg.setBackground(new Color(40, 40, 40));
        bg.setPreferredSize(new Dimension(1120, 660));

        // Drag strip at the top (original Dragger label)
        JLabel dragger = new JLabel();
        dragger.setBounds(0, 0, 1080, 20);
        dragger.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                dragStartX = e.getX();
                dragStartY = e.getY();
            }
        });
        dragger.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                setLocation(e.getXOnScreen() - dragStartX,
                            e.getYOnScreen() - dragStartY);
            }
        });
        bg.add(dragger);

        // Minimise button (original jLabel4)
        JPanel minimiseBtn = buildWindowButton("_", false);
        minimiseBtn.setBounds(1080, 0, 20, 20);
        minimiseBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { setState(JFrame.ICONIFIED); }
        });
        bg.add(minimiseBtn);

        // Close button (original jLabel3)
        JPanel closeBtn = buildWindowButton("X", true);
        closeBtn.setBounds(1100, 0, 20, 20);
        closeBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { System.exit(0); }
        });
        bg.add(closeBtn);

        // Sidebar
        bg.add(buildSidebar());

        // Content area (CardLayout)
        contentArea.setBackground(new Color(40, 40, 40));
        contentArea.setBounds(220, 20, 900, 640);
        contentArea.add(loginPanel,    "login");
        contentArea.add(registerPanel, "register");
        contentArea.add(lsbPanel,      "lsb");
        contentArea.add(pvdPanel,      "pvd");
        contentArea.add(infoPanel,     "info");
        bg.add(contentArea);

        return bg;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(null);
        sidebar.setBounds(0, 0, 220, 660);
        sidebar.setBackground(new Color(80, 80, 80));

        // App title
        JLabel title = new JLabel("Steganography", SwingConstants.CENTER);
        title.setFont(new Font("Cambria", Font.BOLD | Font.ITALIC, 30));
        title.setForeground(Color.WHITE);
        title.setBounds(0, 40, 220, 70);
        sidebar.add(title);

        // Separator lines (original jSeparator8/9)
        JSeparator sep1 = new JSeparator(); sep1.setBounds(10, 100, 200, 2);
        JSeparator sep2 = new JSeparator(); sep2.setBounds(10, 110, 200, 10);
        sidebar.add(sep1); sidebar.add(sep2);

        // LSB nav item
        configureNavItem(lsbNavItem, "Least Significant Bit", 250);
        lsbNavItem.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e)  { onLsbNavClicked(); }
            public void mouseEntered(MouseEvent e)  { lsbNavItem.setBackground(new Color(77, 150, 225)); }
            public void mouseExited(MouseEvent e)   { restoreNavColor(lsbNavItem, "lsb"); }
        });
        sidebar.add(lsbNavItem);

        // PVD nav item
        configureNavItem(pvdNavItem, "Pixel Value Differencing", 300);
        pvdNavItem.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e)  { onPvdNavClicked(); }
            public void mouseEntered(MouseEvent e)  { pvdNavItem.setBackground(new Color(77, 150, 225)); }
            public void mouseExited(MouseEvent e)   { restoreNavColor(pvdNavItem, "pvd"); }
        });
        sidebar.add(pvdNavItem);

        // Info nav item
        configureNavItem(infoNavItem, "Info", 350);
        infoNavItem.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e)  { onInfoNavClicked(); }
            public void mouseEntered(MouseEvent e)  { infoNavItem.setBackground(new Color(77, 150, 225)); }
            public void mouseExited(MouseEvent e)   { restoreNavColor(infoNavItem, "info"); }
        });
        sidebar.add(infoNavItem);

        // Login label
        loginLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        loginLabel.setForeground(Color.WHITE);
        loginLabel.setBounds(150, 550, 40, 20);
        loginLabel.setVisible(false);
        loginLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showContent("login");
                loginLabel.setVisible(false);
            }
        });
        sidebar.add(loginLabel);

        // Logout label
        logoutLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        logoutLabel.setForeground(Color.WHITE);
        logoutLabel.setBounds(150, 580, 55, 20);
        logoutLabel.setVisible(false);
        logoutLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { onLogout(); }
        });
        sidebar.add(logoutLabel);

        // Background image for sidebar
        try {
            Image sideImg = ImageIO.read(
                getClass().getResourceAsStream("/Images/SideBack.jpg"));
            if (sideImg != null) {
                JLabel bg = new JLabel(new ImageIcon(sideImg));
                bg.setBounds(0, 0, 220, 660);
                bg.setOpaque(true);
                sidebar.add(bg);
            }
        } catch (IOException | NullPointerException ignored) {}

        return sidebar;
    }

    // -----------------------------------------------------------------------
    // Navigation callbacks
    // -----------------------------------------------------------------------

    private String activeNav = ""; // tracks which nav item is highlighted

    private void onLsbNavClicked() {
        showContent("lsb");
        lsbPanel.resetToEncodeMode();
        activeNav = "lsb";
        lsbNavItem.setBackground(new Color(77, 208, 225));
        pvdNavItem.setBackground(new Color(150, 150, 150));
        infoNavItem.setBackground(new Color(150, 150, 150));
    }

    private void onPvdNavClicked() {
        showContent("pvd");
        pvdPanel.resetToEncodeMode();
        activeNav = "pvd";
        lsbNavItem.setBackground(new Color(150, 150, 150));
        pvdNavItem.setBackground(new Color(77, 150, 225));
        infoNavItem.setBackground(new Color(150, 150, 150));
    }

    private void onInfoNavClicked() {
        showContent("info");
        activeNav = "info";
        lsbNavItem.setBackground(new Color(150, 150, 150));
        pvdNavItem.setBackground(new Color(150, 150, 150));
        infoNavItem.setBackground(new Color(77, 150, 225));
    }

    private void onLogout() {
        showContent("login");
        logoutLabel.setVisible(false);
        hideSidebarMenus();
    }

    /** Called by AuthController after a successful login. */
    public void onLoginSuccess() {
        hideSidebarMenus();   // reset colours
        lsbNavItem.setVisible(true);
        pvdNavItem.setVisible(true);
        infoNavItem.setVisible(true);
        logoutLabel.setVisible(true);
        loginLabel.setVisible(false);
        showContent("lsb");
        onLsbNavClicked();
    }

    /** Called by AuthController when user clicks "Sign up?" link. */
    public void showRegisterPanel() {
        showContent("register");
        loginLabel.setVisible(true);
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    public void showContent(String cardName) {
        CardLayout cl = (CardLayout) contentArea.getLayout();
        cl.show(contentArea, cardName);
    }

    private void hideSidebarMenus() {
        lsbNavItem.setVisible(false);
        pvdNavItem.setVisible(false);
        infoNavItem.setVisible(false);
        logoutLabel.setVisible(false);
    }

    private void configureNavItem(JPanel panel, String text, int y) {
        panel.setBackground(new Color(150, 150, 150));
        panel.setBounds(0, y, 220, 40);
        panel.setVisible(false);
        JLabel label = new JLabel(text);
        label.setFont(new Font("Tahoma", Font.BOLD, 17));
        label.setForeground(Color.WHITE);
        panel.add(label);
    }

    private void restoreNavColor(JPanel panel, String name) {
        panel.setBackground(name.equals(activeNav)
            ? new Color(77, 208, 225)
            : new Color(150, 150, 150));
    }

    private JPanel buildWindowButton(String symbol, boolean isClose) {
        JPanel btn = new JPanel(new BorderLayout());
        btn.setBackground(new Color(40, 40, 40));
        JLabel lbl = new JLabel(symbol, SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Tahoma", Font.BOLD, 13));
        btn.add(lbl, BorderLayout.CENTER);
        if (isClose) {
            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(178, 34, 34)); }
                public void mouseExited(MouseEvent e)  { btn.setBackground(new Color(40, 40, 40));  }
            });
        } else {
            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(80, 80, 80)); }
                public void mouseExited(MouseEvent e)  { btn.setBackground(new Color(40, 40, 40)); }
            });
        }
        return btn;
    }
}
