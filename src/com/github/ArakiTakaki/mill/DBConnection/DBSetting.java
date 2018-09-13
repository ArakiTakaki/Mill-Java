package com.github.ArakiTakaki.mill.DBConnection;


public abstract class DBSetting {

	protected static String MySQL(String server, String port, String dbname) {
		StringBuilder url = new StringBuilder();
		url.append("jdbc:mysql://").append(server).append(":").append(port).append("/").append(dbname);
		url.append("?useUnicode=true&characterEncoding=utf8"); // 設定
		return url.toString();
	}
	

}
