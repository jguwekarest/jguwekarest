/*
 *
 * This is partly taken and changed from JAQPOT Quattro see https://github.com/KinkyDesign/jaqpot-api/
 * Copyright (C) 2014-2015 KinkyDesign (Charalampos Chomenidis, Pantelis Sopasakis)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *
 */
package io.swagger.api.authorization;

import io.swagger.api.Exeption;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AuthorizationService {

    private static final Logger LOG = Logger.getLogger(AuthorizationService.class.getName());

    public static final String SSO_HOST = "openam.in-silico.ch",
            SSO_IDENTITY = "https://" + SSO_HOST + "/auth/%s",
            SSO_POLICY = "https://" + SSO_HOST + "/pol",
    /**
     * SSO identity service
     */
    SSOidentity = String.format(SSO_IDENTITY, ""),
    /**
     * SSO authentication service
     */
    SSOauthenticate = String.format(SSO_IDENTITY, "authenticate"), //?uri=service=openldap
    /**
     * SSO policy service
     */
    SSOPolicy = String.format(SSO_POLICY, ""),
    /**
     * SSO identity validation service
     */
    SSOvalidate = String.format(SSO_IDENTITY, "isTokenValid"),
    /**
     * SSO logout/token-invalidation service
     */
    SSOlogout = String.format(SSO_IDENTITY, "logout"),
    /**
     * SSO authorization service
     */
    SSOauthorization = String.format(SSO_IDENTITY, "authorize"),
            SSOattributes = String.format(SSO_IDENTITY, "attributes");

    /**
     * Receive an authentication token.
     *
     * @param username username
     * @param password password
     * @return String token
     * @throws Exeption.AAException error
     */
    public static String login(String username, String password) throws Exeption.AAException {
        Client client = ClientBuilder.newClient();
        MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();
        formData.putSingle("username", username);
        formData.putSingle("password", password);
        System.out.println("formData is: " + formData);
        System.out.println("SSOauthenticate is: " + SSOauthenticate);
        Response response = client.target(SSOauthenticate)
                .request()
                .post(Entity.form(formData));
        String responseValue = response.readEntity(String.class);
        response.close();
        if (response.getStatus() == 401) {
            throw new Exeption.AAException(401, "You cannot login - please, check your credentials.");
        } else {
            String token = "";
            token = responseValue.substring(9).replaceAll("\n", "");
            return token;
        }
    }

    /**
     * Validates an authentication token.
     *
     * @param token an authentication token
     * @return boolean validity of the token
     */
    public static boolean validate(String token) {
        Client client = ClientBuilder.newClient();
        MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();
        formData.putSingle("tokenid", token);
        Response response = client.target(SSOvalidate)
                .request()
                .post(Entity.form(formData));
        String message = response.readEntity(String.class).trim();
        int status = response.getStatus();
        response.close();
        return "boolean=true".equals(message) && status == 200;
    }

    /**
     * Logs out a user given their authentication token.
     *
     * @param token an authentication token
     * @return boolean if it was possible to devalue the token
     * @throws Exeption.AAException error
     */
    public static boolean logout(String token) throws Exeption.AAException {
        Client client = ClientBuilder.newClient();
        MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();
        formData.putSingle("subjectid", token);
        Response response = client.target(SSOlogout)
                .request()
                .post(Entity.form(formData));
        Integer status = response.getStatus();
        Integer positiveStatus = 200;
        response.close();
        return Objects.equals(positiveStatus, status);
    }

    /**
     * Queries the SSO server to get user attributes and returns a user.
     *
     * @param token authentication token.
     * @return user entity with the capabilities of a new user and the retrieved
     * attributes.
     */
    public String getUserFromSSO(String token) {
        Client client = ClientBuilder.newClient();
        MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();
        formData.putSingle("subjectid", token);
        Response response = client.target(SSOattributes)
                .request()
                .post(Entity.form(formData));
        String user = "";
        if (200 == response.getStatus()) {
            String attributesList = response.readEntity(String.class);
            Scanner scanner = new Scanner(attributesList);

            String template = "userdetails.attribute.name=%s";
            String line;
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                if (String.format(template, "uid").equals(line)) {
                    if (scanner.hasNextLine()) {
                        line = scanner.nextLine();
                        String[] values = line.split("=");
                        if (values.length >= 2) {
                            user = values[1];
                        }
                    }
                }
            }
        } else {
            LOG.log(Level.SEVERE, "SSO attributes responded with status {0} for token {1}",
                    new Object[]{response.getStatus(), token});
            LOG.log(Level.SEVERE, response.readEntity(String.class));
            LOG.info("Returning null user!");
            return null;
        }
        response.close();
        LOG.log(Level.INFO, "User ID   {0}", user);
        return user;
    }

    public boolean authorize(String token, String httpMethod, String uri) {
        Client client = ClientBuilder.newClient();
        MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();
        formData.putSingle("subjectid", token);
        formData.putSingle("uri", uri);
        formData.putSingle("action", httpMethod);
        Response response = client.target(SSOauthorization)
                .request()
                .post(Entity.form(formData));
        String message = response.readEntity(String.class).trim();
        int status = response.getStatus();
        response.close();
        return "boolean=true".equals(message) && status == 200;
    }
}