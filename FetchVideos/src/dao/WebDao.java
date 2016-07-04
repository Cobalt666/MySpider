package dao;

import java.sql.*;
import java.util.ArrayList;

import db.Connect;
import bean.Web;

public class WebDao {

	public void addUrls(ArrayList<Web> webList) {
		Connection conn = Connect.createConn();
		String sql = "insert into web(name,url,deep) values (?, ?, ?)";
		PreparedStatement ps = Connect.prepare(conn, sql);
		try {
			for (int i = 0; i < webList.size(); i++) {
				Web web = new Web();
				web = webList.get(i);
				ps.setString(1, web.getName());
				ps.setString(2, web.getUrl());
				ps.setInt(3, web.getDeep());
				ps.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Connect.close(ps);
		Connect.close(conn);
	}

	public void addAUrl(Web web) {
		Connection conn = Connect.createConn();
		String sql = "insert into web(name,url,deep) values (?, ?, ?)";
		PreparedStatement ps = Connect.prepare(conn, sql);
		try {
			ps.setString(1, web.getName());
			ps.setString(2, web.getUrl());
			ps.setInt(3, web.getDeep());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Connect.close(ps);
		Connect.close(conn);
	}

}
