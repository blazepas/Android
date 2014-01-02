package pl.mareklatuszek.tpp.activator;

import java.io.Serializable;
import java.util.Date;

/**
 * Bean do obsługi żądań interfejsu <code>TppPremiumCheckServlet</code> dla kont
 * premium.
 * 
 * @author Jacek Jabłonka
 * 
 */
public class TppPremiumCheckBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;
	private String login;
	private int premium;
	private Date created;
	private String appVersion;
	private String hash;

	public TppPremiumCheckBean() {
	}

	public TppPremiumCheckBean(String login, int premium, Date created, String appVersion,
			String hash) {
		super();
		this.login = login;
		this.premium = premium;
		this.created = created;
		this.appVersion = appVersion;
		this.hash = hash;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public int getPremium() {
		return premium;
	}

	public void setPremium(int premium) {
		this.premium = premium;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TppPremiumCheckBean [id=");
		builder.append(id);
		builder.append(", login=");
		builder.append(login);
		builder.append(", premium=");
		builder.append(premium);
		builder.append(", created=");
		builder.append(created);
		builder.append(", appVersion=");
		builder.append(appVersion);
		builder.append(", hash=");
		builder.append(hash);
		builder.append("]");
		return builder.toString();
	}

}
