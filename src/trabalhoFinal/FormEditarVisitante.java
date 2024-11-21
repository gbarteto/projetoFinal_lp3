package trabalhoFinal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class FormEditarVisitante extends JDialog {
    private JTextField txtNome;
    private JTextField txtRg;
    private JTextField txtMotivo;
    private JTextField txtApartamento;
    private JButton btnSalvar;
    private JButton btnCancelar;

    private Visitante visitante; // O visitante a ser editado
    private boolean salvo;       // Indicador de sucesso

    public FormEditarVisitante(Frame owner, Visitante visitante) {
        super(owner, "Editar Visitante", true);
        this.visitante = visitante;

        // Configurar layout
        setLayout(new GridLayout(5, 2, 10, 10));
        setSize(400, 300);
        setLocationRelativeTo(owner);

        // Campos de edição
        add(new JLabel("Nome:"));
        txtNome = new JTextField(visitante.getNome());
        add(txtNome);

        add(new JLabel("RG:"));
        txtRg = new JTextField(visitante.getRg());
        txtRg.setEditable(false); // RG não pode ser alterado
        add(txtRg);

        add(new JLabel("Motivo da Visita:"));
        txtMotivo = new JTextField(visitante.getMotivoVisita());
        add(txtMotivo);

        add(new JLabel("Apartamento:"));
        txtApartamento = new JTextField(visitante.getApartamentoVisitado());
        add(txtApartamento);

        // Botões
        btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(this::salvar);
        add(btnSalvar);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        add(btnCancelar);
    }

    private void salvar(ActionEvent e) {
        visitante.setNome(txtNome.getText());
        visitante.setMotivoVisita(txtMotivo.getText());
        visitante.setApartamentoVisitado(txtApartamento.getText());
        salvo = true;
        dispose();
    }

    public boolean isSalvo() {
        return salvo;
    }

    public Visitante getVisitante() {
        return visitante;
    }
}
