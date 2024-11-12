package trabalhoFinal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import javax.imageio.ImageIO;
import java.util.Timer;
import java.util.TimerTask;

public class ControleEntradaSaida extends JFrame {
    // Componentes da Aba de Registrar Entrada
    private JTextField nomeField;
    private JTextField rgField;
    private JTextField motivoField;
    private JTextField apartamentoField;
    private JLabel fotoLabel;
    private BufferedImage foto; // Armazena a foto carregada

    // Componentes da Aba de Registrar Saída
    private DefaultListModel<Visitante> listaVisitantes; // Lista de visitantes dentro do prédio
    private JList<Visitante> visitantesList;

    // Timer para verificar alertas
    private Timer timer;

    public ControleEntradaSaida() {
        setTitle("Controle de Entrada e Saída");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Criação das Abas
        JTabbedPane tabbedPane = new JTabbedPane();

        // Aba de Registrar Entrada
        JPanel registrarEntradaPanel = criarAbaRegistrarEntrada();
        tabbedPane.addTab("Registrar Entrada", registrarEntradaPanel);

        // Aba de Registrar Saída
        JPanel registrarSaidaPanel = criarAbaRegistrarSaida();
        tabbedPane.addTab("Registrar Saída", registrarSaidaPanel);

        // Aba de Alertas (Opcional)
        JPanel alertasPanel = criarAbaAlertas();
        tabbedPane.addTab("Alertas", alertasPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Iniciar verificação de alertas
        iniciarVerificacaoAlertas();

        setVisible(true);
    }

    private JPanel criarAbaRegistrarEntrada() {
        JPanel entradaPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nome
        gbc.gridx = 0;
        gbc.gridy = 0;
        entradaPanel.add(new JLabel("Nome:"), gbc);

        gbc.gridx = 1;
        nomeField = new JTextField(20);
        entradaPanel.add(nomeField, gbc);

        // RG
        gbc.gridx = 0;
        gbc.gridy = 1;
        entradaPanel.add(new JLabel("RG:"), gbc);

        gbc.gridx = 1;
        rgField = new JTextField(20);
        entradaPanel.add(rgField, gbc);

        // Motivo da Visita
        gbc.gridx = 0;
        gbc.gridy = 2;
        entradaPanel.add(new JLabel("Motivo da Visita:"), gbc);

        gbc.gridx = 1;
        motivoField = new JTextField(20);
        entradaPanel.add(motivoField, gbc);

        // Apartamento Visitado
        gbc.gridx = 0;
        gbc.gridy = 3;
        entradaPanel.add(new JLabel("Apartamento Visitado:"), gbc);

        gbc.gridx = 1;
        apartamentoField = new JTextField(20);
        entradaPanel.add(apartamentoField, gbc);

        // Upload de Foto
        gbc.gridx = 0;
        gbc.gridy = 4;
        JButton uploadFotoButton = new JButton("Carregar Foto");
        uploadFotoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                carregarFoto();
            }
        });
        entradaPanel.add(uploadFotoButton, gbc);

        gbc.gridx = 1;
        fotoLabel = new JLabel();
        fotoLabel.setHorizontalAlignment(JLabel.CENTER);
        fotoLabel.setPreferredSize(new Dimension(100, 100));
        fotoLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        entradaPanel.add(fotoLabel, gbc);

        // Botão para Registrar Entrada
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton registrarEntradaButton = new JButton("Registrar Entrada");
        registrarEntradaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarEntrada();
            }
        });
        entradaPanel.add(registrarEntradaButton, gbc);

        return entradaPanel;
    }

    private JPanel criarAbaRegistrarSaida() {
        JPanel saidaPanel = new JPanel(new BorderLayout());

        // Título
        JLabel titulo = new JLabel("Visitantes Presentes");
        titulo.setHorizontalAlignment(JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        saidaPanel.add(titulo, BorderLayout.NORTH);

        // Lista de Visitantes
        listaVisitantes = new DefaultListModel<>();
        visitantesList = new JList<>(listaVisitantes);
        visitantesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        visitantesList.setCellRenderer(new VisitanteCellRenderer());
        JScrollPane scrollPane = new JScrollPane(visitantesList);
        saidaPanel.add(scrollPane, BorderLayout.CENTER);

        // Botão para Registrar Saída
        JButton registrarSaidaButton = new JButton("Registrar Saída");
        registrarSaidaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarSaida();
            }
        });
        JPanel botaoPanel = new JPanel();
        botaoPanel.add(registrarSaidaButton);
        saidaPanel.add(botaoPanel, BorderLayout.SOUTH);

        return saidaPanel;
    }

    private JPanel criarAbaAlertas() {
        JPanel alertasPanel = new JPanel(new BorderLayout());

        // Título
        JLabel titulo = new JLabel("Alertas de Permanência Prolongada");
        titulo.setHorizontalAlignment(JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        alertasPanel.add(titulo, BorderLayout.NORTH);

        // Área para exibir alertas
        JTextArea alertasArea = new JTextArea();
        alertasArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(alertasArea);
        alertasPanel.add(scrollPane, BorderLayout.CENTER);

        // Armazenar referência para atualizar os alertas
        this.alertasArea = alertasArea;

        return alertasPanel;
    }

    private JTextArea alertasArea; // Área para exibir alertas

    private void carregarFoto() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);

        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                foto = ImageIO.read(selectedFile); // Carrega a imagem
                ImageIcon imageIcon = new ImageIcon(foto.getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                fotoLabel.setIcon(imageIcon); // Exibe a imagem
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar a imagem.");
            }
        }
    }

    private void registrarEntrada() {
        String nome = nomeField.getText().trim();
        String rg = rgField.getText().trim();
        String motivo = motivoField.getText().trim();
        String apartamento = apartamentoField.getText().trim();
        LocalDateTime dataHoraEntrada = LocalDateTime.now();

        if (nome.isEmpty() || rg.isEmpty() || motivo.isEmpty() || apartamento.isEmpty() || foto == null) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos e carregue uma foto.");
            return;
        }

        Visitante visitante = new Visitante(nome, rg, foto, motivo, apartamento);
        listaVisitantes.addElement(visitante); // Adiciona o visitante à lista

        JOptionPane.showMessageDialog(this, "Entrada registrada com sucesso!");

        // Limpar campos após registro
        nomeField.setText("");
        rgField.setText("");
        motivoField.setText("");
        apartamentoField.setText("");
        fotoLabel.setIcon(null);
        foto = null;
    }

    private void registrarSaida() {
        Visitante visitanteSelecionado = visitantesList.getSelectedValue();

        if (visitanteSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um visitante para registrar a saída.");
            return;
        }

        visitanteSelecionado.setDataHoraSaida(LocalDateTime.now());
        listaVisitantes.removeElement(visitanteSelecionado); // Remove o visitante da lista

        long tempoPermanencia = visitanteSelecionado.calcularTempoPermanencia();
        JOptionPane.showMessageDialog(this, "Saída registrada.\nTempo de permanência: " + tempoPermanencia + " minutos.");

        // Verificar se houve excesso de permanência
        if (tempoPermanencia > 60) {
            JOptionPane.showMessageDialog(this, "Alerta: " + visitanteSelecionado.getNome() + " permaneceu mais de 1 hora no prédio.", "Alerta", JOptionPane.WARNING_MESSAGE);
            if (alertasArea != null) {
                alertasArea.append("Alerta: " + visitanteSelecionado.getNome() + " (RG: " + visitanteSelecionado.getRg() + ") permaneceu mais de 1 hora.\n");
            }
        }
    }

    // Renderer personalizado para exibir o nome e a foto do visitante na JList
    private class VisitanteCellRenderer extends JPanel implements ListCellRenderer<Visitante> {
        private JLabel nomeLabel = new JLabel();
        private JLabel fotoLabel = new JLabel();

        public VisitanteCellRenderer() {
            setLayout(new BorderLayout(5, 5));
            add(fotoLabel, BorderLayout.WEST);
            add(nomeLabel, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Visitante> list, Visitante value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            nomeLabel.setText(value.getNome() + " (RG: " + value.getRg() + ")");
            ImageIcon icon = new ImageIcon(value.getFoto().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
            fotoLabel.setIcon(icon);

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            setEnabled(list.isEnabled());
            setFont(list.getFont());
            return this;
        }
    }

    // Método para iniciar a verificação de alertas
    private void iniciarVerificacaoAlertas() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                verificarAlertas();
            }
        }, 0, 60000); // Verifica a cada 60 segundos
    }

    // Método para verificar se algum visitante ultrapassou 1 hora de permanência
    private void verificarAlertas() {
        for (int i = 0; i < listaVisitantes.size(); i++) {
            Visitante visitante = listaVisitantes.getElementAt(i);
            long tempo = visitante.calcularTempoPermanencia();
            if (tempo > 60) {
                // Exibir alerta na aba de Alertas
                if (alertasArea != null) {
                    SwingUtilities.invokeLater(() -> {
                        alertasArea.append("Alerta: " + visitante.getNome() + " (RG: " + visitante.getRg() + ") está há " + tempo + " minutos no prédio.\n");
                    });
                }

                // Exibir mensagem de alerta
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Alerta: " + visitante.getNome() + " está há mais de 1 hora no prédio.", "Alerta de Permanência", JOptionPane.WARNING_MESSAGE);
                });
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ControleEntradaSaida());
    }
}
