package com.devspark.childcare.utils;

import java.sql.*;

public class DbCheck {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:c:/Users/wijesingha/childcare management/DevSpark-childcare-backend/childcare.db";
        try (Connection conn = DriverManager.getConnection(url)) {
            System.out.println("--- NOTIFICATIONS ---");
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM notification")) {
                while (rs.next()) {
                    System.out.println("ID: " + rs.getString("notification_id") + " | Title: " + rs.getString("title") + " | Type: " + rs.getString("type"));
                }
            }
            System.out.println("\n--- TARGETS ---");
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM notification_target")) {
                while (rs.next()) {
                    System.out.println("NID: " + rs.getString("notification_id") + " | TargetType: " + rs.getString("target_type") + " | RefId: " + rs.getString("target_ref_id"));
                }
            }
            System.out.println("\n--- ACCOUNTS ---");
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT firebase_uid, email, role FROM account")) {
                while (rs.next()) {
                    System.out.println("UID: " + rs.getString("firebase_uid") + " | Email: " + rs.getString("email") + " | Role: " + rs.getString("role"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
