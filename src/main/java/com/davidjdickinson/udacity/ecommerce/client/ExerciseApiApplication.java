package com.davidjdickinson.udacity.ecommerce.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExerciseApiApplication {

	private static Logger logger = LoggerFactory.getLogger(ExerciseApiApplication.class);

	public static void main(String[] args) {

		if (args.length < 2) {
			logger.error("Usage: java ExerciseApiApplication <api-hostname> <port> <username-to-create> [, <user-to-create> [, ...]]  - " +
						 "Running exercise of the ecommerce api with default valuse.");
		}

		String hostUrl = (args.length >= 1) ? args[0] : "http://localhost:8080";

		List usernames = new ArrayList<String>();

		if (args.length < 2) {
			usernames.add("DwayneTheRock");
		} else {
			for (int i = 1; i < args.length; i++) {
				usernames.add(args[i]);
			}
		}

		usernames.forEach( (username) -> {
			ApiRunner runner = new ApiRunner(hostUrl, (String)username);
			runner.start();
		});

	}

}
