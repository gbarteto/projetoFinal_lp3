    package trabalhoFinal;

    import javax.imageio.ImageIO;
    import javax.swing.*;
    import javax.swing.table.DefaultTableModel;
    import java.awt.*;
    import java.awt.image.BufferedImage;
    import java.io.IOException;
    import java.time.LocalDateTime;
    import java.util.List;

    public class FormEntrada extends JFrame{
        private JPanel FormEntrada;
        private JTabbedPane tabbedPane1;
        private JLabel lblTitulo;
        private JLabel lblNome;
        private JTextField txtNome;
        private JLabel lblRg;
        private JTextField txtRg;
        private JLabel lblFoto;
        private JLabel lblMotivo;
        private JTextField txtMotivo;
        private JLabel lblApartamento;
        private JTextField txtApartamento;
        private JPanel lblAbaCadastro;
        private JPanel lblBuscaVisita;
        private JButton btnCadastrar;
        private JButton btnCarregarFoto;
        private JTable tblVisitas;
        private JButton btnSaida;
        private JLabel lblVistaTitle;
        private JPanel pnlAbaHistorico;
        private JLabel lblHistoricoTitle;
        private JTable tblHistorico;
        private JButton editarButton;
        private byte[] fotoBytes = null;

        public FormEntrada() {
            btnCadastrar.addActionListener(e -> cadastrarVisita());
            btnCarregarFoto.addActionListener(e -> carregarFoto());
            btnSaida.addActionListener(e -> registrarSaida());
        }

        public void cadastrarVisita() {
            String nome = txtNome.getText();
            String rg = txtRg.getText();
            String motivo = txtMotivo.getText();
            String apartamento = txtApartamento.getText();

            if(nome.isEmpty() || rg.isEmpty()) {
                JOptionPane.showMessageDialog(FormEntrada, "Nome e RG são obrigatórios.");
                return;
            }

            byte[] fotoBytes = null;
            if(fotoBytes != null) {
                fotoBytes = Visitante.imageToBytes(image);
            }
            Visitante visitante = new Visitante(nome, rg, foto, motivo, apartamento);
            RegistroDAO registroDAO = new RegistroDAO();
            registroDAO.inserirRegistro(visitante);

            JOptionPane.showMessageDialog(FormEntrada, "Visitante cadastrado com sucesso!");
            atualizarTabelaVisitas();
        }

        private void carregarFoto() {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(FormEntrada);

            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    BufferedImage image = ImageIO.read(fileChooser.getSelectedFile());
                    ImageIcon icon = new ImageIcon(image.getScaledInstance(lblFoto.getWidth(), lblFoto.getHeight(), Image.SCALE_SMOOTH));
                    lblFoto.setIcon(icon);
                    // Salve a imagem em uma variável para usar ao salvar no banco de dados
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(FormEntrada, "Erro ao carregar a foto.");
                }
            }
        }

        private void registrarSaida() {
            int selectedRow = tblVisitas.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(FormEntrada, "Selecione um visitante na tabela para registrar a saída.");
                return;
            }

            String rg = tblVisitas.getValueAt(selectedRow, 1).toString();
            LocalDateTime horarioSaida = LocalDateTime.now();

            RegistroDAO registroDAO = new RegistroDAO();
            registroDAO.registrarSaida(rg, horarioSaida);

            JOptionPane.showMessageDialog(FormEntrada, "Saída registrada com sucesso.");
            atualizarTabelaVisitas();
        }

        private void atualizarTabelaVisitas() {
            DefaultTableModel model = (DefaultTableModel) tblVisitas.getModel();
            model.setRowCount(0);  // Limpa a tabela

            RegistroDAO registroDAO = new RegistroDAO();
            List<Visitante> visitantes = registroDAO.listarVisitantes();  // Método que busca do banco

            for (Visitante visitante : visitantes) {
                model.addRow(new Object[]{
                        visitante.getNome(),
                        visitante.getRg(),
                        visitante.getDataHoraEntrada(),
                        visitante.getApartamentoVisitado()
                });
            }
        }



        public JPanel getPanel() {
            return FormEntrada;
        }
    }
