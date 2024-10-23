package io.nomard.spoty_api_v1.templates.emails;

import io.nomard.spoty_api_v1.entities.Employee;

public class EmploymentEmail {

    private final Employee employee;
    private final String password;
    private final String hrEmail;

    public EmploymentEmail(Employee employee, String password, String hrEmail) {
        this.employee = employee;
        this.password = password;
        this.hrEmail = hrEmail;
    }

    public String getTemplate() {
        return "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "    <style>" +
                "        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }" +
                "        .container { width: 100%; max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }" +
                "        h1 { font-size: 24px; color: #333333; text-align: center; }" +
                "        p { font-size: 16px; color: #555555; line-height: 1.6; }" +
                "        .details { background-color: #f9f9f9; padding: 10px; border-left: 4px solid #4CAF50; margin: 20px 0; }" +
                "        .details p { margin: 5px 0; }" +
                "        .btn { display: inline-block; padding: 10px 15px; font-size: 16px; color: #ffffff; background-color: #4CAF50; text-align: center; text-decoration: none; border-radius: 5px; margin-top: 20px; }" +
                "        .footer { font-size: 14px; color: #777777; text-align: center; margin-top: 20px; border-top: 1px solid #e4e4e4; padding-top: 10px; }" +
                "    </style>" +
                "    <title>Employment Letter & Work Details</title>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "    <h1>Employment Letter & Work Details</h1>" +
                "    <p>Dear " + employee.getFirstName() + " " + employee.getLastName() + ",</p>" +
                "    <p>Congratulations on joining [Company Name]! We are pleased to inform you that your employment with us has been finalized. Below are your work details and initial credentials:</p>" +
                "    <div class='details'>" +
                "        <p><strong>Email:</strong> " + employee.getEmail() + "</p>" +
                "        <p><strong>Password:</strong> " + password + "</p>" +
                "        <p><strong>Position:</strong> " + employee.getRole().getName() + "</p>" +
                "        <p><strong>Department:</strong> " + employee.getDepartment().getName() + "</p>" +
                "    </div>" +
                "    <p>Please use the provided credentials to log in to your employee portal and update your password upon your first login.</p>" +
                "    <p>We are excited to have you on board and look forward to a productive working relationship.</p>" +
                "    <p>If you have any questions, feel free to contact the HR department at " + hrEmail + ".</p>" +
                "    <a href='[portal.link]' class='btn'>Log in to Employee Portal</a>" +
                "    <div class='footer'>" +
                "        <p>Best regards,</p>" +
                "        <p>[Company Name] HR Department</p>" +
                "    </div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
