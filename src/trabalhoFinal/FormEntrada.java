    package trabalhoFinal;

    import javax.imageio.ImageIO;
    import javax.swing.*;
    import javax.swing.table.DefaultTableModel;
    import java.awt.*;
    import java.awt.image.BufferedImage;
    import java.io.ByteArrayOutputStream;
    import java.io.IOException;
    import java.sql.SQLException;
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
        private JButton btnEditarUsuario;
        private BufferedImage foto;

        public FormEntrada() {

            btnCadastrar.addActionListener(e -> {
                try {
                    cadastrarVisita();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });

            btnCarregarFoto.addActionListener(e -> carregarFoto());

            btnSaida.addActionListener(e -> {
                try {
                    registrarSaida();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });

            tabbedPane1.addChangeListener(e -> {
                int TabSelecionado = tabbedPane1.getSelectedIndex();
                String selectedTabTitle = tabbedPane1.getTitleAt(TabSelecionado);

                // Verifica se a aba de visitantes está ativa
                if ("Visitantes".equals(selectedTabTitle)) {
                    try {
                        atualizarTabelaVisitas();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            btnEditarUsuario.addActionListener(e ->{
                try{
                    editarVisitante();
                }catch(SQLException ex){
                    throw new RuntimeException(ex);
                }
            });

            btnSaida.addActionListener(e -> {
                try{
                    registrarSaida();
                }catch(SQLException ex){
                    throw new RuntimeException(ex);
                }
            });

        }

        private void carregarFoto() {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(FormEntrada);

            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    foto = ImageIO.read(fileChooser.getSelectedFile());
                    ImageIcon icon = new ImageIcon(foto.getScaledInstance(lblFoto.getWidth(), lblFoto.getHeight(), Image.SCALE_SMOOTH));
                    lblFoto.setIcon(icon);
                    // Salve a imagem em uma variável para usar ao salvar no banco de dados
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(FormEntrada, "Erro ao carregar a foto.");
                }
            }
        }

        public void cadastrarVisita() throws SQLException {
            String nome = txtNome.getText();
            String rg = txtRg.getText();
            String motivo = txtMotivo.getText();
            String apartamento = txtApartamento.getText();

            if(nome.isEmpty() || rg.isEmpty() || motivo.isEmpty() || apartamento.isEmpty()) {
                JOptionPane.showMessageDialog(FormEntrada, "TODOS os campos são obrigatórios.");
                return;
            }

            byte[] fotoBytes = null;
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(foto, "jpg", baos);
                fotoBytes = baos.toByteArray();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(FormEntrada, "Erro ao processar a foto.");
                return;
            }catch(IllegalArgumentException ex){
                JOptionPane.showMessageDialog(FormEntrada, "Erro ao blablabla.");
                return;
            }

            Visitante visitante = new Visitante(nome, rg, foto, motivo, apartamento);
            RegistroDAO registroDAO = new RegistroDAO();
            registroDAO.inserirRegistro(visitante);

            JOptionPane.showMessageDialog(FormEntrada, "Visitante cadastrado com sucesso!");
            atualizarTabelaVisitas();
        }


        private void registrarSaida() throws SQLException {
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

        private void atualizarTabelaVisitas() throws SQLException {
            // Define o modelo da tabela
            DefaultTableModel tableModel = new DefaultTableModel(
                    new Object[]{"Nome", "RG", "Motivo", "Apartamento", "Entrada"}, 0
            );
            tblVisitas.setModel(tableModel);

            // Chama o DAO para buscar os dados
            RegistroDAO registroDAO = new RegistroDAO();
            List<Visitante> visitantes = registroDAO.listarVisitantes();

            // Adiciona as linhas na tabela
            for (Visitante visitante : visitantes) {
                tableModel.addRow(new Object[]{
                        visitante.getNome(),
                        visitante.getRg(),
                        visitante.getMotivoVisita(),
                        visitante.getApartamentoVisitado(),
                        visitante.getDataHoraEntrada()
                });
            }
        }

        private void editarVisitante() throws SQLException {
            int selectedRow = tblVisitas.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(FormEntrada, "Selecione um visitante para editar.");
                return;
            }

            // Obter o RG do visitante selecionado
            String rg = tblVisitas.getValueAt(selectedRow, 1).toString();

            // Buscar o visitante no banco de dados
            RegistroDAO registroDAO = new RegistroDAO();
            Visitante visitante = registroDAO.buscarVisitantePorRG(rg);

            if (visitante != null) {
                // Abrir o formulário de edição
                FormEditarVisitante formEditar = new FormEditarVisitante(this, visitante);
                formEditar.setVisible(true);

                // Verificar se foi salvo
                if (formEditar.isSalvo()) {
                    // Atualizar o banco de dados
                    boolean sucesso = registroDAO.atualizarVisitante(formEditar.getVisitante());
                    if (sucesso) {
                        JOptionPane.showMessageDialog(this, "Visitante atualizado com sucesso.");
                        RegistroDAO.listarVisitantes();
                    } else {
                        JOptionPane.showMessageDialog(this, "Erro ao atualizar visitante.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Visitante não encontrado.");
            }
        }


        private void salvarAlteracoes() throws SQLException {
            // Obter os dados dos campos
            String nome = txtNome.getText();
            String rg = txtRg.getText();
            String motivo = txtMotivo.getText();
            String apartamento = txtApartamento.getText();

            // Atualizar no banco de dados
            RegistroDAO registroDAO = new RegistroDAO();
            Visitante visitante = new Visitante();

            boolean sucesso = registroDAO.atualizarVisitante(visitante);

            if (sucesso) {
                JOptionPane.showMessageDialog(FormEntrada, "Dados atualizados com sucesso.");
                atualizarTabelaVisitas();
            } else {
                JOptionPane.showMessageDialog(FormEntrada, "Erro ao atualizar dados do visitante.");
            }
        }



        public JPanel getPanel() {
            return FormEntrada;
        }
    }
