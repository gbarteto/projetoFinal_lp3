package trabalhoFinal;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Visitante {
    String nome;
    String rg;
    BufferedImage foto;
    String motivoVisita;
    String apartamentoVisitado;
    LocalDateTime dataHoraEntrada;
    LocalDateTime dataHoraSaida;
    long tempoPermanencia;

    public Visitante(String nome, String rg, BufferedImage foto, String motivoVisita, String apartamentoVisitado) {
        this.nome = nome;
        this.rg = rg;
        this.foto = foto;
        this.motivoVisita = motivoVisita;
        this.apartamentoVisitado = apartamentoVisitado;
        this.dataHoraEntrada = LocalDateTime.now();
    }

    public void setDataHoraSaida(LocalDateTime dataHoraSaida) {
        this.dataHoraSaida = dataHoraSaida;
    }

    public long calcularTempoPermanencia() {
        LocalDateTime fim = (dataHoraSaida != null) ? dataHoraSaida : LocalDateTime.now();
        tempoPermanencia =  Duration.between(dataHoraEntrada, fim).toMinutes();
        return tempoPermanencia;
    }

    public static BufferedImage bytesToImage(byte[] imageData) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageData)) {
            return ImageIO.read(bais);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] imageToBytes(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", baos);  // ou "png" dependendo do formato
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return nome + " (RG: " + rg + ")";
    }


    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getRg() {
        return rg;
    }
    public void setRg(String rg) {
        this.rg = rg;
    }
    public BufferedImage getFoto() {
        return foto;
    }
    public void setFoto(BufferedImage foto) {
        this.foto = foto;
    }
    public String getMotivoVisita() {
        return motivoVisita;
    }
    public void setMotivoVisita(String motivoVisita) {
        this.motivoVisita = motivoVisita;
    }
    public String getApartamentoVisitado() {
        return apartamentoVisitado;
    }
    public void setApartamentoVisitado(String apartamentoVisitado) {
        this.apartamentoVisitado = apartamentoVisitado;
    }
    public LocalDateTime getDataHoraEntrada() {
        return dataHoraEntrada;
    }
    public void setDataHoraEntrada(LocalDateTime dataHoraEntrada) {
        this.dataHoraEntrada = dataHoraEntrada;
    }
    public LocalDateTime getDataHoraSaida() {
        return dataHoraSaida;
    }
    public void setDataSaida(LocalDateTime dataSaida) {
        this.dataHoraSaida = dataSaida;
    }
    public long getTempoPermanencia() {
        return tempoPermanencia;
    }
    public void setTempoPermanencia(long tempoPermanencia) {
        this.tempoPermanencia = tempoPermanencia;
    }
}
