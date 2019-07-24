package fr.milleis.easyrest.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mailAccount", propOrder = {"value"})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailAccount {

    public static String BY_NAME = "name";

    @XmlValue
    private String value;

    @XmlAttribute(name = "by", required = true)
    private String by;
}
