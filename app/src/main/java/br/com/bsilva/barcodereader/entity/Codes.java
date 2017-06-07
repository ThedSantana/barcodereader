package br.com.bsilva.barcodereader.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Bruno Silva on 01/06/2017.
 */

@Entity
public class Codes {

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "code")
    private String code;

    @Property(nameInDb = "data_cadastro")
    private Date data_leitura;

    public Codes() {
    }

    public Codes(String code, Date data_leitura) {
        this.code = code;
        this.data_leitura = data_leitura;
    }

    @Generated(hash = 852976622)
    public Codes(Long id, String code, Date data_leitura) {
        this.id = id;
        this.code = code;
        this.data_leitura = data_leitura;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getData_leitura() {
        return data_leitura;
    }

    public void setData_leitura(Date data_leitura) {
        this.data_leitura = data_leitura;
    }

    @Override
    public String toString() {
        return "Codes{" +
                "id=" + id +
                ", code=\'" + code + '\'' +
                ", data leitura=\'"+data_leitura.toString()+"\'}";
    }
}
