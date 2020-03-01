/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailbox;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Alessandro Nasso, Giancarlo Stefania
 */
public class Email implements Serializable {

    private final IntegerProperty id = new SimpleIntegerProperty();

    public final IntegerProperty IDProperty() {
        return this.id;
    }

    public final Integer getID() {
        return this.IDProperty().get();
    }

    public final void setID(final Integer id) {
        this.IDProperty().set(id);
    }

    private final StringProperty mittente = new SimpleStringProperty();

    public final StringProperty MittenteProperty() {
        return this.mittente;
    }

    public final String getMittente() {
        return this.MittenteProperty().get();
    }

    public final void setMittente(final String mittente) {
        this.MittenteProperty().set(mittente);
    }

    private final StringProperty destinatario = new SimpleStringProperty();

    public final StringProperty DestinatarioProperty() {
        return this.destinatario;
    }

    public final String getDestinatario() {
        return this.DestinatarioProperty().get();
    }

    public final void setDestinatario(final String destinatario) {
        this.DestinatarioProperty().set(destinatario);
    }

    private final StringProperty oggetto = new SimpleStringProperty();

    public final StringProperty OggettoProperty() {
        return this.oggetto;
    }

    public final String getOggetto() {
        return this.OggettoProperty().get();
    }

    public final void setOggetto(final String oggetto) {
        this.OggettoProperty().set(oggetto);
    }

    private final StringProperty testo = new SimpleStringProperty();

    public final StringProperty TestoProperty() {
        return this.testo;
    }

    public final String getTesto() {
        return this.TestoProperty().get();
    }

    public final void setTesto(final String testo) {
        this.TestoProperty().set(testo);
    }

    private final ObjectProperty<Date> data = new SimpleObjectProperty<Date>();

    public final ObjectProperty<Date> DataProperty() {
        return this.data;
    }

    public final Date getData() {
        return this.data.get();
    }

    public final void setData(final Date data) {
        this.data.set(data);
    }

    public Email(int id, String mittente, String destinatario, String oggetto, String testo, Date data) {
        setID(id);
        setMittente(mittente);
        setDestinatario(destinatario);
        setOggetto(oggetto);
        setTesto(testo);
        setData(data);
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeInt(getID());
        out.writeUTF(getMittente());
        out.writeUTF(getDestinatario());
        out.writeUTF(getOggetto());
        out.writeUTF(getTesto());
        out.writeObject(getData());
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {

        try {

            Field field = this.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(this, new SimpleIntegerProperty());

            field = this.getClass().getDeclaredField("mittente");
            field.setAccessible(true);
            field.set(this, new SimpleStringProperty());

            field = this.getClass().getDeclaredField("destinatario");
            field.setAccessible(true);
            field.set(this, new SimpleStringProperty());

            field = this.getClass().getDeclaredField("oggetto");
            field.setAccessible(true);
            field.set(this, new SimpleStringProperty());

            field = this.getClass().getDeclaredField("testo");
            field.setAccessible(true);
            field.set(this, new SimpleStringProperty());

            field = this.getClass().getDeclaredField("data");
            field.setAccessible(true);
            field.set(this, new SimpleObjectProperty<Date>());

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IOException(e);
        }

        setID(in.readInt());
        setMittente(in.readUTF());
        setDestinatario(in.readUTF());
        setOggetto(in.readUTF());
        setTesto(in.readUTF());
        setData((Date) in.readObject());
    }
}
