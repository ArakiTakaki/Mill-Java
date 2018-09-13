package com.github.ArakiTakaki.mill;

import java.util.ArrayList;
import java.util.List;


import java.sql.*;
import com.github.ArakiTakaki.mill.DBConnection.*;
/**
 * closeを行う必要のある、SQL接続クラス。
 * 
 * @author ArakiTakaki
 *
 */
public class Mill extends DBSetting{

	private static String dbname = "default";
	private static String server = "localhost";
	private static String port = "3306";
	private static String driver = "com.mysql.jdbc.Driver";
	private String table = "";
	private static Connection con = null;
	private Statement st = null;
	private StringBuilder query;
	private boolean where;
	
	
	public Mill() {
		this.connect();
	}

	public static void millSetting(String dbname) {
		Mill.dbname = dbname;
	}

	public static void millSetting(String dbname, String server) {
		Mill.dbname = dbname;
		Mill.server = server;
	}

	public static void millSetting(String dbname, String server, String port) {
		Mill.dbname = dbname;
		Mill.server = server;
		Mill.port = port;
	}

	public Mill setDriver(String driver) {
		Mill.driver = driver;
		return this;
	}

	public void connect() {
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(DBSetting.MySQL(Mill.server, Mill.port, Mill.dbname));
			this.st = con.createStatement();
		} catch (ClassNotFoundException e) {
			System.out.println(e);
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
		}
	}
	
	/**
	 * トランザクションスタート
	 * @throws SQLException
	 */
	public void transactionStart() throws SQLException{
		this.con.setAutoCommit(false);
	}
	
	/**
	 * ロールバック
	 */
	public void rollback() {
		try {
			this.con.rollback();			
		} catch(SQLException e) {}
	}
	
	/**
	 * コミット
	 * @throws SQLException
	 */
	public void commit() throws SQLException {
		this.con.commit();
	}

	/**
	 * テーブルの選択
	 * @param table
	 * @return method chain
	 */
	public Mill table(String table) {
		this.table = table;
		this.where = false;
		return this;
	}

	public Mill select(String[] attributes) {
		String result = String.join(", ", attributes);
		this.query.replace(7,8,result);
		System.out.println(this.query.toString());
		// TODO debug中
		return this;
	}
	
	public List<List<String>> toStringArray() {
		this.query.insert(0, "SELECT * FROM ");
		try {
			this.st.executeQuery("");
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 
	 * @param values
	 * @return
	 */

	public int insert(String[] values) {
		String data = String.join("', '", values);
		this.query = new StringBuilder();
		this.query.append("INSERT INTO ").append(this.table).append(" VALUES");
		this.query.append("('").append(data).append("')");
		// TODO debug
		System.out.println(this.query.toString());
		return this.executeUpdate();
	}
	
	public int insert(String[] values, String[] attributes) {
		String data = String.join("', '", values);
		String work = String.join(", ", values);
		this.query = new StringBuilder();
		this.query.append("INSERT INTO ")
			.append(this.table)
			.append("(")
			.append(work)
			.append(")")
			.append(" VALUES");
		this.query.append("('").append(data).append("')");
		// TODO debug
		System.out.println(this.query.toString());
		return this.executeUpdate();
	}

	public void update(String table) {
		this.query = new StringBuilder();
		this.query.append("UPDATE ").append(table).append(" SET ");
		this.where = false;
	}
	
	private int executeUpdate() {
		try {
			return this.st.executeUpdate(this.query.toString());
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return -1;
	}


	public void delete(String table) {
		this.query = new StringBuilder();
		this.query.append("DELETE ").append(table);
		this.where = false;
	}
	
	public int query(String query) {
		int result = -1;
		try {
			result = this.st.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}


	public Mill where(String col, String key) {
		String opperand = this.where ? " AND " : " WHERE ";
		this.query.append(opperand).append(col)
			.append(" = ").append(" '").append(key).append("' ");
		this.where = true;
		return this;
	}

	/**
	 * Stringの二次元配列として値を返却する。
	 * 
	 * @return table
	 */
	public ArrayList<ArrayList<String>> toArray() {
		try {
			ArrayList<ArrayList<String>> tbl = new ArrayList<ArrayList<String>>();
			ResultSet rs = this.st.executeQuery(this.query.toString());
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				ArrayList<String> rec = new ArrayList<String>();
				for (int i = 1; i < rsmd.getColumnCount(); i++) {
					rec.add(rs.getString(i));
				}
				tbl.add(rec);
			}
			return tbl;
		} catch (SQLException e) {
			System.out.println(e);
		}
		return null;
	}
	


	/**
	 * DTOモデルの通りに値を返却する。
	 * 
	 * @param mapping
	 * @return
	 */
	public List<Model> get(Model mapping) {
		this.table(mapping.getTable());
		System.out.println(mapping.getTable());
		System.out.println(mapping.getPrimaryKey());
		try {
			// (2) SQLの実行
			List<Model> list = new ArrayList<Model>();
			System.out.println(this.query.toString());
			ResultSet rs = this.st.executeQuery(this.query.toString());
			while (rs.next()) {
				Model dto = mapping.setMapping(rs);
				list.add(dto);
			}
			st.close();
			return list;
		} catch (SQLException e) {
			System.out.println(e);
		}
		return null;
	}
	
	public boolean isExists(){
		try {
			ResultSet rs = this.st.executeQuery(this.query.toString());
			if(rs.next()) {
				return true;
			}
		} catch(SQLException e){
			System.out.println(e);
		}
		return false;
	}
	
	public Model first(Model mapping) {
		try {
			ResultSet rs = this.st.executeQuery(this.query.toString());
			if(!rs.next())
				return null;
			Model dto = mapping.setMapping(rs);
			return dto;
		} catch (SQLException e) {
			System.out.println(e);
		}
		return null;
	}

	public Model find(String key, Model mapping) {
		this.table(mapping.getTable());
		this.where(mapping.primaryKey, key);
		try {
			ResultSet rs = this.st.executeQuery(this.query.toString());
			if (!rs.next())
				return null;
			Model dto = mapping.setMapping(rs);
			st.close();
			return dto;
		} catch (SQLException e) {
			System.out.println(e);
		}
		return null;
	}

	public Model mltipleFind(String[] keys, Model mapping) {
		this.table(mapping.getTable());
		for (int i = 0; i < keys.length; i++) {
			this.where(mapping.getMultipleKey()[i], keys[i]);
		}
		try {
			ResultSet rs = this.st.executeQuery(this.query.toString());
			Model rec = mapping.setMapping(rs);
			st.close();
			return rec;
		} catch (SQLException e) {
			System.out.println(e);
		}
		return null;
	}

	public List<Model> hasMany(
			String key, 
			Model one,
			Model many, 
			String primaryKey) {
		
		this.table(many.getTable());
		if (primaryKey != null) {
			this.where(primaryKey, key);
		} else {
			this.where(one.primaryKey, key);
		}
		try {
			List<Model> table = new ArrayList<Model>();
			System.out.println(this.query.toString());
			ResultSet rs = this.st.executeQuery(this.query.toString());
			while (rs.next()) {
				Model dto = many.setMapping(rs);
				table.add(dto);
			}
			st.close();
			return table;
		} catch (SQLException e) {
			System.out.println(e);
		}
		return null;
	}

	public void close() {
		try {
			this.con.close();
		} catch (SQLException e) {
		}
		try {
			this.st.close();
		} catch (SQLException e) {
		}
	}
}
