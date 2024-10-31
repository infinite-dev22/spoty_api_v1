package io.nomard.spoty_api_v1.templates.emails;

import io.nomard.spoty_api_v1.entities.Employee;
import io.nomard.spoty_api_v1.entities.TenantSettings;

import java.time.LocalDate;

public class EmploymentData {

    private final Employee employee;
    private final String password;
    private final Employee user;
    private final TenantSettings tenantSettings;
    private final String middleName;
    private final String userDesignation;

    public EmploymentData(Employee employee, String password, Employee user, TenantSettings tenantSettings) {
        this.employee = employee;
        this.password = password;
        this.user = user;
        this.tenantSettings = tenantSettings;
        this.middleName = (!employee.getOtherName().isEmpty() && !employee.getOtherName().isBlank()) ? " " + employee.getOtherName() : "";
        this.userDesignation = (user.getDesignation() != null) ? user.getDesignation().getName() : "Human Resource";
    }

    public String getTemplate() {
        return "<!DOCTYPE html>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "    <style>" +
                "        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }" +
                "        .container { width: 100%; max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; }" +
                "        h1 { font-size: 24px; color: #333333; text-align: center; }" +
                "        p { font-size: 16px; color: #555555; line-height: 1.6; }" +
                "        .details { background-color: #f9f9f9; padding: 10px; border-left: 4px solid #4CAF50; margin: 20px 0; }" +
                "        .details p { margin: 5px 0; }" +
                "        .footer { font-size: 14px; color: #777777; text-align: center; margin-top: 20px; border-top: 1px solid #e4e4e4; padding-top: 10px; }" +
                "    </style>" +
                "    <title>Employment Letter & Work Details</title>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "    <h1>Employment Letter & Work Details</h1>" +
                "    <p>Dear " + employee.getFirstName() + " " + middleName + employee.getLastName() + ",</p>" +
                "    <p>Congratulations on joining " + tenantSettings.getName() + "! We are pleased to inform you that your employment with us has been finalized. Below are your work details and initial credentials:</p>" +
                "    <div class='details'>" +
                "        <p><strong>Email:</strong> " + employee.getEmail() + "</p>" +
                "        <p><strong>Password:</strong> " + password + "</p>" +
                "    </div>" +
                "    <p>Please use the provided credentials to log in to your employee portal and update your password upon your first login.</p>" +
                "    <p>We are excited to have you on board and look forward to a productive working relationship.</p>" +
                "    <p>If you have any questions, feel free to contact the HR department at " + tenantSettings.getHrEmail() + ".</p>" +
                "    <div class='footer'>" +
                "        <p>Best regards,</p>" +
                "        <p>" + tenantSettings.getName() + " HR Department</p>" +
                "    </div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    public String getEmploymentLetterHtml() {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<title>Confirmation of Employment</title>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; line-height: 1.6; background-color: #f4f4f9; }" +
                ".container { padding: 20px; max-width: 800px; margin: auto; background: #fff; }" +
                ".header { text-align: center; padding-bottom: 20px; }" +
                ".content p { margin: 10px 0; }" +
                ".footer { text-align: center; color: #555; margin-top: 40px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'><h1>Confirmation of Employment</h1></div>" +
                "<div class='content'>" +
                "<p>" + user.getFirstName() + " " + user.getLastName() +
                "<br/>" + userDesignation +
                "<br/>" + tenantSettings.getName() +
                "<br/>" + tenantSettings.getPhysicalAddress() +
                "<br/>" + LocalDate.now() + "</p>" +
                "<p>" + employee.getFirstName() + " " + middleName + employee.getLastName() + "</p>" +
                "<p>Dear " + employee.getFirstName() + " " + middleName + employee.getLastName() + ",</p>" +
                "<p>We are pleased to inform you that you have been confirmed as " +
                "a " + employee.getEmploymentStatus().getName() + " employee in the position of " +
                employee.getDesignation().getName() + " with <strong>" + tenantSettings.getName() + "</strong> as of " +
                "<strong>" + employee.getStartDate() + "</strong>. We value you and believe that will be an asset to " +
                "our organization.</p>" +
                "<p>Effective from " + employee.getStartDate() + ", your status as a " + employee.getDesignation().getName() + " will commence. You " +
                "will be entitled to the terms and conditions of employment as stated in your " +
                "employment contract, except for any changes that may be mutually agreed upon in writing.</p>" +
                "<p>Your employment will be subjected to the companies policies and procedures, and " +
                "we trust that you will offer high standards of performance and professionalism that you have demonstrated so far.</p>" +
                "<p>If you have any questions or need further clarification, please feel free to " +
                "contact the HR Department.</p>" +
                "<p>Congratulations once again on the confirmation of your employment, We look forward to your " +
                "contributions and success at " + tenantSettings.getName() + "</p>" +
                "</div>" +
                "<div class='footer'><p>© " + tenantSettings.getName() + " | All rights reserved</p></div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

}
