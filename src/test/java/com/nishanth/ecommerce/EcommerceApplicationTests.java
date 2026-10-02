package com.nishanth.ecommerce;

import com.nishanth.ecommerce.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.HttpURLConnection;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EcommerceApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private JwtService jwtService;

	// 1. No JWT → 401
	@Test
	void protectedEndpointWithoutTokenShouldReturn401() throws Exception {

		URI uri = URI.create(
				"http://localhost:" + port + "/api/auth/me"
		);

		HttpURLConnection connection =
				(HttpURLConnection) uri.toURL().openConnection();

		connection.setRequestMethod("GET");

		assertEquals(
				HttpURLConnection.HTTP_UNAUTHORIZED,
				connection.getResponseCode()
		);
	}

	// 2. CUSTOMER → ADMIN → 403
	@Test
	void customerShouldNotAccessAdminEndpoint() throws Exception {

		String token = jwtService.generateToken(
				1L,
				"customer@test.com",
				"CUSTOMER"
		);

		URI uri = URI.create(
				"http://localhost:" + port + "/api/auth/admin-test"
		);

		HttpURLConnection connection =
				(HttpURLConnection) uri.toURL().openConnection();

		connection.setRequestMethod("GET");
		connection.setRequestProperty(
				"Authorization",
				"Bearer " + token
		);

		assertEquals(
				HttpURLConnection.HTTP_FORBIDDEN,
				connection.getResponseCode()
		);
	}

	// 3. ADMIN → ADMIN → 200
	@Test
	void adminShouldAccessAdminEndpoint() throws Exception {

		String token = jwtService.generateToken(
				1L,
				"admin@test.com",
				"ADMIN"
		);

		URI uri = URI.create(
				"http://localhost:" + port + "/api/auth/admin-test"
		);

		HttpURLConnection connection =
				(HttpURLConnection) uri.toURL().openConnection();

		connection.setRequestMethod("GET");
		connection.setRequestProperty(
				"Authorization",
				"Bearer " + token
		);

		assertEquals(
				HttpURLConnection.HTTP_OK,
				connection.getResponseCode()
		);
	}

	// 4. Invalid JWT → 401
	@Test
	void invalidTokenShouldReturn401() throws Exception {

		URI uri = URI.create(
				"http://localhost:" + port + "/api/auth/me"
		);

		HttpURLConnection connection =
				(HttpURLConnection) uri.toURL().openConnection();

		connection.setRequestMethod("GET");
		connection.setRequestProperty(
				"Authorization",
				"Bearer this-is-an-invalid-token"
		);

		assertEquals(
				HttpURLConnection.HTTP_UNAUTHORIZED,
				connection.getResponseCode()
		);
	}

	// 5. Public endpoint without JWT → should NOT be 401
	@Test
	void loginEndpointShouldBePublic() throws Exception {

		URI uri = URI.create(
				"http://localhost:" + port + "/api/auth/login"
		);

		HttpURLConnection connection =
				(HttpURLConnection) uri.toURL().openConnection();

		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setRequestProperty(
				"Content-Type",
				"application/json"
		);

		String json = """
            {
                "email": "admin@gmail.com",
                "password": "admin123"
            }
            """;

		connection.getOutputStream().write(
				json.getBytes()
		);

		int status = connection.getResponseCode();

		assertEquals(
				HttpURLConnection.HTTP_OK,
				status
		);
	}
}