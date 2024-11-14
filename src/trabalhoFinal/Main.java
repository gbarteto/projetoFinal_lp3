package trabalhoFinal;

import javax.swing.*;

public class Main extends JFrame {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Controle de Entrada e Saída");
        frame.setContentPane(new FormEntrada().getPanel());  // Ajuste para acessar o JPanel
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);  // Centraliza a janela
        frame.setVisible(true);
    }

}
