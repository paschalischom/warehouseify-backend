package com.uoi.spmsearch.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class Config {

	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public OkHttpClient getOkHttpClient() {
		return new OkHttpClient();
	}

	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public Request.Builder getRequestBuilder() {
		return new Request.Builder();
	}

	/*@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public Firestore getFirestore() throws IOException {
		FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(GoogleCredentials.getApplicationDefault())
				.build();
		FirebaseApp.initializeApp(options);

		return FirestoreClient.getFirestore();
	}*/
}
