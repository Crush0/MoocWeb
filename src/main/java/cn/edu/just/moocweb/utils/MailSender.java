package cn.edu.just.moocweb.utils;

import org.dom4j.Attribute;
import org.dom4j.Element;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

/**
 * @author Admin
 */
public class MailSender {
    private final Properties prop = new Properties();
    private final Session session;
    private final Message msg;
    private final Transport transport;


    public static class Builder {
        private final String mailContent;
        private final String toAddress;
        private String auth = "true";
        private String host = "smtp.qq.com";
//        private String protocol = "smtp";
        private String subject = "";
        private String fromAddress = "1377370607@qq.com";
        private String fromCount = "1377370607@qq.com";
        private String fromPassword = "zwfvmjpncbhrjegd";

        public Builder Subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder Auth(String auth) {
            this.auth = auth;
            return this;
        }

        public Builder Host(String host) {
            this.host = host;
            return this;
        }

        public Builder FromCount(String fromCount) {
            this.fromCount = fromCount;
            return this;
        }

        public Builder FromAddress(String fromAddress) {
            this.fromAddress = fromAddress;
            return this;
        }

        public Builder FromPassword(String fromPassword) {
            this.fromPassword = fromPassword;
            return this;
        }

        public Builder(String mailContent, String toAddress) {
            this.mailContent = mailContent;
            this.toAddress = toAddress;
        }

//        public Builder Protocol(String protocol) {
//            this.protocol = protocol;
//            return this;
//        }

        public MailSender send() throws Exception {
            return new MailSender(this);
        }

    }

    private MailSender(Builder builder) throws Exception {
        prop.setProperty("mail.debug", "false");
        prop.setProperty("mail.default-encoding","utf-8");
        prop.setProperty("mail.host",builder.host);
        prop.setProperty("mail.password",builder.fromPassword);
        prop.setProperty("mail.port","465");
        prop.setProperty("mail.username",builder.fromAddress);
        prop.setProperty("mail.stmp.auth",builder.auth);
        prop.setProperty("mail.stmp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        prop.setProperty("mail.stmp.starttls.enable","true");
        session = Session.getInstance(prop);
        msg = new MimeMessage(session);
        transport = session.getTransport();
        msg.setSubject(builder.subject);
        msg.setFrom(new InternetAddress(builder.fromAddress));
        transport.connect(builder.fromCount, builder.fromPassword);
        msg.setContent(builder.mailContent, "text/html;charset=utf-8");
        transport.sendMessage(msg, new Address[]{new InternetAddress(builder.toAddress)});
    }
//    public static Element getNodes(Element node, String attrName, String attrValue) {
//        final List<Attribute> listAttr = node.attributes();
//        for (final Attribute attr : listAttr) {
//            final String name = attr.getName();
//            final String value = attr.getValue();
//            if (attrName.equals(name) && attrValue.equals(value)) {
//                return node;
//            }
//        }
//        // 递归遍历当前节点所有的子节点
//        final List<Element> listElement = node.elements();
//        for (Element e : listElement) {
//            Element temp = getNodes(e, attrName, attrValue);
//            // 递归
//            if (temp != null) {
//                return temp;
//            }
//        }
//        return null;
//    }
}
