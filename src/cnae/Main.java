package cnae;

import javax.swing.*;

public class Main extends JFrame {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Pesquisa de Código CNAE");
        frame.setContentPane(new PesquisaCnae().getPanel()); //Chama o Form PesquinaCnae
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1015,500); //Ajusta o tamanho automaticamente
        frame.setLocationRelativeTo(null); //Centraliza a janela
        frame.setVisible(true);
    }
}
