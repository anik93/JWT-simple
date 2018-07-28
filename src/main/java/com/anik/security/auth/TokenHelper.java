package com.anik.security.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.anik.domain.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class TokenHelper {

	@Value("${app.name}")
	private String APP_NAME;

	@Value("${jwt.secret}")
	public String SECRET;

	@Value("${jwt.expires_in}")
	private int EXPIRES_IN;

	@Value("${jwt.mobile_expires_in}")
	private int MOBILE_EXPIRES_IN;

	@Value("${jwt.header}")
	private String AUTH_HEADER;

	private SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

	public String getUsernameFromToken(String token) {
		String username;
		try {
			Claims claims = getAllClaimsFromToken(token);
			username = claims.getSubject();
		} catch (Exception e) {
			username = null;
		}
		return username;
	}

	public Date getIssuedAtDateFromToken(String token) {
		Date issueAt;
		try {
			Claims claims = getAllClaimsFromToken(token);
			issueAt = claims.getIssuedAt();
		} catch (Exception e) {
			issueAt = null;
		}
		return issueAt;
	}

	public String getAudienceFromToken(String token) {
		String audience;
		try {
			Claims claims = getAllClaimsFromToken(token);
			audience = claims.getAudience();
		} catch (Exception e) {
			audience = null;
		}
		return audience;
	}

	public String refreshToken(String token, Device device) {
		String refreshedToken;
		Date a = new Date();
		try {
			Claims claims = getAllClaimsFromToken(token);
			claims.setIssuedAt(a);
			refreshedToken = Jwts.builder().setClaims(claims).setExpiration(generateExpirationDate(device))
					.signWith(SIGNATURE_ALGORITHM, SECRET).compact();
		} catch (Exception e) {
			refreshedToken = null;
		}
		return refreshedToken;
	}

	public String generateToken(String username, Device device) {
		return Jwts.builder().setExpiration(generateExpirationDate(device)).setIssuer(APP_NAME).setSubject(username)
				.setIssuedAt(new Date()).signWith(SIGNATURE_ALGORITHM, SECRET).compact();
	}

	public int getExpiredIn(Device device) {
		return device.isMobile() || device.isTablet() ? MOBILE_EXPIRES_IN : EXPIRES_IN;
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		User user = (User) userDetails;
		String username = getUsernameFromToken(token);
		Date created = getIssuedAtDateFromToken(token);
		return username != null && username.equals(userDetails.getUsername())
				&& !isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate());
	}

	public String getToken(HttpServletRequest request) {
		String authHeader = getAuthHeaderFromHeader(request);
		return authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
	}

	public String getAuthHeaderFromHeader(HttpServletRequest request) {
		return request.getHeader(AUTH_HEADER);
	}

	private Date generateExpirationDate(Device device) {
		long expiresIn = device.isTablet() || device.isMobile() ? MOBILE_EXPIRES_IN : EXPIRES_IN;
		return new Date(new Date().getTime() + expiresIn * 1000);
	}

	private Claims getAllClaimsFromToken(String token) {
		try {
			return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
		} catch (Exception e) {
			return null;
		}
	}

	private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
		return lastPasswordReset != null && created.before(lastPasswordReset);
	}
}