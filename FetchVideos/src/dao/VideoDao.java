package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import db.Connect;
import bean.Video;

public class VideoDao {

	public void add(Video v) {
		Connection conn = Connect.createConn();
		String sql = "insert into video(title, author, intro, watchCount, collectCount, danmakuCount, createTime, url) values (?,?,?,?,?,?,?,?)";
		PreparedStatement ps = Connect.prepare(conn, sql);
		try {
			ps.setString(1, v.getTitle());
			ps.setString(2, v.getAuthor());
			ps.setString(3, v.getIntro());
			ps.setInt(4, v.getWatchCount());
			ps.setInt(5, v.getCollectCount());
			ps.setInt(6, v.getDanmakuCount());
			ps.setString(7, v.getCreateTime());
			ps.setString(8, v.getUrl());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Connect.close(ps);
		Connect.close(conn);
	}
}
