package com.appsbydmk.hospitalmgmt;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.ParseException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/BookAppointmentServlet")
public class BookAppointmentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String docSpcl, docTimings, patientName, apptDate;
	private Connection con;
	private final String DOC_SELECT_QUERY = "SELECT doc_id, doc_name "
			+ "FROM hosp_mgmt_doctor where lower(doc_specialization) like ? AND lower(doc_time) like ?";
	private final String APPT_INSERT_QUERY = "INSERT INTO hosp_mgmt_doc_appt(doc_id, doc_name, patient_id, patient_name, appt_time, appt_date) "
			+ "values(?,?,?,?,?,?)";
	private final String APPT_SELECT_QUERY = "SELECT COUNT(*) FROM hosp_mgmt_doc_appt "
			+ "where lower(doc_id) like ? AND lower(doc_name) like ? AND appt_date = ?";
	private final String PATIENT_SELECT_QUERY = "SELECT patient_id, patient_name FROM hosp_mgmt_patient WHERE lower(patient_name) like ? ";
	private PreparedStatement docPt, apptSelectPt, apptPt, patientSelectPt;
	private ResultSet docResults, apptSelectResults, patientSelectResults;

	public BookAppointmentServlet() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		con = DbConnectionHandler.getConnection();
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

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		this.getAllParameters(request);
		PrintWriter printWriter = response.getWriter();
		try {
			docPt = con.prepareStatement(DOC_SELECT_QUERY);
			docPt.setString(1, "%" + docSpcl.toLowerCase().trim() + "%");
			docPt.setString(2, "%" + docTimings.toLowerCase().trim() + "%");
			docResults = docPt.executeQuery();
			if (docResults.next()) {
				patientSelectPt = con.prepareStatement(PATIENT_SELECT_QUERY);
				patientSelectPt.setString(1, "%" + patientName.trim().toLowerCase() + "%");
				patientSelectResults = patientSelectPt.executeQuery();
				if (patientSelectResults.next()) {
					apptSelectPt = con.prepareStatement(APPT_SELECT_QUERY);
					apptSelectPt.setString(1, "%" + docResults.getString(1).trim().toLowerCase() + "%");
					apptSelectPt.setString(2, "%" + docResults.getString(2).trim().toLowerCase() + "%");
					apptSelectPt.setDate(3, this.getApptDate());
					apptSelectResults = apptSelectPt.executeQuery();
					if (apptSelectResults.next()) {
						int count = apptSelectResults.getInt(1);
						if (count < 2) {
							apptPt = con.prepareStatement(APPT_INSERT_QUERY);
							apptPt.setInt(1, docResults.getInt(1));
							apptPt.setString(2, docResults.getString(2));
							apptPt.setInt(3, patientSelectResults.getInt(1));
							apptPt.setString(4, patientSelectResults.getString(2));
							apptPt.setString(5, docTimings);
							apptPt.setDate(6, this.getApptDate());
							boolean result = apptPt.execute();
							if (!result) {
								printWriter.println("Your appointment has been booked.");
							} else {
								printWriter.println("Sorry, there was some error in booking you appointment.");
							}
						} else {
							printWriter.println("Sorry, appointments are full for this date. Please select another date.");
						}
					}
				} else {
					printWriter.println("There is no patient in our database of that name. "
							+ "Please register yourself first and then book an appointment.");
					printWriter.println("<a href = '/patient_registration.html'>Register the patient</a>");
				}
			} else {
				printWriter.println("There are no doctors available currently.");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	private void getAllParameters(HttpServletRequest request) {
		docSpcl = request.getParameter("docSpcl");
		apptDate = request.getParameter("apptDate");
		docTimings = request.getParameter("docTimings");
		patientName = request.getParameter("patientName");
	}

	private Date getApptDate() throws ParseException {
		Date sqlDob = Date.valueOf(apptDate);
		return sqlDob;
	}
}
