package com.appsbydmk.hospitalmgmt;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;

import java.text.ParseException;

@WebServlet("/PatientRegisterServlet")
public class PatientRegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String patientName, dob, pAddress, pCity, pState, pEmail, pGender, pMaritalStatus;
	private long phoneNo;
	private Connection con;
	private PreparedStatement pt = null;
	private final String PATIENT_INSERT_QUERY = "INSERT INTO hosp_mgmt_patient(patient_name, "
			+ "patient_dob, patient_addr, patient_city, patient_state, patient_phone_no, patient_email, patient_gender, patient_marital_status)"
			+ " VALUES(?,?,?,?,?,?,?,?,?)";

	public PatientRegisterServlet() {
		super();
	}

	@Override
	public void init() throws ServletException {
		super.init();
		con = DbConnectionHandler.getConnection();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		this.getAllParameters(request);
		PrintWriter printWriter = response.getWriter();
		try {
			this.prepareInsertQuery();
			boolean result = pt.execute();
			if (!result) {
				int n = pt.getUpdateCount();
				if (n > 0) {
					printWriter.println("You have been registered!");
				}
			} else {
				printWriter.println("Some error occured while saving your data!");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (pt != null)
					pt.close();
				if (con != null)
					con.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	private void prepareInsertQuery() throws SQLException, ParseException {
		pt = con.prepareStatement(PATIENT_INSERT_QUERY);
		pt.setString(1, patientName);
		pt.setDate(2, this.getDob());
		pt.setString(3, pAddress);
		pt.setString(4, pCity);
		pt.setString(5, pState);
		pt.setLong(6, phoneNo);
		pt.setString(7, pEmail);
		pt.setString(8, pGender);
		pt.setString(9, pMaritalStatus);
	}

	private void getAllParameters(HttpServletRequest request) {
		patientName = request.getParameter("patientName");
		dob = request.getParameter("birthDate");
		pAddress = request.getParameter("patientAddr");
		pCity = request.getParameter("city");
		pState = request.getParameter("state");
		phoneNo = Long.parseLong(request.getParameter("phoneNo"));
		pEmail = request.getParameter("emailAddr");
		pGender = request.getParameter("gender");
		pMaritalStatus = request.getParameter("maritalStatus");
	}

	private Date getDob() throws ParseException {
		Date sqlDob = Date.valueOf(dob);
		return sqlDob;
	}
	
	@Override
	public void destroy() {
		super.destroy();
		try {
			if (con != null)
				con.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
