package pl.mareklatuszek.tpp.activator;

import java.io.Serializable;
import java.util.Date;

/**
 * Bean do obsługi odpowiedzi interfejsu <code>TppPremiumCheckServlet</code> dla
 * kont premium.
 * 
 * @author Jacek Jabłonka
 * 
 */
public class TppPremiumStatusBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private int loginStatus; // 0 - existing; 1 - added
	private String login;
	private Date premiumExpires;
	private String message;

	public int getLoginStatus() {
		return loginStatus;
	}

	public void setLoginStatus(int loginStatus) {
		this.loginStatus = loginStatus;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public Date getPremiumExpires() {
		return premiumExpires;
	}

	public void setPremiumExpires(Date premiumExpires) {
		this.premiumExpires = premiumExpires;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TppPremiumStatusBean [loginStatus=");
		builder.append(loginStatus);
		builder.append(", login=");
		builder.append(login);
		builder.append(", premiumExpires=");
		builder.append(premiumExpires);
		builder.append(", message=");
		builder.append(message);
		builder.append("]");
		return builder.toString();
	}

}
