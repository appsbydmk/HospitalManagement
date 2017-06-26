package com.appsbydmk.hospitalmgmt;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;

@WebServlet("/DoctorRegisterServlet")
public class DoctorRegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection con = null;
	private PreparedStatement pt = null;
	private String docName, docAddress, docEmail, docQual, docSpcl, docAchievements, docTime;
	private int docAge, docExp;
	private long docMobile;
	private final String DOC_INSERT_QUERY = "INSERT INTO hosp_mgmt_doctor(doc_name, doc_age, doc_address, "
			+ "doc_mobile_no, doc_email, doc_qualification, doc_specialization, doc_experience, doc_achievement, doc_time) values (?,?,?,?,?,?,?,?,?,?)";

	public DoctorRegisterServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		this.getAllParameters(request);
		PrintWriter printWriter = response.getWriter();
		try {
			this.prepareQuery();
			boolean result = pt.execute();
			if (!result) {
				int n = pt.getUpdateCount();
				if (n > 0) {
					printWriter.println("You have been registered!");
				}
			} else {
				printWriter.print("Some error occured while saving your data!");
			}

		} catch (SQLException ex) {
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

	private void prepareQuery() throws SQLException {
		pt = con.prepareStatement(DOC_INSERT_QUERY);
		pt.setString(1, docName);
		pt.setInt(2, docAge);
		pt.setString(3, docAddress);
		pt.setLong(4, docMobile);
		pt.setString(5, docEmail);
		pt.setString(6, docQual);
		pt.setString(7, docSpcl);
		pt.setInt(8, docExp);
		pt.setString(9, docAchievements);
		pt.setString(10, docTime);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	@Override
	public void init() throws ServletException {
		super.init();
		con = DbConnectionHandler.getConnection();
	}

	private void getAllParameters(HttpServletRequest request) {
		docName = request.getParameter("doctorName");
		docAge = Integer.parseInt(request.getParameter("doctorAge"));
		docAddress = request.getParameter("docAddress");
		docMobile = Long.parseLong(request.getParameter("docMobile"));
		docEmail = request.getParameter("docEmailAddr");
		docQual = request.getParameter("docQualifications");
		docSpcl = request.getParameter("docSpcl");
		docExp = Integer.parseInt(request.getParameter("docExperience"));
		docAchievements = request.getParameter("docAchievements");
		docTime = request.getParameter("docTimings");
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
